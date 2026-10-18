package dal;

import java.util.Vector;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Order;

public class OrderDAO extends DBContext {

    private static final Logger LOGGER = Logger.getLogger(OrderDAO.class.getName());

    public Vector<Order> getAllOrder() {
        Vector<Order> vector = new Vector<>();
        String sql = "SELECT orderId, userId, totalAmount, orderStatus, shippingAddress, "
                + "recipientPhone, paymentMethod, paymentStatus, createdAt FROM [Order] ORDER BY orderId DESC";
        try (PreparedStatement ptm = connection.prepareStatement(sql);
             ResultSet rs = ptm.executeQuery()) {
            while (rs.next()) {
                Order o = new Order(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getDouble(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getString(7),
                        rs.getString(8),
                        rs.getDate(9)
                );
                vector.add(o);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy danh sách đơn hàng", ex);
        }
        return vector;
    }

    public Order searchOrder(int orderId) {
        String sql = "SELECT orderId, userId, totalAmount, orderStatus, shippingAddress, "
                + "recipientPhone, paymentMethod, paymentStatus, createdAt FROM [Order] WHERE orderId = ?";
        try (PreparedStatement ptm = connection.prepareStatement(sql)) {
            ptm.setInt(1, orderId);
            try (ResultSet rs = ptm.executeQuery()) {
                if (rs.next()) {
                    return new Order(
                            rs.getInt(1),
                            rs.getInt(2),
                            rs.getDouble(3),
                            rs.getString(4),
                            rs.getString(5),
                            rs.getString(6),
                            rs.getString(7),
                            rs.getString(8),
                            rs.getDate(9)
                    );
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tìm đơn hàng theo id: " + orderId, ex);
        }
        return null;
    }

    public Vector<Order> getOrdersByUserId(int userId) {
        Vector<Order> vector = new Vector<>();
        String sql = "SELECT orderId, userId, totalAmount, orderStatus, shippingAddress, "
                + "recipientPhone, paymentMethod, paymentStatus, createdAt FROM [Order] WHERE userId = ? ORDER BY orderId DESC";
        try (PreparedStatement ptm = connection.prepareStatement(sql)) {
            ptm.setInt(1, userId);
            try (ResultSet rs = ptm.executeQuery()) {
                while (rs.next()) {
                    Order o = new Order(
                            rs.getInt(1),
                            rs.getInt(2),
                            rs.getDouble(3),
                            rs.getString(4),
                            rs.getString(5),
                            rs.getString(6),
                            rs.getString(7),
                            rs.getString(8),
                            rs.getDate(9)
                    );
                    vector.add(o);
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy đơn hàng của userId: " + userId, ex);
        }
        return vector;
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
        try (PreparedStatement ptm = connection.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ptm.setInt(1, o.getUserId());
            ptm.setDouble(2, o.getTotalAmount());
            ptm.setString(3, o.getOrderStatus());
            ptm.setString(4, o.getShippingAddress());
            ptm.setString(5, o.getRecipientPhone());
            ptm.setString(6, o.getPaymentMethod());
            ptm.setString(7, o.getPaymentStatus());
            ptm.setDate(8, o.getCreatedAt());
            n = ptm.executeUpdate();
            if (n > 0) {
                try (ResultSet generatedKeys = ptm.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        o.setOrderId(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi thêm đơn hàng mới", ex);
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
        try (PreparedStatement ptm = connection.prepareStatement(sql)) {
            ptm.setInt(1, o.getUserId());
            ptm.setDouble(2, o.getTotalAmount());
            ptm.setString(3, o.getOrderStatus());
            ptm.setString(4, o.getShippingAddress());
            ptm.setString(5, o.getRecipientPhone());
            ptm.setString(6, o.getPaymentMethod());
            ptm.setString(7, o.getPaymentStatus());
            ptm.setDate(8, o.getCreatedAt());
            ptm.setInt(9, o.getOrderId());
            n = ptm.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi cập nhật đơn hàng", ex);
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
