package im.eg.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import im.eg.common.exception.Assert;
import im.eg.common.result.ResponseEnum;
import im.eg.srb.core.enums.UserBindEnum;
import im.eg.srb.core.hfb.FormHelper;
import im.eg.srb.core.hfb.HfbConst;
import im.eg.srb.core.hfb.RequestHelper;
import im.eg.srb.core.mapper.UserBindMapper;
import im.eg.srb.core.mapper.UserInfoMapper;
import im.eg.srb.core.pojo.entity.UserBind;
import im.eg.srb.core.pojo.entity.UserInfo;
import im.eg.srb.core.pojo.vo.UserBindVO;
import im.eg.srb.core.service.UserBindService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 用户绑定表 服务实现类
 * </p>
 *
 * @author EGU0
 * @since 2024-11-23
 */
@Service
public class UserBindServiceImpl extends ServiceImpl<UserBindMapper, UserBind> implements UserBindService {

    @Resource
    private UserInfoMapper userInfoMapper;

    @Override
    public String commitBindUser(UserBindVO userBindVO, Long userId) {

        // 如果 idCard 已經被綁定，且 userId 不一致，則不被允許
        QueryWrapper<UserBind> userBindQueryWrapper = new QueryWrapper<>();
        userBindQueryWrapper.eq("id_card", userBindVO.getIdCard())
                .ne("user_id", userId);
        UserBind existedUserBind = baseMapper.selectOne(userBindQueryWrapper);
        Assert.isNull(existedUserBind, ResponseEnum.USER_BIND_ID_EXIST_ERROR);

        // 創建或修改用戶綁定記錄
        userBindQueryWrapper = new QueryWrapper<>();
        userBindQueryWrapper.eq("user_id", userId);
        existedUserBind = baseMapper.selectOne(userBindQueryWrapper);
        if (existedUserBind != null) {
            BeanUtils.copyProperties(userBindVO, existedUserBind);
            baseMapper.updateById(existedUserBind);
        } else {
            UserBind userBind = new UserBind();
            BeanUtils.copyProperties(userBindVO, userBind);
            userBind.setUserId(userId);
            userBind.setStatus(UserBindEnum.NO_BIND.getStatus());
            baseMapper.insert(userBind);
        }

        // 組裝參數集合
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("agentId", HfbConst.AGENT_ID); // 商戶 ID
        paramMap.put("agentUserId", userId); // 商戶系統中的會員 ID

        paramMap.put("idCard", userBindVO.getIdCard()); // 會員身份證號
        paramMap.put("personalName", userBindVO.getName()); // 會員姓名
        paramMap.put("bankType", userBindVO.getBankType()); // 銀行類型
        paramMap.put("bankNo", userBindVO.getBankNo()); // 銀行卡號
        paramMap.put("mobile", userBindVO.getMobile());// 銀行預留手機號

        paramMap.put("returnUrl", HfbConst.USER_BIND_RETURN_URL); // 對應 srb-site 的 /user 頁面
        paramMap.put("notifyUrl", HfbConst.USER_BIND_NOTIFY_URL); // 對應 srb 的接口地址

        paramMap.put("timestamp", RequestHelper.getTimestamp()); //時間戳
        paramMap.put("sign", RequestHelper.getSign(paramMap)); // 計算簽名

        return FormHelper.buildForm(HfbConst.USER_BIND_URL, paramMap);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void notify(Map<String, Object> params) {
        String bindCode = (String) params.get("bindCode");
        String userId = (String) params.get("agentUserId");

        // 更新 user_bind 表
        QueryWrapper<UserBind> userBindQueryWrapper = new QueryWrapper<>();
        userBindQueryWrapper.eq("user_id", userId);
        UserBind userBind = baseMapper.selectOne(userBindQueryWrapper);
        userBind.setBindCode(bindCode);
        userBind.setStatus(UserBindEnum.BIND_OK.getStatus());
        baseMapper.updateById(userBind);

        // 更新 user_info 表
        UserInfo userInfo = userInfoMapper.selectById(userId);
        userInfo.setBindCode(bindCode);
        userInfo.setName(userBind.getName());
        userInfo.setIdCard(userBind.getIdCard());
        userInfo.setBindStatus(UserBindEnum.BIND_OK.getStatus());
        userInfoMapper.updateById(userInfo);
    }
}
