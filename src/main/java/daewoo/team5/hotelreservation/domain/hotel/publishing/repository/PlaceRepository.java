package daewoo.team5.hotelreservation.domain.hotel.publishing.repository;

import daewoo.team5.hotelreservation.domain.hotel.publishing.entity.Place;
import daewoo.team5.hotelreservation.domain.hotel.publishing.projection.TestProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {
    //JPA레파스토리 불러오면 기본적인 findAll같은 함수 다 끌어다 쓰기 가능

    /*쿼리에서는 내가 했던 publishing에 대응되는 p.hotelname을 가져와야 되므로 이렇게 치고 as name으로 하면
    a.인 Address의 sido(city)를 가져와서 publishing을 p로 객체를 주어 줄임말처럼 만들어주고
    join으로 Room에 추가시켜준다. Join (추가시키려는 엔티티) (줄인 객체) on (줄인 객체).address.addressId = publishing.publishingId로 매핑


    sql 문 사용
     */
    @Query("select p.hotelName as name, a.city as sido " +
            "from Place p join p.addresses a")
    List<TestProjection> getPublishings();
     //리스트 형식으로 패키지(projection)의 프로젝션인 인터페이스 1개 생성 후
    // getPulishings() 함수 사용으로 Publishing이란 엔티티 객체를 자체 사용해서 추가(퍼블리싱)한다는 의미
    /* publishing p join Room a on p.publishingId = a.publishing.publishingId인 맨 마지막은
    퍼블리싱은 p로 객체를 주고 join으로 room에 추가할건데 조건을 준다라는 뜻으로
    publishing의 publishing_id랑 a인 address에서 참조하고 있는 publishing의 publishing_id가 같으면 실행한다~라는 뜻
    
     */
}
