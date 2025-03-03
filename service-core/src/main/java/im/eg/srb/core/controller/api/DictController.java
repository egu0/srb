package im.eg.srb.core.controller.api;


import im.eg.common.result.R;
import im.eg.srb.core.pojo.entity.Dict;
import im.eg.srb.core.service.DictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 数据字典 前端控制器
 * </p>
 *
 * @author EGU0
 * @since 2024-11-23
 */
@Slf4j
@RestController
@Api(tags = "数据字典")
@RequestMapping("/api/core/dict")
public class DictController {

    @Resource
    private DictService dictService;

    @ApiOperation("根据字典数据类型编码获取下级结点")
    @GetMapping("/findByDictCode/{dictCode}")
    public R findByDictCode(
            @ApiParam(value = "字典数据类型编码" ,required = true)
            @PathVariable String dictCode) {
        List<Dict> list = dictService.findByDictCode(dictCode);
        return R.ok().data("list", list);
    }

}

