package daewoo.team5.hotelreservation.domain.notification.entity;

import daewoo.team5.hotelreservation.domain.users.entity.Users;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "NotificationRead")
@Table(name = "notification_read")
public class NotificationReadEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Users user;

    @Column
    private LocalDateTime readAt;
}
