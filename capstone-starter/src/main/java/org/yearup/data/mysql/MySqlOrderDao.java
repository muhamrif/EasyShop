package org.yearup.data.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yearup.data.OrderDao;
import org.yearup.models.Product;
import org.yearup.models.Profile;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;

@Component
public class MySqlOrderDao extends MySqlDaoBase implements OrderDao {

    @Autowired
    public MySqlOrderDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void checkout(int userId, ShoppingCart shoppingCart, Profile profile) {
        int orderId=0;

        String query = "INSERT INTO orders (user_id, date, address,city,state,zip,shipping_amount) VALUES (?, ?,?, ?, ?, ?, ?)";

        try (Connection connection = this.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setDate(2, Date.valueOf(LocalDate.now()));
            preparedStatement.setString(3, profile.getAddress());
            preparedStatement.setString(4, profile.getCity());
            preparedStatement.setString(5, profile.getState());
            preparedStatement.setString(6, profile.getZip());
            preparedStatement.setBigDecimal(7, shoppingCart.getTotal());
            int rows = preparedStatement.executeUpdate();
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    orderId = generatedKeys.getInt(1);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String query2 = "INSERT INTO order_line_items (order_id, product_id,sales_price, quantity, discount) VALUES (?, ?, ?, ?, ?)";
        try(Connection connection = this.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query2, Statement.RETURN_GENERATED_KEYS)) {
            for (ShoppingCartItem item : shoppingCart.returnAsList()) {
                preparedStatement.setInt(1, orderId);
                preparedStatement.setInt(2, item.getProduct().getProductId());
                preparedStatement.setBigDecimal(3, item.getProduct().getPrice());
                preparedStatement.setInt(4, item.getQuantity());
                preparedStatement.setBigDecimal(5, item.getDiscountPercent());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
