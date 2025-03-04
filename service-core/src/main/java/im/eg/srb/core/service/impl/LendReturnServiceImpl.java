package im.eg.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import im.eg.srb.core.mapper.LendReturnMapper;
import im.eg.srb.core.pojo.entity.LendReturn;
import im.eg.srb.core.service.LendReturnService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 还款记录表 服务实现类
 * </p>
 *
 * @author EGU0
 * @since 2024-11-23
 */
@Service
public class LendReturnServiceImpl extends ServiceImpl<LendReturnMapper, LendReturn> implements LendReturnService {

    @Override
    public List<LendReturn> listById(Long lendId) {
        QueryWrapper<LendReturn> q = new QueryWrapper<>();
        q.eq("lend_id", lendId);
        return baseMapper.selectList(q);
    }
}
