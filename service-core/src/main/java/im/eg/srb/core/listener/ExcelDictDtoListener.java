package im.eg.srb.core.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import im.eg.srb.core.pojo.dto.ExcelDictDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExcelDictDtoListener extends AnalysisEventListener<ExcelDictDTO> {
    @Override
    public void invoke(ExcelDictDTO data, AnalysisContext context) {
        log.info("解析到一條數據: {}", data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("所有數據處理完成！");
    }
}
