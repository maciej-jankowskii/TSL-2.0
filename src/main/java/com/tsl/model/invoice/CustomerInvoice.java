package com.tsl.model.invoice;

import com.tsl.model.cargo.Cargo;
import com.tsl.model.contractor.Customer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class CustomerInvoice extends Invoice {
    @OneToOne
    @JoinColumn(name = "cargo_id")
    private Cargo cargo;
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

}
