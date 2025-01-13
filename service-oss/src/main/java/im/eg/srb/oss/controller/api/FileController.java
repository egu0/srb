package im.eg.srb.oss.controller.api;

import im.eg.common.exception.BusinessException;
import im.eg.common.result.R;
import im.eg.common.result.ResponseEnum;
import im.eg.srb.oss.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;

@Slf4j
@CrossOrigin
@RestController
@Api(tags = "文件管理")
@RequestMapping("/api/oss")
public class FileController {
    @Resource
    private FileService fileService;

    @ApiOperation("文件上傳")
    @PostMapping("/upload")
    public R upload(@ApiParam(value = "文件", required = true)
                    @RequestParam("file") MultipartFile file,
                    @ApiParam(value = "模塊名", required = true)
                    @RequestParam("module") String module) {
        try {
            String url = fileService.upload(file.getInputStream(), module, file.getOriginalFilename());
            return R.ok().message("文件上傳成功").data("url", url);
        } catch (IOException e) {
            throw new BusinessException(ResponseEnum.UPLOAD_ERROR);
        }
    }

    @ApiOperation("刪除文件")
    @DeleteMapping("/remove")
    public R remove(@ApiParam(value = "文件訪問連結", required = true)
                    @RequestParam("url") String url) {
        fileService.remove(url);
        return R.ok().message("刪除成功");
    }
}
