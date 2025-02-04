package im.eg.srb.base.util;

import im.eg.common.exception.BusinessException;
import im.eg.common.result.ResponseEnum;
import io.jsonwebtoken.*;
import org.springframework.util.StringUtils;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;

public class JwtUtils {

    private static final long TOKEN_EXPIRATION = 24 * 60 * 60 * 1000;
    private static final String TOKEN_SIGN_KEY = "A1t2g3squid123456"; // 硬編碼

    private static Key getKeyInstance() {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        byte[] bytes = DatatypeConverter.parseBase64Binary(TOKEN_SIGN_KEY);
        return new SecretKeySpec(bytes, signatureAlgorithm.getJcaName());
    }

    public static String createToken(Long userId, String userName) {
        return Jwts.builder()
                .setSubject("SRB-USER")
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION))
                .claim("userId", userId)
                .claim("userName", userName)
                .signWith(SignatureAlgorithm.HS512, getKeyInstance())
                .compressWith(CompressionCodecs.GZIP)
                .compact();
    }

    /**
     * 判断token是否有效
     */
    public static boolean checkToken(String token) {
        if (StringUtils.isEmpty(token)) {
            return false;
        }
        try {
            Jwts.parser().setSigningKey(getKeyInstance()).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 獲取用戶 ID
     */
    public static Long getUserId(String token) {
        Claims claims = getClaims(token);
        Integer userId = (Integer) claims.get("userId");
        return userId.longValue();
    }

    /**
     * 獲取用戶名
     */
    public static String getUserName(String token) {
        Claims claims = getClaims(token);
        return (String) claims.get("userName");
    }

    public static void removeToken(String token) {
        // jwt 無法在服務端銷毀，不用考慮
    }

    /**
     * 校验token并返回Claims
     */
    private static Claims getClaims(String token) {
        if (StringUtils.isEmpty(token)) {
            // LOGIN_AUTH_ERROR(-211, "未登录"),
            throw new BusinessException(ResponseEnum.LOGIN_AUTH_ERROR);
        }
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(getKeyInstance()).parseClaimsJws(token);
            return claimsJws.getBody();
        } catch (Exception e) {
            throw new BusinessException(ResponseEnum.LOGIN_AUTH_ERROR);
        }
    }
}