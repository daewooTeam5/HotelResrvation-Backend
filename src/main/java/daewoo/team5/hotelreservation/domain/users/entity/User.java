package daewoo.team5.hotelreservation.domain.users.entity;

import daewoo.team5.hotelreservation.global.core.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Integer userId;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('customer', 'hotel_owner', 'admin') DEFAULT 'customer'")
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('active', 'inactive', 'banned') DEFAULT 'active'")
    private Status status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public enum Role {
        customer,
        hotel_owner,
        admin
    }

    public enum Status {
        active,
        inactive,
        banned
    }
}

