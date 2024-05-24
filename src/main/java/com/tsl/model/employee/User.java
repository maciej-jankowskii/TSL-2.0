package com.tsl.model.employee;

import com.tsl.enums.FormOfEmployment;
import com.tsl.model.role.EmployeeRole;
import com.tsl.model.address.Address;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "employees")
@Inheritance(strategy = InheritanceType.JOINED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String telephone;
    @OneToOne
    @JoinColumn(name = "address_id")
    private Address address;
    private BigDecimal basicGrossSalary;
    private LocalDate dateOfEmployment;
    @Enumerated(EnumType.STRING)
    private FormOfEmployment formOfEmployment;
    private LocalDate contractExpiryDate;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "employees_roles",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<EmployeeRole> roles = new ArrayList<>();
}
