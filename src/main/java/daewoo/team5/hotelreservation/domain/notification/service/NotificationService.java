package daewoo.team5.hotelreservation.domain.notification.service;

import daewoo.team5.hotelreservation.domain.notification.entity.NotificationEntity;
import daewoo.team5.hotelreservation.domain.notification.entity.NotificationReadEntity;
import daewoo.team5.hotelreservation.domain.notification.repository.NotificationReadRepository;
import daewoo.team5.hotelreservation.domain.notification.repository.NotificationRepository;
import daewoo.team5.hotelreservation.domain.users.repository.UsersRepository;
import daewoo.team5.hotelreservation.global.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationReadRepository notificationReadRepository;
    private final UsersRepository usersRepository;

    public Page<NotificationEntity> getMyNotification(Long userId, int page) {
        return notificationRepository.findByUserIdOrUserIdIsNull(userId, PageRequest.of(page, 10));
    }

    public Long countByUserIdAndIsReadFalse(Long userId) {
        Optional<NotificationReadEntity> userNotiRead = notificationReadRepository.findByUserId(userId);
        if (userNotiRead.isEmpty()) {

            return notificationRepository.countByUserId(userId);
        }

        return notificationReadRepository.findByUserIdReadCountWithNow(userId);
    }

    public void readMyNotification(Long userId) {
        notificationReadRepository.findByUserId(userId).ifPresentOrElse(
                notiRead -> {
                    notiRead.setReadAt(LocalDateTime.now());
                    notificationReadRepository.save(notiRead);
                },
                () -> {
                    NotificationReadEntity newNotiRead = NotificationReadEntity.builder()
                            .user(usersRepository.findById(userId).orElseThrow(UserNotFoundException::new))
                            .readAt(LocalDateTime.now())
                            .build();
                    notificationReadRepository.save(newNotiRead);
                }
        );

    }
}
