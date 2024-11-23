package im.eg.srb.core.controller.admin;


import im.eg.srb.core.pojo.entity.IntegralGrade;
import im.eg.srb.core.service.IntegralGradeService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    // http://localhost:8110/admin/core/integralGrade/list
    @GetMapping("/list")
    public List<IntegralGrade> listAll() {
        return integralGradeService.list();
    }
}

