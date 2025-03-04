package im.eg.srb.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import im.eg.srb.core.pojo.entity.LendReturn;

import java.util.List;

/**
 * <p>
 * 还款记录表 服务类
 * </p>
 *
 * @author EGU0
 * @since 2024-11-23
 */
public interface LendReturnService extends IService<LendReturn> {

    /**
     * 根据 lendId 查询标的的还款计划
     */
    List<LendReturn> listById(Long lendId);
}
