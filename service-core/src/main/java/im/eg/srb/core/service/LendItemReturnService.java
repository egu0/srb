package im.eg.srb.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import im.eg.srb.core.pojo.entity.LendItemReturn;

import java.util.List;
import java.util.Map;

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

    /**
     * 根据《汇付宝商户账户技术文档 - 3.14.3.还款扣款请求》组装还款记录对应的回款明细
     */
    List<Map<String, Object>> obtainReturnDetail(Long lendReturnId);

    /**
     * 根据还款计划 id 获取对应的回款计划列表
     */
    List<LendItemReturn> listByLendReturnId(Long lendReturnId);
}
