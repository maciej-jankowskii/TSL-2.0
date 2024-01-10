package com.tsl.repository.contactAndAddress;

import com.tsl.model.address.Address;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends CrudRepository<Address, Long> {
    Optional<Address> findById(Long id);
    List<Address> findAll();

}
