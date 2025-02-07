package im.eg.srb.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import im.eg.srb.core.pojo.entity.UserLoginRecord;

import java.util.List;

/**
 * <p>
 * 用户登录记录表 服务类
 * </p>
 *
 * @author EGU0
 * @since 2024-11-23
 */
public interface UserLoginRecordService extends IService<UserLoginRecord> {

    /**
     * 最近 50 條日誌
     */
    List<UserLoginRecord> listTop50(Long userId);
}
