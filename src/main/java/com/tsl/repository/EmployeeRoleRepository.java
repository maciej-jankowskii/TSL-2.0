package com.tsl.repository;

import com.tsl.model.role.EmployeeRole;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface EmployeeRoleRepository extends CrudRepository<EmployeeRole, Long> {
    Optional<EmployeeRole> findByName(String name);
}
