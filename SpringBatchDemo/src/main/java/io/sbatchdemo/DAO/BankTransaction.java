package io.sbatchdemo.DAO;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Entity
@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class BankTransaction {
    @Id
    private Long id;
    private Long accountId;
    private Date transactionDate;
    @Transient
    private String strTransactionDate; // Stored as string to evade date formatting issues
    private String transactionType;
    private Double amount;
}
