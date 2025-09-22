package daewoo.team5.hotelreservation.redis;

import daewoo.team5.hotelreservation.global.core.common.ApiResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/autocomplete")
@RequiredArgsConstructor
public class AutoCompleteController {

    private final AutoCompleteFacade autoCompleteFacade;

    @GetMapping
    public ApiResult<AutoCompleteResponse> getAutoComplete(@RequestParam String keyword) {
        return ApiResult.ok(autoCompleteFacade.getSuggestions(keyword), "자동완성 성공");
    }
}