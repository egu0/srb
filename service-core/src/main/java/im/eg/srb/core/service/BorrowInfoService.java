package im.eg.srb.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import im.eg.srb.core.pojo.entity.BorrowInfo;
import im.eg.srb.core.pojo.vo.BorrowInfoApprovalVO;
import im.eg.srb.core.pojo.vo.BorrowInfoDetailVO;

import java.math.BigDecimal;
import java.util.Map;

/**
 * <p>
 * 借款信息表 服务类
 * </p>
 *
 * @author EGU0
 * @since 2024-11-23
 */
public interface BorrowInfoService extends IService<BorrowInfo> {

    /**
     * 获取用户借款额度
     */
    BigDecimal getBorrowAmount(Long userId);

    /**
     * 借款申请
     */
    void saveBorrowInfo(BorrowInfo borrowInfo, Long userId);

    /**
     * 获取用户的借款状态
     */
    Integer getStatusByUserId(Long userId);

    /**
     * 借款信息分页列表
     */
    IPage<BorrowInfoDetailVO> listPage(Page<BorrowInfo> pageParam);

    /**
     * 获取借款信息详情
     */
    Map<String, Object> getBorrowInfoDetailById(Long borrowInfoId);

    /**
     * 审批借款信息
     */
    void approval(BorrowInfoApprovalVO borrowInfoApprovalVO);
}
