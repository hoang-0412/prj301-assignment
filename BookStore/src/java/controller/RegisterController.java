package controller;

import dal.CartDAO;
import dal.CategoryDAO;
import dal.UserDAO;
import java.io.IOException;
import java.sql.Date;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Cart;
import model.User;

@WebServlet(name = "RegisterController", urlPatterns = {"/register"})
public class RegisterController extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final CartDAO cartDAO = new CartDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String service = request.getParameter("service");
        if (service == null || service.trim().isEmpty()) {
            service = request.getParameter("action");
        }
        if (service == null || service.trim().isEmpty()) {
            service = "register";
        }
        service = service.trim();

        if ("register".equals(service)) {
            String submit = request.getParameter("submit");
            if (!"POST".equalsIgnoreCase(request.getMethod()) && submit == null) {
                HttpSession session = request.getSession(true);
                if (session.getAttribute("user") != null) {
                    response.sendRedirect(request.getContextPath() + "/home");
                    return;
                }

                request.setAttribute("categories", categoryDAO.getAllCategory());
                request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);
            } else {

        String fullName = trimParam(request.getParameter("fullName"));
        String email = trimParam(request.getParameter("email"));
        String phoneNumber = trimParam(request.getParameter("phoneNumber"));
        String address = trimParam(request.getParameter("address"));
        String password = trimParam(request.getParameter("password"));
        String confirmPassword = trimParam(request.getParameter("confirmPassword"));

        request.setAttribute("fullName", fullName);
        request.setAttribute("email", email);
        request.setAttribute("phoneNumber", phoneNumber);
        request.setAttribute("address", address);
        request.setAttribute("categories", categoryDAO.getAllCategory());

        if (fullName.isEmpty() || email.isEmpty() || phoneNumber.isEmpty() || password.isEmpty()) {
            request.setAttribute("errorMsg", "Vui lòng điền đầy đủ các trường thông tin bắt buộc (*)!");
            request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);
            return;
        }

        if (password.length() < 6) {
            request.setAttribute("errorMsg", "Mật khẩu phải có độ dài tối thiểu 6 ký tự!");
            request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);
            return;
        }

        if (!password.equals(confirmPassword)) {
            request.setAttribute("errorMsg", "Mật khẩu xác nhận không khớp!");
            request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);
            return;
        }

        if (userDAO.getUserByEmail(email) != null) {
            request.setAttribute("errorMsg", "Địa chỉ email \"" + email + "\" đã được đăng ký trên hệ thống!");
            request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);
            return;
        }

        if (userDAO.getUserByEmailOrPhone(phoneNumber) != null) {
            request.setAttribute("errorMsg", "Số điện thoại \"" + phoneNumber + "\" đã được sử dụng!");
            request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);
            return;
        }

        Date now = new Date(System.currentTimeMillis());
        User newUser = new User(
                1, // Role Customer
                fullName,
                email,
                password,
                phoneNumber,
                address.isEmpty() ? "Chưa cập nhật" : address,
                now
        );

        int n = userDAO.insertUser(newUser);
        if (n > 0 && newUser.getUserId() > 0) {
            // Khởi tạo giỏ hàng cho tài khoản mới
            Cart cart = new Cart(newUser.getUserId(), now, now);
            cartDAO.insertCart(cart);

            HttpSession session = request.getSession(true);
            session.setAttribute("user", newUser);
            session.setAttribute("profileSuccessMsg", "Chào mừng " + fullName + "! Tài khoản của bạn đã được tạo thành công.");
            response.sendRedirect(request.getContextPath() + "/profile");
        } else {
            request.setAttribute("errorMsg", "Đăng ký tài khoản thất bại, vui lòng thử lại!");
            request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);
        }
            }
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

    private String trimParam(String val) {
        return val != null ? val.trim() : "";
    }
}

