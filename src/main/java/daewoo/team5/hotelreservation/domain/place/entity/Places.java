package daewoo.team5.hotelreservation.domain.place.entity;

import daewoo.team5.hotelreservation.domain.users.entity.Users;
import daewoo.team5.hotelreservation.global.core.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "places")   // 실제 DB 테이블명
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Places extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;   // 숙소 아이디

    @ManyToOne
    private Users owner;  // 숙소 주인 ID

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

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt; // 수정일시


    @Column(name = "check_in")
    private LocalTime checkIn;   // 체크인 시간

    @Column(name = "check_out")
    private LocalTime checkOut;// 체크아웃 시간

    @ManyToMany
    @JoinTable(
            name = "place_amenities",
            joinColumns = @JoinColumn(name = "place_id"),
            inverseJoinColumns = @JoinColumn(name = "amenity_id")
    )
    private List<Amenity> amenities;

    
   // 기본값을 true로 설정

    public void updateDetails(String name, String description, LocalTime checkIn, LocalTime checkOut, PlaceCategory category) {
        this.name = name;
        this.description = description;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.category = category;
    }

    public enum Status {
        PENDING,
        APPROVED,
        REJECTED,
        INACTIVE
    }
}
