package im.eg.srb.core.controller.admin;


import im.eg.srb.core.pojo.entity.IntegralGrade;
import im.eg.srb.core.service.IntegralGradeService;
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
    @GetMapping("/list")
    public List<IntegralGrade> listAll() {
        return integralGradeService.list();
    }

    /**
     * 根据 id 删除指定的积分等级记录
     * <p>
     * 尝试：curl -X DELETE http://localhost:8110/admin/core/integralGrade/remove/1
     */
    @DeleteMapping("/remove/{id}")
    public boolean removeById(@PathVariable Long id) {
        return integralGradeService.removeById(id);
    }
}

