package im.eg.srb.sms.service.impl;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.google.gson.Gson;
import im.eg.common.exception.Assert;
import im.eg.common.exception.BusinessException;
import im.eg.common.result.ResponseEnum;
import im.eg.srb.sms.service.SmsService;
import im.eg.srb.sms.util.SmsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class SmsServiceImpl implements SmsService {

    @Override
    public void send(String mobile, String templateCode, Map<String, Object> params) {
        DefaultProfile profile = DefaultProfile.getProfile(SmsProperties.REGION_ID,
                SmsProperties.KEY_ID, SmsProperties.KEY_SECRET);
        IAcsClient client = new DefaultAcsClient(profile);

        SendSmsRequest request = new SendSmsRequest();
        request.setPhoneNumbers(mobile);
        request.setTemplateCode(templateCode);
        request.setSignName(SmsProperties.SIGN_NAME);
        request.setTemplateParam(new Gson().toJson(params));

        try {
            SendSmsResponse response = client.getAcsResponse(request);
            log.info("發送短信完成，狀態為: {}", new Gson().toJson(response));
            // 發送失敗的情況
            Assert.equals("OK", response.getCode(), ResponseEnum.ALIYUN_SMS_ERROR);
        } catch (ClientException e) {
            log.error("發送短信失敗, errCode={}, errMsg={}", e.getErrCode(), e.getMessage());
            throw new BusinessException(ResponseEnum.ALIYUN_SMS_ERROR, e);
        }
    }
}
