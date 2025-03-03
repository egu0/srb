package im.eg.srb.core.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import im.eg.srb.core.enums.TransTypeEnum;
import im.eg.srb.core.hfb.FormHelper;
import im.eg.srb.core.hfb.HfbConst;
import im.eg.srb.core.hfb.RequestHelper;
import im.eg.srb.core.mapper.UserAccountMapper;
import im.eg.srb.core.mapper.UserInfoMapper;
import im.eg.srb.core.pojo.bo.TransFlowBO;
import im.eg.srb.core.pojo.entity.UserAccount;
import im.eg.srb.core.pojo.entity.UserInfo;
import im.eg.srb.core.service.TransFlowService;
import im.eg.srb.core.service.UserAccountService;
import im.eg.srb.core.util.LendNoUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 用户账户 服务实现类
 * </p>
 *
 * @author EGU0
 * @since 2024-11-23
 */
@Service
public class UserAccountServiceImpl extends ServiceImpl<UserAccountMapper, UserAccount> implements UserAccountService {

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private TransFlowService transFlowService;

    @Override
    public String commitCharge(BigDecimal chargeAmount, Long userId) {
        UserInfo userInfo = userInfoMapper.selectById(userId);
        String bindCode = userInfo.getBindCode();
        // 组装参数
        Map<String, Object> params = new HashMap<>();
        params.put("agentId", HfbConst.AGENT_ID);
        params.put("agentBillNo", LendNoUtils.getChargeNo());
        params.put("bindCode", bindCode);
        params.put("chargeAmt", chargeAmount);
        params.put("feeAmt", new BigDecimal("0"));
        params.put("notifyUrl", HfbConst.RECHARGE_NOTIFY_URL);
        params.put("returnUrl", HfbConst.RECHARGE_RETURN_URL);
        params.put("timestamp", RequestHelper.getTimestamp());
        params.put("sign", RequestHelper.getSign(params));

        // 组装表单
        return FormHelper.buildForm(HfbConst.RECHARGE_URL, params);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String notify(Map<String, Object> params) {
        // 幂等性判断。查询流水表「trans_flow」判断是否已经处理
        String agentBillNo = (String) params.get("agentBillNo");
        Integer count = transFlowService.countByTransNo(agentBillNo);
        if (count == 1) {
            return "success";
        }

        // 账户处理。更新 user_account 表中的金额字段
        String bindCode = (String) params.get("bindCode");
        String chargeAmount = (String) params.get("chargeAmt");
        baseMapper.updateAccount(bindCode, new BigDecimal(chargeAmount), new BigDecimal("0"));

        // 新增账户流水
        TransFlowBO transFlowBO = new TransFlowBO(agentBillNo, bindCode,
                new BigDecimal(chargeAmount), TransTypeEnum.RECHARGE, "充值");
        transFlowService.saveTransFlow(transFlowBO);

        return "success";
    }

}
