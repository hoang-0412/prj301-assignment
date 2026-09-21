package dal;

import java.util.Vector;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Order;

public class OrderDAO extends DBContext {

    public Vector<Order> getAllOrder() {
        Vector<Order> vector = new Vector<>();
        String sql = "select * from Order";
        try {
            PreparedStatement ptm = connection.prepareStatement(sql);
            ResultSet rs = ptm.executeQuery();
            while (rs.next()) {
                Order o = new Order(rs.getInt(1),
                        rs.getInt(2),
                        rs.getDouble(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getString(7),
                        rs.getString(8),
                        rs.getDate(9));
                vector.add(o);
            }
        } catch (SQLException ex) {
            ex.getStackTrace();
        }
        return vector;
    }

    public Order searchOrder(int orderId) {
        String sql = "select * from Order\n"
                + "where orderId = ?";
        try {
            PreparedStatement ptm = connection.prepareStatement(sql);
            ptm.setInt(1, orderId);
            ResultSet rs = ptm.executeQuery();
            if (rs.next()) {
                Order o = new Order(rs.getInt(1),
                        rs.getInt(2),
                        rs.getDouble(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getString(7),
                        rs.getString(8),
                        rs.getDate(9));
                return o;
            }
        } catch (SQLException ex) {
            ex.getStackTrace();
        }
        return null;
    }

    public int insertOrder(Order o) {
        String sql = "INSERT INTO [dbo].[Order]\n"
                + "           ([userId]\n"
                + "           ,[totalAmount]\n"
                + "           ,[orderStatus]\n"
                + "           ,[shippingAddress]\n"
                + "           ,[recipientPhone]\n"
                + "           ,[paymentMethod]\n"
                + "           ,[paymentStatus]\n"
                + "           ,[createdAt])\n"
                + "     VALUES\n"
                + "           (?,?,?,?,?,?,?,?)";
        int n = 0;
        try {
            PreparedStatement ptm = connection.prepareStatement(sql);
            ptm.setInt(1, o.getUserId());
            ptm.setDouble(2, o.getTotalAmount());
            ptm.setString(3, o.getPaymentStatus());
            ptm.setString(4, o.getShippingAddress());
            ptm.setString(5, o.getPaymentMethod());
            ptm.setString(6, o.getPaymentStatus());
            ptm.setDate(7, o.getCreatedAt());
            n = ptm.executeUpdate();
        } catch (SQLException ex) {
            ex.getStackTrace();
        }
        return n;
    }

    public int updateOrder(Order o) {
        String sql = "UPDATE [dbo].[Order]\n"
                + "   SET [userId] = ?\n"
                + "      ,[totalAmount] = ?\n"
                + "      ,[orderStatus] = ?\n"
                + "      ,[shippingAddress] = ?\n"
                + "      ,[recipientPhone] = ?\n"
                + "      ,[paymentMethod] = ?\n"
                + "      ,[paymentStatus] = ?\n"
                + "      ,[createdAt] = ?\n"
                + " WHERE orderId = ?";
        int n = 0;
        try {
            PreparedStatement ptm = connection.prepareStatement(sql);
            ptm.setInt(1, o.getUserId());
            ptm.setDouble(2, o.getTotalAmount());
            ptm.setString(3, o.getPaymentStatus());
            ptm.setString(4, o.getShippingAddress());
            ptm.setString(5, o.getPaymentMethod());
            ptm.setString(6, o.getPaymentStatus());
            ptm.setDate(7, o.getCreatedAt());
            n = ptm.executeUpdate();
        } catch (SQLException ex) {
            ex.getStackTrace();
        }
        return n;
    }

    public static void main(String[] args) {
        OrderDAO dao = new OrderDAO();
        Vector<Order> vector = dao.getAllOrder();
        for (Order o : vector) {
            System.out.println(o);
        }
    }
}
