package im.eg.srb.core.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import im.eg.srb.core.listener.ExcelDictDtoListener;
import im.eg.srb.core.mapper.DictMapper;
import im.eg.srb.core.pojo.dto.ExcelDictDTO;
import im.eg.srb.core.pojo.entity.Dict;
import im.eg.srb.core.service.DictService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;

/**
 * <p>
 * 数据字典 服务实现类
 * </p>
 *
 * @author EGU0
 * @since 2024-11-23
 */
@Slf4j
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    @Transactional(rollbackFor = Exception.class) // 出現異常時會滾
    @Override
    public void importData(InputStream ins) {
        EasyExcel.read(ins, ExcelDictDTO.class, new ExcelDictDtoListener()).sheet().doRead();
        log.info("導入成功！");
    }
}
