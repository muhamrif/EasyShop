package org.yearup.data.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MySqlShoppingCartDao extends MySqlDaoBase implements ShoppingCartDao{

    @Autowired
    public MySqlShoppingCartDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
     public ShoppingCart getByUserId(int userId) {
        ShoppingCart shoppingCart = new ShoppingCart();

        String sql = "SELECT shopping_cart.*, products.* " +
                "FROM shopping_cart " +
                "JOIN products " +
                "ON shopping_cart.product_id = products.product_id " +
                "WHERE shopping_cart.user_id = ?";

        try (Connection connection = this.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            ResultSet row = statement.executeQuery();
            while(row.next()) {
                Product product = new Product();
                product.setProductId(row.getInt("products.product_id"));
                product.setName(row.getString("name"));
                product.setPrice(row.getBigDecimal("price"));
                product.setCategoryId(row.getInt("category_id"));
                product.setDescription(row.getString("description"));
                product.setColor(row.getString("color"));
                product.setStock(row.getInt("stock"));
                product.setFeatured(row.getBoolean("featured"));
                product.setImageUrl(row.getString("image_url"));

                ShoppingCartItem shoppingCartItem = new ShoppingCartItem();
                shoppingCartItem.setProduct(product);

                shoppingCart.add(shoppingCartItem);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return shoppingCart;
    }
}
