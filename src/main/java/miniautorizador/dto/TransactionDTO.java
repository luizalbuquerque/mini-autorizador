package miniautorizador.dto;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {

        private Long id;

        private CardDTO cardDTO;

        private BigDecimal amount;

        @CreationTimestamp
        @Temporal( TemporalType.TIMESTAMP )
        private Date createdAt;

        @UpdateTimestamp
        @Temporal( TemporalType.TIMESTAMP )
        private Date updatedAt;
}
