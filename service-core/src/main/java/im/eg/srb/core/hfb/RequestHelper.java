package im.eg.srb.core.hfb;

import com.alibaba.fastjson.JSONObject;
import im.eg.common.util.HttpUtils;
import im.eg.common.util.MD5;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Slf4j
public class RequestHelper {

    /**
     * 请求数据获取签名
     */
    public static String getSign(Map<String, Object> paramMap) {
        paramMap.remove("sign");
        TreeMap<String, Object> sorted = new TreeMap<>(paramMap);
        StringBuilder str = new StringBuilder();
        for (Map.Entry<String, Object> param : sorted.entrySet()) {
            str.append(param.getValue()).append("|");
        }
        str.append(HfbConst.SIGN_KEY);
        log.info("加密前：{}", str);
        String md5Str = MD5.encrypt(str.toString());
        log.info("加密后：{}", md5Str);
        return md5Str;
    }

    /**
     * Map 转换
     */
    public static Map<String, Object> switchMap(Map<String, String[]> paramMap) {
        Map<String, Object> resultMap = new HashMap<>();
        for (Map.Entry<String, String[]> param : paramMap.entrySet()) {
            resultMap.put(param.getKey(), param.getValue()[0]);
        }
        return resultMap;
    }

    /**
     * 签名校验
     */
    public static boolean isSignEquals(Map<String, Object> paramMap) {
        String sign = (String) paramMap.get("sign");
        String md5Str = getSign(paramMap);
        return sign.equals(md5Str);
    }

    /**
     * 获取时间戳
     */
    public static long getTimestamp() {
        return new Date().getTime();
    }

    /**
     * 封装同步请求
     */
    public static JSONObject sendRequest(Map<String, Object> paramMap, String url) {
        String result = "";
        try {
            //封装post参数
            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String, Object> param : paramMap.entrySet()) {
                postData.append(param.getKey()).append("=")
                        .append(param.getValue()).append("&");
            }
            log.info("--> 发送请求到汇付宝：post data {}", String.format("%1s", postData));
            byte[] reqData = postData.toString().getBytes(StandardCharsets.UTF_8);
            byte[] respData = HttpUtils.doPost(url, reqData);
            result = new String(respData);
            log.info("--> 汇付宝应答结果: result data {}", String.format("%1s", result));
        } catch (Exception ex) {
            log.error("封装请求时出错", ex);
        }
        return JSONObject.parseObject(result);
    }
}