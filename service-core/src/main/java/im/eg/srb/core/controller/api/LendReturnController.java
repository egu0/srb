package im.eg.srb.core.controller.api;


import im.eg.common.result.R;
import im.eg.srb.core.service.LendReturnService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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
}
