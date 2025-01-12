package im.eg.srb.sms;

import im.eg.srb.sms.util.SmsProperties;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UtilTests {

    @org.junit.jupiter.api.Test // 和 org.junit.Test 做區別
    public void printProperties() {
        System.out.println(SmsProperties.REGION_ID);
        System.out.println(SmsProperties.KEY_ID);
        System.out.println(SmsProperties.KEY_SECRET);
        System.out.println(SmsProperties.TEMPLATE_CODE);
        System.out.println(SmsProperties.SIGN_NAME);
    }
}

