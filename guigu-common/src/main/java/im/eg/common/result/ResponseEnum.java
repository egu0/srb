package im.eg.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum ResponseEnum {

    SUCCESS(0, "成功"),
    ERROR(-1, "服务器内部错误"),

    //-1xx 服务器错误
    BAD_SQL_GRAMMAR_ERROR(-101, "sql语法错误"),
    SERVLET_ERROR(-102, "servlet请求异常"), //-2xx 参数校验
    UPLOAD_ERROR(-103, "文件上传错误"),
    EXPORT_DATA_ERROR(104, "数据导出失败"),

    //-2xx 参数校验
    BORROW_AMOUNT_NULL_ERROR(-201, "借款额度不能为空"),
    MOBILE_NULL_ERROR(-202, "手机号码不能为空"),
    MOBILE_ERROR(-203, "手机号码不正确"),
    PASSWORD_NULL_ERROR(204, "密码不能为空"),
    PASSWORD_FORMAT_ERROR(212, "密码格式错误"),
    CAPTCHA_NULL_ERROR(205, "验证码不能为空"),
    CAPTCHA_ERROR(206, "验证码错误"),
    MOBILE_EXIST_ERROR(207, "手机号已被注册"),
    LOGIN_MOBILE_ERROR(208, "用户不存在"),
    LOGIN_PASSWORD_ERROR(209, "密码错误"),
    LOGIN_LOCKED_ERROR(210, "用户被锁定"),
    LOGIN_AUTH_ERROR(-211, "未登录"),

    USER_BIND_ID_EXIST_ERROR(-301, "身份证号码已绑定"),
    USER_NO_BIND_ERROR(302, "用户未绑定"),
    USER_NO_AMOUNT_ERROR(303, "用户信息未审核"),
    USER_AMOUNT_LESS_ERROR(304, "您的借款额度不足"),
    LEND_INVEST_ERROR(305, "当前状态无法投标"),
    LEND_FULL_SCALE_ERROR(306, "已满标，无法投标"),
    NOT_SUFFICIENT_FUNDS_ERROR(307, "余额不足，请充值"),
    BORROWER_NOT_EXIST(308, "借款人不存在"),
    LEND_NOT_EXIST(309, "标的不存在"),
    INVEST_AMOUNT_NOT_VALID(310, "投资金额不能小于标的最低限制"),
    INVALID_AMOUNT(311, "金额非法"),

    PAY_UNIFIED_ORDER_ERROR(401, "统一下单错误"),

    ALIYUN_SMS_LIMIT_CONTROL_ERROR(-502, "短信发送过于频繁"),//业务限流
    ALIYUN_SMS_ERROR(-503, "短信发送失败"),//其他失败

    WEIXIN_CALLBACK_PARAM_ERROR(-601, "回调参数不正确"),
    WEIXIN_FETCH_ACCESS_TOKEN_ERROR(-602, "获取access_token失败"),
    WEIXIN_FETCH_USERINFO_ERROR(-603, "获取用户信息失败");

    /**
     * 响应状态码
     */
    private Integer code;
    /**
     * 响应信息
     */
    private String message;
}