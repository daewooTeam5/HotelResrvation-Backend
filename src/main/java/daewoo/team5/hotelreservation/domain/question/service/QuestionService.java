package daewoo.team5.hotelreservation.domain.question.service;

import daewoo.team5.hotelreservation.domain.place.entity.Places;
import daewoo.team5.hotelreservation.domain.place.repository.PlaceRepository;
import daewoo.team5.hotelreservation.domain.question.dto.CreateAnswerRequest;
import daewoo.team5.hotelreservation.domain.question.dto.CreateQuestionRequest;
import daewoo.team5.hotelreservation.domain.question.dto.QuestionResponse;
import daewoo.team5.hotelreservation.domain.question.entity.Question;
import daewoo.team5.hotelreservation.domain.question.repository.QuestionRepository;
import daewoo.team5.hotelreservation.domain.users.entity.Users;
import daewoo.team5.hotelreservation.domain.users.projection.UserProjection;
import daewoo.team5.hotelreservation.domain.users.repository.UsersRepository;
import daewoo.team5.hotelreservation.global.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import daewoo.team5.hotelreservation.domain.question.dto.QuestionSearchRequest;
import daewoo.team5.hotelreservation.domain.question.specification.QuestionSpecification;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final PlaceRepository placeRepository;
    private final UsersRepository usersRepository;

    // 문의 등록
    public QuestionResponse createQuestion(Long placeId, CreateQuestionRequest request, UserProjection userProjection) {
        Users user = usersRepository.findById(userProjection.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.", "존재하지 않는 사용자입니다."));

        Places place = placeRepository.findById(placeId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "숙소를 찾을 수 없습니다.", "존재하지 않는 숙소입니다."));

        Question question = Question.builder()
                .place(place)
                .user(user)
                .title(request.getTitle())
                .content(request.getContent())
                .build();

        questionRepository.save(question);
        return new QuestionResponse(question);
    }

    // 숙소별 문의 목록 조회
    @Transactional(readOnly = true)
    public List<QuestionResponse> getQuestionsByPlace(Long placeId) {
        return questionRepository.findByPlaceId(placeId).stream()
                .map(QuestionResponse::new)
                .collect(Collectors.toList());
    }

    // 문의 답변 등록 (숙소 주인)
    public void addAnswer(Long questionId, CreateAnswerRequest request, UserProjection userProjection) {
        Users owner = usersRepository.findById(userProjection.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.", "존재하지 않는 사용자입니다."));

        Question question = questionRepository.findByIdWithOwner(questionId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "문의를 찾을 수 없습니다.", "존재하지 않는 문의입니다."));

        if (!question.getPlace().getOwner().getId().equals(owner.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "권한 없음", "숙소 관리자만 답변을 작성할 수 있습니다.");
        }

        question.setAnswer(request.getAnswer());
    }
    // 문의 삭제 (숙소 주인)
    public void deleteQuestion(Long questionId, UserProjection userProjection) {
        Users owner = usersRepository.findById(userProjection.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.", "존재하지 않는 사용자입니다."));

        Question question = questionRepository.findByIdWithOwner(questionId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "문의를 찾을 수 없습니다.", "존재하지 않는 문의입니다."));

        if (!question.getPlace().getOwner().getId().equals(owner.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "권한 없음", "숙소 관리자만 문의를 삭제할 수 있습니다.");
        }

        questionRepository.delete(question);
    }
    @Transactional(readOnly = true)
    public List<QuestionResponse> getQuestionsByPlace(Long placeId, UserProjection userProjection) {
        if (userProjection != null) {
            return questionRepository.findByPlaceIdOrderByUserAndCreatedAtDesc(placeId, userProjection.getId()).stream()
                    .map(QuestionResponse::new)
                    .collect(Collectors.toList());
        }
        return questionRepository.findByPlaceId(placeId).stream()
                .map(QuestionResponse::new)
                .collect(Collectors.toList());
    }
    // 관리자 문의 검색
    @Transactional(readOnly = true)
    public List<QuestionResponse> searchQuestions(Long placeId, QuestionSearchRequest searchRequest) { // placeId 파라미터 추가
        // TODO: 로그인한 사용자가 placeId에 대한 소유권을 가지고 있는지 확인하는 로직 추가 권장
        Specification<Question> spec = QuestionSpecification.filterBy(placeId, searchRequest);
        return questionRepository.findAll(spec).stream()
                .map(QuestionResponse::new)
                .collect(Collectors.toList());
    }

}