package im.eg.srb.core.pojo.vo;

import im.eg.srb.core.pojo.entity.BorrowerAttach;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "借款人认证信息")
public class BorrowerVO {
    @ApiModelProperty(value = "性别（1 男；0 女）")
    private Integer sex;

    @ApiModelProperty(value = "年龄")
    private Integer age;

    @ApiModelProperty(value = "学历")
    private Integer education;

    @ApiModelProperty(value = "是否结婚（1 是；0 否）")
    private Boolean marry;

    @ApiModelProperty(value = "行业")
    private Integer industry;

    @ApiModelProperty(value = "月收入")
    private Integer income;

    @ApiModelProperty(value = "还款来源")
    private Integer returnSource;

    @ApiModelProperty(value = "联系人姓名")
    private String contactsName;

    @ApiModelProperty(value = "联系人手机号")
    private String contactsMobile;

    @ApiModelProperty(value = "联系人关系")
    private Integer contactsRelation;

    @ApiModelProperty(value = "借款人附件资料")
    private List<BorrowerAttach> borrowerAttachList;
}
