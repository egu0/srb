package im.eg.srb.sms.controller.api;

import im.eg.common.exception.Assert;
import im.eg.common.result.R;
import im.eg.common.result.ResponseEnum;
import im.eg.common.util.RandomUtils;
import im.eg.common.util.RegexValidateUtils;
import im.eg.srb.sms.openfeign.CoreUserInfoClient;
import im.eg.srb.sms.service.SmsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@Api(tags = "短信服務")
@RequestMapping("/api/sms")
public class ApiSmsController {

    @Resource
    private CoreUserInfoClient coreUserInfoClient;

    @Resource
    private SmsService smsService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/send/{mobile}")
    @ApiOperation("發送驗證碼")
    public R send(@ApiParam(value = "手機號", required = true)
                  @PathVariable("mobile") String mobile) {

        // 做校驗
        Assert.notEmpty(mobile, ResponseEnum.MOBILE_NULL_ERROR);
        Assert.isTrue(RegexValidateUtils.checkCellphone(mobile), ResponseEnum.MOBILE_ERROR);

        // 通過 OpenFeign 調用「service-core」微服務中的接口判斷手機號是否已註冊
        R mobileRegisterStatus = coreUserInfoClient.checkMobileRegisterStatus(mobile);
        Assert.equals(mobileRegisterStatus.getData().get("exist"), false, ResponseEnum.MOBILE_EXIST_ERROR);

        // 發送短信
        Map<String, Object> params = new HashMap<>();
        String code = RandomUtils.getFourBitRandom();
        params.put("code", code);
//        TODO: 生產環境需要放開這裡喔
//        smsService.send(mobile, SmsProperties.TEMPLATE_CODE, params);

        // 將驗證碼放入緩存
        redisTemplate.opsForValue().set("srb:sms:code:" + mobile, code, 5, TimeUnit.MINUTES);

        return R.ok().message("短信發送成功");
    }
}
