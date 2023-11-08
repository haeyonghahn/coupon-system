package com.example.api.service;

import org.springframework.stereotype.Service;

import com.example.api.producer.CouponCreateProducer;
import com.example.api.repository.AppliedUserRepository;
import com.example.api.repository.CouponCountRepository;
import com.example.api.repository.CouponRepository;

@Service
public class ApplyService {

	private final CouponRepository couponRepository;
	private final CouponCountRepository couponCountRepository;
	private final CouponCreateProducer couponCreateProducer;
	private final AppliedUserRepository appliedUserRepository;

	public ApplyService(CouponRepository couponRepository, CouponCountRepository couponCountRepository,
		CouponCreateProducer couponCreateProducer, AppliedUserRepository appliedUserRepository) {
		this.couponRepository = couponRepository;
		this.couponCountRepository = couponCountRepository;
		this.couponCreateProducer = couponCreateProducer;
		this.appliedUserRepository = appliedUserRepository;
	}

	public void apply(Long userId) {
		Long apply = appliedUserRepository.add(userId);

		// 추가된 개수가 1이 아니라면 이 유저는 이미 발급 요청을 했던 유저
		if (apply != 1) {
			return;
		}

		// long count = couponRepository.count();
		Long count = couponCountRepository.increment();

		if (count > 100) {
			return;
		}

		// couponRepository.save(new Coupon(userId));
		couponCreateProducer.create(userId);
	}
}
