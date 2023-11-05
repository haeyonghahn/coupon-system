package com.example.api.service;

import static org.assertj.core.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.api.repository.CouponRepository;

@SpringBootTest
public class ApplyServiceTest {

	@Autowired
	private ApplyService applyService;

	@Autowired
	private CouponRepository couponRepository;

	@Test
	public void 한번만응모() {
		applyService.apply(1L);

		long count = couponRepository.count();

		assertThat(count).isEqualTo(1);
	}

	@Test
	public void 여러명응모() throws InterruptedException {
		int threadCount = 1000; // 1000개의 요청을 보낸다.
		/**
		 * 멀티스레드 이용
		 * ExecutorService는 병렬 작업을 간단하게 할 수 있게 도와주는
		 * JAVA API 이다.
		 */
		ExecutorService executorService = Executors.newFixedThreadPool(32);
		/**
		 * CountDownLatch는 다른 Thread에서 수행하는 작업을 기다리도록 도와주는
		 * 클래스이다.
		 */
		CountDownLatch latch = new CountDownLatch(threadCount);

		for (int i = 0; i < threadCount; i++) {
			long userId = i;
			executorService.submit(() -> {
				try {
					applyService.apply(userId);
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();

		long count = couponRepository.count();

		assertThat(count).isEqualTo(100);
	}
}
