package com.springProjects.onlineStore.cart.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.springProjects.onlineStore.cart.dto.CartItemResponseDTO;
import com.springProjects.onlineStore.cart.mapper.CartItemMapper;
import com.springProjects.onlineStore.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springProjects.onlineStore.cart.dto.CartDetailsResponseDTO;
import com.springProjects.onlineStore.cart.dto.CartPaymentDetailsDTO;
import com.springProjects.onlineStore.cart.dto.CartResponseDTO;
import com.springProjects.onlineStore.cart.entity.Cart;
import com.springProjects.onlineStore.cart.entity.CartItem;
import com.springProjects.onlineStore.cart.mapper.CartMapper;
import com.springProjects.onlineStore.cart.repository.CartItemRepository;
import com.springProjects.onlineStore.cart.repository.CartRepository;
import com.springProjects.onlineStore.cart.service.CartService;
import com.springProjects.onlineStore.common.utils.CommonUtils;
import com.springProjects.onlineStore.exceptions.ResourceNotFoundException;
import com.springProjects.onlineStore.product.entity.Product;
import com.springProjects.onlineStore.user.entity.User;
import com.springProjects.onlineStore.user.repository.UserRepository;
import org.springframework.util.CollectionUtils;

@RequiredArgsConstructor
@Service
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;

    private final CartMapper cartMapper;

    private final CartItemMapper cartItemMapper;

    private final UserRepository userRepository;

    private final CartItemRepository cartItemRepository;

    private final ProductService productService;

    @Transactional
    @Override
    public CartDetailsResponseDTO createCartForUser(Integer userId) throws IllegalArgumentException,
            ResourceNotFoundException, UnsupportedOperationException {
        Cart cart = createCart(userId);
        return cartMapper.toDetailResponseDTO(cart);
    }

    @Transactional
    @Override
    public Cart createCart(Integer userId) throws IllegalArgumentException, ResourceNotFoundException,
            UnsupportedOperationException {
        if(userId == null){
            throw new IllegalArgumentException("userId cannot be null");
        }
        User user = userRepository.findByUserIdAndDeletedFalse(userId);
        if(user == null){
            throw new IllegalArgumentException("User not found with userId : " + userId);
        }
        // Validate if cart already exists
        Cart cart = cartRepository.findByUserIdAndDeletedFalse(userId);
        if(cart != null){
            throw new UnsupportedOperationException("Cart already exists for userId : " + userId);
        }
        // Create new cart
        // TODO : throw exception in parent methods
        cart = new Cart();
        cart.setUserId(userId);
        return cartRepository.save(cart);
    }

    @Override
    public CartResponseDTO getCart(Integer userId) throws IllegalArgumentException, ResourceNotFoundException {
        Cart cart = getCartByUserId(userId);
        return cartMapper.toResponseDTO(cart);
    }

    @Override
    public CartDetailsResponseDTO getCartDetails(Integer userId) throws IllegalArgumentException, ResourceNotFoundException {
        Cart cart = getCartByUserId(userId);
        return cartMapper.toDetailResponseDTO(cart);
    }

    @Override
    public CartPaymentDetailsDTO getCartPaymentDetails(Integer userId) throws IllegalArgumentException, ResourceNotFoundException {
        Cart cart = getCartByUserId(userId);
        CartPaymentDetailsDTO cartPaymentDetailsDTO = cartMapper.toPaymentDetailsDTO(cart);
        if(!CollectionUtils.isEmpty(cart.getCartItems())) {
            List<CartItemResponseDTO> cartItemResponseDTOS = cart.getCartItems().stream()
                    .map(cartItemMapper::toResponseDTO)
                    .toList();
            cartPaymentDetailsDTO.setCartItems(cartItemResponseDTOS);
        }
        return cartPaymentDetailsDTO;
    }

    private Cart getCartByUserId(Integer userId) throws IllegalArgumentException, ResourceNotFoundException {
        if(userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }
        Cart cart = cartRepository.findByUserIdAndDeletedFalse(userId);
        if(cart == null) {
            throw new ResourceNotFoundException("cart not found for userId : " + userId);
        }
        return cart;
    }

    @Transactional
    @Override
    public CartDetailsResponseDTO clearCart(Integer userId, Boolean restoreInventory)
            throws IllegalArgumentException, ResourceNotFoundException {
        Cart cart = getCartByUserId(userId);

        // Clear cartItems list  :  orphanRemoval = true , will delete all CartItem
        // Don't replace it with new ArrayList<>()
        //      Error  :  "A collection with orphan deletion was no longer referenced by the owning entity instance"
        //      ->  as Hibernate-managed persistent collection got replaced by a completely new Java list
//        cart.setCartItems(new ArrayList<>());

        // Also don't clear entire cart  ->  Saved For Later items should not be removed
//        cart.getCartItems().clear();

        // Removing only active CartItem
        List<CartItem> activeCartItems = cart.getCartItems().stream()
                .filter(cartItem -> Boolean.FALSE.equals(cartItem.getSaveForLater()))
                .toList();

        cart.getCartItems().removeAll(activeCartItems);

        // Disconnect Cart-mapping in all active CartItem  :  so hibernate doesn't persist them anyhow
        for (CartItem cartItem : activeCartItems) {
            cartItem.setCart(null);
        }

        if(Boolean.TRUE.equals(restoreInventory)) {
            // Restore inventory for active CartItems - to be removed
            Map<Integer, Integer> productIdRetainedQtyMap = new HashMap<>();
            for(CartItem cartItem : activeCartItems) {
                if(cartItem.getProduct() != null) {
                    Integer productId = cartItem.getProduct().getProductId();
                    Integer quantity = cartItem.getQuantity();
                    productIdRetainedQtyMap.put(productId, quantity);
                }
            }
            productService.retainProductQuantity(productIdRetainedQtyMap);
        }

        // This will reflect the deleted CartItem in Cart  -  will also update itemCount & cartAmount details
        cart = updateCartItemsAndPaymentDetails(cart.getCartId());
        return cartMapper.toDetailResponseDTO(cart);
    }

    private Cart getCartByCartId(Integer cartId) throws IllegalArgumentException, ResourceNotFoundException {
        if(cartId == null) {
            throw new IllegalArgumentException("cartId cannot be null");
        }
        Cart cart = cartRepository.findByCartIdAndDeletedFalse(cartId);
        if(cart == null) {
            throw new ResourceNotFoundException("cart not found for cartId : " + cartId);
        }
        return cart;
    }

    // TODO : Handle concurrency issue , multiple threads might compute details independently
    //          Can result in overriding the same Cart's payment details .. resulting in data inconsistency
    @Transactional
    @Override
    public Cart updateCartItemsAndPaymentDetails(Integer cartId) throws IllegalArgumentException, ResourceNotFoundException {
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalBilledAmount = BigDecimal.ZERO;
        int totalItems = 0;
        Cart cart = getCartByCartId(cartId);
        List<CartItem> cartItemList = cart.getCartItems();
        List<CartItem> activeCartItems = cartItemList.stream()
                .filter(cartItem -> !Boolean.TRUE.equals(cartItem))
                .toList();
        for(CartItem cartItem : activeCartItems) {
            if(Boolean.FALSE.equals(cartItem.getSaveForLater())) {
                Product product = cartItem.getProduct();
                Integer quantity = CommonUtils.getValueOrZero(cartItem.getQuantity());
                if(product != null) {
                    // aggregating MRP
                    BigDecimal cartItemUnitPrice = CommonUtils.getPrecisionFixedValueOrZero(product.getPrice());
                    BigDecimal cartItemTotalPrice = cartItemUnitPrice.multiply(new BigDecimal(quantity));
                    totalAmount = totalAmount.add(cartItemTotalPrice);
                    // aggregating discounted-price
                    BigDecimal cartItemDiscountedPrice = CommonUtils.getDiscountedAmount(cartItemTotalPrice,
                            product.getDiscountPercentage());
                    totalBilledAmount = totalBilledAmount.add(cartItemDiscountedPrice);
                    // aggregating cartItems-count
                    totalItems += cartItem.getQuantity();
                }
            }
        }
        cart.setTotalAmount(CommonUtils.getPrecisionFixedValueOrZero(totalAmount));
        cart.setAmountToBePaid(CommonUtils.getPrecisionFixedValueOrZero(totalBilledAmount));
        cart.setTotalItems(totalItems);
        return cartRepository.save(cart);
    }
}
