package im.eg.srb.core.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import im.eg.srb.core.mapper.DictMapper;
import im.eg.srb.core.pojo.dto.ExcelDictDTO;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ExcelDictDtoListener extends AnalysisEventListener<ExcelDictDTO> {

    List<ExcelDictDTO> list = new ArrayList<>();

    // 允許的最大數據條數。list 長度達到 MAX_COUNT 時進行一次批量插入
    private static final int MAX_COUNT = 5;

    private DictMapper dictMapper;

    public ExcelDictDtoListener() {
    }

    public ExcelDictDtoListener(DictMapper dictMapper) {
        this.dictMapper = dictMapper;
    }

    @Override
    public void invoke(ExcelDictDTO data, AnalysisContext context) {
        log.info("解析到一條數據: {}", data);
        list.add(data);
        if (list.size() >= MAX_COUNT) {
            saveData();
            list.clear();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        saveData();
        list.clear();
        log.info("所有數據處理完成！");
    }

    private void saveData() {
        if (!list.isEmpty()) {
            dictMapper.insertBatch(list);
            log.info("批量插入 {} 條字典數據", list.size());
        }
    }
}
