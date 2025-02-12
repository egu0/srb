package im.eg.srb.core.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import im.eg.srb.core.hfb.FormHelper;
import im.eg.srb.core.hfb.HfbConst;
import im.eg.srb.core.hfb.RequestHelper;
import im.eg.srb.core.mapper.UserBindMapper;
import im.eg.srb.core.pojo.entity.UserBind;
import im.eg.srb.core.pojo.vo.UserBindVO;
import im.eg.srb.core.service.UserBindService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 用户绑定表 服务实现类
 * </p>
 *
 * @author EGU0
 * @since 2024-11-23
 */
@Service
public class UserBindServiceImpl extends ServiceImpl<UserBindMapper, UserBind> implements UserBindService {

    @Override
    public String commitBindUser(UserBindVO userBindVO, Long userId) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("agentId", HfbConst.AGENT_ID); // 商戶 ID
        paramMap.put("agentUserId", userId); // 商戶系統中的會員 ID

        paramMap.put("idCard", userBindVO.getIdCard()); // 會員身份證號
        paramMap.put("personalName", userBindVO.getName()); // 會員姓名
        paramMap.put("bankType", userBindVO.getBankType()); // 銀行類型
        paramMap.put("bankNo", userBindVO.getBankNo()); // 銀行卡號
        paramMap.put("mobile", userBindVO.getMobile());// 銀行預留手機號

        paramMap.put("returnUrl", HfbConst.USER_BIND_RETURN_URL); // 對應 srb-site 的 /user 頁面
        paramMap.put("notifyUrl", HfbConst.USER_BIND_NOTIFY_URL); // 對應 srb 的接口地址

        paramMap.put("timestamp", RequestHelper.getTimestamp()); //時間戳
        paramMap.put("sign", RequestHelper.getSign(paramMap)); // 計算簽名

        return FormHelper.buildForm(HfbConst.USER_BIND_URL, paramMap);
    }
}
