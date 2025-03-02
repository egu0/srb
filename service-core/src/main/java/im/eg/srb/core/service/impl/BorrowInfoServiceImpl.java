package im.eg.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import im.eg.common.exception.Assert;
import im.eg.common.result.ResponseEnum;
import im.eg.srb.core.enums.BorrowAuthEnum;
import im.eg.srb.core.enums.BorrowInfoStatusEnum;
import im.eg.srb.core.enums.UserBindEnum;
import im.eg.srb.core.mapper.BorrowInfoMapper;
import im.eg.srb.core.mapper.BorrowerMapper;
import im.eg.srb.core.mapper.IntegralGradeMapper;
import im.eg.srb.core.mapper.UserInfoMapper;
import im.eg.srb.core.pojo.entity.BorrowInfo;
import im.eg.srb.core.pojo.entity.Borrower;
import im.eg.srb.core.pojo.entity.IntegralGrade;
import im.eg.srb.core.pojo.entity.UserInfo;
import im.eg.srb.core.pojo.vo.BorrowInfoApprovalVO;
import im.eg.srb.core.pojo.vo.BorrowInfoDetailVO;
import im.eg.srb.core.pojo.vo.BorrowerDetailVO;
import im.eg.srb.core.service.BorrowInfoService;
import im.eg.srb.core.service.BorrowerService;
import im.eg.srb.core.service.DictService;
import im.eg.srb.core.service.LendService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private LendService lendService;

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private IntegralGradeMapper integralGradeMapper;

    @Resource
    private DictService dictService;

    @Resource
    private BorrowerMapper borrowerMapper;

    @Resource
    private BorrowerService borrowerService;

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
        BigDecimal yearRate = borrowInfo.getBorrowYearRate().divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        borrowInfo.setBorrowYearRate(yearRate);
        borrowInfo.setStatus(BorrowInfoStatusEnum.CHECK_RUN.getStatus()); // 状态：审核中
        baseMapper.insert(borrowInfo);
    }

    @Override
    public Integer getStatusByUserId(Long userId) {
        QueryWrapper<BorrowInfo> borrowInfoQueryWrapper = new QueryWrapper<>();
        borrowInfoQueryWrapper.select("status").eq("user_id", userId);
        List<Object> result = baseMapper.selectObjs(borrowInfoQueryWrapper);
        if (!result.isEmpty()) {
            return (Integer) result.get(0);
        } else {
            return BorrowInfoStatusEnum.NO_AUTH.getStatus(); // 未认证（未申请）
        }
    }

    @Override
    public IPage<BorrowInfoDetailVO> listPage(Page<BorrowInfo> pageParam) {
        IPage<BorrowInfoDetailVO> page = baseMapper.selectBorrowInfoPageList(pageParam);
        page.getRecords().forEach(vo -> {
            vo.getParam().put("status", BorrowInfoStatusEnum.getMsgByStatus(vo.getStatus()));
            vo.getParam().put("returnMethod",
                    dictService.getDictName("returnMethod", vo.getReturnMethod()));
            vo.getParam().put("moneyUse",
                    dictService.getDictName("moneyUse", vo.getMoneyUse()));
        });
        return page;
    }

    @Override
    public Map<String, Object> getBorrowInfoDetailById(Long borrowInfoId) {
        // 组装 BorrowInfoDetailVO
        BorrowInfoDetailVO borrowInfoDetailVO = new BorrowInfoDetailVO();
        BorrowInfo borrowInfo = baseMapper.selectById(borrowInfoId);
        BeanUtils.copyProperties(borrowInfo, borrowInfoDetailVO);
        borrowInfoDetailVO.getParam().put("status",
                BorrowInfoStatusEnum.getMsgByStatus(borrowInfoDetailVO.getStatus()));
        borrowInfoDetailVO.getParam().put("returnMethod",
                dictService.getDictName("returnMethod", borrowInfoDetailVO.getReturnMethod()));
        borrowInfoDetailVO.getParam().put("moneyUse",
                dictService.getDictName("moneyUse", borrowInfoDetailVO.getMoneyUse()));

        // 组装 BorrowerDetailVO
        Long userId = borrowInfo.getUserId();
        QueryWrapper<Borrower> borrowerQueryWrapper = new QueryWrapper<>();
        borrowerQueryWrapper.eq("user_id", userId);
        Borrower borrower = borrowerMapper.selectOne(borrowerQueryWrapper);
        BorrowerDetailVO borrowerDetailVO = borrowerService.getBorrowerDetailVOByBorrowerId(borrower.getId());

        // 组装数据
        Map<String, Object> data = new HashMap<>();
        data.put("borrowInfoDetail", borrowInfoDetailVO); // 借款申请信息
        data.put("borrowerDetail", borrowerDetailVO); // 借款人信息
        return data;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void approval(BorrowInfoApprovalVO borrowInfoApprovalVO) {
        // 修改「borrow_info」表对应记录的状态
        Long borrowInfoId = borrowInfoApprovalVO.getId();
        BorrowInfo updateBorrowInfo = new BorrowInfo();
        updateBorrowInfo.setId(borrowInfoId);
        updateBorrowInfo.setStatus(borrowInfoApprovalVO.getStatus());
        baseMapper.updateById(updateBorrowInfo);

        // 创建标的
        if (BorrowInfoStatusEnum.CHECK_OK.getStatus().equals(borrowInfoApprovalVO.getStatus())) {
            BorrowInfo borrowInfo = baseMapper.selectById(borrowInfoId);
            lendService.createLend(borrowInfoApprovalVO, borrowInfo);
        }
    }
}
