package im.eg.srb.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import im.eg.srb.core.pojo.entity.BorrowInfo;
import im.eg.srb.core.pojo.vo.BorrowInfoDetailVO;

/**
 * <p>
 * 借款信息表 Mapper 接口
 * </p>
 *
 * @author EGU0
 * @since 2024-11-23
 */
public interface BorrowInfoMapper extends BaseMapper<BorrowInfo> {

    /**
     * 分页查询借款信息（BorrowInfoDetailVO 是对 BorrowInfo 的拓展）
     */
    IPage<BorrowInfoDetailVO> selectBorrowInfoPageList(IPage<BorrowInfo> page);

}
