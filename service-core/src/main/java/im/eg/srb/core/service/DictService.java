package im.eg.srb.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import im.eg.srb.core.pojo.dto.ExcelDictDTO;
import im.eg.srb.core.pojo.entity.Dict;

import java.io.InputStream;
import java.util.List;

/**
 * <p>
 * 数据字典 服务类
 * </p>
 *
 * @author EGU0
 * @since 2024-11-23
 */
public interface DictService extends IService<Dict> {

    /**
     * 導入數據
     */
    void importData(InputStream ins);

    List<ExcelDictDTO> listDictData();

    List<Dict> listByParentId(Long parentId);

    List<Dict> findByDictCode(String dictCode);

    /**
     * 查询字典项的名称，
     *
     * @param parentCode 父级别字典项的 dict_code 字段
     * @param dictValue  字典项值
     */
    String getDictName(String parentCode, Integer dictValue);
}
