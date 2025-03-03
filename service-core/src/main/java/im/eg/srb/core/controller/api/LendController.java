package im.eg.srb.core.controller.api;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import im.eg.common.result.R;
import im.eg.srb.core.pojo.entity.Lend;
import im.eg.srb.core.pojo.vo.LendVO;
import im.eg.srb.core.service.LendService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * <p>
 * 标的准备表 前端控制器
 * </p>
 *
 * @author EGU0
 * @since 2024-11-23
 */
@Slf4j
@RestController
@Api(tags = "标的")
@RequestMapping("/api/core/lend")
public class LendController {

    @Resource
    private LendService lendService;

    @ApiOperation("标的分页列表")
    @GetMapping("/list/{page}/{limit}")
    public R listPage(@ApiParam(value = "当前页码", required = true)
                      @PathVariable Long page,
                      @ApiParam(value = "每页记录数", required = true)
                      @PathVariable Long limit) {
        Page<Lend> pageParam = new Page<>(page, limit);
        IPage<LendVO> listModel = lendService.listPage(pageParam);
        return R.ok().data("list", listModel);
    }

    @ApiOperation("获取标的详细信息")
    @GetMapping("/detail/{lendId}")
    public R detail(@ApiParam(value = "标的 id", required = true) @PathVariable Long lendId) {
        Map<String, Object> detail = lendService.getLendDetail(lendId);
        return R.ok().data("detail", detail);
    }
}
