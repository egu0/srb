package im.eg.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import im.eg.srb.core.mapper.TransFlowMapper;
import im.eg.srb.core.mapper.UserInfoMapper;
import im.eg.srb.core.pojo.bo.TransFlowBO;
import im.eg.srb.core.pojo.entity.TransFlow;
import im.eg.srb.core.pojo.entity.UserInfo;
import im.eg.srb.core.service.TransFlowService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 交易流水表 服务实现类
 * </p>
 *
 * @author EGU0
 * @since 2024-11-23
 */
@Service
public class TransFlowServiceImpl extends ServiceImpl<TransFlowMapper, TransFlow> implements TransFlowService {

    @Resource
    private UserInfoMapper userInfoMapper;

    @Override
    public void saveTransFlow(TransFlowBO transFlowBO) {
        String bindCode = transFlowBO.getBindCode();
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("bind_code", bindCode);
        UserInfo userInfo = userInfoMapper.selectOne(queryWrapper);

        TransFlow transFlow = new TransFlow();
        transFlow.setTransNo(transFlowBO.getAgentBillNo());
        transFlow.setTransAmount(transFlowBO.getAmount());
        transFlow.setTransTypeName(transFlowBO.getTransTypeEnum().getTransTypeName());
        transFlow.setTransType(transFlowBO.getTransTypeEnum().getTransType());
        transFlow.setMemo(transFlowBO.getMemo());
        transFlow.setUserId(userInfo.getId());
        transFlow.setUserName(userInfo.getName());
        baseMapper.insert(transFlow);
    }

    @Override
    public Integer countByTransNo(String transNo) {
        QueryWrapper<TransFlow> transFlowQueryWrapper = new QueryWrapper<>();
        transFlowQueryWrapper.eq("trans_no", transNo);
        return baseMapper.selectCount(transFlowQueryWrapper);
    }

    @Override
    public List<TransFlow> listByUserId(Long userId) {
        QueryWrapper<TransFlow> q = new QueryWrapper<>();
        q.eq("user_id", userId).orderByDesc("id");
        return baseMapper.selectList(q);
    }
}
