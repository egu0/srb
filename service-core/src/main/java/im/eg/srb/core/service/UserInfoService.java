package im.eg.srb.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import im.eg.srb.core.pojo.entity.UserInfo;
import im.eg.srb.core.pojo.query.UserInfoQuery;
import im.eg.srb.core.pojo.vo.LoginVO;
import im.eg.srb.core.pojo.vo.RegisterVO;
import im.eg.srb.core.pojo.vo.UserIndexVO;
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

    /**
     * 「管理系統」分頁查詢會員列表
     */
    IPage<UserInfo> listPage(Page<UserInfo> userInfoPage, UserInfoQuery userInfoQuery);

    /**
     * 用戶鎖定與解鎖
     */
    void lock(Long id, Integer status);

    /**
     * 校驗手機號註冊狀態
     *
     * @param mobile 手機號
     * @return true 表示已註冊；false 表示未註冊
     */
    boolean checkMobileRegisterStatus(String mobile);

    /**
     * 获取用户空间页面所需用户信息
     */
    UserIndexVO getIndexUserVo(Long userId);
}
