package im.eg.srb.core.controller.api;


import im.eg.common.result.R;
import im.eg.srb.base.util.JwtUtils;
import im.eg.srb.core.pojo.vo.InvestVO;
import im.eg.srb.core.service.LendItemService;
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
 * 标的出借记录表 前端控制器
 * </p>
 *
 * @author EGU0
 * @since 2024-11-23
 */
@Slf4j
@RestController
@Api(tags = "标的投资")
@RequestMapping("/api/core/lendItem")
public class LendItemController {

    @Resource
    private LendItemService lendItemService;

    @ApiOperation("投资人投标")
    @PostMapping("/auth/commitInvest")
    public R commitInvest(@RequestBody InvestVO investVO, HttpServletRequest request) {
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        String userName = JwtUtils.getUserName(token);
        investVO.setInvestUserId(userId);
        investVO.setInvestUserName(userName);

        // 构建包含表单的自动提交页面
        String formStr = lendItemService.commitInvest(investVO);
        return R.ok().data("formStr", formStr);
    }
}
