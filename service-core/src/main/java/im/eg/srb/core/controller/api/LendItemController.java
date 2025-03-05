package im.eg.srb.core.controller.api;


import com.alibaba.fastjson.JSON;
import im.eg.common.result.R;
import im.eg.srb.base.util.JwtUtils;
import im.eg.srb.core.hfb.RequestHelper;
import im.eg.srb.core.pojo.vo.InvestVO;
import im.eg.srb.core.service.LendItemService;
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
 * 标的出借记录表 前端控制器
 * </p>
 *
 * @author EGU0
 * @since 2024-11-23
 */
@Slf4j
@RestController
@Api(tags = "标的投资")
@RequestMapping("/api/core/lendItem")
public class LendItemController {

    @Resource
    private LendItemService lendItemService;

    @ApiOperation("投资人投标")
    @PostMapping("/auth/commitInvest")
    public R commitInvest(@RequestBody InvestVO investVO, HttpServletRequest request) {
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        String userName = JwtUtils.getUserName(token);
        investVO.setInvestUserId(userId);
        investVO.setInvestUserName(userName);

        // 构建包含表单的自动提交页面
        String formStr = lendItemService.commitInvest(investVO);
        return R.ok().data("formStr", formStr);
    }

    @ApiOperation(value = "投标异步回调")
    @PostMapping("/notify")
    public String notify(HttpServletRequest request) {
        Map<String, Object> params = RequestHelper.switchMap(request.getParameterMap());
        log.info("投标异步回调接口 - 接收的参数：{}", JSON.toJSONString(params));

        // 验证签名
        if (!RequestHelper.isSignEquals(params)) {
            log.error("投标异步回调接口 - 簽名校驗失敗：{}", JSON.toJSONString(params));
            return "fail";
        }

        String resultCode = (String) params.get("resultCode");
        if ("0001".equals(resultCode)) {
            return lendItemService.notify(params);
        } else {
            log.error("投标异步回调接口 - 投标失败：{}", JSON.toJSONString(params));
        }

        return "success";
    }

    @ApiOperation("获取标的投资列表")
    @GetMapping("/list/{lendId}")
    public R list(@ApiParam(value = "标的 id", required = true) @PathVariable Long lendId) {
        return R.ok().data("list", lendItemService.listByLendId(lendId));
    }
}
