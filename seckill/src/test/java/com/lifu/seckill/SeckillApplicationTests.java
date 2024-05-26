package com.lifu.seckill;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
class SeckillApplicationTests {

	@Autowired
	private RedisTemplate redisTemplate;

	@Test
	public void testLock01() {

	}

}
