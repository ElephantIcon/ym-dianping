package com.hmdp.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.hmdp.dto.Result;
import com.hmdp.entity.Shop;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static com.hmdp.utils.RedisConstants.CACHE_SHOP_KEY;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result queryShopType() {
        // 1 查缓存
        String key = "cache:shopType";
        String shopJson = stringRedisTemplate.opsForValue().get(key);
        // 2 判断存不存在
        if (StrUtil.isNotBlank(shopJson)) {
            // 3 存在，直接返回
            List<ShopType> shopType = JSONUtil.toList(shopJson, ShopType.class);
            return Result.ok(shopType);
        }

        // 4 不存在，查数据库
        List<ShopType> shopType = query().orderByAsc("sort").list();
        if (shopType == null) {
            return Result.fail("店铺类型不存在！");
        }
        // 5 存在，写入redis
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(shopType));
        // 7 返回

        return Result.ok(shopType);
    }
}
