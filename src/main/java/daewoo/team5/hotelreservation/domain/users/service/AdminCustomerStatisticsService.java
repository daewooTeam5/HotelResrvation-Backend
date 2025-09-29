package daewoo.team5.hotelreservation.domain.users.service;

import daewoo.team5.hotelreservation.domain.place.repository.PaymentRepository;
import daewoo.team5.hotelreservation.domain.place.repository.ReservationRepository;
import daewoo.team5.hotelreservation.domain.users.dto.request.CustomerStatisticsDto;
import daewoo.team5.hotelreservation.domain.users.dto.request.MonthlyCountDto;
import daewoo.team5.hotelreservation.domain.users.dto.request.RetentionDto;
import daewoo.team5.hotelreservation.domain.users.dto.request.TopCustomerDto;
import daewoo.team5.hotelreservation.domain.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminCustomerStatisticsService {

    private final UsersRepository usersRepository;
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;

    public CustomerStatisticsDto getStatistics() {
        long totalUsers = usersRepository.count();

        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        long newUsersThisMonth = usersRepository.countNewUsers(startOfMonth);

        long activeUsers = reservationRepository.countActiveUsers();

        long repeatUsers = paymentRepository.countRepeatUsers();
        double repeatRate = (totalUsers > 0) ? (repeatUsers * 100.0 / totalUsers) : 0;

        // Top customers
        List<TopCustomerDto> topReservations = reservationRepository.findTopCustomersByReservations()
                .stream().map(o -> new TopCustomerDto((Long) o[0], (Long) o[1])).toList();

        List<TopCustomerDto> topPayments = paymentRepository.findTopCustomersByPayments()
                .stream().map(o -> new TopCustomerDto((Long) o[0], (Long) o[1])).toList();

        double avgReservations = reservationRepository.findAvgReservationsPerCustomer();
        double avgPayments = paymentRepository.findAvgPaymentsPerCustomer();

        // 세분화
        List<Long> vipCustomers = topPayments.stream()
                .limit(Math.max(1, (int) (totalUsers * 0.1)))
                .map(TopCustomerDto::getUserId)
                .collect(Collectors.toList());

        List<Long> newCustomers = usersRepository.findNewUsers(LocalDateTime.now().minusDays(30));
        List<Long> dormantCustomers = usersRepository.findDormantUsers(
                LocalDate.now().minusMonths(6)
        );

        // 트렌드
        List<MonthlyCountDto> monthlyNewUsers = usersRepository.countMonthlyNewUsers()
                .stream().map(o -> new MonthlyCountDto((String) o[0], (Long) o[1])).toList();

        // 유지율은 계산 복잡 → 여기서는 더미 값
        List<RetentionDto> monthlyRetention = List.of(
                new RetentionDto("2025-07", 65.0),
                new RetentionDto("2025-08", 70.0),
                new RetentionDto("2025-09", 68.0)
        );

        return new CustomerStatisticsDto(
                totalUsers,
                newUsersThisMonth,
                activeUsers,
                repeatRate,
                topReservations,
                topPayments,
                avgReservations,
                avgPayments,
                vipCustomers,
                newCustomers,
                dormantCustomers,
                monthlyNewUsers,
                monthlyRetention
        );
    }

    public List<MonthlyCountDto> getUserStats(String type) {
        List<Object[]> results;
        switch (type) {
            case "daily" -> results = usersRepository.countDailyNewUsers();
            case "yearly" -> results = usersRepository.countYearlyNewUsers();
            default -> results = usersRepository.countMonthlyNewUsers();
        }
        return results.stream()
                .map(o -> new MonthlyCountDto((String) o[0], (Long) o[1]))
                .toList();
    }

    public List<MonthlyCountDto> getReservationStats(String type) {
        List<Object[]> results;
        switch (type) {
            case "daily" -> results = reservationRepository.countDailyReservations();
            case "yearly" -> results = reservationRepository.countYearlyReservations();
            default -> results = reservationRepository.countMonthlyReservations();
        }
        return results.stream()
                .map(o -> new MonthlyCountDto((String) o[0], (Long) o[1]))
                .toList();
    }

    public List<MonthlyCountDto> getPaymentStats(String type) {
        List<Object[]> results;
        switch (type) {
            case "daily" -> results = paymentRepository.sumDailyPayments();
            case "yearly" -> results = paymentRepository.sumYearlyPayments();
            default -> results = paymentRepository.sumMonthlyPayments();
        }
        return results.stream()
                .map(o -> new MonthlyCountDto((String) o[0], ((Number) o[1]).longValue()))
                .toList();
    }

}