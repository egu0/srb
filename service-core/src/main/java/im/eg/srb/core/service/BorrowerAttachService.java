package im.eg.srb.core.service;

import im.eg.srb.core.pojo.entity.BorrowerAttach;
import com.baomidou.mybatisplus.extension.service.IService;
import im.eg.srb.core.pojo.vo.BorrowerAttachVO;

import java.util.List;

/**
 * <p>
 * 借款人上传资源表 服务类
 * </p>
 *
 * @author EGU0
 * @since 2024-11-23
 */
public interface BorrowerAttachService extends IService<BorrowerAttach> {
    /**
     * 获取「借款人 id」获取他所有的附件信息
     */
    List<BorrowerAttachVO> selectBorrowerAttachVOList(Long borrowerId);
}
