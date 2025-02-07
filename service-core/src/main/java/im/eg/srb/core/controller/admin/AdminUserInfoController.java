package im.eg.srb.core.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import im.eg.common.result.R;
import im.eg.srb.core.pojo.entity.UserInfo;
import im.eg.srb.core.pojo.query.UserInfoQuery;
import im.eg.srb.core.service.UserInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 * 用户基本信息 前端控制器
 * </p>
 *
 * @author EGU0
 * @since 2024-11-23
 */
@Slf4j
@RestController
@Api(tags = "會員管理")
@RequestMapping("/admin/core/userInfo")
public class AdminUserInfoController {

    @Resource
    private UserInfoService userInfoService;

    @ApiOperation("獲取會員分頁列表")
    @GetMapping("/list/{page}/{limit}")
    public R listPage(
            @ApiParam(value = "頁碼", required = true)
            @PathVariable("page") Integer page,
            @ApiParam(value = "條數", required = true)
            @PathVariable("limit") Integer limit,
            @ApiParam(value = "查詢對象")
            UserInfoQuery userInfoQuery
    ) {
        Page<UserInfo> userInfoPage = new Page<>(page, limit);
        IPage<UserInfo> pageModel = userInfoService.listPage(userInfoPage, userInfoQuery);
        return R.ok().data("pageModel", pageModel);
    }

    @ApiOperation("鎖定和解鎖")
    @PutMapping("/lock/{id}/{status}")
    public R lock(
            @ApiParam(value = "用戶 ID", required = true)
            @PathVariable Long id,
            @ApiParam(value = "鎖定狀態（0：鎖定 1：正常）", required = true)
            @PathVariable Integer status) {
        userInfoService.lock(id, status);
        return R.ok().message(status == 1 ? "解鎖成功" : "鎖定成功");
    }
}
