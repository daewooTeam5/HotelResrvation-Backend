package daewoo.team5.hotelreservation.domain.place.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "places")   // 실제 DB 테이블명
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Places {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;   // 숙소 아이디

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;  // 숙소 주인 ID

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private PlaceCategory category;

    @Column(name = "name", length = 100, nullable = false)
    private String name;  // 호텔 이름

    @Column(columnDefinition = "TEXT")
    private String description; // 설명

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Status status;  // 상태 (pending, approved, rejected, inactive)

    @Column(name = "is_public")
    private Boolean isPublic; // 공개 여부

    @Column(name = "avg_rating", precision = 3, scale = 2)
    private BigDecimal avgRating;  // 평균 별점

    @Column(name = "review_count")
    private Integer reviewCount;   // 리뷰 수

    @Column(name = "min_price", precision = 10, scale = 2)
    private BigDecimal minPrice;   // 최소 가격

    @Column(name = "created_at")
    private LocalDateTime createdAt; // 생성일시

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 수정일시

    @Column(name = "check_in")
    private LocalTime checkIn;   // 체크인 시간

    @Column(name = "check_out")
    private LocalTime checkOut;  // 체크아웃 시간

    public enum Status {
        PENDING,
        APPROVED,
        REJECTED,
        INACTIVE
    }
}
