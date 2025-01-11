package im.eg.srb.core.controller.admin;


import com.alibaba.excel.EasyExcel;
import im.eg.common.exception.BusinessException;
import im.eg.common.result.R;
import im.eg.common.result.ResponseEnum;
import im.eg.srb.core.pojo.dto.ExcelDictDTO;
import im.eg.srb.core.service.DictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

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
        } catch (Exception ex) {
            throw new BusinessException(ResponseEnum.UPLOAD_ERROR, ex);
        }
    }

    @ApiOperation("導出數據到 excel 文件")
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("dict-data", "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        List<ExcelDictDTO> result = dictService.listDictData();
        EasyExcel.write(response.getOutputStream(), ExcelDictDTO.class).sheet("數據字典").doWrite(result);
    }

}

