package im.eg.srb.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import im.eg.srb.core.pojo.entity.UserAccount;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * <p>
 * 用户账户 Mapper 接口
 * </p>
 *
 * @author EGU0
 * @since 2024-11-23
 */
public interface UserAccountMapper extends BaseMapper<UserAccount> {
    void updateAccount(@Param("bindCode") String bindCode,
                       @Param("amount") BigDecimal amount,
                       @Param("freezeAmount") BigDecimal freezeAmount);
}
