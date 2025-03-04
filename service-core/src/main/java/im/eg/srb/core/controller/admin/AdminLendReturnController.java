package im.eg.srb.core.controller.admin;

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

@Slf4j
@Api(tags = "还款记录")
@RestController
@RequestMapping("/admin/core/lendReturn")
public class AdminLendReturnController {
    @Resource
    private LendReturnService lendReturnService;

    @ApiOperation("获取标的还款列表")
    @GetMapping("/list/{lendId}")
    public R list(@ApiParam(value = "标的 id", required = true) @PathVariable Long lendId) {
        return R.ok().data("list", lendReturnService.listById(lendId));
    }
}
