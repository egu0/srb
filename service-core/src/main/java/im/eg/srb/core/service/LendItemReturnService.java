package im.eg.srb.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import im.eg.srb.core.pojo.entity.LendItemReturn;

import java.util.List;

/**
 * <p>
 * 标的出借回款记录表 服务类
 * </p>
 *
 * @author EGU0
 * @since 2024-11-23
 */
public interface LendItemReturnService extends IService<LendItemReturn> {

    /**
     * 根据 lendId 查询标的回款计划列表
     */
    List<LendItemReturn> listByLendId(Long lendId, Long userId);
}
