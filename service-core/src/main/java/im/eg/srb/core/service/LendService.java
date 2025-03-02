package im.eg.srb.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import im.eg.srb.core.pojo.entity.BorrowInfo;
import im.eg.srb.core.pojo.entity.Lend;
import im.eg.srb.core.pojo.vo.BorrowInfoApprovalVO;
import im.eg.srb.core.pojo.vo.LendVO;

/**
 * <p>
 * 标的准备表 服务类
 * </p>
 *
 * @author EGU0
 * @since 2024-11-23
 */
public interface LendService extends IService<Lend> {

    /**
     * 根据「借款信息」创建标的
     */
    void createLend(BorrowInfoApprovalVO borrowInfoApprovalVO, BorrowInfo borrowInfo);

    /**
     * 查询标的分页列表
     */
    IPage<LendVO> listPage(Page<Lend> pageParam);
}
