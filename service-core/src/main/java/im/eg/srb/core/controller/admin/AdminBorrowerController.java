package im.eg.srb.core.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import im.eg.common.result.R;
import im.eg.srb.core.pojo.entity.Borrower;
import im.eg.srb.core.pojo.vo.BorrowerApprovalVO;
import im.eg.srb.core.pojo.vo.BorrowerDetailVO;
import im.eg.srb.core.service.BorrowerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 * 借款人 前端控制器
 * </p>
 *
 * @author EGU0
 * @since 2024-11-23
 */
@Slf4j
@RestController
@Api(tags = "借款人管理")
@RequestMapping("/admin/core/borrower")
public class AdminBorrowerController {

    @Resource
    private BorrowerService borrowerService;

    @ApiOperation("获取借款人分页列表")
    @GetMapping("/list/{page}/{limit}")
    public R listPage(@ApiParam(value = "当前页码", required = true)
                      @PathVariable Long page,
                      @ApiParam(value = "每页记录数", required = true)
                      @PathVariable Long limit,
                      @ApiParam(value = "查询关键字")
                      @RequestParam String keyword) {
        Page<Borrower> pageParam = new Page<>(page, limit);
        IPage<Borrower> listModel = borrowerService.listPage(pageParam, keyword);
        return R.ok().data("list", listModel);
    }

    @ApiOperation("获取借款人详细信息")
    @GetMapping("/detail/{userId}")
    public R detail(@ApiParam(value = "用户 id", required = true) @PathVariable Long userId) {
        BorrowerDetailVO vo = borrowerService.getBorrowerDetailVOByBorrowerId(userId);
        return R.ok().data("detail", vo);
    }

    @ApiOperation("借款额度审批")
    @PostMapping("/approval")
    public R approval(@RequestBody BorrowerApprovalVO borrowerApprovalVO) {
        borrowerService.approval(borrowerApprovalVO);
        return R.ok().message("审批完成");
    }
}
