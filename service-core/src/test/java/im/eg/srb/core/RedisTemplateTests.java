package im.eg.srb.core;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import im.eg.srb.core.mapper.DictMapper;
import im.eg.srb.core.pojo.entity.Dict;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class RedisTemplateTests {
    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private DictMapper dictMapper;

    @Test
    public void saveDict() {
        Dict dict = dictMapper.selectById(1);
        redisTemplate.opsForValue().set("dict", dict, 5, TimeUnit.MINUTES);
    }

    @Test
    public void readDict() {
        Object value = redisTemplate.opsForValue().get("dict");
        System.out.println(value);
    }

    @Test
    public void saveDictArr() {
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", 1);
        List<Dict> dictList = dictMapper.selectList(queryWrapper);
        redisTemplate.opsForValue().set("dict-arr", dictList, 5, TimeUnit.MINUTES);
    }

    @Test
    public void readDictArr() {
        Object value = redisTemplate.opsForValue().get("dict-arr");
        System.out.println(value);
    }
}
