package im.eg.srb.core.controller.api;


import com.alibaba.fastjson.JSON;
import im.eg.common.result.R;
import im.eg.srb.base.util.JwtUtils;
import im.eg.srb.core.hfb.RequestHelper;
import im.eg.srb.core.service.UserAccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Map;

/**
 * <p>
 * 用户账户 前端控制器
 * </p>
 *
 * @author EGU0
 * @since 2024-11-23
 */
@Slf4j
@RestController
@Api(tags = "会员帐户")
@RequestMapping("/api/core/userAccount")
public class UserAccountController {

    @Resource
    private UserAccountService userAccountService;

    @ApiOperation("充值")
    @PostMapping("/auth/commitCharge/{chargeAmount}")
    public R commitCharge(@ApiParam(value = "充值金额", required = true) @PathVariable BigDecimal chargeAmount,
                          HttpServletRequest request) {
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        // 组装表单字符串，用于往汇付宝系统提交数据
        String formStr = userAccountService.commitCharge(chargeAmount, userId);
        return R.ok().data("formStr", formStr);
    }

    @ApiOperation(value = "用户充值异步回调")
    @PostMapping("/notify")
    public String notify(HttpServletRequest request) {
        Map<String, Object> params = RequestHelper.switchMap(request.getParameterMap());
        log.info("用户充值异步回调接口接受的参数：{}", JSON.toJSONString(params));

        // 验证签名
        if (!RequestHelper.isSignEquals(params)) {
            log.error("用户充值异步回调接口 - 簽名校驗失敗：{}", JSON.toJSONString(params));
            return "fail";
        }

        String resultCode = (String) params.get("resultCode");
        if ("0001".equals(resultCode)) {
            return userAccountService.notify(params);
        } else {
            log.error("用户充值异步回调接口 - 充值失败：{}", JSON.toJSONString(params));
        }

//        return "此行用来测试幂等性问题";
        return "success";
    }

}
