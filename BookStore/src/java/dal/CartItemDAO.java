package dal;

import java.util.Vector;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.CartItem;

public class CartItemDAO extends DBContext {

    public Vector<CartItem> getAllCartItem() {
        Vector<CartItem> vector = new Vector<>();
        String sql = "select * from CartItem";
        try {
            PreparedStatement ptm = connection.prepareStatement(sql);
            ResultSet rs = ptm.executeQuery();
            while (rs.next()) {
                CartItem ci = new CartItem(rs.getInt(1),
                        rs.getInt(2),
                        rs.getInt(3),
                        rs.getInt(4),
                        rs.getDate(5));
                vector.add(ci);
            }
        } catch (SQLException ex) {
            ex.getStackTrace();
        }
        return vector;
    }

    public CartItem searchCartItem(int cartItemId) {
        String sql = "select * from CartItem\n"
                + "where cartItemId = ?";
        try {
            PreparedStatement ptm = connection.prepareStatement(sql);
            ptm.setInt(1, cartItemId);
            ResultSet rs = ptm.executeQuery();
            if (rs.next()) {
                CartItem ci = new CartItem(rs.getInt(1),
                        rs.getInt(2),
                        rs.getInt(3),
                        rs.getInt(4),
                        rs.getDate(5));
                return ci;
            }
        } catch (SQLException ex) {
            ex.getStackTrace();
        }
        return null;
    }

    public int insertCartItem(CartItem ci) {
        String sql = "INSERT INTO [dbo].[CartItem]\n"
                + "           ([cartId]\n"
                + "           ,[bookId]\n"
                + "           ,[quantity]\n"
                + "           ,[addedAt])\n"
                + "     VALUES\n"
                + "           (?,?,?,?)";
        int n = 0;
        try {
            PreparedStatement ptm = connection.prepareStatement(sql);
            ptm.setInt(1, ci.getCartId());
            ptm.setInt(2, ci.getBookId());
            ptm.setInt(3, ci.getQuantity());
            ptm.setDate(4, ci.getAddedAt());
            n = ptm.executeUpdate();
        } catch (SQLException ex) {
            ex.getStackTrace();
        }
        return n;
    }

    public int updateCartItem(CartItem ci) {
        String sql = "UPDATE [dbo].[CartItem]\n"
                + "   SET [cartId] = ?\n"
                + "      ,[bookId] = ?\n"
                + "      ,[quantity] = ?\n"
                + "      ,[addedAt] = ?\n"
                + " WHERE cartItemId = ?";
        int n = 0;
        try {
            PreparedStatement ptm = connection.prepareStatement(sql);
            ptm.setInt(1, ci.getCartId());
            ptm.setInt(2, ci.getBookId());
            ptm.setInt(3, ci.getQuantity());
            ptm.setDate(4, ci.getAddedAt());
            ptm.setInt(5, ci.getCartId());
            n = ptm.executeUpdate();
        } catch (SQLException ex) {
            ex.getStackTrace();
        }
        return n;
    }

    public static void main(String[] args) {
        CartItemDAO dao = new CartItemDAO();
        Vector<CartItem> vector = dao.getAllCartItem();
        for (CartItem ci : vector) {
            System.out.println(ci);
        }
    }
}
