package daewoo.team5.hotelreservation.domain.users.controller;

import daewoo.team5.hotelreservation.domain.users.dto.request.CustomerStatisticsDto;
import daewoo.team5.hotelreservation.domain.users.dto.request.MonthlyCountDto;
import daewoo.team5.hotelreservation.domain.users.service.AdminCustomerStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/statistics/customers")
@RequiredArgsConstructor
public class AdminCustomerStatisticsController {

    private final AdminCustomerStatisticsService customerStatisticsService;

    @GetMapping
    public CustomerStatisticsDto getCustomerStatistics() {
        return customerStatisticsService.getStatistics();
    }

    @GetMapping("/stats/users")
    public List<MonthlyCountDto> getUserStats(@RequestParam(defaultValue = "monthly") String type) {
        return customerStatisticsService.getUserStats(type);
    }

    @GetMapping("/stats/reservations")
    public List<MonthlyCountDto> getReservationStats(@RequestParam(defaultValue = "monthly") String type) {
        return customerStatisticsService.getReservationStats(type);
    }

    @GetMapping("/stats/payments")
    public List<MonthlyCountDto> getPaymentStats(@RequestParam(defaultValue = "monthly") String type) {
        return customerStatisticsService.getPaymentStats(type);
    }
}