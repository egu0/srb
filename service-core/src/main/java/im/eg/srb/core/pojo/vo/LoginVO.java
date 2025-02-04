package im.eg.srb.core.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "登錄對象")
public class LoginVO {
    @ApiModelProperty(value = "用戶類型")
    private Integer userType;

    @ApiModelProperty(value = "手機號")
    private String mobile;

    @ApiModelProperty(value = "密碼")
    private String password;
}
