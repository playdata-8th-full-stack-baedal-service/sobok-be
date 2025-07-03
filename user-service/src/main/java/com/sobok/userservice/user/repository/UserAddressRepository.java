package com.sobok.userservice.user.repository;

import com.sobok.userservice.user.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {
    List<UserAddress> getUserAddressByUserId(Long userId);
}
