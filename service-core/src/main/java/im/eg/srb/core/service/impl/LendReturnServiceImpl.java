package im.eg.srb.core.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import im.eg.common.exception.Assert;
import im.eg.common.result.ResponseEnum;
import im.eg.srb.core.hfb.FormHelper;
import im.eg.srb.core.hfb.HfbConst;
import im.eg.srb.core.hfb.RequestHelper;
import im.eg.srb.core.mapper.LendMapper;
import im.eg.srb.core.mapper.LendReturnMapper;
import im.eg.srb.core.pojo.entity.Lend;
import im.eg.srb.core.pojo.entity.LendReturn;
import im.eg.srb.core.service.LendItemReturnService;
import im.eg.srb.core.service.LendReturnService;
import im.eg.srb.core.service.UserAccountService;
import im.eg.srb.core.service.UserBindService;
import im.eg.srb.core.util.LendNoUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
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
        params.put("agentBatchNo", LendNoUtils.getReturnNo()); // 批次号
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
}
