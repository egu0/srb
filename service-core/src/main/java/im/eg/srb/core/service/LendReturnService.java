package im.eg.srb.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import im.eg.srb.core.pojo.entity.LendReturn;

import java.util.List;
import java.util.Map;

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

    /**
     * 构建用于自动提交表单的页面，用于调用汇付宝还款接口
     */
    String commitReturn(Long lendReturnId, Long userId);

    /**
     * 还款异步回调
     */
    String notify(Map<String, Object> params);
}
