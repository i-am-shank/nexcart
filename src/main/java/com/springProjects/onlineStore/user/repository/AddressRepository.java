package com.springProjects.onlineStore.user.repository;

import com.springProjects.onlineStore.user.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {
    List<Address> findByUser_UserIdAndDeletedFalse(Integer userId);

    Address findByAddressIdAndDeletedFalse(Integer addressId);

    Address findByAddressIdAndUser_UserIdAndDeletedFalse(Integer addressId, Integer userId);
}
