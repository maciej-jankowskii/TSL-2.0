package com.tsl.model.contractor;

import com.tsl.model.address.Address;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "contractors")
@Inheritance(strategy = InheritanceType.JOINED)
public class Contractor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fullName;
    private String shortName;
    @OneToOne
    @JoinColumn(name = "address_id")
    private Address address;
    private String vatNumber;
    private String description;
    private Integer termOfPayment;
    private BigDecimal balance;
    @OneToMany(mappedBy = "contractor")
    private List<ContactPerson> contactPersons = new ArrayList<>();
}
