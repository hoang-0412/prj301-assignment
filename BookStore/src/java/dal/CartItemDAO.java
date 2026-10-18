package dal;

import java.util.Vector;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.CartItem;

public class CartItemDAO extends DBContext {

    private static final Logger LOGGER = Logger.getLogger(CartItemDAO.class.getName());

    public Vector<CartItem> getAllCartItem() {
        Vector<CartItem> vector = new Vector<>();
        String sql = "SELECT cartItemId, cartId, bookId, quantity, addedAt FROM CartItem";
        try (PreparedStatement ptm = connection.prepareStatement(sql);
             ResultSet rs = ptm.executeQuery()) {
            while (rs.next()) {
                CartItem ci = new CartItem(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getInt(3),
                        rs.getInt(4),
                        rs.getDate(5)
                );
                vector.add(ci);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy danh sách sản phẩm trong giỏ", ex);
        }
        return vector;
    }

    public CartItem searchCartItem(int cartItemId) {
        String sql = "SELECT cartItemId, cartId, bookId, quantity, addedAt FROM CartItem WHERE cartItemId = ?";
        try (PreparedStatement ptm = connection.prepareStatement(sql)) {
            ptm.setInt(1, cartItemId);
            try (ResultSet rs = ptm.executeQuery()) {
                if (rs.next()) {
                    return new CartItem(
                            rs.getInt(1),
                            rs.getInt(2),
                            rs.getInt(3),
                            rs.getInt(4),
                            rs.getDate(5)
                    );
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tìm sản phẩm giỏ hàng theo id: " + cartItemId, ex);
        }
        return null;
    }

    public int insertCartItem(CartItem ci) {
        String sql = "INSERT INTO [dbo].[CartItem] ([cartId], [bookId], [quantity], [addedAt]) VALUES (?, ?, ?, ?)";
        int n = 0;
        try (PreparedStatement ptm = connection.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ptm.setInt(1, ci.getCartId());
            ptm.setInt(2, ci.getBookId());
            ptm.setInt(3, ci.getQuantity());
            ptm.setDate(4, ci.getAddedAt());
            n = ptm.executeUpdate();
            if (n > 0) {
                try (ResultSet generatedKeys = ptm.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        ci.setCartItemId(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi thêm sản phẩm vào giỏ", ex);
        }
        return n;
    }

    public int updateCartItem(CartItem ci) {
        String sql = "UPDATE [dbo].[CartItem] SET [cartId] = ?, [bookId] = ?, [quantity] = ?, [addedAt] = ? WHERE cartItemId = ?";
        int n = 0;
        try (PreparedStatement ptm = connection.prepareStatement(sql)) {
            ptm.setInt(1, ci.getCartId());
            ptm.setInt(2, ci.getBookId());
            ptm.setInt(3, ci.getQuantity());
            ptm.setDate(4, ci.getAddedAt());
            ptm.setInt(5, ci.getCartItemId());
            n = ptm.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi cập nhật sản phẩm giỏ hàng", ex);
        }
        return n;
    }

    public int deleteCartItem(int cartItemId) {
        String sql = "DELETE FROM CartItem WHERE cartItemId = ?";
        int n = 0;
        try (PreparedStatement ptm = connection.prepareStatement(sql)) {
            ptm.setInt(1, cartItemId);
            n = ptm.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi xóa sản phẩm khỏi giỏ: " + cartItemId, ex);
        }
        return n;
    }

    public Vector<CartItem> getCartItemsByCartId(int cartId) {
        Vector<CartItem> vector = new Vector<>();
        String sql = "SELECT cartItemId, cartId, bookId, quantity, addedAt FROM CartItem WHERE cartId = ?";
        try (PreparedStatement ptm = connection.prepareStatement(sql)) {
            ptm.setInt(1, cartId);
            try (ResultSet rs = ptm.executeQuery()) {
                while (rs.next()) {
                    CartItem ci = new CartItem(
                            rs.getInt(1),
                            rs.getInt(2),
                            rs.getInt(3),
                            rs.getInt(4),
                            rs.getDate(5)
                    );
                    vector.add(ci);
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy danh sách sản phẩm giỏ hàng của cartId: " + cartId, ex);
        }
        return vector;
    }

    public static void main(String[] args) {
        CartItemDAO dao = new CartItemDAO();
        Vector<CartItem> vector = dao.getAllCartItem();
        for (CartItem ci : vector) {
            System.out.println(ci);
        }
    }
}
