package im.eg.srb.core.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "註冊對象")
public class RegisterVO {
    @ApiModelProperty(value = "用戶類型")
    private Integer userType;

    @ApiModelProperty(value = "手機號")
    private String mobile;

    @ApiModelProperty(value = "驗證碼")
    private String code;

    @ApiModelProperty(value = "密碼")
    private String password;
}
