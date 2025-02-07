package im.eg.srb.sms.openfeign.fallback;

import im.eg.common.result.R;
import im.eg.srb.sms.openfeign.CoreUserInfoClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CoreUserInfoClientFallback implements CoreUserInfoClient {
    @Override
    public R checkMobileRegisterStatus(String mobile) {
        log.error("遠程調用失敗，服務熔斷");
        return R.ok().data("exist", false);
    }
}
