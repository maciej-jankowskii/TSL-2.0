package com.tsl.model.invoice;

import com.tsl.model.cargo.Cargo;
import com.tsl.model.contractor.Customer;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "customer_invoices")
public class CustomerInvoice extends Invoice {
    @OneToOne
    @JoinColumn(name = "cargo_id")
    private Cargo cargo;
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

}
