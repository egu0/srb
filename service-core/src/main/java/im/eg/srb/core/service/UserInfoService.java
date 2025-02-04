package im.eg.srb.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import im.eg.srb.core.pojo.entity.UserInfo;
import im.eg.srb.core.pojo.vo.LoginVO;
import im.eg.srb.core.pojo.vo.RegisterVO;
import im.eg.srb.core.pojo.vo.UserInfoVO;

/**
 * <p>
 * 用户基本信息 服务类
 * </p>
 *
 * @author EGU0
 * @since 2024-11-23
 */
public interface UserInfoService extends IService<UserInfo> {

    /**
     * 用戶註冊
     */
    void register(RegisterVO registerVO);

    /**
     * 用戶登錄
     */
    UserInfoVO login(LoginVO loginVO, String ip);
}
