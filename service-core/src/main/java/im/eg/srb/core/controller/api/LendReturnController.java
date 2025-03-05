package im.eg.srb.core.controller.api;


import com.alibaba.fastjson.JSON;
import im.eg.common.result.R;
import im.eg.srb.base.util.JwtUtils;
import im.eg.srb.core.hfb.RequestHelper;
import im.eg.srb.core.service.LendReturnService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * <p>
 * 还款记录表 前端控制器
 * </p>
 *
 * @author EGU0
 * @since 2024-11-23
 */
@Slf4j
@RestController
@Api(tags = "标的还款计划")
@RequestMapping("/api/core/lendReturn")
public class LendReturnController {
    @Resource
    private LendReturnService lendReturnService;

    @ApiOperation("获取标的还款计划列表")
    @GetMapping("/list/{lendId}")
    public R list(@ApiParam(value = "标的 id", required = true) @PathVariable Long lendId) {
        return R.ok().data("list", lendReturnService.listById(lendId));
    }

    @ApiOperation("用户还款")
    @PostMapping("/auth/commitReturn/{lendReturnId}")
    public R commitReturn(@ApiParam(value = "还款计划 id", required = true) @PathVariable Long lendReturnId,
                          HttpServletRequest request) {
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        return R.ok().data("formStr", lendReturnService.commitReturn(lendReturnId, userId));
    }

    @ApiOperation(value = "还款异步回调")
    @PostMapping("/notifyUrl")
    public String notify(HttpServletRequest request) {
        Map<String, Object> params = RequestHelper.switchMap(request.getParameterMap());
        log.info("还款异步回调接口 - 接收的参数：{}", JSON.toJSONString(params));

        // 验证签名
        if (!RequestHelper.isSignEquals(params)) {
            log.error("还款异步回调接口 - 簽名校驗失敗：{}", JSON.toJSONString(params));
            return "fail";
        }

        String resultCode = (String) params.get("resultCode");
        if ("0001".equals(resultCode)) {
            return lendReturnService.notify(params);
        } else {
            log.error("还款异步回调接口 - 还款失败：{}", JSON.toJSONString(params));
        }

        return "success";
    }

}
