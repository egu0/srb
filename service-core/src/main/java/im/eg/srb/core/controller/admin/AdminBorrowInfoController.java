package im.eg.srb.core.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import im.eg.common.result.R;
import im.eg.srb.core.pojo.entity.BorrowInfo;
import im.eg.srb.core.pojo.vo.BorrowInfoApprovalVO;
import im.eg.srb.core.pojo.vo.BorrowInfoDetailVO;
import im.eg.srb.core.service.BorrowInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * <p>
 * 借款信息表 前端控制器
 * </p>
 *
 * @author EGU0
 * @since 2024-11-23
 */
@Slf4j
@Api(tags = "借款信息管理")
@RestController
@RequestMapping("/admin/core/borrowInfo")
public class AdminBorrowInfoController {

    @Resource
    private BorrowInfoService borrowInfoService;

    @ApiOperation("获取借款信息分页列表")
    @GetMapping("/list/{page}/{limit}")
    public R listPage(@ApiParam(value = "当前页码", required = true)
                      @PathVariable Long page,
                      @ApiParam(value = "每页记录数", required = true)
                      @PathVariable Long limit) {
        Page<BorrowInfo> pageParam = new Page<>(page, limit);
        IPage<BorrowInfoDetailVO> listModel = borrowInfoService.listPage(pageParam);
        return R.ok().data("list", listModel);
    }

    @ApiOperation("借款信息详情")
    @GetMapping("/detail/{id}")
    public R detail(@ApiParam(value = "借款信息 id", required = true) @PathVariable Long id) {
        Map<String, Object> detail = borrowInfoService.getBorrowInfoDetailById(id);
        return R.ok().data("detail", detail);
    }

    @ApiOperation("审批借款信息")
    @PostMapping("/approval")
    public R approval(@RequestBody BorrowInfoApprovalVO borrowInfoApprovalVO) {
        borrowInfoService.approval(borrowInfoApprovalVO);
        return R.ok().message("审批完成");
    }

}
