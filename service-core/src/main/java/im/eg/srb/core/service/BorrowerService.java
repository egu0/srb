package im.eg.srb.core.service;

import im.eg.srb.core.pojo.entity.Borrower;
import com.baomidou.mybatisplus.extension.service.IService;
import im.eg.srb.core.pojo.vo.BorrowerVO;

/**
 * <p>
 * 借款人 服务类
 * </p>
 *
 * @author EGU0
 * @since 2024-11-23
 */
public interface BorrowerService extends IService<Borrower> {

    void saveBorrowerVOByUserId(BorrowerVO borrowerVO, Long userId);
}
