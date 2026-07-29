package com.springProjects.onlineStore.cart.service;

import com.springProjects.onlineStore.cart.dto.CartItemResponseDTO;
import com.springProjects.onlineStore.exceptions.ResourceNotFoundException;

public interface CartItemService {
    /**
     * Adds an item to existing cart, or iff cart not present for userId, create new cart & then add item(s)
     * @param userId unique cart entry for this
     * @param productId Product to add to cart
     * @param quantity Product-quantity to add to cart
     * @return Created CartItem entry as CartItemResponseDTO
     * @throws IllegalArgumentException for invalid userId / productId
     * @throws ResourceNotFoundException if User / Product not found for provided id(s)
     * @throws UnsupportedOperationException If item already exists in cart  OR  quantity > product remainingQuantity
     * @throws IllegalStateException if Product not found for the CartItem, when updating quantity-stock
     */
    CartItemResponseDTO addToCart(Integer userId, Integer productId, Integer quantity)
            throws IllegalArgumentException, ResourceNotFoundException, UnsupportedOperationException,
            IllegalStateException;

    /**
     * Fetch CartItem table entry for provided cartItemId
     * @param cartItemId unique CartItem entry for this
     * @return CartItem table entry as CartItemResponseDTO
     * @throws IllegalArgumentException for invalid cartItemId
     * @throws ResourceNotFoundException if no cartItem table entry exists for provided cartItemId
     */
    CartItemResponseDTO getCartItem(Integer cartItemId) throws IllegalArgumentException, ResourceNotFoundException;

    /**
     * Update quantity & saveForLater for CartItem , also updating Cart corresponding details
     * @param cartItemId CartItem to update
     * @param quantity updated quantity
     * @param saveForLater updated saveForLater (can be same as before)
     * @return Updated CartItem as CartItemResponseDTO
     * @throws IllegalArgumentException for invalid cartItemId
     * @throws ResourceNotFoundException if no CartItem found for provided cartItemId
     * @throws UnsupportedOperationException if updated qty exceeds (Product remainingQty - IN_STOCK_QTY)
     * @throws IllegalStateException if Product not found for the CartItem, when updating quantity-stock
     */
    CartItemResponseDTO updateCartItem(Integer cartItemId, Integer quantity, Boolean saveForLater)
            throws IllegalArgumentException, ResourceNotFoundException, UnsupportedOperationException,
            IllegalStateException;

    /**
     * Mark CartItem as deleted & free the Product-quantity to Product table remainingQuantity column  :
     * load cartItem  -->  restore product quantity  -->  remove child from parent collection
     * break child->parent association  -->  Hibernate orphan-removes child  -->  recalculate cart
     *
     * @param cartItemId CartItem to delete
     * @throws IllegalArgumentException for invalid cartItemId
     * @throws ResourceNotFoundException if no cartItem entry found for provided cartItemId
     * @throws IllegalStateException if Product not found for the CartItem, when updating quantity-stock
     */
    void removeCartItem(Integer cartItemId) throws IllegalArgumentException, ResourceNotFoundException,
            IllegalStateException;

    void saveItemForLater(Integer cartItemId) throws IllegalArgumentException, ResourceNotFoundException;
}
