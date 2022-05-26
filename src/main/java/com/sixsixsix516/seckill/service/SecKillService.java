package com.sixsixsix516.seckill.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sixsixsix516.seckill.lock.RedisSegmentLock;
import com.sixsixsix516.seckill.mapper.CommodityMapper;
import com.sixsixsix516.seckill.mapper.CommoditySecKillMapper;
import com.sixsixsix516.seckill.mapper.CommoditySecKillSegmentStockMapper;
import com.sixsixsix516.seckill.mapper.OrderSecKillMapper;
import com.sixsixsix516.seckill.model.Commodity;
import com.sixsixsix516.seckill.model.CommoditySecKill;
import com.sixsixsix516.seckill.model.CommoditySecKillSegmentStock;
import com.sixsixsix516.seckill.model.OrderSecKill;
import com.sixsixsix516.seckill.utils.LuaUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author SUN
 * @date 2022/5/3
 */
@RequiredArgsConstructor
@Service
public class SecKillService {

    private final StringRedisTemplate redisTemplate;
    // 默认段数量
    private final int segmentDefault = 10;

    /**
     * TODO 保证幂等性
     * 暴漏秒杀 TODO 问题：何时开启？定时任务
     * 1. 准备好redis限流
     * 2. 创建分段库存数据（解决热点行性能问题）
     * 4. 创建秒杀key，解决提前蹲点问题
     */
    public String expose(Long commodityId) {
        Commodity commodity = commodityMapper.selectById(commodityId);
        if (commodity == null) {
            return "商品不存在";
        }

        // 保存商品秒杀
        String exposeKey = saveCommoditySecKill(commodityId);

        // 保存库存分段
        Integer stock = saveStockSegment(commodityId);

        // 开启当前商品的限流
        redisTemplate.execute(LuaUtil.getRateLimiterScript(), List.of(commodityId.toString()), "init", "10000000", "1000");

        // 将商品库存信息、商品key->id 放入缓存
        redisTemplate.opsForValue().set("seckill:stock:" + exposeKey, stock.toString());
        redisTemplate.opsForValue().set("seckill:exposeKey:" + exposeKey, commodityId.toString());

        return exposeKey;
    }

    /**
     * 保存库存分段信息
     *
     * @return 总库存
     */
    private Integer saveStockSegment(Long commodityId) {
        Commodity commodity = commodityMapper.selectById(commodityId);
        // 总库存
        Integer stock = commodity.getStock();


        Long count = commoditySeckillSegmentStockMapper.selectCount(new QueryWrapper<CommoditySecKillSegmentStock>().lambda().eq(CommoditySecKillSegmentStock::getCommodityId, commodityId));
        if (count > 0) {
            return stock;
        }


        // 平均库存
        int avgStock = stock / segmentDefault;

        List<CommoditySecKillSegmentStock> commoditySecKillSegmentStocks = IntStream.range(0, segmentDefault).mapToObj(index -> {
            CommoditySecKillSegmentStock commoditySeckillSegmentStock = new CommoditySecKillSegmentStock();
            commoditySeckillSegmentStock.setCommodityId(commodityId);
            commoditySeckillSegmentStock.setSegmentNo((byte) index);

            if (index == segmentDefault - 1) {
                // 如果是最后一个了，需要补全全部  总库存 - （总分段 - 1 * 库存）
                commoditySeckillSegmentStock.setTotalStock(stock - (avgStock * (segmentDefault - 1)));
            } else {
                commoditySeckillSegmentStock.setTotalStock(avgStock);
            }

            commoditySeckillSegmentStock.setStock(commoditySeckillSegmentStock.getTotalStock());
            return commoditySeckillSegmentStock;
        }).collect(Collectors.toList());


        commoditySeckillSegmentStockMapper.batchInsert(commoditySecKillSegmentStocks);
        return stock;
    }

    private String saveCommoditySecKill(Long commodityId) {
        CommoditySecKill commoditySecKill = new CommoditySecKill();
        commoditySecKill.setCommodityId(commodityId);

        CommoditySecKill selectOne = commoditySecKillMapper.selectOne(new QueryWrapper<>(commoditySecKill));
        if (selectOne == null) {
            String exposeKey = UUID.randomUUID().toString().replaceAll("-", "");

            commoditySecKill.setExposeKey(exposeKey);
            commoditySecKillMapper.insert(commoditySecKill);
            return exposeKey;
        } else {
            return selectOne.getExposeKey();
        }
    }


    /**
     * 获取秒杀资格，获取秒杀令牌
     */
    public String getToken(String exposeKey, Long userId) {
        String token = UUID.randomUUID().toString();
        Long res = redisTemplate.execute(LuaUtil.getSecKillScript(), List.of("setToken"),
                exposeKey,
                userId.toString(),
                token
        );
        if (res == -1) {
            return token;
        }

        return res + "";

    }


    /**
     * 秒杀
     */
    public String secKill(String exposeKey, Long commodityId, Long userId, String token) {
        Long res = redisTemplate.execute(LuaUtil.getSecKillScript(), List.of("checkToken"), exposeKey, userId.toString(), token);
        if (res != null && res != 5) {
            // 请提前排队
            return "请提前排队";
        }

        // 获取分布式锁
        String localKey = "seckill:segmentLock:" + commodityId;

        RedisSegmentLock lock = new RedisSegmentLock(localKey, UUID.randomUUID().toString(), segmentDefault);

        // 执行秒杀，获取锁
        boolean locked;
        try {
            locked = lock.tryLock(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            return "秒杀失败";
        }

        if (!locked) {
            return "秒杀失败";
        }

        // 开始秒杀
        try {
            return doSecKill(lock);
        } finally {
            lock.unlock();
        }

    }


    private String doSecKill(RedisSegmentLock lock) {
        int stock = commoditySeckillSegmentStockMapper.getStockByCommodityId(1L);
        if (stock <= 0) {
            return "库存不够";
        }
        doTransaction(lock);
        return "成功";
    }


    private void doTransaction(RedisSegmentLock lock) {
        // 创建订单
        OrderSecKill orderSecKill = new OrderSecKill();
        orderSecKill.setCommodityId(1L);
        orderSecKill.setUserId(1L);
        orderSecKillMapper.insert(orderSecKill);

        // 减少分段库存
        int segment = lock.getSegmentIndexLocked();

        // 减库存
        commoditySeckillSegmentStockMapper.decraseStock(orderSecKill.getCommodityId(), segment);
        commodityMapper.decreaseStockById(orderSecKill.getCommodityId());
    }


    /**
     * 获取秒杀结果
     */
    public List<OrderSecKill> getSecKillResult(Long userId, Long commodityId) {
        return orderSecKillMapper.selectList(new QueryWrapper<OrderSecKill>().lambda().eq(OrderSecKill::getCommodityId, commodityId).eq(OrderSecKill::getUserId, userId));
    }


    private final OrderSecKillMapper orderSecKillMapper;
    private final CommoditySecKillMapper commoditySecKillMapper;
    private final CommodityMapper commodityMapper;
    private final CommoditySecKillSegmentStockMapper commoditySeckillSegmentStockMapper;

}
