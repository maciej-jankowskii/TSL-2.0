package com.tsl.model.contractor;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "contact_persons")
public class ContactPerson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String telephone;
    @ManyToOne
    @JoinColumn(name = "contractor_id")
    private Contractor contractor;
}
