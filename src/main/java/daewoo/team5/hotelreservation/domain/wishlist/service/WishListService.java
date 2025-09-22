package daewoo.team5.hotelreservation.domain.wishlist.service;

import daewoo.team5.hotelreservation.domain.place.entity.Places;
import daewoo.team5.hotelreservation.domain.place.projection.PlaceItemInfomation;
import daewoo.team5.hotelreservation.domain.place.repository.PlaceRepository;
import daewoo.team5.hotelreservation.domain.users.entity.Users;
import daewoo.team5.hotelreservation.domain.users.repository.UsersRepository;
import daewoo.team5.hotelreservation.domain.wishlist.entity.WishList;
import daewoo.team5.hotelreservation.domain.wishlist.repository.WishListRepository;
import daewoo.team5.hotelreservation.global.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishListService {

    private final WishListRepository wishListRepository;
    private final PlaceRepository placeRepository;
    private final UsersRepository usersRepository;

    public Boolean addWishList(Long placeId, Long userId){
        Places place = placeRepository.findById(placeId).orElseThrow(() -> new ApiException(HttpStatus.MULTI_STATUS, "존재하지 않는 숙소", "숙소가 존재하지 않습니다."));
        Users user = usersRepository.findById(userId).orElseThrow(() -> new ApiException(HttpStatus.MULTI_STATUS, "존재하지 않는 유저", "유저가 존재하지 않습니다."));

        if (wishListRepository.existsByUserIdAndPlaceId(userId, placeId)) {
            throw new ApiException(HttpStatus.CONFLICT, "중복 찜 불가", "이미 찜한 숙소입니다.");
        }

        WishList wishList = WishList.builder()
                .place(place)
                .user(user)
                .build();

        WishList saved = wishListRepository.save(wishList);
        return saved.getId() != null;
    }

    @Transactional
    public Boolean removeWishList(Long placeId, Long userId){
        if (!wishListRepository.existsByUserIdAndPlaceId(userId, placeId)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "찜 내역 없음", "해당 유저가 이 숙소를 찜하지 않았습니다.");
        }
        wishListRepository.deleteByUserIdAndPlaceId(userId, placeId);
        return true;
    }

    @Transactional(readOnly = true)
    public Page<PlaceItemInfomation> getUserWishList(
            Long userId,
            String name,
            String checkIn,
            String checkOut,
            int people,
            int room,
            String placeCategory,
            Double minRating,
            Double minPrice,
            Double maxPrice,
            int start
    ) {
        Pageable pageable = PageRequest.of(start, 10, Sort.by("id").descending());
        return wishListRepository.findUserWishList(
                userId, name, checkIn, checkOut, people, room,
                placeCategory, minRating, minPrice, maxPrice, pageable
        );
    }
}
