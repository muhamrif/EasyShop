package org.yearup.data;

import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

public interface ShoppingCartDao
{
    ShoppingCart getByUserId(int userId);
    // add additional method signatures here
    ShoppingCart addToCart(int userId, int productId, int quantity);
    void updateQuantity(int userId,int productId, ShoppingCartItem item);
    ShoppingCart emptyCart(int userId);






}
