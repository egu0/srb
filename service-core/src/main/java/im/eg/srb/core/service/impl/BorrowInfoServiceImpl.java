package im.eg.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
}
