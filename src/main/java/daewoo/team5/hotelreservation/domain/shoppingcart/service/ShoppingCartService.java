package daewoo.team5.hotelreservation.domain.shoppingcart.service;

import daewoo.team5.hotelreservation.domain.place.entity.Room;
import daewoo.team5.hotelreservation.domain.place.repository.RoomRepository;
import daewoo.team5.hotelreservation.domain.shoppingcart.entity.ShoppingCart;
import daewoo.team5.hotelreservation.domain.shoppingcart.projection.CartProjection;
import daewoo.team5.hotelreservation.domain.shoppingcart.repository.ShoppingCartRepository;
import daewoo.team5.hotelreservation.domain.users.entity.Users;
import daewoo.team5.hotelreservation.domain.users.repository.UsersRepository;
import daewoo.team5.hotelreservation.global.core.common.ApiResult;
import daewoo.team5.hotelreservation.global.exception.ApiException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final UsersRepository usersRepository;
    private final RoomRepository roomRepository;

    @Transactional
    public Boolean addToCart(Long roodId, Long userId, LocalDate startDate, LocalDate endDate, int quantity) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "존재하지 않는 유저", "유저가 존재하지 않습니다."));

        Room room = roomRepository.findById(roodId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "존재하지 않는 숙소", "숙소가 존재하지 않습니다."));

        Optional<ShoppingCart> existing = shoppingCartRepository
                .findByUser_IdAndRoom_IdAndStartDateAndEndDate(userId, roodId, startDate, endDate);

        if (existing.isPresent()) {
            ShoppingCart cart = existing.get();
            cart.setQuantity(cart.getQuantity() + quantity);
            shoppingCartRepository.save(cart);
        } else {
            ShoppingCart cart = ShoppingCart.builder()
                    .user(user)
                    .room(room)
                    .startDate(startDate)
                    .endDate(endDate)
                    .quantity(quantity)
                    .build();
            shoppingCartRepository.save(cart);
        }

        return true;
    }

    @Transactional
    public Boolean removeFromCart(Long roodId, Long userId, LocalDate startDate, LocalDate endDate) {
        if (shoppingCartRepository.findByUser_IdAndRoom_IdAndStartDateAndEndDate(userId, roodId, startDate, endDate).isEmpty()) {
            throw new ApiException(HttpStatus.NOT_FOUND, "장바구니 내역 없음", "해당 유저가 이 숙소를 장바구니에 담지 않았습니다.");
        }
        shoppingCartRepository.deleteByUser_IdAndRoom_IdAndStartDateAndEndDate(userId, roodId, startDate, endDate);
        return true;
    }

    public Integer getCartItemCount(Long userId) {
        return shoppingCartRepository.countByUser_Id(userId);
    }

    public List<CartProjection> getCartItems(Long userId) {
        return shoppingCartRepository.findCartItemsByUserId(userId);
    }
}
