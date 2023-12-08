package com.tsl.model.employee;

import com.tsl.enums.TypeOfAccounting;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Accountant extends User{
    @Enumerated(EnumType.STRING)
    private TypeOfAccounting typeOfRole;
}
