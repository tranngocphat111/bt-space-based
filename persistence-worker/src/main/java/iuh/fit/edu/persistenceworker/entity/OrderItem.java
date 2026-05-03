package iuh.fit.edu.persistenceworker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Long orderId;

    @Column(nullable = false)
    private Integer productId;

    @Column(nullable = false)
    private Short  quantity = 1;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;
}
