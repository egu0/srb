package im.eg.srb.core.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import im.eg.common.exception.Assert;
import im.eg.common.result.ResponseEnum;
import im.eg.srb.core.enums.LendStatusEnum;
import im.eg.srb.core.enums.TransTypeEnum;
import im.eg.srb.core.hfb.FormHelper;
import im.eg.srb.core.hfb.HfbConst;
import im.eg.srb.core.hfb.RequestHelper;
import im.eg.srb.core.mapper.*;
import im.eg.srb.core.pojo.bo.TransFlowBO;
import im.eg.srb.core.pojo.entity.Lend;
import im.eg.srb.core.pojo.entity.LendItem;
import im.eg.srb.core.pojo.entity.LendItemReturn;
import im.eg.srb.core.pojo.entity.LendReturn;
import im.eg.srb.core.service.*;
import im.eg.srb.core.util.LendNoUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 还款记录表 服务实现类
 * </p>
 *
 * @author EGU0
 * @since 2024-11-23
 */
@Service
public class LendReturnServiceImpl extends ServiceImpl<LendReturnMapper, LendReturn> implements LendReturnService {

    @Resource
    private UserBindService userBindService;

    @Resource
    private LendItemReturnService lendItemReturnService;

    @Resource
    private LendMapper lendMapper;

    @Resource
    private UserAccountService userAccountService;

    @Resource
    private UserAccountMapper userAccountMapper;

    @Resource
    private TransFlowService transFlowService;

    @Resource
    private LendItemReturnMapper lendItemReturnMapper;

    @Resource
    private LendItemMapper lendItemMapper;

    @Override
    public List<LendReturn> listById(Long lendId) {
        QueryWrapper<LendReturn> q = new QueryWrapper<>();
        q.eq("lend_id", lendId);
        return baseMapper.selectList(q);
    }

    @Override
    public String commitReturn(Long lendReturnId, Long userId) {
        LendReturn lendReturn = baseMapper.selectById(lendReturnId);
        Lend lend = lendMapper.selectById(lendReturn.getLendId());
        String bindCode = userBindService.getBindCodeByUserId(userId);

        // 余额校验
        BigDecimal returnTotal = lendReturn.getTotal();
        BigDecimal accAmt = userAccountService.getAccAmt(userId);
        Assert.isTrue(accAmt.compareTo(returnTotal) >= 0, ResponseEnum.NOT_SUFFICIENT_FUNDS_ERROR);

        // 构建参数集合
        Map<String, Object> params = new HashMap<>();
        params.put("agentId", HfbConst.AGENT_ID);
        params.put("agentGoodsName", lend.getTitle()); // 商品名称
        params.put("agentBatchNo", lendReturn.getReturnNo()); // 批次号
        params.put("fromBindCode", bindCode); // 还款人绑定协议好
        params.put("totalAmt", returnTotal); // 还款总额
        List<Map<String, Object>> detail = lendItemReturnService.obtainReturnDetail(lendReturnId); // 获取还款明细
        params.put("data", JSONObject.toJSONString(detail));
        params.put("voteFeeAmt", BigDecimal.ZERO);
        params.put("notifyUrl", HfbConst.BORROW_RETURN_NOTIFY_URL);
        params.put("returnUrl", HfbConst.BORROW_RETURN_RETURN_URL);
        params.put("timestamp", RequestHelper.getTimestamp());
        params.put("sign", RequestHelper.getSign(params));
        return FormHelper.buildForm(HfbConst.BORROW_RETURN_URL, params);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String notify(Map<String, Object> params) {
        // 幂等性判断
        String agentBatchNo = (String) params.get("agentBatchNo"); // 还款编号
        Integer count = transFlowService.countByTransNo(agentBatchNo);
        if (count == 1) {
            return "success";
        }

        // 更新还款状态
        QueryWrapper<LendReturn> lendReturnQueryWrapper = new QueryWrapper<>();
        lendReturnQueryWrapper.eq("return_no", agentBatchNo);
        LendReturn lendReturn = baseMapper.selectOne(lendReturnQueryWrapper);
        lendReturn.setStatus(1);
        lendReturn.setFee(new BigDecimal((String) params.get("voteFeeAmt"))); // 手续费
        lendReturn.setRealReturnTime(LocalDateTime.now());
        baseMapper.updateById(lendReturn);

        // 更新标的状态。这里存在 bug：没限制期数还款顺序，所以还了最后一期后整个标的就结清了
        Lend lend = lendMapper.selectById(lendReturn.getLendId());
        if (lendReturn.getLast()) {
            lend.setStatus(LendStatusEnum.PAY_OK.getStatus());
            lendMapper.updateById(lend);
        }

        // 修改还款账户金额
        String bindCode1 = userBindService.getBindCodeByUserId(lendReturn.getUserId());
        BigDecimal totalAmt = new BigDecimal((String) params.get("totalAmt")); // 扣款额
        userAccountMapper.updateAccount(bindCode1, totalAmt.negate(), BigDecimal.ZERO);

        // 还款流水
        TransFlowBO transFlowBO = new TransFlowBO(agentBatchNo, bindCode1,
                totalAmt, TransTypeEnum.RETURN_DOWN,
                String.format("还款流水，项目编号 %s，项目名称 %s", lend.getLendNo(), lend.getTitle()));
        transFlowService.saveTransFlow(transFlowBO);

        // 回款明细
        List<LendItemReturn> lendItemReturnList = lendItemReturnService.listByLendReturnId(lendReturn.getId());
        for (LendItemReturn lendItemReturn : lendItemReturnList) {
            // 更新回款状态
            lendItemReturn.setStatus(1);
            lendItemReturn.setRealReturnTime(LocalDateTime.now());
            lendItemReturnMapper.updateById(lendItemReturn);

            // 更新出借信息
            LendItem lendItem = lendItemMapper.selectById(lendItemReturn.getLendItemId());
            if (lendReturn.getLast()) {
                lendItem.setStatus(2); // 已还款
            }
            lendItem.setRealAmount(lendItem.getRealAmount().add(lendItemReturn.getInterest())); // 更新收益
            lendItemMapper.updateById(lendItem);

            // 修改投资账户金额
            String bindCode2 = userBindService.getBindCodeByUserId(lendItemReturn.getInvestUserId());
            userAccountMapper.updateAccount(bindCode2, lendItemReturn.getTotal(), BigDecimal.ZERO);

            // 回款流水
            TransFlowBO transFlowBO2 = new TransFlowBO(LendNoUtils.getReturnItemNo(), bindCode2,
                    lendItemReturn.getTotal(), TransTypeEnum.INVEST_BACK,
                    String.format("还款到账，项目编号 %s，项目名称 %s", lend.getLendNo(), lend.getTitle()));
            transFlowService.saveTransFlow(transFlowBO2);
        }

        return "success";
    }

}
