package im.eg.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import im.eg.srb.core.mapper.LendItemMapper;
import im.eg.srb.core.mapper.LendItemReturnMapper;
import im.eg.srb.core.mapper.LendMapper;
import im.eg.srb.core.mapper.LendReturnMapper;
import im.eg.srb.core.pojo.entity.Lend;
import im.eg.srb.core.pojo.entity.LendItem;
import im.eg.srb.core.pojo.entity.LendItemReturn;
import im.eg.srb.core.pojo.entity.LendReturn;
import im.eg.srb.core.service.LendItemReturnService;
import im.eg.srb.core.service.UserBindService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的出借回款记录表 服务实现类
 * </p>
 *
 * @author EGU0
 * @since 2024-11-23
 */
@Service
public class LendItemReturnServiceImpl extends ServiceImpl<LendItemReturnMapper, LendItemReturn> implements LendItemReturnService {

    @Resource
    private LendReturnMapper lendReturnMapper;

    @Resource
    private LendMapper lendMapper;

    @Resource
    private LendItemMapper lendItemMapper;

    @Resource
    private UserBindService userBindService;

    @Override
    public List<LendItemReturn> listByLendId(Long lendId, Long userId) {
        QueryWrapper<LendItemReturn> q = new QueryWrapper<>();
        q.eq("lend_id", lendId)
                .eq("invest_user_id", userId)
                .orderByAsc("current_period");
        return baseMapper.selectList(q);
    }

    @Override
    public List<Map<String, Object>> obtainReturnDetail(Long lendReturnId) {

        LendReturn lendReturn = lendReturnMapper.selectById(lendReturnId);
        Lend lend = lendMapper.selectById(lendReturn.getLendId());

        List<LendItemReturn> lendItemReturnList = this.listByLendReturnId(lendReturnId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (LendItemReturn lendItemReturn : lendItemReturnList) {
            LendItem lendItem = lendItemMapper.selectById(lendItemReturn.getLendItemId());

            Map<String, Object> map = new HashMap<>();
            map.put("agentProjectCode", lend.getLendNo()); // 标的编号
            map.put("voteBillNo", lendItem.getLendItemNo()); // 投资编号
            String toBindCode = userBindService.getBindCodeByUserId(lendItemReturn.getInvestUserId());
            map.put("toBindCode", toBindCode); // 投资人绑定协议号
            map.put("transitAmt", lendItemReturn.getTotal());
            map.put("baseAmt", lendItemReturn.getPrincipal());
            map.put("benifitAmt", lendItemReturn.getInterest());
            map.put("feeAmt", BigDecimal.ZERO);
            result.add(map);
        }
        return result;
    }

    @Override
    public List<LendItemReturn> listByLendReturnId(Long lendReturnId) {
        QueryWrapper<LendItemReturn> q = new QueryWrapper<>();
        q.eq("lend_return_id", lendReturnId).eq("status", 0);
        return baseMapper.selectList(q);
    }
}
