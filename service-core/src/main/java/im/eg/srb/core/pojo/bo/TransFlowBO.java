package im.eg.srb.core.pojo.bo;

import im.eg.srb.core.enums.TransTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransFlowBO {
    // 流水号（订单号）
    private String agentBillNo;

    // 用户的绑定协议号
    private String bindCode;

    private BigDecimal amount;

    private TransTypeEnum transTypeEnum;

    private String memo;
}
