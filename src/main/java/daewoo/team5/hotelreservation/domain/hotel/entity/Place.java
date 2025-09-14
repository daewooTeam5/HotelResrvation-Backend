package daewoo.team5.hotelreservation.domain.hotel.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "place")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long ownerId;

    @Column(nullable = false)
    private Long categoryId;

    @Column(nullable = false, length = 100)
    private String name; // 호텔 이름

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('pending', 'approved', 'rejected', 'inactive') DEFAULT 'pending'")
    private Status status;

    @Column(nullable = false)
    private Boolean isPublic;

    @Column(precision = 3, scale = 2)
    private BigDecimal avgRating;

    @Column(nullable = false)
    private Integer reviewCount;

    @Column(nullable = false)
    private BigDecimal minPrice;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private String checkIn;

    @Column
    private String checkOut;

    public enum Status {
        pending,
        approved,
        rejected,
        inactive
    }
}
