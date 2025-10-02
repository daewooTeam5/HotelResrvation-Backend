package daewoo.team5.hotelreservation.domain.question.controller;

import daewoo.team5.hotelreservation.domain.question.dto.*;
import daewoo.team5.hotelreservation.domain.question.service.QuestionService;
import daewoo.team5.hotelreservation.domain.users.projection.UserProjection;
import daewoo.team5.hotelreservation.global.aop.annotation.AuthUser;
import daewoo.team5.hotelreservation.global.core.common.ApiResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    /**
     * 특정 숙소의 문의 목록 조회 (사용자 문의 우선 정렬)
     * @param placeId 숙소 ID
     * @param user 현재 로그인한 사용자 정보 (Optional)
     * @return 문의 목록
     */
    @GetMapping("/places/{placeId}/questions")
    @AuthUser // @AuthUser를 통해 로그인 사용자를 선택적으로 받음
    public ApiResult<List<QuestionResponse>> getQuestionsByPlace(@PathVariable Long placeId, UserProjection user) {
        return ApiResult.ok(questionService.getQuestionsByPlace(placeId, user));
    }

    // 문의 등록
    @PostMapping("/places/{placeId}/questions")
    @AuthUser
    public ApiResult<QuestionResponse> createQuestion(@PathVariable Long placeId,
                                                      @Valid @RequestBody CreateQuestionRequest request,
                                                      UserProjection user) {
        return ApiResult.created(questionService.createQuestion(placeId, request, user));
    }

    // 문의 답변 (숙소 주인)
    @PostMapping("/owner/questions/{questionId}/answer")
    @AuthUser
    public ApiResult<Void> addAnswer(@PathVariable Long questionId,
                                     @Valid @RequestBody CreateAnswerRequest request,
                                     UserProjection user) {
        questionService.addAnswer(questionId, request, user);
        return ApiResult.ok(null, "답변이 등록되었습니다.");
    }

    // 문의 삭제 (숙소 주인)
    @DeleteMapping("/owner/questions/{questionId}")
    @AuthUser
    public ApiResult<Void> deleteQuestion(@PathVariable Long questionId,
                                          UserProjection user) {
        questionService.deleteQuestion(questionId, user);
        return ApiResult.ok(null, "문의가 삭제되었습니다.");
    }

    // 관리자 문의 검색
    @PostMapping("/owner/places/{placeId}/questions/search") // 👈 엔드포인트 변경
    public ApiResult<List<QuestionResponse>> searchQuestions(
            @PathVariable Long placeId, // 👈 @PathVariable 추가
            @RequestBody QuestionSearchRequest request
    ) {
        return ApiResult.ok(questionService.searchQuestions(placeId, request));
    }

    @GetMapping("/questions/my-questions")
    public ResponseEntity<Page<MyQuestionResponse>> getMyQuestions(
            @AuthenticationPrincipal Long userId, // 또는 커스텀 UserDetails 사용
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<MyQuestionResponse> questions = questionService.getMyQuestions(userId, page, size);
        return ResponseEntity.ok(questions);
    }
}