package controller;

import dal.BookDAO;
import dal.CartDAO;
import dal.CartItemDAO;
import dal.CategoryDAO;
import dal.OrderDAO;
import dal.OrderItemDAO;
import dal.UserDAO;
import java.io.IOException;
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

@WebServlet(name = "ProfileController", urlPatterns = {"/profile", "/my-orders"})
public class ProfileController extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final OrderDAO orderDAO = new OrderDAO();
    private final OrderItemDAO orderItemDAO = new OrderItemDAO();
    private final BookDAO bookDAO = new BookDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final CartDAO cartDAO = new CartDAO();
    private final CartItemDAO cartItemDAO = new CartItemDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(true);
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Đồng bộ lại thông tin mới nhất từ CSDL
        User currentUser = userDAO.searchUser(sessionUser.getUserId());
        if (currentUser == null) {
            session.invalidate();
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        session.setAttribute("user", currentUser);

        // Flash messages
        String successMsg = (String) session.getAttribute("profileSuccessMsg");
        String errorMsg = (String) session.getAttribute("profileErrorMsg");
        if (successMsg != null) {
            request.setAttribute("successMsg", successMsg);
            session.removeAttribute("profileSuccessMsg");
        }
        if (errorMsg != null) {
            request.setAttribute("errorMsg", errorMsg);
            session.removeAttribute("profileErrorMsg");
        }

        String servletPath = request.getServletPath();
        String activeTab = request.getParameter("tab");
        if (activeTab == null || activeTab.trim().isEmpty()) {
            if ("/my-orders".equalsIgnoreCase(servletPath)) {
                activeTab = "orders";
            } else {
                activeTab = "profile";
            }
        }

        Vector<Order> myOrders = orderDAO.getOrdersByUserId(currentUser.getUserId());
        int totalOrdersCount = myOrders.size();
        int pendingOrdersCount = 0;
        int completedOrdersCount = 0;
        double totalSpent = 0;

        for (Order o : myOrders) {
            if ("Pending".equalsIgnoreCase(o.getOrderStatus())) {
                pendingOrdersCount++;
            } else if ("Completed".equalsIgnoreCase(o.getOrderStatus())) {
                completedOrdersCount++;
            }
            if (!"Cancelled".equalsIgnoreCase(o.getOrderStatus())) {
                totalSpent += o.getTotalAmount();
            }
        }

        String formattedTotalSpent = String.format("%,.0f đ", totalSpent).replace(',', '.');

        // Tính số lượng giỏ hàng
        int cartCount = 0;
        try {
            Cart cart = cartDAO.getCartByUserId(currentUser.getUserId());
            if (cart != null) {
                Vector<CartItem> items = cartItemDAO.getCartItemsByCartId(cart.getCartId());
                for (CartItem ci : items) {
                    cartCount += ci.getQuantity();
                }
            }
        } catch (Exception ignored) {
        }
        session.setAttribute("cartCount", cartCount);

        Vector<Category> categories = categoryDAO.getAllCategory();

        request.setAttribute("activeTab", activeTab);
        request.setAttribute("userProfile", currentUser);
        request.setAttribute("myOrders", myOrders);
        request.setAttribute("totalOrdersCount", totalOrdersCount);
        request.setAttribute("pendingOrdersCount", pendingOrdersCount);
        request.setAttribute("completedOrdersCount", completedOrdersCount);
        request.setAttribute("formattedTotalSpent", formattedTotalSpent);
        request.setAttribute("cartCount", cartCount);
        request.setAttribute("categories", categories);

        request.getRequestDispatcher("/jsp/profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(true);
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User currentUser = userDAO.searchUser(sessionUser.getUserId());
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        if (action == null) {
            action = "";
        }

        String redirectTab = "profile";

        switch (action) {
            case "updateProfile":
                handleUpdateProfile(request, session, currentUser);
                redirectTab = "profile";
                break;
            case "changePassword":
                handleChangePassword(request, session, currentUser);
                redirectTab = "password";
                break;
            case "cancelOrder":
                handleCancelOrder(request, session, currentUser);
                redirectTab = "orders";
                break;
            default:
                break;
        }

        response.sendRedirect(request.getContextPath() + "/profile?tab=" + redirectTab);
    }

    private void handleUpdateProfile(HttpServletRequest request, HttpSession session, User currentUser) {
        String fullName = trimParam(request.getParameter("fullName"));
        String email = trimParam(request.getParameter("email"));
        String phoneNumber = trimParam(request.getParameter("phoneNumber"));
        String address = trimParam(request.getParameter("address"));

        if (fullName.isEmpty() || email.isEmpty()) {
            session.setAttribute("profileErrorMsg", "Họ và tên và Email không được để trống!");
            return;
        }

        // Kiểm tra trùng email với tài khoản khác
        if (!email.equalsIgnoreCase(currentUser.getEmail())) {
            User other = userDAO.getUserByEmail(email);
            if (other != null && other.getUserId() != currentUser.getUserId()) {
                session.setAttribute("profileErrorMsg", "Địa chỉ email \"" + email + "\" đã được sử dụng bởi tài khoản khác!");
                return;
            }
        }

        currentUser.setFullName(fullName);
        currentUser.setEmail(email);
        currentUser.setPhoneNumber(phoneNumber);
        currentUser.setAddress(address);

        int n = userDAO.updateUser(currentUser);
        if (n > 0) {
            session.setAttribute("user", currentUser);
            session.setAttribute("profileSuccessMsg", "Đã cập nhật thông tin hồ sơ cá nhân thành công!");
        } else {
            session.setAttribute("profileErrorMsg", "Cập nhật thông tin thất bại, vui lòng kiểm tra lại!");
        }
    }

    private void handleChangePassword(HttpServletRequest request, HttpSession session, User currentUser) {
        String currentPassword = trimParam(request.getParameter("currentPassword"));
        String newPassword = trimParam(request.getParameter("newPassword"));
        String confirmPassword = trimParam(request.getParameter("confirmPassword"));

        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            session.setAttribute("profileErrorMsg", "Vui lòng nhập đầy đủ mật khẩu hiện tại và mật khẩu mới!");
            return;
        }

        if (!userDAO.checkPassword(currentUser, currentPassword)) {
            session.setAttribute("profileErrorMsg", "Mật khẩu hiện tại không chính xác!");
            return;
        }

        if (newPassword.length() < 6) {
            session.setAttribute("profileErrorMsg", "Mật khẩu mới phải có tối thiểu 6 ký tự!");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            session.setAttribute("profileErrorMsg", "Mật khẩu xác nhận không khớp với mật khẩu mới!");
            return;
        }

        currentUser.setPasswordHash(newPassword);
        int n = userDAO.updateUser(currentUser);
        if (n > 0) {
            session.setAttribute("user", currentUser);
            session.setAttribute("profileSuccessMsg", "Đã đổi mật khẩu tài khoản thành công!");
        } else {
            session.setAttribute("profileErrorMsg", "Không thể đổi mật khẩu, vui lòng thử lại!");
        }
    }

    private void handleCancelOrder(HttpServletRequest request, HttpSession session, User currentUser) {
        int orderId = parseIntOrDefault(request.getParameter("orderId"), 0);
        Order order = orderDAO.searchOrder(orderId);

        if (order == null || order.getUserId() != currentUser.getUserId()) {
            session.setAttribute("profileErrorMsg", "Không tìm thấy đơn hàng #" + orderId + " của bạn!");
            return;
        }

        if (!"Pending".equalsIgnoreCase(order.getOrderStatus())) {
            session.setAttribute("profileErrorMsg", "Chỉ có thể hủy đơn hàng đang ở trạng thái Chờ xử lý (Pending)!");
            return;
        }

        order.setOrderStatus("Cancelled");
        int n = orderDAO.updateOrder(order);
        if (n > 0) {
            // Hoàn lại số lượng tồn kho cho các sách trong đơn đã hủy
            Vector<OrderItem> items = orderItemDAO.getOrderItemsByOrderId(orderId);
            for (OrderItem oi : items) {
                Book b = bookDAO.searchBook(oi.getBookId());
                if (b != null) {
                    b.setStockQuantity(b.getStockQuantity() + oi.getQuantity());
                    bookDAO.updateBook(b);
                }
            }
            session.setAttribute("profileSuccessMsg", "Đã hủy đơn hàng #" + orderId + " và hoàn lại số lượng tồn kho.");
        } else {
            session.setAttribute("profileErrorMsg", "Không thể hủy đơn hàng #" + orderId + "!");
        }
    }

    private String trimParam(String val) {
        return val != null ? val.trim() : "";
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

