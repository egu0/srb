package im.eg.srb.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import im.eg.srb.core.pojo.entity.UserBind;
import im.eg.srb.core.pojo.vo.UserBindVO;

/**
 * <p>
 * 用户绑定表 服务类
 * </p>
 *
 * @author EGU0
 * @since 2024-11-23
 */
public interface UserBindService extends IService<UserBind> {

    /**
     * 組裝表單字符串
     */
    String commitBindUser(UserBindVO userBindVO, Long userId);
}
