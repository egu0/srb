package im.eg.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import im.eg.srb.core.mapper.BorrowerAttachMapper;
import im.eg.srb.core.pojo.entity.BorrowerAttach;
import im.eg.srb.core.pojo.vo.BorrowerAttachVO;
import im.eg.srb.core.service.BorrowerAttachService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 借款人上传资源表 服务实现类
 * </p>
 *
 * @author EGU0
 * @since 2024-11-23
 */
@Service
public class BorrowerAttachServiceImpl extends ServiceImpl<BorrowerAttachMapper, BorrowerAttach> implements BorrowerAttachService {

    @Override
    public List<BorrowerAttachVO> selectBorrowerAttachVOList(Long borrowerId) {
        QueryWrapper<BorrowerAttach> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("borrower_id", borrowerId);
        List<BorrowerAttach> borrowerAttaches = baseMapper.selectList(queryWrapper);
        return borrowerAttaches.stream()
                .map(attach -> new BorrowerAttachVO(attach.getImageType(), attach.getImageUrl()))
                .collect(Collectors.toList());
    }
}
