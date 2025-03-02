package im.eg.srb.core.pojo.vo;

import im.eg.srb.core.pojo.entity.Lend;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "Lend 对象", description = "标的准备表")
public class LendVO extends Lend implements Serializable {

    // 扩展字段
    @ApiModelProperty(value = "其他参数")
    private Map<String, Object> param = new HashMap<>();
}
