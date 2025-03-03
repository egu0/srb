package im.eg.srb.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import im.eg.srb.core.pojo.entity.LendItem;
import im.eg.srb.core.pojo.vo.InvestVO;

import java.util.Map;

/**
 * <p>
 * 标的出借记录表 服务类
 * </p>
 *
 * @author EGU0
 * @since 2024-11-23
 */
public interface LendItemService extends IService<LendItem> {

    /**
     * 构建用于投资人投标的自动表单提交页（自动发送到汇付宝平台）
     */
    String commitInvest(InvestVO investVO);

    /**
     * 投标回调
     */
    String notify(Map<String, Object> params);

    LendItem getByLendItemNo(String lendItemNo);
}
