package im.eg.srb.core.pojo.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class ExcelDictDTO {
    @ExcelProperty("編號")
    private Long id;

    @ExcelProperty("父級編號")
    private Long parentId;

    @ExcelProperty("名稱")
    private String name;

    @ExcelProperty("值")
    private Integer value;

    @ExcelProperty("編碼")
    private String dictCode;
}
