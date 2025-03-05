package im.eg.srb.core.controller.api;


import im.eg.common.exception.Assert;
import im.eg.common.result.R;
import im.eg.common.result.ResponseEnum;
import im.eg.common.util.RegexValidateUtils;
import im.eg.srb.base.util.JwtUtils;
import im.eg.srb.core.pojo.vo.LoginVO;
import im.eg.srb.core.pojo.vo.RegisterVO;
import im.eg.srb.core.pojo.vo.UserInfoVO;
import im.eg.srb.core.service.UserInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 用户基本信息 前端控制器
 * </p>
 *
 * @author EGU0
 * @since 2024-11-23
 */
@Api(tags = "用户管理")
@Slf4j
@RestController
@RequestMapping("/api/core/userInfo")
public class UserInfoController {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private UserInfoService userInfoService;

    @ApiOperation(value = "會員註冊")
    @PostMapping(value = "/register")
    public R register(@RequestBody RegisterVO registerVO) {
        String mobile = registerVO.getMobile();
        String code = registerVO.getCode();
        String password = registerVO.getPassword();

        // 校驗
        Assert.notEmpty(mobile, ResponseEnum.MOBILE_NULL_ERROR);
        Assert.notEmpty(code, ResponseEnum.CAPTCHA_NULL_ERROR);
        Assert.notEmpty(password, ResponseEnum.PASSWORD_NULL_ERROR);
        Assert.isTrue(RegexValidateUtils.checkCellphone(mobile), ResponseEnum.MOBILE_ERROR);
        Assert.isTrue(RegexValidateUtils.checkPassword(password), ResponseEnum.PASSWORD_FORMAT_ERROR);

        // 校驗驗證碼
        String key = "srb:sms:code:" + mobile;
        String value = (String) redisTemplate.opsForValue().get(key);
        Assert.equals(code, value, ResponseEnum.CAPTCHA_ERROR);

        // 註冊
        userInfoService.register(registerVO);
        return R.ok();
    }

    @ApiOperation("會員登錄")
    @PostMapping("/login")
    public R login(@RequestBody LoginVO loginVO, HttpServletRequest request) {
        String mobile = loginVO.getMobile();
        String password = loginVO.getPassword();

        Assert.notEmpty(mobile, ResponseEnum.MOBILE_NULL_ERROR);
        Assert.notEmpty(password, ResponseEnum.PASSWORD_NULL_ERROR);

        // 客戶 IP
        String remoteAddr = request.getRemoteAddr();

        UserInfoVO userInfoVO = userInfoService.login(loginVO, remoteAddr);
        return R.ok().data("userInfo", userInfoVO);
    }

    @ApiOperation("校驗令牌")
    @GetMapping("/checkToken")
    public R checkToken(HttpServletRequest request) {
        String token = request.getHeader("token");
        boolean valid = JwtUtils.checkToken(token);
        if (valid) {
            return R.ok();
        } else {
            return R.setResult(ResponseEnum.LOGIN_AUTH_ERROR);
        }
    }

    @ApiOperation("檢查手機號是否已經被註冊")
    @GetMapping("/checkMobileRegStatus/{mobile}")
    public R checkMobileRegisterStatus(@PathVariable String mobile) {
        Assert.isTrue(RegexValidateUtils.checkCellphone(mobile), ResponseEnum.MOBILE_ERROR);
        return R.ok().data("exist", userInfoService.checkMobileRegisterStatus(mobile));
    }

    @ApiOperation("获取用户个人空间页面所需信息")
    @GetMapping("/auth/getIndexUserInfo")
    public R getIndexUserInfo(HttpServletRequest request) {
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        return R.ok().data("detail", userInfoService.getIndexUserVo(userId));
    }
}
