package im.eg.srb.core.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import im.eg.common.result.R;
import im.eg.srb.core.pojo.entity.BorrowInfo;
import im.eg.srb.core.pojo.vo.BorrowInfoDetailVO;
import im.eg.srb.core.service.BorrowInfoService;
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

}

