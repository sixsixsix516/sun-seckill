package com.sixsixsix516.seckill;

import com.sixsixsix516.seckill.controller.SecKillController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SecKillTests {

    @Autowired
    private SecKillController secKillController;

    /**
     * 秒杀的用户
     */
    Long userId = 1L;
    /**
     * 秒杀的商品
     */
    Long commodityId = 1L;

    String exposeKey = "6ff15818afde43eb8af488a6fb7a9698";
    String token = "d2bc14dd-50e8-464e-9b4a-7dec7e2e4398";

    /**
     * 暴露秒杀
     */
    @Test
    void testExposeSecKill() {
        String expose = secKillController.expose(commodityId);
        System.out.println("exposeKey: " + expose);
        this.exposeKey = expose;
    }

    /**
     * 获取秒杀令牌
     */
    @Test
    void testGetToken() {
        if (this.exposeKey == null) {
            testExposeSecKill();
        }

        String token = secKillController.getToken(this.exposeKey, userId);
        System.out.println("获取秒杀令牌结果：" + token);
        this.token = token;
/*

--返回值说明
--1 排队成功
--2 秒杀库存没有找到
--3 人数超过限制
--4 库存不足
--5 排队过了
--6 秒杀过了
-- -2 Lua 方法不存在


* */

    }


    /**
     * 开始秒杀
     */
    @Test
    void testSecKill() {
        if (this.exposeKey == null) {
            testExposeSecKill();
        }
        if (this.token == null) {
            testGetToken();
        }
        String res = secKillController.secKill(exposeKey, commodityId, userId, token);
        System.out.println("秒杀结果：" + res);
    }

}
