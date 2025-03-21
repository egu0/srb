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
import org.springframework.web.bind.annotation.*;

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
        log.info("用户充值异步回调接口接收的参数：{}", JSON.toJSONString(params));

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

        return "success";
    }

    @ApiOperation("查询账户余额")
    @GetMapping("/auth/getAccAmt")
    public R getAccAmt(HttpServletRequest request) {
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        BigDecimal amount = userAccountService.getAccAmt(userId);
        return R.ok().data("amount", amount);
    }

    @ApiOperation("用户提现")
    @PostMapping("/auth/commitWithdraw/{fetchAmt}")
    public R commitWithdraw(@ApiParam(value = "金额", required = true) @PathVariable BigDecimal fetchAmt,
                            HttpServletRequest request) {
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        return R.ok().data("formStr", userAccountService.commitWithdraw(fetchAmt, userId));
    }

    @ApiOperation(value = "用户提现异步回调")
    @PostMapping("/notifyWithdraw")
    public String notifyOfWithdraw(HttpServletRequest request) {
        Map<String, Object> params = RequestHelper.switchMap(request.getParameterMap());
        log.info("用户提现异步回调接口 - 接收的参数：{}", JSON.toJSONString(params));

        // 验证签名
        if (!RequestHelper.isSignEquals(params)) {
            log.error("用户提现异步回调接口 - 簽名校驗失敗：{}", JSON.toJSONString(params));
            return "fail";
        }

        String resultCode = (String) params.get("resultCode");
        if ("0001".equals(resultCode)) {
            return userAccountService.notifyWithdraw(params);
        } else {
            log.error("用户提现异步回调接口 - 提现失败：{}", JSON.toJSONString(params));
        }

        return "success";
    }

}
