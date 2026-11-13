package controller;

import dal.BookDAO;
import dal.CartDAO;
import dal.CartItemDAO;
import dal.CategoryDAO;
import dal.OrderDAO;
import dal.OrderItemDAO;
import dal.UserDAO;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Book;
import model.Cart;
import model.CartItem;
import model.Category;
import model.Order;
import model.OrderItem;
import model.User;

@WebServlet(name = "OrderDetailController", urlPatterns = {"/order-detail"})
public class OrderDetailController extends HttpServlet {

    private final OrderDAO orderDAO = new OrderDAO();
    private final OrderItemDAO orderItemDAO = new OrderItemDAO();
    private final BookDAO bookDAO = new BookDAO();
    private final UserDAO userDAO = new UserDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final CartDAO cartDAO = new CartDAO();
    private final CartItemDAO cartItemDAO = new CartItemDAO();

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(true);
        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String service = request.getParameter("service");
        if (service == null || service.trim().isEmpty()) {
            service = request.getParameter("action");
        }
        if (service == null || service.trim().isEmpty()) {
            service = "viewOrderDetail";
        }
        service = service.trim();

        if ("viewOrderDetail".equals(service)) {
            int orderId = parseIntOrDefault(request.getParameter("id"), 0);
            if (orderId <= 0) {
                orderId = parseIntOrDefault(request.getParameter("orderId"), 0);
            }

            if (orderId <= 0) {
                response.sendRedirect(request.getContextPath() + (currentUser.getRoleId() == 2 ? "/admindashboard?tab=orders" : "/my-orders"));
                return;
            }

            Order order = orderDAO.searchOrder(orderId);
            if (order == null) {
                response.sendRedirect(request.getContextPath() + (currentUser.getRoleId() == 2 ? "/admindashboard?tab=orders" : "/my-orders"));
                return;
            }

            // Bảo mật: Khách hàng chỉ được xem đơn của chính mình; Admin (roleId == 2) xem được mọi đơn
            if (currentUser.getRoleId() != 2 && order.getUserId() != currentUser.getUserId()) {
                response.sendRedirect(request.getContextPath() + "/my-orders");
                return;
            }

            // Flash messages
            String successMsg = (String) session.getAttribute("orderDetailSuccessMsg");
            String errorMsg = (String) session.getAttribute("orderDetailErrorMsg");
            if (successMsg != null) {
                request.setAttribute("successMsg", successMsg);
                session.removeAttribute("orderDetailSuccessMsg");
            }
            if (errorMsg != null) {
                request.setAttribute("errorMsg", errorMsg);
                session.removeAttribute("orderDetailErrorMsg");
            }

            // Lấy thông tin người đặt hàng
            User customer = userDAO.searchUser(order.getUserId());
            if (customer != null) {
                order.setCustomerName(customer.getFullName());
            }

            // Lấy danh sách OrderItem và thông tin chi tiết từng cuốn sách
            Vector<OrderItem> items = orderItemDAO.getOrderItemsByOrderId(orderId);
            order.setItems(items);

            Map<Integer, Book> bookMap = new HashMap<>();
            int totalQty = 0;
            for (OrderItem item : items) {
                totalQty += item.getQuantity();
                Book b = bookDAO.searchBook(item.getBookId());
                if (b != null) {
                    bookMap.put(item.getBookId(), b);
                    if (item.getBookTitle() == null || item.getBookTitle().isEmpty()) {
                        item.setBookTitle(b.getTitle());
                    }
                }
            }

            // Tính số lượng giỏ hàng trên Header
            int cartCount = 0;
            try {
                Cart cart = cartDAO.getCartByUserId(currentUser.getUserId());
                if (cart != null) {
                    Vector<CartItem> cItems = cartItemDAO.getCartItemsByCartId(cart.getCartId());
                    for (CartItem ci : cItems) {
                        cartCount += ci.getQuantity();
                    }
                }
            } catch (Exception ignored) {
            }
            session.setAttribute("cartCount", cartCount);

            Vector<Category> categories = categoryDAO.getAllCategory();

            request.setAttribute("order", order);
            request.setAttribute("customer", customer);
            request.setAttribute("bookMap", bookMap);
            request.setAttribute("totalQty", totalQty);
            request.setAttribute("cartCount", cartCount);
            request.setAttribute("categories", categories);

            request.getRequestDispatcher("/jsp/orderdetail.jsp").forward(request, response);
        } else if ("adminUpdate".equals(service) || "customerCancel".equals(service)) {
            int orderId = parseIntOrDefault(request.getParameter("orderId"), 0);
            Order order = orderDAO.searchOrder(orderId);
            if (order == null) {
                response.sendRedirect(request.getContextPath() + "/home");
                return;
            }

            if ("adminUpdate".equals(service) && currentUser.getRoleId() == 2) {
                String orderStatus = request.getParameter("orderStatus");
                String paymentStatus = request.getParameter("paymentStatus");
                if (orderStatus != null && !orderStatus.trim().isEmpty()) {
                    order.setOrderStatus(orderStatus.trim());
                }
                if (paymentStatus != null && !paymentStatus.trim().isEmpty()) {
                    order.setPaymentStatus(paymentStatus.trim());
                }
                int n = orderDAO.updateOrder(order);
                if (n > 0) {
                    session.setAttribute("orderDetailSuccessMsg", "Đã cập nhật trạng thái đơn hàng #" + orderId + " thành công!");
                } else {
                    session.setAttribute("orderDetailErrorMsg", "Không thể cập nhật trạng thái đơn hàng!");
                }
            } else if ("customerCancel".equals(service) && order.getUserId() == currentUser.getUserId()) {
                if ("Pending".equalsIgnoreCase(order.getOrderStatus())) {
                    order.setOrderStatus("Cancelled");
                    int n = orderDAO.updateOrder(order);
                    if (n > 0) {
                        Vector<OrderItem> items = orderItemDAO.getOrderItemsByOrderId(orderId);
                        for (OrderItem oi : items) {
                            Book b = bookDAO.searchBook(oi.getBookId());
                            if (b != null) {
                                b.setStockQuantity(b.getStockQuantity() + oi.getQuantity());
                                bookDAO.updateBook(b);
                            }
                        }
                        session.setAttribute("orderDetailSuccessMsg", "Đã hủy đơn hàng #" + orderId + " và hoàn lại số lượng tồn kho.");
                    }
                } else {
                    session.setAttribute("orderDetailErrorMsg", "Chỉ có thể hủy đơn hàng khi đang ở trạng thái Chờ xử lý (Pending)!");
                }
            }

            response.sendRedirect(request.getContextPath() + "/order-detail?id=" + orderId);
        } else {
            response.sendRedirect(request.getContextPath() + "/home");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    private int parseIntOrDefault(String val, int defaultVal) {
        if (val == null || val.trim().isEmpty()) {
            return defaultVal;
        }
        try {
            return Integer.parseInt(val.trim());
        } catch (NumberFormatException ex) {
            return defaultVal;
        }
    }
}

