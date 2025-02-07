package im.eg.srb.core.pojo.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "會員搜索對象")
public class UserInfoQuery {
    @ApiModelProperty(value = "手機號")
    private String mobile;

    @ApiModelProperty(value = "狀態")
    private Integer status;

    @ApiModelProperty(value = "用戶類型（1.出借人，2.借款人）")
    private Integer userType;
}
