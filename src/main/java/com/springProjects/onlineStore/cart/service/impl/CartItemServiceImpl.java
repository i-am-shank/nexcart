package com.springProjects.onlineStore.cart.service.impl;

import com.springProjects.onlineStore.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.springProjects.onlineStore.cart.dto.CartItemResponseDTO;
import com.springProjects.onlineStore.cart.entity.Cart;
import com.springProjects.onlineStore.cart.entity.CartItem;
import com.springProjects.onlineStore.cart.mapper.CartItemMapper;
import com.springProjects.onlineStore.cart.repository.CartItemRepository;
import com.springProjects.onlineStore.cart.repository.CartRepository;
import com.springProjects.onlineStore.cart.service.CartItemService;
import com.springProjects.onlineStore.cart.service.CartService;
import com.springProjects.onlineStore.common.utils.CommonUtils;
import com.springProjects.onlineStore.exceptions.ResourceNotFoundException;
import com.springProjects.onlineStore.product.constants.ProductConstants;
import com.springProjects.onlineStore.product.entity.Product;
import com.springProjects.onlineStore.user.repository.UserRepository;

import jakarta.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class CartItemServiceImpl implements CartItemService {
    private final CartRepository cartRepository;

    private final CartItemRepository cartItemRepository;

    private final UserRepository userRepository;

    private final CartService cartService;

    private final CartItemMapper cartItemMapper;

    private final ProductService productService;

    @Transactional
    @Override
    public CartItemResponseDTO addToCart(Integer userId, Integer productId, Integer quantity)
            throws IllegalArgumentException, ResourceNotFoundException, UnsupportedOperationException,
            IllegalStateException {
        if(userId == null || productId == null || quantity == null) {
            throw new IllegalArgumentException("userId, productId, quantity are mandatory for Add to Cart process");
        }
        Boolean userExists = userRepository.existsByUserIdAndDeletedFalse(userId);
        if(!userExists) {
            throw new ResourceNotFoundException("User does not exist for id : " + userId);
        }
        // Validate if Cart exists for this userId
        Cart cart = cartRepository.findByUserIdAndDeletedFalse(userId);
        if(cart == null) {
            // If not exists, create a cart for this userId
            cart = cartService.createCart(userId);
        }
        // Validate if this product already exists in this Cart
        validateIfProductAlreadyExistsInCart(productId, cart.getCartId());
        // Validate Product requested quantity
        Product product = validateProductAndRequestedQuantity(productId, quantity);
        // Create new CartItem entry
        CartItem cartItem = createCartItem(cart, product, quantity);
        if(cartItem.getCart() != null) {
            cart = cartService.updateCartItemsAndPaymentDetails(cartItem.getCart().getCartId());
            cartItem.setCart(cart);
        }
        // Update product remaining quantity
        product = productService.updateProductRemainingQuantity(quantity, cartItem);
        cartItem.setProduct(product);
        cartItemRepository.save(cartItem);
        return cartItemMapper.toResponseDTO(cartItem);
    }

    // TODO : Have to make this validation concurrency safe
    private void validateIfProductAlreadyExistsInCart(Integer productId, Integer cartId)
            throws UnsupportedOperationException {
        boolean productExistsInCart = cartItemRepository.
                existsByCart_CartIdAndProduct_ProductIdAndDeletedFalse(cartId, productId);
        if(productExistsInCart) {
            throw new UnsupportedOperationException("Product already added to Cart");
        }
    }

    // no need to be public , as self-invoked method , will go inside same transaction as parent
    private CartItem createCartItem(Cart cart, Product product, Integer quantity) {
        CartItem cartItem = new CartItem(cart, product, CommonUtils.getValueOrZero(quantity));
        return cartItemRepository.save(cartItem);
    }

    /**
     * Validates if Product exists for productId & requestedQuantity is within remainingQuantity limit
     * @param productId Product table entry to get
     * @param requestedQuantity More quantity requested for a product
     * @return Product fetched for productId
     * @throws ResourceNotFoundException if product not found for productId
     * @throws UnsupportedOperationException if (remainingQty - IN_STOCK_QTY)  <  requestedQty
     */
    private Product validateProductAndRequestedQuantity(Integer productId, Integer requestedQuantity)
            throws ResourceNotFoundException, UnsupportedOperationException {
        Product product = productService.getProductByProductId(productId);
        if(product == null) {
            throw new ResourceNotFoundException("Product not found for id : " + productId);
        }
        if((product.getRemainingQuantity() == null) ||
                (product.getRemainingQuantity() - ProductConstants.IN_STOCK_QTY) < requestedQuantity) {
            throw new UnsupportedOperationException("Requested quantity exceeds the stock quantity");
        }
        return product;
    }

    @Override
    public CartItemResponseDTO getCartItem(Integer cartItemId) throws IllegalArgumentException, ResourceNotFoundException {
        CartItem cartItem = getCartItemForId(cartItemId);
        return cartItemMapper.toResponseDTO(cartItem);
    }

    private CartItem getCartItemForId(Integer cartItemId) throws IllegalArgumentException, ResourceNotFoundException {
        if(cartItemId == null) {
            throw new IllegalArgumentException("cartItemId is null");
        }
        CartItem cartItem = cartItemRepository.findByCartItemIdAndDeletedFalse(cartItemId);
        if(cartItem == null) {
            throw new ResourceNotFoundException("CartItem not found for id : " + cartItemId);
        }
        return cartItem;
    }

    // @Transactional  :  TODO : Will all operation happen in single Hibernate session ? transaction commit at end ?
    @Transactional
    @Override
    public CartItemResponseDTO updateCartItem(Integer cartItemId, Integer quantity, Boolean saveForLater)
            throws IllegalArgumentException, ResourceNotFoundException, UnsupportedOperationException,
            IllegalStateException {
        CartItem cartItem = getCartItemForId(cartItemId);
        Product product = productService.updateProductRemainingQuantity(quantity, cartItem);
        cartItem.setProduct(product);
        // update CartItem
        if(quantity != null) {
            cartItem.setQuantity(quantity);
        }
        if(saveForLater != null) {
            cartItem.setSaveForLater(saveForLater);
        }
        if(cartItem.getCart() != null) {
            cartService.updateCartItemsAndPaymentDetails(cartItem.getCart().getCartId());
        }
        cartItem = cartItemRepository.save(cartItem);
        return cartItemMapper.toResponseDTO(cartItem);
    }

    @Transactional
    @Override
    public void removeCartItem(Integer cartItemId) throws IllegalArgumentException, ResourceNotFoundException,
            IllegalStateException {
        CartItem cartItem = getCartItemForId(cartItemId);
        // Also free this quantity of Product
        if(cartItem.getProduct() != null) {
            Product product = productService.updateProductRemainingQuantity(0, cartItem);
            cartItem.setProduct(product);
        }
        Cart cart = cartItem.getCart();

        /*
        Below 2-lines code wasn't alone able to delete CartItem because :-
            -->  orphanRemoval = true is telling Hibernate to persist all child CartItem of a Cart
            -->  And CartItemRepository is telling to delete the CartItem
            ====>  CONFLICT
            -->  cascade = CascadeType.ALL    :    comes to resolve the conflict , re-manages (persist) all child

        cartItemRepository.delete(cartItem);
        // Flushing all pending changes to db , as before update-Cart if cartItem not deleted - data inconsistency
        cartItemRepository.flush();

        Due to same reason, soft-delete will not work for CartItem
            --> as deleted marked CartItem will also be fetched while fetching Cart
         */

        if(cart != null) {
            // orphanRemoval = true  :  If CartItem removed from Cart -> cartItems,
            //          Hibernate automatically delete it from DB  (using same concept)
            cart.getCartItems().remove(cartItem);

            // Disconnect child entity side mapping as well  ->  Hibernate doesn't persist it anyhow
            cartItem.setCart(null);

            cartService.updateCartItemsAndPaymentDetails(cart.getCartId());
        }
    }

    @Transactional
    @Override
    public void saveItemForLater(Integer cartItemId) throws IllegalArgumentException, ResourceNotFoundException {
        CartItem cartItem = getCartItemForId(cartItemId);
        cartItem.setSaveForLater(Boolean.TRUE);
        if(cartItem.getCart() != null) {
            Cart cart = cartService.updateCartItemsAndPaymentDetails(cartItem.getCart().getCartId());
            cartItem.setCart(cart);
        }
        cartItemRepository.save(cartItem);
    }
}
