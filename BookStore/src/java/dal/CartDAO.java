package dal;

import java.util.Vector;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Cart;

public class CartDAO extends DBContext {

    public Vector<Cart> getAllCart() {
        Vector<Cart> vector = new Vector<>();
        String sql = "select * from Cart";
        try {
            PreparedStatement ptm = connection.prepareStatement(sql);
            ResultSet rs = ptm.executeQuery();
            while (rs.next()) {
                Cart c = new Cart(rs.getInt(1), rs.getInt(2), rs.getDate(3), rs.getDate(4));
                vector.add(c);
            }
        } catch (SQLException ex) {
            ex.getStackTrace();
        }
        return vector;
    }

    public Cart searchCart(int cartId) {
        String sql = "select * from Cart\n"
                + "where cartId = ?";
        try {
            PreparedStatement ptm = connection.prepareStatement(sql);
            ptm.setInt(1, cartId);
            ResultSet rs = ptm.executeQuery();
            if (rs.next()) {
                Cart c = new Cart(rs.getInt(1), rs.getInt(2), rs.getDate(3), rs.getDate(4));
                return c;
            }
        } catch (SQLException ex) {
            ex.getStackTrace();
        }
        return null;
    }

    public int insertCart(Cart c) {
        String sql = "INSERT INTO [dbo].[Cart]\n"
                + "           ([userId]\n"
                + "           ,[createdAt]\n"
                + "           ,[updatedAt])\n"
                + "     VALUES\n"
                + "           (?,?,?)";
        int n = 0;
        try {
            PreparedStatement ptm = connection.prepareStatement(sql);
            ptm.setInt(1, c.getUserId());
            ptm.setDate(2, c.getCreatedAt());
            ptm.setDate(3, c.getUpdatedAt());
            n = ptm.executeUpdate();
        } catch (SQLException ex) {
            ex.getStackTrace();
        }
        return n;
    }

    public int updateCart(Cart c) {
        String sql = "UPDATE [dbo].[Cart]\n"
                + "   SET [userId] = ?\n"
                + "      ,[createdAt] = ?\n"
                + "      ,[updatedAt] = ?\n"
                + " WHERE cartId = ?";
        int n = 0;
        try {
            PreparedStatement ptm = connection.prepareStatement(sql);
            ptm.setInt(1, c.getUserId());
            ptm.setDate(2, c.getCreatedAt());
            ptm.setDate(3, c.getUpdatedAt());
            ptm.setInt(4, c.getCartId());
            n = ptm.executeUpdate();
        } catch (SQLException ex) {
            ex.getStackTrace();
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
