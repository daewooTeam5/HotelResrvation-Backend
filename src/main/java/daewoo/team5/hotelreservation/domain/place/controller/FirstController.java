package daewoo.team5.hotelreservation.domain.place.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Controller
@RequiredArgsConstructor
@Slf4j
public class FirstController {
    @GetMapping("/first")
    public String first(Model model){
        model.addAttribute("userName","홍길동");
        return "first";
    }

}
