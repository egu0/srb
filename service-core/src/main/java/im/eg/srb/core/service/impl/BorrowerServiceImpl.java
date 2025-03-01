package im.eg.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import im.eg.common.exception.Assert;
import im.eg.common.result.ResponseEnum;
import im.eg.srb.core.enums.BorrowerStatusEnum;
import im.eg.srb.core.enums.IntegralEnum;
import im.eg.srb.core.mapper.BorrowerAttachMapper;
import im.eg.srb.core.mapper.BorrowerMapper;
import im.eg.srb.core.mapper.UserInfoMapper;
import im.eg.srb.core.mapper.UserIntegralMapper;
import im.eg.srb.core.pojo.entity.Borrower;
import im.eg.srb.core.pojo.entity.BorrowerAttach;
import im.eg.srb.core.pojo.entity.UserInfo;
import im.eg.srb.core.pojo.entity.UserIntegral;
import im.eg.srb.core.pojo.vo.BorrowerApprovalVO;
import im.eg.srb.core.pojo.vo.BorrowerDetailVO;
import im.eg.srb.core.pojo.vo.BorrowerVO;
import im.eg.srb.core.service.BorrowerAttachService;
import im.eg.srb.core.service.BorrowerService;
import im.eg.srb.core.service.DictService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 借款人 服务实现类
 * </p>
 *
 * @author EGU0
 * @since 2024-11-23
 */
@Service
public class BorrowerServiceImpl extends ServiceImpl<BorrowerMapper, Borrower> implements BorrowerService {

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private BorrowerAttachMapper borrowerAttachMapper;

    @Resource
    private DictService dictService;

    @Resource
    private BorrowerAttachService borrowerAttachService;

    @Resource
    private UserIntegralMapper userIntegralMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveBorrowerVOByUserId(BorrowerVO borrowerVO, Long userId) {
        // 获取用户基本信息
        UserInfo userInfo = userInfoMapper.selectById(userId);
        if (userInfo == null) {
            log.error("保存借款人信息：未查询到 userId=" + userId + " 的用户");
            return;
        }

        // 保存借款人信息
        Borrower borrower = new Borrower();
        BeanUtils.copyProperties(borrowerVO, borrower);
        borrower.setUserId(userId);
        borrower.setName(userInfo.getName());
        borrower.setIdCard(userInfo.getIdCard());
        borrower.setMobile(userInfo.getMobile());
        borrower.setStatus(BorrowerStatusEnum.AUTH_RUN.getStatus()); // 认证中
        baseMapper.insert(borrower);

        // 保存附件
        List<BorrowerAttach> borrowerAttachList = borrowerVO.getBorrowerAttachList();
        borrowerAttachList.forEach(borrowerAttach -> {
            borrowerAttach.setBorrowerId(borrower.getId());
            borrowerAttachMapper.insert(borrowerAttach);
        });

        // 更新 user_info 中的【借款人认证状态】
        userInfo.setBorrowAuthStatus(BorrowerStatusEnum.AUTH_RUN.getStatus());
        userInfoMapper.updateById(userInfo);
    }

    @Override
    public Integer getBorrowerStatus(Long userId) {
        QueryWrapper<Borrower> borrowerQueryWrapper = new QueryWrapper<>();
        borrowerQueryWrapper
                .select("status")
                .eq("user_id", userId);
        List<Object> objects = baseMapper.selectObjs(borrowerQueryWrapper);
        if (objects == null || objects.isEmpty()) {
            return BorrowerStatusEnum.NO_AUTH.getStatus();
        }
        return (Integer) objects.get(0);
    }

    @Override
    public IPage<Borrower> listPage(Page<Borrower> pageParam, String keyword) {
        QueryWrapper<Borrower> queryWrapper = new QueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            queryWrapper.like("name", keyword)
                    .or().like("mobile", keyword)
                    .or().like("id_card", keyword)
                    .orderByDesc("id");
        }
        return baseMapper.selectPage(pageParam, queryWrapper);
    }

    @Override
    public BorrowerDetailVO getBorrowerDetailVOByBorrowerId(Long borrowerId) {

        BorrowerDetailVO borrowerDetailVO = new BorrowerDetailVO();

        // 填充基本信息
        Borrower borrower = baseMapper.selectById(borrowerId);
        Assert.notNull(borrower, ResponseEnum.BORROWER_NOT_EXIST);
        BeanUtils.copyProperties(borrower, borrowerDetailVO);

        // 填充特殊信息
        borrowerDetailVO.setMarry(borrower.getMarry() ? "已婚" : "未婚");
        borrowerDetailVO.setSex(borrower.getSex() == 1 ? "男" : "女");

        // 下拉列表
        borrowerDetailVO.setEducation(dictService.getDictName("education", borrower.getEducation()));
        borrowerDetailVO.setIncome(dictService.getDictName("income", borrower.getIncome()));
        borrowerDetailVO.setIndustry(dictService.getDictName("industry", borrower.getIndustry()));
        borrowerDetailVO.setReturnSource(dictService.getDictName("returnSource", borrower.getReturnSource()));
        borrowerDetailVO.setContactsRelation(dictService.getDictName("relation", borrower.getContactsRelation()));

        // 状态
        borrowerDetailVO.setStatus(BorrowerStatusEnum.getMsgByStatus(borrower.getStatus()));

        // 附件列表
        borrowerDetailVO.setBorrowerAttachVOList(borrowerAttachService.selectBorrowerAttachVOList(borrower.getId()));

        return borrowerDetailVO;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void approval(BorrowerApprovalVO borrowerApprovalVO) {
        // 获取「借款额度申请」数据
        Long borrowerId = borrowerApprovalVO.getBorrowerId();
        Borrower borrower = baseMapper.selectById(borrowerId);
        if (borrower == null) {
            return;
        }

        // 获取「user-info」表中的用户积分
        Long userId = borrower.getUserId();
        UserInfo userInfo = userInfoMapper.selectById(userId);
        Integer currentIntegral = userInfo.getIntegral();
        if (currentIntegral == null || currentIntegral < 0) {
            currentIntegral = 0;
        }
        currentIntegral += borrowerApprovalVO.getInfoIntegral();

        // 向「user-integral 表」中插入「基本信息」积分数据
        UserIntegral userIntegral = buildUserIntegralEntity(userId, borrowerApprovalVO.getInfoIntegral(),
                "借款人基本信息");
        userIntegralMapper.insert(userIntegral);

        // 根据用户基本信息添加对应积分项
        if (borrowerApprovalVO.getIsIdCardOk()) {
            userIntegral = buildUserIntegralEntity(userId, IntegralEnum.BORROWER_ID_CARD.getIntegral(),
                    IntegralEnum.BORROWER_ID_CARD.getMsg());
            userIntegralMapper.insert(userIntegral);
            currentIntegral += IntegralEnum.BORROWER_ID_CARD.getIntegral();
        }

        // 根据房产信息添加对应积分项
        if (borrowerApprovalVO.getIsHouseOk()) {
            userIntegral = buildUserIntegralEntity(userId, IntegralEnum.BORROWER_HOUSE.getIntegral(),
                    IntegralEnum.BORROWER_HOUSE.getMsg());
            userIntegralMapper.insert(userIntegral);
            currentIntegral += IntegralEnum.BORROWER_HOUSE.getIntegral();
        }

        // 根据车辆信息添加对应积分项
        if (borrowerApprovalVO.getIsCarOk()) {
            userIntegral = buildUserIntegralEntity(userId, IntegralEnum.BORROWER_CAR.getIntegral(),
                    IntegralEnum.BORROWER_CAR.getMsg());
            userIntegralMapper.insert(userIntegral);
            currentIntegral += IntegralEnum.BORROWER_CAR.getIntegral();
        }

        // 更新「borrower 表」中记录的审核状态
        Borrower updateStatusBorrower = new Borrower().setId(borrowerId)
                .setStatus(borrowerApprovalVO.getStatus());
        baseMapper.updateById(updateStatusBorrower);

        // 更新「user-info 表」中用户的 用户积分 和 借款审核状态
        UserInfo updateUserInfo = new UserInfo().setId(userId)
                .setIntegral(currentIntegral)
                .setBorrowAuthStatus(borrowerApprovalVO.getStatus());
        userInfoMapper.updateById(updateUserInfo);
    }

    private static UserIntegral buildUserIntegralEntity(Long userId, Integer integral, String remark) {
        UserIntegral userIntegral = new UserIntegral();
        userIntegral.setUserId(userId);
        userIntegral.setIntegral(integral);
        userIntegral.setContent(remark);
        return userIntegral;
    }
}
