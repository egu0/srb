package im.eg.srb.core.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import im.eg.common.exception.Assert;
import im.eg.common.exception.BusinessException;
import im.eg.common.result.ResponseEnum;
import im.eg.srb.core.enums.LendStatusEnum;
import im.eg.srb.core.enums.TransTypeEnum;
import im.eg.srb.core.hfb.HfbConst;
import im.eg.srb.core.hfb.RequestHelper;
import im.eg.srb.core.mapper.BorrowerMapper;
import im.eg.srb.core.mapper.LendMapper;
import im.eg.srb.core.mapper.UserAccountMapper;
import im.eg.srb.core.mapper.UserInfoMapper;
import im.eg.srb.core.pojo.bo.TransFlowBO;
import im.eg.srb.core.pojo.entity.*;
import im.eg.srb.core.pojo.vo.BorrowInfoApprovalVO;
import im.eg.srb.core.pojo.vo.BorrowerDetailVO;
import im.eg.srb.core.pojo.vo.LendVO;
import im.eg.srb.core.service.*;
import im.eg.srb.core.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 标的准备表 服务实现类
 * </p>
 *
 * @author EGU0
 * @since 2024-11-23
 */
@Slf4j
@Service
public class LendServiceImpl extends ServiceImpl<LendMapper, Lend> implements LendService {

    @Resource
    private DictService dictService;

    @Resource
    private BorrowerMapper borrowerMapper;

    @Resource
    private BorrowerService borrowerService;

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private UserAccountMapper userAccountMapper;

    @Resource
    private TransFlowService transFlowService;

    @Resource
    private LendItemService lendItemService;

    @Resource
    private LendReturnService lendReturnService;

    @Resource
    private LendItemReturnService lendItemReturnService;

    @Override
    public void createLend(BorrowInfoApprovalVO borrowInfoApprovalVO, BorrowInfo borrowInfo) {
        Lend lend = new Lend();
        lend.setLendNo(LendNoUtils.getLendNo());
        lend.setUserId(borrowInfo.getUserId()); // 借款人 user_id
        lend.setBorrowInfoId(borrowInfo.getId()); // 借款 id
        lend.setTitle(borrowInfoApprovalVO.getTitle()); // 标的标题
        lend.setAmount(borrowInfo.getAmount());
        lend.setPeriod(borrowInfo.getPeriod());
        lend.setLendYearRate(divide100(borrowInfoApprovalVO.getLendYearRate())); // 管理员审批时可能会修改年化利率
        lend.setServiceRate(divide100(borrowInfoApprovalVO.getServiceRate())); // 同上
        lend.setReturnMethod(borrowInfo.getReturnMethod());
        lend.setLowestAmount(new BigDecimal("100")); // 最低投资金额
        lend.setInvestAmount(new BigDecimal("0")); // 已投资金额
        lend.setInvestNum(0); // 已投资人数
        lend.setPublishDate(LocalDateTime.now()); // 标的发布日期
        String lendStartDate = borrowInfoApprovalVO.getLendStartDate();
        LocalDate lendStartDate2 = LocalDate.parse(lendStartDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        lend.setLendStartDate(lendStartDate2); // 开始日期（起息日）
        lend.setLendEndDate(lendStartDate2.plusMonths(borrowInfo.getPeriod())); // 结束日期
        lend.setLendInfo(borrowInfoApprovalVO.getLendInfo()); // 标的描述
        // 平台预期收入 = amount * ( serviceRate / 12 * 期数 )
        BigDecimal expectedProfit = lend.getServiceRate()
                .divide(new BigDecimal(12), 8, RoundingMode.HALF_DOWN) // 抹零
                .multiply(new BigDecimal(lend.getPeriod()))
                .multiply(lend.getAmount());
        lend.setExpectAmount(expectedProfit);
        lend.setRealAmount(new BigDecimal("0")); // 实际收益（在结束时计算）
        lend.setStatus(LendStatusEnum.INVEST_RUN.getStatus()); // 状态：募资中
        lend.setCheckTime(LocalDateTime.now());
        lend.setCheckAdminId(1L); // 暂时忽略
        baseMapper.insert(lend);
    }

    @Override
    public IPage<LendVO> listPage(Page<Lend> pageParam) {
        Page<Lend> lendPage = baseMapper.selectPage(pageParam, null);
        List<LendVO> records = lendPage.getRecords().stream()
                .map(lend -> {
                    LendVO lendVO = new LendVO();
                    BeanUtils.copyProperties(lend, lendVO);
                    lendVO.getParam().put("returnMethod",
                            dictService.getDictName("returnMethod", lend.getReturnMethod()));
                    lendVO.getParam().put("status",
                            LendStatusEnum.getMsgByStatus(lend.getStatus()));
                    return lendVO;
                })
                .collect(Collectors.toList());

        Page<LendVO> lendVOPage = new Page<>();
        lendVOPage.setRecords(records);
        lendVOPage.setTotal(lendPage.getTotal());
        lendVOPage.setPages(lendPage.getPages());
        lendVOPage.setCurrent(lendPage.getCurrent());
        lendVOPage.setSize(lendPage.getSize());
        return lendVOPage;
    }

    @Override
    public Map<String, Object> getLendDetail(Long lendId) {
        // 组装 LendVO
        Lend lend = baseMapper.selectById(lendId);
        Assert.notNull(lend, ResponseEnum.LEND_NOT_EXIST);
        LendVO lendVO = new LendVO();
        BeanUtils.copyProperties(lend, lendVO);
        lendVO.getParam().put("returnMethod",
                dictService.getDictName("returnMethod", lend.getReturnMethod()));
        lendVO.getParam().put("status",
                LendStatusEnum.getMsgByStatus(lend.getStatus()));

        // 获取 BorrowerDetailVO
        Long userId = lend.getUserId();
        QueryWrapper<Borrower> borrowerQueryWrapper = new QueryWrapper<>();
        borrowerQueryWrapper.eq("user_id", userId);
        Borrower borrower = borrowerMapper.selectOne(borrowerQueryWrapper);
        BorrowerDetailVO borrowerDetailVO = borrowerService.getBorrowerDetailVOByBorrowerId(borrower.getId());

        // 整合数据
        Map<String, Object> data = new HashMap<>();
        data.put("lend", lendVO);
        data.put("borrower", borrowerDetailVO);
        return data;
    }

    @Override
    public BigDecimal calculateInvestmentInterest(BigDecimal invest, BigDecimal yearRate, Integer totalMonth, Integer returnMethod) {
        BigDecimal interest = new BigDecimal("0");
        if (returnMethod == null || yearRate == null || invest == null || totalMonth == null) {
            return interest;
        }
        switch (returnMethod) {
            case 1: // ReturnMethodEnum.ONE.getMethod() == 1
                interest = Amount1Helper.getInterestCount(invest, yearRate, totalMonth);
                break;
            case 2:
                interest = Amount2Helper.getInterestCount(invest, yearRate, totalMonth);
                break;
            case 3:
                interest = Amount3Helper.getInterestCount(invest, yearRate, totalMonth);
                break;
            case 4:
                interest = Amount4Helper.getInterestCount(invest, yearRate, totalMonth);
                break;
            default:
                break;
        }
        return interest;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void makeLoan(Long lendId) {
        Lend lend = baseMapper.selectById(lendId);

        // 调用汇付宝放款接口
        Map<String, Object> params = new HashMap<>();
        params.put("agentId", HfbConst.AGENT_ID);
        params.put("agentProjectCode", lend.getLendNo()); // 标的编号
        params.put("agentBillNo", LendNoUtils.getLoanNo()); // 放款编号
        // 平台服务费 = 已投金额 x 月年化 x 投资时长
        BigDecimal monthRate = lend.getServiceRate().divide(new BigDecimal("12"), 8, RoundingMode.HALF_DOWN);
        BigDecimal mchFee = lend.getInvestAmount().multiply(monthRate).multiply(new BigDecimal(lend.getPeriod()));
        params.put("mchFee", mchFee);
        params.put("timestamp", RequestHelper.getTimestamp());
        params.put("sign", RequestHelper.getSign(params));
        // 提交远程请求
        JSONObject response = RequestHelper.sendRequest(params, HfbConst.MAKE_LOAD_URL);

        // 无需校验签名，因为是同步请求
        // 放款失败
        if (!"0000".equals(response.getString("resultCode"))) {
            throw new BusinessException(response.getString("resultMsg"));
        }

//        a. 标的状态和标的平台收益
        lend.setRealAmount(mchFee); // 平台实际收益
        lend.setStatus(LendStatusEnum.PAY_RUN.getStatus());
        lend.setPaymentTime(LocalDateTime.now());
        baseMapper.updateById(lend);

//        b. 给借款账号转入金额
        Long userId = lend.getUserId();
        UserInfo userInfo = userInfoMapper.selectById(userId);
        String bindCode = userInfo.getBindCode();
        BigDecimal voteAmt = new BigDecimal(response.getString("voteAmt"));
        userAccountMapper.updateAccount(bindCode, voteAmt,
                new BigDecimal("0"));

//        c. 增加借款交易流水
        TransFlowBO transFlowBO = new TransFlowBO(
                response.getString("agentBillNo"), // 放款编号
                bindCode, // 借款人绑定编号
                voteAmt, // 到账金额
                TransTypeEnum.BORROW_BACK,
                String.format("标的放款，标的编号 %s，标的标题 %s", lend.getLendNo(), lend.getTitle())
        );
        transFlowService.saveTransFlow(transFlowBO);

        List<LendItem> lendItemList = lendItemService.lendItemsOfLend(lendId, 1);
        lendItemList.forEach(lendItem -> {
            Long investUserId = lendItem.getInvestUserId();
            UserInfo userInfo1 = userInfoMapper.selectById(investUserId);
            String bindCode1 = userInfo1.getBindCode();
//        d. 扣除投资人解冻资金
            userAccountMapper.updateAccount(bindCode1, new BigDecimal("0"),
                    lendItem.getInvestAmount().negate() // 取负数
            );

//        e. 增加投资人交易流水
            TransFlowBO transFlowBO1 = new TransFlowBO(
                    LendNoUtils.getTransNo(), //
                    bindCode1, // 借款人绑定编号
                    lendItem.getInvestAmount(), // 解冻金额
                    TransTypeEnum.INVEST_UNLOCK,
                    String.format("冻结资金转出，标的编号 %s，标的标题 %s", lend.getLendNo(), lend.getTitle())
            );
            transFlowService.saveTransFlow(transFlowBO1);
        });

//        f. 生成借款人还款计划和出借人回款计划
        this.repaymentPlan(lend);
    }

    private BigDecimal divide100(BigDecimal o) {
        return o.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }

    /**
     * 还款计划（针对借款人、单个标的）
     */
    private void repaymentPlan(Lend lend) {
        //----------------------------------------------------------------------------
        // 1. 生成标的的所有还款计划
        //----------------------------------------------------------------------------
        // 用于保存所有还款计划的列表
        List<LendReturn> lendReturnList = new ArrayList<>();
        int lendPeriod = lend.getPeriod();
        for (int i = 1; i <= lendPeriod; i++) {
            // 构造每一期的还款计划
            LendReturn lendReturn = buildLendReturn(lend, i, lendPeriod);

            // 这三个字段推迟在后边填充
            //lendReturn.setPrincipal(); // 本金
            //lendReturn.setInterest(); // 利息
            //lendReturn.setTotal(); // 总金额

            // 加入集合
            lendReturnList.add(lendReturn);
        }

        // 批量保存还款计划
        lendReturnService.saveBatch(lendReturnList);

        // 获取映射： 还款期数  -->  还款计划ID
        Map<Integer, Long> period2LendReturnId = lendReturnList.stream()
                .collect(Collectors.toMap(LendReturn::getCurrentPeriod, LendReturn::getId));

        //----------------------------------------------------------------------------
        // 2. 生成所有投资的回款计划
        //----------------------------------------------------------------------------

        // 用于记录所有投资的所有回款计划
        List<LendItemReturn> lendItemReturnList = new ArrayList<>();

        // 获取所有已支付的投资记录
        List<LendItem> lendItemList = lendItemService.lendItemsOfLend(lend.getId(), 1);
        for (LendItem lendItem : lendItemList) {
            lendItemReturnList.addAll(this.returnInvest(lendItem.getId(), period2LendReturnId, lend));
        }

        //----------------------------------------------------------------------------
        // 3. 更新所有还款计划的 本金/利息/本息 三个字段（为何要放在最后计算？答：确保数据一致）
        //----------------------------------------------------------------------------

        for (LendReturn lendReturn : lendReturnList) {
            BigDecimal sumOfPrincipal = lendItemReturnList.stream()
                    .filter(item -> item.getLendReturnId().equals(lendReturn.getId()))
                    .map(LendItemReturn::getPrincipal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal sumOfInterest = lendItemReturnList.stream()
                    .filter(item -> item.getLendReturnId().equals(lendReturn.getId()))
                    .map(LendItemReturn::getInterest)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal sumOfTotal = lendItemReturnList.stream()
                    .filter(item -> item.getLendReturnId().equals(lendReturn.getId()))
                    .map(LendItemReturn::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            lendReturn.setPrincipal(sumOfPrincipal);
            lendReturn.setInterest(sumOfInterest);
            lendReturn.setTotal(sumOfTotal);
        }

        // 批量更新
        lendReturnService.updateBatchById(lendReturnList);
    }

    private static LendReturn buildLendReturn(Lend lend, int i, int lendPeriod) {
        LendReturn lendReturn = new LendReturn();
        lendReturn.setReturnNo(LendNoUtils.getReturnNo()); // 流水号
        lendReturn.setLendId(lend.getId()); // 标的 id
        lendReturn.setBorrowInfoId(lend.getBorrowInfoId()); // 借款信息 id
        lendReturn.setUserId(lend.getUserId());
        lendReturn.setAmount(lend.getAmount());
        lendReturn.setBaseAmount(lend.getInvestAmount()); // 放款金额
        lendReturn.setLendYearRate(lend.getLendYearRate());
        lendReturn.setCurrentPeriod(i); // 当前期数
        lendReturn.setReturnMethod(lend.getReturnMethod());
        lendReturn.setFee(new BigDecimal("0"));
        lendReturn.setReturnDate(lend.getLendStartDate().plusMonths(i));
        lendReturn.setOverdue(false); // 是否预期。忽略该处功能
        lendReturn.setLast(i == lendPeriod); // 是否为最后一期
        lendReturn.setStatus(0); // 未归还
        return lendReturn;
    }

    /**
     * 回款计划（针对单个投资人、单个标的所有回款记录）
     *
     * @param lendItemId          标的投资 id
     * @param period2LendReturnId 还款期数 -> 还款计划 id
     */
    private List<LendItemReturn> returnInvest(Long lendItemId,
                                              Map<Integer, Long> period2LendReturnId,
                                              Lend lend) {
        // 获取当前投资记录
        LendItem lendItem = lendItemService.getById(lendItemId);

        // 调用工具类计算不同还款方式下的还款本金和还款利息
        Map<Integer, BigDecimal> period2Interest; // 每期利息
        Map<Integer, BigDecimal> period2Principal; // 每期本金
        BigDecimal investAmount = lendItem.getInvestAmount();
        BigDecimal lendYearRate = lendItem.getLendYearRate();
        Integer period = lend.getPeriod();
        int returnMethod = lend.getReturnMethod();
        switch (returnMethod) {
            case 1: // ReturnMethodEnum.ONE.getMethod() == 1
                period2Interest = Amount1Helper.getPerMonthInterest(investAmount, lendYearRate, period);
                period2Principal = Amount1Helper.getPerMonthPrincipal(investAmount, lendYearRate, period);
                break;
            case 2:
                period2Interest = Amount2Helper.getPerMonthInterest(investAmount, lendYearRate, period);
                period2Principal = Amount2Helper.getPerMonthPrincipal(investAmount, lendYearRate, period);
                break;
            case 3:
                period2Interest = Amount3Helper.getPerMonthInterest(investAmount, lendYearRate, period);
                period2Principal = Amount3Helper.getPerMonthPrincipal(investAmount, lendYearRate, period);
                break;
            case 4:
                period2Interest = Amount4Helper.getPerMonthInterest(investAmount, lendYearRate, period);
                period2Principal = Amount4Helper.getPerMonthPrincipal(investAmount, lendYearRate, period);
                break;
            default:
                period2Interest = new HashMap<>();
                period2Principal = new HashMap<>();
                break;
        }

        // 回款计划列表
        ArrayList<LendItemReturn> lendItemReturnList = new ArrayList<>();
        for (int currentPeriod = 1; currentPeriod <= period; currentPeriod++) {
            // 还款计划ID
            Long lendReturnId = period2LendReturnId.get(currentPeriod);

            // 回款计划
            LendItemReturn lendItemReturn = new LendItemReturn();
            lendItemReturn.setLendReturnId(lendReturnId); // 还款记录 ID
            lendItemReturn.setLendItemId(lendItemId); // 投资记录 ID
            lendItemReturn.setInvestUserId(lendItem.getInvestUserId());
            lendItemReturn.setLendId(lendItem.getLendId());
            lendItemReturn.setInvestAmount(lendItem.getInvestAmount());
            lendItemReturn.setLendYearRate(lend.getLendYearRate());
            lendItemReturn.setCurrentPeriod(currentPeriod);
            lendItemReturn.setReturnMethod(lend.getReturnMethod());

            // 填充本金、利息和总金额
            // 最后一期：采用「总 - (非最后一期之和)」的方式进行计算
            if (currentPeriod == period) {
                // 本金
                BigDecimal sumOfPrincipal = lendItemReturnList.stream()
                        .map(LendItemReturn::getPrincipal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                lendItemReturn.setPrincipal(lendItem.getInvestAmount().subtract(sumOfPrincipal));
                // 利息
                BigDecimal sumOfInterest = lendItemReturnList.stream()
                        .map(LendItemReturn::getInterest)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                lendItemReturn.setInterest(lendItem.getExpectAmount().subtract(sumOfInterest));
            } else {
                lendItemReturn.setInterest(period2Interest.get(currentPeriod));
                lendItemReturn.setPrincipal(period2Principal.get(currentPeriod));
            }
            lendItemReturn.setTotal(lendItemReturn.getPrincipal().add(lendItemReturn.getInterest()));
            lendItemReturn.setFee(BigDecimal.ZERO);
            lendItemReturn.setReturnDate(lend.getLendStartDate().plusMonths(currentPeriod));
            lendItemReturn.setOverdue(false);
            lendItemReturn.setStatus(0);

            lendItemReturnList.add(lendItemReturn);
        }

        lendItemReturnService.saveBatch(lendItemReturnList);

        return lendItemReturnList;
    }
}
