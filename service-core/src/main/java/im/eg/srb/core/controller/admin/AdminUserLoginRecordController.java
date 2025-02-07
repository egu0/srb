package im.eg.srb.core.controller.admin;


import im.eg.common.result.R;
import im.eg.srb.core.pojo.entity.UserLoginRecord;
import im.eg.srb.core.service.UserLoginRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 用户登录记录表 前端控制器
 * </p>
 *
 * @author EGU0
 * @since 2024-11-23
 */
@RestController
@Api(tags = "會員登錄日誌接口")
@RequestMapping("/admin/core/userLoginRecord")
public class AdminUserLoginRecordController {
    @Resource
    private UserLoginRecordService userLoginRecordService;

    @ApiOperation(value = "獲取會員登錄日誌列表")
    @GetMapping("/listTop50/{userId}")
    public R listTop50(
            @ApiParam(value = "用戶 ID", required = true)
            @PathVariable("userId") Long userId) {
        List<UserLoginRecord> list = userLoginRecordService.listTop50(userId);
        return R.ok().data("list", list);
    }
}
