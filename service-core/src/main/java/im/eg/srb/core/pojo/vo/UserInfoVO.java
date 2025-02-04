package im.eg.srb.core.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "用戶信息對象")
public class UserInfoVO {
    @ApiModelProperty(value = "用戶姓名")
    private String name;

    @ApiModelProperty(value = "用戶暱稱")
    private String nickName;

    @ApiModelProperty(value = "頭像")
    private String headImg;

    @ApiModelProperty(value = "手機號")
    private String mobile;

    @ApiModelProperty(value = "用戶類型（1 出借人，2 借款人")
    private Integer userType;

    @ApiModelProperty(value = "JWT 訪問令牌")
    private String token;
}
