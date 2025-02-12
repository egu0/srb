package im.eg.srb.core.controller.api;


import com.alibaba.fastjson.JSON;
import im.eg.common.result.R;
import im.eg.srb.base.util.JwtUtils;
import im.eg.srb.core.hfb.RequestHelper;
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
import java.util.Map;

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

    @ApiOperation("帳戶綁定異步回調處理")
    @PostMapping("/notify")
    public String notify(HttpServletRequest request) {
        // 「匯付寶系統」發送請求傳遞的參數
        Map<String, Object> params = RequestHelper.switchMap(request.getParameterMap());
        log.info("帳戶綁定異步回調處理接口接收的參數為: {}", JSON.toJSONString(params));

        if (!RequestHelper.isSignEquals(params)) {
            log.error("用戶帳號綁定異步回調處理接口簽名校驗失敗 {}", JSON.toJSONString(params));
            return "fail";
        }

        userBindService.notify(params);

        // 返回 success 給「匯付寶系統」時表示成功；如果返回別的，那麼「匯付寶系統」會進行重試
        return "success";
    }

}
