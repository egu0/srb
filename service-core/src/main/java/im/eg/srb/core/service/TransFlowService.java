package im.eg.srb.core.service;

import im.eg.srb.core.pojo.bo.TransFlowBO;
import im.eg.srb.core.pojo.entity.TransFlow;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 交易流水表 服务类
 * </p>
 *
 * @author EGU0
 * @since 2024-11-23
 */
public interface TransFlowService extends IService<TransFlow> {

    void saveTransFlow(TransFlowBO transFlowBO);

    /**
     * 统计流水号（trans_flow.trans_no）是否存在
     */
    Integer countByTransNo(String transNo);
}
