package com.sixsixsix516.seckill.controller;

import com.sixsixsix516.seckill.model.OrderSecKill;
import com.sixsixsix516.seckill.service.SecKillService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author SUN
 * @date 2022/5/3
 */
@RequiredArgsConstructor
@RestController
public class SecKillController {

    /**
     * 暴露秒杀
     */
    @GetMapping("/expose")
    public String expose(Long commodityId) {
        return secKillService.expose(commodityId);
    }

    /**
     * 获取秒杀令牌
     */
    @GetMapping("/getToken")
    public String getToken(String exposeKey, Long userId) {
        return secKillService.getToken(exposeKey, userId);
    }

    /**
     * 秒杀
     */
    @GetMapping("/secKill")
    public String secKill(String exposeKey, Long commodityId, Long userId, String token) {
        return secKillService.secKill(exposeKey, commodityId, userId, token);
    }

    /**
     * 获取秒杀结果
     */
    @GetMapping("/getSecKillResult")
    public List<OrderSecKill> getSecKillResult(Long userId, Long commodityId) {
        return secKillService.getSecKillResult(userId, commodityId);
    }



    private final SecKillService secKillService;

}
