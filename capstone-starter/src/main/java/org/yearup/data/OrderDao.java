package org.yearup.data;

import org.yearup.models.Profile;
import org.yearup.models.ShoppingCart;

public interface OrderDao {

    void checkout(int userId, ShoppingCart shoppingCart, Profile profile);

}
