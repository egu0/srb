package im.eg.srb.core.controller.admin;


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
@CrossOrigin
@RestController
@RequestMapping("/admin/core/integralGrade")
public class AdminIntegralGradeController {

    @Resource
    private IntegralGradeService integralGradeService;

    /**
     * 查询积分等级列表
     * <p>
     * 尝试：curl http://localhost:8110/admin/core/integralGrade/list
     */
    @ApiOperation("积分等级列表")
    @GetMapping("/list")
    public List<IntegralGrade> listAll() {
        return integralGradeService.list();
    }

    /**
     * 根据 id 删除指定的积分等级记录
     * <p>
     * 尝试：curl -X DELETE http://localhost:8110/admin/core/integralGrade/remove/1
     */
    @ApiOperation(value = "根据 ID 删除积分等级", notes = "逻辑删除数据")
    @DeleteMapping("/remove/{id}")
    public boolean removeById(@ApiParam(value = "记录编号 ID", example = "100", required = true) @PathVariable Long id) {
        return integralGradeService.removeById(id);
    }
}

