package im.eg.srb.core.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(description = "借款信息类的扩展类")
public class BorrowInfoDetailVO {
    @ApiModelProperty(value = "编号")
    private Long id;

    @ApiModelProperty(value = "借款用户id")
    private Long userId;

    @ApiModelProperty(value = "借款金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "借款期限")
    private Integer period;

    @ApiModelProperty(value = "年化利率")
    private BigDecimal borrowYearRate;

    @ApiModelProperty(value = "还款方式 1-等额本息 2-等额本金 3-每月还息一次还本 4-一次还本")
    private Integer returnMethod;

    @ApiModelProperty(value = "资金用途")
    private Integer moneyUse;

    @ApiModelProperty(value = "状态（0：未提交，1：审核中， 2：审核通过， -1：审核不通过）")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    // 以上字段来自 BorrowInfo 类
    // 以下字段是基于业务需要拓展的结果

    @ApiModelProperty(value = "姓名")
    private String name;

    @ApiModelProperty(value = "手机")
    private String mobile;

    @ApiModelProperty(value = "其他参数")
    private Map<String, Object> param = new HashMap<>();

}
