package dal;

import java.util.Vector;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Cart;

public class CartDAO extends DBContext {

    private static final Logger LOGGER = Logger.getLogger(CartDAO.class.getName());

    public Vector<Cart> getAllCart() {
        Vector<Cart> vector = new Vector<>();
        String sql = "SELECT cartId, userId, createdAt, updatedAt FROM Cart";
        try (PreparedStatement ptm = connection.prepareStatement(sql);
             ResultSet rs = ptm.executeQuery()) {
            while (rs.next()) {
                Cart c = new Cart(rs.getInt(1), rs.getInt(2), rs.getDate(3), rs.getDate(4));
                vector.add(c);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy danh sách giỏ hàng", ex);
        }
        return vector;
    }

    public Cart searchCart(int cartId) {
        String sql = "SELECT cartId, userId, createdAt, updatedAt FROM Cart WHERE cartId = ?";
        try (PreparedStatement ptm = connection.prepareStatement(sql)) {
            ptm.setInt(1, cartId);
            try (ResultSet rs = ptm.executeQuery()) {
                if (rs.next()) {
                    return new Cart(rs.getInt(1), rs.getInt(2), rs.getDate(3), rs.getDate(4));
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tìm giỏ hàng theo id: " + cartId, ex);
        }
        return null;
    }

    public Cart getCartByUserId(int userId) {
        String sql = "SELECT cartId, userId, createdAt, updatedAt FROM Cart WHERE userId = ?";
        try (PreparedStatement ptm = connection.prepareStatement(sql)) {
            ptm.setInt(1, userId);
            try (ResultSet rs = ptm.executeQuery()) {
                if (rs.next()) {
                    return new Cart(rs.getInt(1), rs.getInt(2), rs.getDate(3), rs.getDate(4));
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy giỏ hàng của userId: " + userId, ex);
        }
        return null;
    }

    public int insertCart(Cart c) {
        String sql = "INSERT INTO [dbo].[Cart] ([userId], [createdAt], [updatedAt]) VALUES (?, ?, ?)";
        int n = 0;
        try (PreparedStatement ptm = connection.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ptm.setInt(1, c.getUserId());
            ptm.setDate(2, c.getCreatedAt());
            ptm.setDate(3, c.getUpdatedAt());
            n = ptm.executeUpdate();
            if (n > 0) {
                try (ResultSet generatedKeys = ptm.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        c.setCartId(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi thêm giỏ hàng mới", ex);
        }
        return n;
    }

    public int updateCart(Cart c) {
        String sql = "UPDATE [dbo].[Cart] SET [userId] = ?, [createdAt] = ?, [updatedAt] = ? WHERE cartId = ?";
        int n = 0;
        try (PreparedStatement ptm = connection.prepareStatement(sql)) {
            ptm.setInt(1, c.getUserId());
            ptm.setDate(2, c.getCreatedAt());
            ptm.setDate(3, c.getUpdatedAt());
            ptm.setInt(4, c.getCartId());
            n = ptm.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi cập nhật giỏ hàng", ex);
        }
        return n;
    }

    public static void main(String[] args) {
        CartDAO dao = new CartDAO();
        Vector<Cart> vector = dao.getAllCart();
        for (Cart c : vector) {
            System.out.println(c);
        }
    }
}
