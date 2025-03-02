package im.eg.srb.core.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import im.eg.srb.core.enums.LendStatusEnum;
import im.eg.srb.core.mapper.LendMapper;
import im.eg.srb.core.pojo.entity.BorrowInfo;
import im.eg.srb.core.pojo.entity.Lend;
import im.eg.srb.core.pojo.vo.BorrowInfoApprovalVO;
import im.eg.srb.core.service.LendService;
import im.eg.srb.core.util.LendNoUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * <p>
 * 标的准备表 服务实现类
 * </p>
 *
 * @author EGU0
 * @since 2024-11-23
 */
@Service
public class LendServiceImpl extends ServiceImpl<LendMapper, Lend> implements LendService {

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

    private BigDecimal divide100(BigDecimal o) {
        return o.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }
}
