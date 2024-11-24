package im.eg.common.exception;

import im.eg.common.result.ResponseEnum;
import org.springframework.util.StringUtils;

public class Assert {
    public static void notNull(Object val, ResponseEnum responseEnum) {
        if (val == null) {
            throw new BusinessException(responseEnum);
        }
    }

    public static void isNull(Object val, ResponseEnum responseEnum) {
        if (val != null) {
            throw new BusinessException(responseEnum);
        }
    }

    public static void isTrue(boolean val, ResponseEnum responseEnum) {
        if (!val) {
            throw new BusinessException(responseEnum);
        }
    }

    public static void notEquals(Object o1, Object o2, ResponseEnum responseEnum) {
        if (o1.equals(o2)) {
            throw new BusinessException(responseEnum);
        }
    }

    public static void equals(Object o1, Object o2, ResponseEnum responseEnum) {
        if (!o1.equals(o2)) {
            throw new BusinessException(responseEnum);
        }
    }

    public static void notEmpty(String s, ResponseEnum responseEnum) {
        if (StringUtils.isEmpty(s)) {
            throw new BusinessException(responseEnum);
        }
    }
}
