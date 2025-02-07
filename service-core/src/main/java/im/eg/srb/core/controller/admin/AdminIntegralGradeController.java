package im.eg.srb.core.controller.admin;


import im.eg.common.exception.Assert;
import im.eg.common.result.R;
import im.eg.common.result.ResponseEnum;
import im.eg.srb.core.pojo.entity.IntegralGrade;
import im.eg.srb.core.service.IntegralGradeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 积分等级表 前端控制器
 * </p>
 *
 * @author EGU0
 * @since 2024-11-23
 */
@Api(tags = "积分等级管理")
@RestController
@RequestMapping("/admin/core/integralGrade")
public class AdminIntegralGradeController {

    @Resource
    private IntegralGradeService integralGradeService;

    @ApiOperation("积分等级列表")
    @GetMapping("/list")
    public R listAll() {
        List<IntegralGrade> list = integralGradeService.list();
        return R.ok().data("list", list).message("获取列表成功");
    }

    @ApiOperation(value = "根据 ID 删除积分等级", notes = "逻辑删除数据")
    @DeleteMapping("/remove/{id}")
    public R removeById(@ApiParam(value = "记录编号 ID", example = "100", required = true) @PathVariable Long id) {
        if (integralGradeService.removeById(id)) {
            return R.ok().message("删除记录成功");
        } else {
            return R.error().message("删除记录失败");
        }
    }

    @ApiOperation(value = "新增积分等级")
    @PostMapping("/save")
    public R save(@ApiParam(value = "积分等级对象", required = true) @RequestBody IntegralGrade integralGrade) {

        Assert.notNull(integralGrade.getBorrowAmount(), ResponseEnum.BORROW_AMOUNT_NULL_ERROR);

        if (integralGradeService.save(integralGrade)) {
            return R.ok().message("保存成功");
        } else {
            return R.error().message("保存失败");
        }
    }

    @ApiOperation(value = "根据 ID 获取积分等级记录")
    @GetMapping("/get/{id}")
    public R getById(@ApiParam(value = "记录 id", required = true) @PathVariable Long id) {
        IntegralGrade record = integralGradeService.getById(id);
        if (record != null) {
            return R.ok().data("record", record);
        } else {
            return R.error().message("数据获取失败");
        }
    }

    @ApiOperation(value = "更新积分等级")
    @PutMapping("/update")
    public R updateById(@ApiParam(value = "积分等级对象", required = true) @RequestBody IntegralGrade integralGrade) {
        if (integralGradeService.updateById(integralGrade)) {
            return R.ok().message("更新成功");
        } else {
            return R.error().message("更新失败");
        }
    }
}
