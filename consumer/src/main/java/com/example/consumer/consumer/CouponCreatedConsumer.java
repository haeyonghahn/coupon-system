package com.example.consumer.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.example.consumer.domain.Coupon;
import com.example.consumer.repository.CouponRepository;

@Component
public class CouponCreatedConsumer {

	private final CouponRepository couponRepository;

	public CouponCreatedConsumer(CouponRepository couponRepository) {
		this.couponRepository = couponRepository;
	}

	@KafkaListener(topics = "coupon_create", groupId = "group_1")
	public void listener(Long userId) {
		couponRepository.save(new Coupon(userId));
	}
}
