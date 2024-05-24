package com.tsl.model.employee;

import com.tsl.enums.TypeOfAccounting;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "accountants")
public class Accountant extends User{
    @Enumerated(EnumType.STRING)
    private TypeOfAccounting typeOfAccounting;
}
