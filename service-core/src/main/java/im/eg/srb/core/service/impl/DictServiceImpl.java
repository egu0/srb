package im.eg.srb.core.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import im.eg.srb.core.listener.ExcelDictDtoListener;
import im.eg.srb.core.mapper.DictMapper;
import im.eg.srb.core.pojo.dto.ExcelDictDTO;
import im.eg.srb.core.pojo.entity.Dict;
import im.eg.srb.core.service.DictService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Transactional(rollbackFor = Exception.class) // 出現異常時會滾
    @Override
    public void importData(InputStream ins) {
        EasyExcel.read(ins, ExcelDictDTO.class, new ExcelDictDtoListener(baseMapper)).sheet().doRead();
        log.info("導入成功！");
    }

    @Override
    public List<ExcelDictDTO> listDictData() {
        List<Dict> dictList = baseMapper.selectList(null);
        List<ExcelDictDTO> result = new ArrayList<>(dictList.size());
        for (Dict dict : dictList) {
            ExcelDictDTO dto = new ExcelDictDTO();
            BeanUtils.copyProperties(dict, dto);
            result.add(dto);
        }
        return result;
    }

    @Override
    public List<Dict> listByParentId(Long parentId) {
        String cacheKey = "srb:core:dict_list:" + parentId;
        try {
            // 1.查詢緩存
            Object cached = redisTemplate.opsForValue().get(cacheKey);

            // 2.如果命中緩存，那麼直接返回
            if (cached instanceof List) {
                return (List<Dict>) cached;
            }
        } catch (Exception ex) {
            log.error("查詢字典列表緩存失敗", ex);
        }

        // 3.若不存在，則把從數據庫中查到的數據放入緩存
        // 查詢數據庫
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", parentId);
        List<Dict> dictList = baseMapper.selectList(queryWrapper);
        // 填充 hasChildren 屬性
        dictList.forEach(dict -> {
            // 判斷當前節點是否有子節點
            dict.setHasChildren(hasChildren(dict.getId()));
        });

        try {
            redisTemplate.opsForValue().set(cacheKey, dictList, 5, TimeUnit.MINUTES);
        } catch (Exception ex) {
            log.error("保存字典列表緩存失敗", ex);
        }

        return dictList;
    }

    @Override
    public List<Dict> findByDictCode(String dictCode) {
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dict_code", dictCode);
        Dict dict = baseMapper.selectOne(queryWrapper);
        if (dict == null) {
            return Collections.emptyList();
        } else {
            return listByParentId(dict.getId());
        }
    }

    /**
     * 判斷當前節點是否有子節點
     *
     * @param id 當前節點 id
     */
    private boolean hasChildren(Long id) {
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", id);
        Integer count = baseMapper.selectCount(queryWrapper);
        return count > 0;
    }
}
