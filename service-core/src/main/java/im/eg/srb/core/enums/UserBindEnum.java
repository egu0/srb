package im.eg.srb.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserBindEnum {
    NO_BIND(0, "未绑定"),
    BIND_OK(1, "绑定成功"),
    BIND_FAIL(2, "绑定失败"),
    ;
    private final Integer status;
    private final String msg;
}
