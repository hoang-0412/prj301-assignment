package dal;

import java.util.Vector;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.OrderItem;

public class OrderItemDAO extends DBContext {

    private static final Logger LOGGER = Logger.getLogger(OrderItemDAO.class.getName());

    public Vector<OrderItem> getAllOrderItem() {
        Vector<OrderItem> vector = new Vector<>();
        String sql = "SELECT orderItemId, orderId, bookId, quantity, unitPrice FROM OrderItem";
        try (PreparedStatement ptm = connection.prepareStatement(sql);
             ResultSet rs = ptm.executeQuery()) {
            while (rs.next()) {
                OrderItem oi = new OrderItem(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getInt(3),
                        rs.getInt(4),
                        rs.getDouble(5)
                );
                vector.add(oi);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy danh sách chi tiết đơn hàng", ex);
        }
        return vector;
    }

    public OrderItem searchOrderItem(int orderItemId) {
        String sql = "SELECT orderItemId, orderId, bookId, quantity, unitPrice FROM OrderItem WHERE orderItemId = ?";
        try (PreparedStatement ptm = connection.prepareStatement(sql)) {
            ptm.setInt(1, orderItemId);
            try (ResultSet rs = ptm.executeQuery()) {
                if (rs.next()) {
                    return new OrderItem(
                            rs.getInt(1),
                            rs.getInt(2),
                            rs.getInt(3),
                            rs.getInt(4),
                            rs.getDouble(5)
                    );
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tìm chi tiết đơn hàng theo id: " + orderItemId, ex);
        }
        return null;
    }

    public Vector<OrderItem> getOrderItemsByOrderId(int orderId) {
        Vector<OrderItem> vector = new Vector<>();
        String sql = "SELECT orderItemId, orderId, bookId, quantity, unitPrice FROM OrderItem WHERE orderId = ?";
        try (PreparedStatement ptm = connection.prepareStatement(sql)) {
            ptm.setInt(1, orderId);
            try (ResultSet rs = ptm.executeQuery()) {
                while (rs.next()) {
                    OrderItem oi = new OrderItem(
                            rs.getInt(1),
                            rs.getInt(2),
                            rs.getInt(3),
                            rs.getInt(4),
                            rs.getDouble(5)
                    );
                    vector.add(oi);
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy chi tiết đơn hàng của orderId: " + orderId, ex);
        }
        return vector;
    }

    public int insertOrderItem(OrderItem oi) {
        String sql = "INSERT INTO [dbo].[OrderItem] ([orderId], [bookId], [quantity], [unitPrice]) VALUES (?, ?, ?, ?)";
        int n = 0;
        try (PreparedStatement ptm = connection.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ptm.setInt(1, oi.getOrderId());
            ptm.setInt(2, oi.getBookId());
            ptm.setInt(3, oi.getQuantity());
            ptm.setDouble(4, oi.getUnitPrice());
            n = ptm.executeUpdate();
            if (n > 0) {
                try (ResultSet generatedKeys = ptm.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        oi.setOrderItemId(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi thêm chi tiết đơn hàng", ex);
        }
        return n;
    }

    public int updateOrderItem(OrderItem oi) {
        String sql = "UPDATE [dbo].[OrderItem] SET [orderId] = ?, [bookId] = ?, [quantity] = ?, [unitPrice] = ? WHERE orderItemId = ?";
        int n = 0;
        try (PreparedStatement ptm = connection.prepareStatement(sql)) {
            ptm.setInt(1, oi.getOrderId());
            ptm.setInt(2, oi.getBookId());
            ptm.setInt(3, oi.getQuantity());
            ptm.setDouble(4, oi.getUnitPrice());
            ptm.setInt(5, oi.getOrderItemId());
            n = ptm.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi cập nhật chi tiết đơn hàng", ex);
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
