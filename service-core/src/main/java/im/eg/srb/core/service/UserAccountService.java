package im.eg.srb.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import im.eg.srb.core.pojo.entity.UserAccount;

import java.math.BigDecimal;
import java.util.Map;

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

    /**
     * 用户支付回调
     */
    String notify(Map<String, Object> params);

    /**
     * 获取账户余额
     */
    BigDecimal getAccAmt(Long userId);

    /**
     * 构造自动提交表单，用于向汇付宝系统发送提现请求
     */
    String commitWithdraw(BigDecimal fetchAmt, Long userId);
}
