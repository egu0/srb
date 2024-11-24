package im.eg.common.result;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 统一返回结果
 */
@Data
public class R {

    private Integer code;
    private String message;
    private Map<String, Object> data = new HashMap<>();

    private R() {
    }

    /**
     * 返回成功结果
     */
    public static R ok() {
        return setResult(ResponseEnum.SUCCESS);
    }

    /**
     * 返回失败结果
     */
    public static R error() {
        return setResult(ResponseEnum.ERROR);
    }

    /**
     * 返回特定结果
     */
    public static R setResult(ResponseEnum responseEnum) {
        R r = new R();
        r.setCode(responseEnum.getCode());
        r.setMessage(responseEnum.getMessage());
        return r;
    }

    /**
     * 封装返回数据
     */
    public R data(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

    /**
     * 封装返回数据，若为Map集合，则直接赋值即可
     */
    public R data(Map<String, Object> map) {
        this.setData(map);
        return this;
    }

    /**
     * 设置特定的消息
     */
    public R message(String message) {
        this.setMessage(message);
        return this;
    }

    /**
     * 设置特定的响应码
     */
    public R code(Integer code) {
        this.setCode(code);
        return this;
    }
}