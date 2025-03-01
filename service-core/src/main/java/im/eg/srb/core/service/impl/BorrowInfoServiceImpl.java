package im.eg.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import im.eg.common.exception.Assert;
import im.eg.common.result.ResponseEnum;
import im.eg.srb.core.enums.BorrowAuthEnum;
import im.eg.srb.core.enums.BorrowInfoStatusEnum;
import im.eg.srb.core.enums.UserBindEnum;
import im.eg.srb.core.mapper.BorrowInfoMapper;
import im.eg.srb.core.mapper.IntegralGradeMapper;
import im.eg.srb.core.mapper.UserInfoMapper;
import im.eg.srb.core.pojo.entity.BorrowInfo;
import im.eg.srb.core.pojo.entity.IntegralGrade;
import im.eg.srb.core.pojo.entity.UserInfo;
import im.eg.srb.core.service.BorrowInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * <p>
 * 借款信息表 服务实现类
 * </p>
 *
 * @author EGU0
 * @since 2024-11-23
 */
@Service
public class BorrowInfoServiceImpl extends ServiceImpl<BorrowInfoMapper, BorrowInfo> implements BorrowInfoService {

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private IntegralGradeMapper integralGradeMapper;

    @Override
    public BigDecimal getBorrowAmount(Long userId) {
        UserInfo userInfo = userInfoMapper.selectById(userId);
        Integer integral = userInfo.getIntegral();

        QueryWrapper<IntegralGrade> queryWrapper = new QueryWrapper<>();
        queryWrapper.le("integral_start", integral).ge("integral_end", integral);
        IntegralGrade integralGrade = integralGradeMapper.selectOne(queryWrapper);
        if (integralGrade == null) {
            return new BigDecimal("0");
        } else {
            return integralGrade.getBorrowAmount();
        }
    }

    @Override
    public void saveBorrowInfo(BorrowInfo borrowInfo, Long userId) {
        UserInfo userInfo = userInfoMapper.selectById(userId);

        // 判断：用户绑定状态
        Assert.equals(UserBindEnum.BIND_OK.getStatus(), userInfo.getBindStatus(),
                ResponseEnum.USER_NO_BIND_ERROR);

        // 判断：借款人额度申请状态
        Assert.equals(BorrowAuthEnum.AUTH_OK.getStatus(), userInfo.getBorrowAuthStatus(),
                ResponseEnum.USER_NO_AMOUNT_ERROR);

        // 判断：借款额度是否在正确区间内
        BigDecimal maxBorrowAmount = this.getBorrowAmount(userId);
        Assert.isTrue(maxBorrowAmount.compareTo(borrowInfo.getAmount()) >= 0,
                ResponseEnum.USER_AMOUNT_LESS_ERROR);

        // 借款申请
        borrowInfo.setUserId(userId);
        BigDecimal yearRate = borrowInfo.getBorrowYearRate().divide(new BigDecimal("100"), RoundingMode.HALF_UP);
        borrowInfo.setBorrowYearRate(yearRate);
        borrowInfo.setStatus(BorrowInfoStatusEnum.CHECK_RUN.getStatus()); // 状态：审核中
        baseMapper.insert(borrowInfo);
    }
}
