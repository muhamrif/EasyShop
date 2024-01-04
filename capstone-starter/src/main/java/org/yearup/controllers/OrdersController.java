package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.OrderDao;
import org.yearup.data.ProfileDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;
import org.yearup.models.Profile;
import org.yearup.models.ShoppingCart;
import org.yearup.models.User;

import java.security.Principal;

@RestController
@RequestMapping("orders")
@CrossOrigin
@PreAuthorize("isAuthenticated()")
public class OrdersController {

    private final OrderDao orderDao;
    private final ProfileDao profileDao;
    private final ShoppingCartDao shoppingCartDao;
    private final UserDao userDao;

    @Autowired
    public OrdersController(OrderDao orderDao, ProfileDao profileDao, ShoppingCartDao shoppingCartDao, UserDao userDao) {
        this.orderDao = orderDao;
        this.profileDao = profileDao;
        this.shoppingCartDao = shoppingCartDao;
        this.userDao = userDao;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public void checkout(Principal principal) {
        try
        {
            // get the currently logged in username
            String userName = principal.getName();
            // find database user by userId
            User user = userDao.getByUserName(userName);
            int userId = user.getId();

            // use the shoppingcartDao to get all items in the cart and return the cart
            ShoppingCart shoppingCart = shoppingCartDao.getByUserId(userId);
            Profile profile = profileDao.get(userId);
            orderDao.checkout(userId, shoppingCart, profile);
            shoppingCartDao.emptyCart(userId);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }
}
