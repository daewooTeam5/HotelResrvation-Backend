package daewoo.team5.hotelreservation.domain.question.entity;

import daewoo.team5.hotelreservation.domain.place.entity.Places;
import daewoo.team5.hotelreservation.domain.users.entity.Users;
import daewoo.team5.hotelreservation.global.core.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "questions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Question extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Places place; // 문의가 달린 숙소

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user; // 문의를 작성한 사용자

    @Column(nullable = false, length = 200)
    private String title; // 문의 제목

    @Lob
    private String content; // 문의 내용

    @Lob
    private String answer; // 호텔 관리자의 답변

    @Builder
    public Question(Places place, Users user, String title, String content) {
        this.place = place;
        this.user = user;
        this.title = title;
        this.content = content;
    }

    //== 연관관계 메서드 ==//
    public void setAnswer(String answer) {
        this.answer = answer;
    }
}