package dal;

import java.util.Vector;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.OrderItem;

public class OrderItemDAO extends DBContext {

    public Vector<OrderItem> getAllOrderItem() {
        Vector<OrderItem> vector = new Vector<>();
        String sql = "select * from OrderItem";
        try {
            PreparedStatement ptm = connection.prepareStatement(sql);
            ResultSet rs = ptm.executeQuery();
            while (rs.next()) {
                OrderItem oi = new OrderItem(rs.getInt(1),
                        rs.getInt(2),
                        rs.getInt(3),
                        rs.getInt(4),
                        rs.getDouble(5));
                vector.add(oi);
            }
        } catch (SQLException ex) {
            ex.getStackTrace();
        }
        return vector;
    }

    public OrderItem searchOrderItem(int orderItemId) {
        String sql = "select * from OrderItem\n"
                + "where orderItemId = ?";
        try {
            PreparedStatement ptm = connection.prepareStatement(sql);
            ptm.setInt(1, orderItemId);
            ResultSet rs = ptm.executeQuery();
            if (rs.next()) {
                OrderItem oi = new OrderItem(rs.getInt(1),
                        rs.getInt(2),
                        rs.getInt(3),
                        rs.getInt(4),
                        rs.getDouble(5));
                return oi;
            }
        } catch (SQLException ex) {
            ex.getStackTrace();
        }
        return null;
    }

    public int insertOrderItem(OrderItem oi) {
        String sql = "INSERT INTO [dbo].[OrderItem]\n"
                + "           ([orderId]\n"
                + "           ,[bookId]\n"
                + "           ,[quantity]\n"
                + "           ,[unitPrice])\n"
                + "     VALUES\n"
                + "           (?,?,?,?)";
        int n = 0;
        try {
            PreparedStatement ptm = connection.prepareStatement(sql);
            ptm.setInt(1, oi.getOrderId());
            ptm.setInt(2, oi.getBookId());
            ptm.setInt(3, oi.getQuantity());
            ptm.setDouble(3, oi.getUnitPrice());
            n = ptm.executeUpdate();
        } catch (SQLException ex) {
            ex.getStackTrace();
        }
        return n;
    }

    public int updateOrderItem(OrderItem oi) {
        String sql = "UPDATE [dbo].[OrderItem]\n"
                + "   SET [orderId] = ?\n"
                + "      ,[bookId] = ?\n"
                + "      ,[quantity] = ?\n"
                + "      ,[unitPrice] = ?\n"
                + " WHERE orderItemId = ?";
        int n = 0;
        try {
            PreparedStatement ptm = connection.prepareStatement(sql);
            ptm.setInt(1, oi.getOrderId());
            ptm.setInt(2, oi.getBookId());
            ptm.setInt(3, oi.getQuantity());
            ptm.setDouble(3, oi.getUnitPrice());
            ptm.setInt(4, oi.getOrderItemId());
            n = ptm.executeUpdate();
        } catch (SQLException ex) {
            ex.getStackTrace();
        }
        return n;
    }

    public static void main(String[] args) {
        OrderItemDAO dao = new OrderItemDAO();
        Vector<OrderItem> vector = dao.getAllOrderItem();
        for (OrderItem oi : vector) {
            System.out.println(oi);
        }
    }
}
