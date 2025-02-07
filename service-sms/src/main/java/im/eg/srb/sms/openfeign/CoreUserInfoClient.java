package im.eg.srb.sms.openfeign;

import im.eg.common.result.R;
import im.eg.srb.sms.openfeign.fallback.CoreUserInfoClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "service-core", fallback = CoreUserInfoClientFallback.class) // 微服務標識
public interface CoreUserInfoClient {
    @GetMapping("/api/core/userInfo/checkMobileRegStatus/{mobile}")
    public R checkMobileRegisterStatus(@PathVariable String mobile);
}
