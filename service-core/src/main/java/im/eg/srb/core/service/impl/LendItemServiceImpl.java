package im.eg.srb.core.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import im.eg.common.exception.Assert;
import im.eg.common.result.ResponseEnum;
import im.eg.srb.core.enums.LendStatusEnum;
import im.eg.srb.core.hfb.FormHelper;
import im.eg.srb.core.hfb.HfbConst;
import im.eg.srb.core.hfb.RequestHelper;
import im.eg.srb.core.mapper.LendItemMapper;
import im.eg.srb.core.mapper.LendMapper;
import im.eg.srb.core.pojo.entity.Lend;
import im.eg.srb.core.pojo.entity.LendItem;
import im.eg.srb.core.pojo.vo.InvestVO;
import im.eg.srb.core.service.LendItemService;
import im.eg.srb.core.service.LendService;
import im.eg.srb.core.service.UserAccountService;
import im.eg.srb.core.service.UserBindService;
import im.eg.srb.core.util.LendNoUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 标的出借记录表 服务实现类
 * </p>
 *
 * @author EGU0
 * @since 2024-11-23
 */
@Service
public class LendItemServiceImpl extends ServiceImpl<LendItemMapper, LendItem> implements LendItemService {

    @Resource
    private LendMapper lendMapper;

    @Resource
    private UserAccountService userAccountService;

    @Resource
    private UserBindService userBindService;

    @Resource
    private LendService lendService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String commitInvest(InvestVO investVO) {
        Long userId = investVO.getInvestUserId();
        Long lendId = investVO.getLendId();
        Lend lend = lendMapper.selectById(lendId);
        Assert.notNull(lend, ResponseEnum.LEND_NOT_EXIST);

        // 判断标的状态是否为募资中
        Assert.equals(lend.getStatus(), LendStatusEnum.INVEST_RUN.getStatus(), ResponseEnum.LEND_INVEST_ERROR);

        // 已投金额 + 当前投资金额 <= 标的金额
        BigDecimal investAmount = new BigDecimal(investVO.getInvestAmount());
        BigDecimal sum = lend.getInvestAmount().add(investAmount);
        Assert.isTrue(sum.compareTo(lend.getAmount()) <= 0, ResponseEnum.LEND_FULL_SCALE_ERROR);

        // 当前投资金额 <= 用户余额
        BigDecimal accAmt = userAccountService.getAccAmt(userId);
        Assert.isTrue(investAmount.compareTo(accAmt) <= 0,
                ResponseEnum.NOT_SUFFICIENT_FUNDS_ERROR);

        // 投资金额 >= lowestAmount
        BigDecimal lowestAmount = lend.getLowestAmount();
        Assert.isTrue(investAmount.compareTo(lend.getLowestAmount()) >= 0,
                ResponseEnum.INVEST_AMOUNT_NOT_VALID);

        // 标的投资信息
        LendItem lendItem = new LendItem();
        lendItem.setInvestUserId(userId); // 投资人 user_id
        lendItem.setInvestName(investVO.getInvestUserName()); // 投资人名字
        String lendItemNo = LendNoUtils.getLendItemNo();
        lendItem.setLendItemNo(lendItemNo); // 投资条目编号
        lendItem.setLendId(investVO.getLendId()); // 标的 id
        lendItem.setInvestAmount(investAmount); // 投资金额
        lendItem.setLendYearRate(lend.getLendYearRate()); // 年化
        lendItem.setInvestTime(LocalDateTime.now()); // 投资时间
        lendItem.setLendStartDate(lend.getLendStartDate()); // 开始时间
        lendItem.setLendEndDate(lend.getLendEndDate()); // 结束时间
        BigDecimal expectedProfit = lendService.calculateInvestmentInterest(investAmount,
                lend.getLendYearRate(), lend.getPeriod(), lend.getReturnMethod());
        lendItem.setExpectAmount(expectedProfit); // 预期收益
        lendItem.setRealAmount(new BigDecimal("0")); // 实际收益
        lendItem.setStatus(0); // 状态（0：默认 1：已支付 2：已还款）
        baseMapper.insert(lendItem);

        // 封装参数
        String voteBindCode = userBindService.getBindCodeByUserId(userId); // 投资人的绑定号
        String benefitBindCode = userBindService.getBindCodeByUserId(userId); // 借款人的绑定号

        Map<String, Object> params = new HashMap<>();
        params.put("agentId", HfbConst.AGENT_ID);
        params.put("voteBindCode", voteBindCode);
        params.put("benefitBindCode", benefitBindCode);
        params.put("agentProjectCode", lend.getLendNo()); // 标的编号
        params.put("agentProjectName", lend.getTitle());
        params.put("agentBillNo", lendItemNo); // 标的投资编号
        params.put("voteAmt", investAmount);
        params.put("votePrizeAmt", "0");
        params.put("voteFeeAmt", "0");
        params.put("projectAmt", lend.getAmount()); // 标的总金额
        params.put("note", "标的投资");
        params.put("notifyUrl", HfbConst.INVEST_NOTIFY_URL);
        params.put("returnUrl", HfbConst.INVEST_RETURN_URL);
        params.put("timestamp", RequestHelper.getTimestamp());
        String sign = RequestHelper.getSign(params);
        params.put("sign", sign);

        return FormHelper.buildForm(HfbConst.INVEST_URL, params);
    }
}
