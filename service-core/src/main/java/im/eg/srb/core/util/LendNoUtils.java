package im.eg.srb.core.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class LendNoUtils {

    public static String getNo() {

        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String strDate = dtf.format(time);

        StringBuilder result = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 3; i++) {
            result.append(random.nextInt(10));
        }

        return strDate + result;
    }

    /**
     * 借款编号
     */
    public static String getLendNo() {
        return "LEND" + getNo();
    }

    /**
     * 投资编号
     */
    public static String getLendItemNo() {
        return "INVEST" + getNo();
    }

    /**
     * 贷款编号
     */
    public static String getLoanNo() {
        return "LOAN" + getNo();
    }

    /**
     * 还款编号
     */
    public static String getReturnNo() {
        return "RETURN" + getNo();
    }

    /**
     * 体现编码
     */
    public static Object getWithdrawNo() {
        return "WITHDRAW" + getNo();
    }

    /**
     * 分期还款编号
     */
    public static String getReturnItemNo() {
        return "RETURNITEM" + getNo();
    }

    /**
     * 充值编码
     */
    public static String getChargeNo() {
        return "CHARGE" + getNo();
    }

    /**
     * 获取交易编码
     */
    public static String getTransNo() {
        return "TRANS" + getNo();
    }

}