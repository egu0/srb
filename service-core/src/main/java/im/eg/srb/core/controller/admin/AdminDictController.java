package im.eg.srb.core.controller.admin;


import im.eg.common.exception.BusinessException;
import im.eg.common.result.R;
import im.eg.common.result.ResponseEnum;
import im.eg.srb.core.service.DictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * <p>
 * 数据字典 前端控制器
 * </p>
 *
 * @author EGU0
 * @since 2024-11-23
 */
@Api(tags = "數據字典管理")
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/admin/core/dict")
public class AdminDictController {

    @Resource
    private DictService dictService;

    @ApiOperation("通過 excel 文件導入數據")
    @PostMapping("/import")
    public R importData(
            @ApiParam(value = "Excel 數據字典文件", required = true)
            @RequestParam("file") MultipartFile file) {
        try {
            dictService.importData(file.getInputStream());
            return R.ok().message("字典數據批量導入成功");
        } catch (IOException ex) {
            throw new BusinessException(ResponseEnum.UPLOAD_ERROR, ex);
        }
    }

}

