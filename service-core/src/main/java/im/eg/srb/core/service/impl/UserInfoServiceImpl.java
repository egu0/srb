package im.eg.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import im.eg.common.exception.Assert;
import im.eg.common.result.ResponseEnum;
import im.eg.common.util.MD5;
import im.eg.srb.base.util.JwtUtils;
import im.eg.srb.core.mapper.UserAccountMapper;
import im.eg.srb.core.mapper.UserInfoMapper;
import im.eg.srb.core.mapper.UserLoginRecordMapper;
import im.eg.srb.core.pojo.entity.UserAccount;
import im.eg.srb.core.pojo.entity.UserInfo;
import im.eg.srb.core.pojo.entity.UserLoginRecord;
import im.eg.srb.core.pojo.query.UserInfoQuery;
import im.eg.srb.core.pojo.vo.LoginVO;
import im.eg.srb.core.pojo.vo.RegisterVO;
import im.eg.srb.core.pojo.vo.UserInfoVO;
import im.eg.srb.core.service.UserInfoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * <p>
 * 用户基本信息 服务实现类
 * </p>
 *
 * @author EGU0
 * @since 2024-11-23
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {
    @Resource
    private UserAccountMapper userAccountMapper;
    @Resource
    private UserLoginRecordMapper userLoginRecordMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void register(RegisterVO registerVO) {
        // 判斷用戶是否已經註冊
        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper.eq("mobile", registerVO.getMobile());
        Integer count = baseMapper.selectCount(userInfoQueryWrapper);
        Assert.isTrue(count == 0, ResponseEnum.MOBILE_EXIST_ERROR);

        // 插入 user_info 插入
        UserInfo userInfo = new UserInfo();
        userInfo.setUserType(registerVO.getUserType());
        userInfo.setNickName(registerVO.getMobile());// 暱稱
        userInfo.setName(registerVO.getMobile());
        userInfo.setMobile(registerVO.getMobile());
        userInfo.setStatus(UserInfo.STATUS_NORMAL);
        userInfo.setPassword(MD5.encrypt(registerVO.getPassword())); // md5 密碼
        userInfo.setHeadImg(UserInfo.DEFAULT_AVATAR);
        baseMapper.insert(userInfo);

        // 插入 user_account
        UserAccount userAccount = new UserAccount();
        userAccount.setUserId(userInfo.getId());
        userAccountMapper.insert(userAccount);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserInfoVO login(LoginVO loginVO, String ip) {
        String mobile = loginVO.getMobile();
        String password = loginVO.getPassword();
        Integer userType = loginVO.getUserType();
        // 判斷用戶是否存在
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_type", userType);
        queryWrapper.eq("mobile", mobile);
        UserInfo userInfo = baseMapper.selectOne(queryWrapper);
        Assert.notNull(userInfo, ResponseEnum.LOGIN_MOBILE_ERROR);

        // 判斷密碼是否正確
        String realPassword = userInfo.getPassword();
        Assert.equals(realPassword, MD5.encrypt(password), ResponseEnum.LOGIN_PASSWORD_ERROR);

        // 判斷用戶是否被禁用
        Assert.equals(UserInfo.STATUS_NORMAL, userInfo.getStatus(), ResponseEnum.LOGIN_LOCKED_ERROR);

        // 紀錄登錄日誌
        UserLoginRecord userLoginRecord = new UserLoginRecord();
        userLoginRecord.setUserId(userInfo.getId());
        userLoginRecord.setIp(ip);
        userLoginRecordMapper.insert(userLoginRecord);

        // 生成 JWT
        String token = JwtUtils.createToken(userInfo.getId(), userInfo.getName());

        // 組裝 UserInfoVO
        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setToken(token);
        userInfoVO.setName(userInfo.getName());
        userInfoVO.setMobile(userInfo.getMobile());
        userInfoVO.setNickName(userInfo.getNickName());
        userInfoVO.setUserType(userInfo.getUserType());
        userInfoVO.setHeadImg(userInfo.getHeadImg());
        return userInfoVO;
    }

    @Override
    public IPage<UserInfo> listPage(Page<UserInfo> userInfoPage,
                                    UserInfoQuery userInfoQuery) {

        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();

        if (userInfoQuery != null) {
            String mobile = userInfoQuery.getMobile();
            Integer userType = userInfoQuery.getUserType();
            Integer status = userInfoQuery.getStatus();

            queryWrapper.eq(StringUtils.isNotBlank(mobile), "mobile", mobile);
            queryWrapper.eq(userType != null, "user_type", userType);
            queryWrapper.eq(status != null, "status", status);
        }

        return baseMapper.selectPage(userInfoPage, queryWrapper);
    }

    @Override
    public void lock(Long id, Integer status) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(id);
        userInfo.setStatus(status);
        baseMapper.updateById(userInfo);
    }

    @Override
    public boolean checkMobileRegisterStatus(String mobile) {
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("mobile", mobile);
        Integer count = baseMapper.selectCount(queryWrapper);
        return count != null && count.equals(1);
    }
}
