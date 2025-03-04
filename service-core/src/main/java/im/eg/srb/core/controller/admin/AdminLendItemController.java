package im.eg.srb.core.controller.admin;

import im.eg.common.result.R;
import im.eg.srb.core.pojo.entity.LendItem;
import im.eg.srb.core.service.LendItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Api(tags = "标的投资")
@RestController
@RequestMapping("/admin/core/lendItem")
public class AdminLendItemController {
    @Resource
    private LendItemService lendItemService;

    @ApiOperation("标的投资列表")
    @GetMapping("/list/{lendId}")
    public R list(@ApiParam(value = "标的 id", required = true)
                  @PathVariable Long lendId) {
        List<LendItem> list = lendItemService.listByLendId(lendId);
        return R.ok().data("list", list);
    }
}
