package im.eg.srb.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import im.eg.srb.core.pojo.entity.Borrower;
import im.eg.srb.core.pojo.vo.BorrowerDetailVO;
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

    Integer getBorrowerStatus(Long userId);

    /**
     * 分页查询借款人额度审核列表
     */
    IPage<Borrower> listPage(Page<Borrower> pageParam, String keyword);

    /**
     * 获取借款人详细信息
     */
    BorrowerDetailVO getBorrowerDetailVOByUserId(Long userId);
}
