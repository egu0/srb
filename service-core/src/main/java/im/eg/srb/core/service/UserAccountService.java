package im.eg.srb.core.service;

import im.eg.srb.core.pojo.entity.UserAccount;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;

/**
 * <p>
 * 用户账户 服务类
 * </p>
 *
 * @author EGU0
 * @since 2024-11-23
 */
public interface UserAccountService extends IService<UserAccount> {

    String commitCharge(BigDecimal chargeAmount, Long userId);
}
