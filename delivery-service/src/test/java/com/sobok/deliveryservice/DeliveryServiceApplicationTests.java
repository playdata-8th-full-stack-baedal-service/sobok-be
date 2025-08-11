package com.sobok.deliveryservice;

import com.sobok.deliveryservice.delivery.entity.Rider;
import com.sobok.deliveryservice.delivery.repository.RiderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DeliveryServiceApplicationTests {

	@Autowired
	private RiderRepository riderRepository;

	@Test
	void contextLoads() {
	}

	@Test
	void insertTestRiders() {
		for (long i = 4; i <= 103; i++) {
			if (riderRepository.existsById(i)) continue; // 중복 방지

			Rider rider = Rider.builder()
					.authId(100 + i)
					.name("테스트라이더" + i)
					.phone(String.format("010-0000-%04d", i))
					.permissionNumber("PERM-" + String.format("%04d", i))
					.build();

			riderRepository.save(rider);
		}
		System.out.println("라이더 100명 테스트 데이터 삽입 완료");
	}
}
