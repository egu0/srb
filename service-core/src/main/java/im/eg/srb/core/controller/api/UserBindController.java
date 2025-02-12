package im.eg.srb.core.controller.api;


import im.eg.common.result.R;
import im.eg.srb.base.util.JwtUtils;
import im.eg.srb.core.pojo.vo.UserBindVO;
import im.eg.srb.core.service.UserBindService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 用户绑定表 前端控制器
 * </p>
 *
 * @author EGU0
 * @since 2024-11-23
 */
@Slf4j
@RestController
@Api(tags = "會員帳號綁定")
@RequestMapping("/api/core/userBind")
public class UserBindController {

    @Resource
    private UserBindService userBindService;

    @PostMapping("/auth/bind") // `/auth/` 路徑後期用來在網關模塊做登錄校驗
    @ApiOperation("帳戶綁定之提交數據")
    public R bind(@RequestBody UserBindVO userBindVO, HttpServletRequest request) {

        // token 校驗，並從中獲取 user_id
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);

        // 根據 userId 進行用戶校驗，得到一個「動態表單」字符串
        String formStr = userBindService.commitBindUser(userBindVO, userId);

        return R.ok().data("formStr", formStr);
    }

}
