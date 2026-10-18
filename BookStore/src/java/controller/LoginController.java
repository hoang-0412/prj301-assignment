package controller;

import dal.CartDAO;
import dal.CartItemDAO;
import dal.UserDAO;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Cart;
import model.User;

@WebServlet(name = "LoginController", urlPatterns = {"/login", "/auth/login"})
public class LoginController extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final CartDAO cartDAO = new CartDAO();
    private final CartItemDAO cartItemDAO = new CartItemDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            response.sendRedirect(request.getContextPath() + "/homepage");
            return;
        }

        request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if (action == null || action.trim().isEmpty()) {
            action = "login";
        }

        String isAjax = request.getHeader("X-Requested-With");
        boolean ajax = "XMLHttpRequest".equalsIgnoreCase(isAjax) || "true".equalsIgnoreCase(request.getParameter("ajax"));

        if ("check".equalsIgnoreCase(action)) {
            handleCheckIdentifier(request, response);
        } else if ("register".equalsIgnoreCase(action)) {
            handleRegister(request, response, ajax);
        } else {
            handleLogin(request, response, ajax);
        }
    }

    private void handleCheckIdentifier(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String identifier = request.getParameter("identifier");

        if (identifier == null || identifier.trim().isEmpty()) {
            out.print("{\"status\":\"error\",\"message\":\"Vui lòng nhập số điện thoại hoặc email!\"}");
            return;
        }

        identifier = identifier.trim();
        User user = userDAO.getUserByEmailOrPhone(identifier);

        if (user != null) {
            String safeName = escapeJson(user.getFullName());
            out.print("{\"status\":\"exists\",\"identifier\":\"" + escapeJson(identifier) + "\",\"fullName\":\"" + safeName + "\"}");
        } else {
            out.print("{\"status\":\"not_found\",\"identifier\":\"" + escapeJson(identifier) + "\",\"message\":\"Tài khoản chưa tồn tại. Bạn có thể đăng ký tài khoản mới!\"}");
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response, boolean ajax)
            throws ServletException, IOException {
        String identifier = request.getParameter("identifier");
        String password = request.getParameter("password");

        if (identifier != null) identifier = identifier.trim();
        if (password != null) password = password.trim();

        if (identifier == null || identifier.isEmpty() || password == null || password.isEmpty()) {
            if (ajax) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().print("{\"status\":\"error\",\"message\":\"Vui lòng nhập đầy đủ thông tin đăng nhập!\"}");
            } else {
                request.setAttribute("error", "Vui lòng nhập đầy đủ thông tin đăng nhập!");
                request.setAttribute("identifier", identifier);
                request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
            }
            return;
        }

        User user = userDAO.getUserByEmailOrPhone(identifier);

        if (user == null) {
            if (ajax) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().print("{\"status\":\"error\",\"message\":\"Tài khoản không tồn tại trên hệ thống!\"}");
            } else {
                request.setAttribute("error", "Tài khoản không tồn tại trên hệ thống!");
                request.setAttribute("identifier", identifier);
                request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
            }
            return;
        }

        if (!userDAO.checkPassword(user, password)) {
            if (ajax) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().print("{\"status\":\"error\",\"message\":\"Mật khẩu không chính xác. Vui lòng kiểm tra lại!\"}");
            } else {
                request.setAttribute("error", "Mật khẩu không chính xác. Vui lòng kiểm tra lại!");
                request.setAttribute("identifier", identifier);
                request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
            }
            return;
        }

        // Login success
        HttpSession session = request.getSession(true);
        session.setAttribute("user", user);

        // Fetch cart quantity
        int cartCount = 0;
        try {
            Cart cart = cartDAO.getCartByUserId(user.getUserId());
            if (cart != null) {
                java.util.Vector<model.CartItem> items = cartItemDAO.getCartItemsByCartId(cart.getCartId());
                if (items != null) {
                    for (model.CartItem item : items) {
                        cartCount += item.getQuantity();
                    }
                }
            }
        } catch (Exception ignored) {
        }
        session.setAttribute("cartCount", cartCount);

        String redirectUrl = request.getContextPath() + "/homepage";

        if (ajax) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().print("{\"status\":\"success\",\"message\":\"Đăng nhập thành công!\",\"redirect\":\"" + redirectUrl + "\",\"fullName\":\"" + escapeJson(user.getFullName()) + "\"}");
        } else {
            response.sendRedirect(redirectUrl);
        }
    }

    private void handleRegister(HttpServletRequest request, HttpServletResponse response, boolean ajax)
            throws ServletException, IOException {
        String identifier = request.getParameter("identifier");
        String password = request.getParameter("password");
        String fullName = request.getParameter("fullName");

        if (identifier != null) identifier = identifier.trim();
        if (password != null) password = password.trim();
        if (fullName != null) fullName = fullName.trim();

        if (identifier == null || identifier.isEmpty() || password == null || password.isEmpty() || fullName == null || fullName.isEmpty()) {
            if (ajax) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().print("{\"status\":\"error\",\"message\":\"Vui lòng điền đầy đủ họ tên, tài khoản và mật khẩu!\"}");
            } else {
                request.setAttribute("error", "Vui lòng điền đầy đủ thông tin đăng ký!");
                request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
            }
            return;
        }

        User existing = userDAO.getUserByEmailOrPhone(identifier);
        if (existing != null) {
            if (ajax) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().print("{\"status\":\"error\",\"message\":\"Tài khoản này đã tồn tại trên hệ thống!\"}");
            } else {
                request.setAttribute("error", "Tài khoản này đã tồn tại trên hệ thống!");
                request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
            }
            return;
        }

        String email = identifier.contains("@") ? identifier : (identifier + "@phone.bookstore.com");
        String phone = identifier.contains("@") ? "" : identifier;

        User newUser = new User(1, fullName, email, password, phone, "Chưa cập nhật", new Date(System.currentTimeMillis()));
        int n = userDAO.insertUser(newUser);

        if (n > 0) {
            HttpSession session = request.getSession(true);
            session.setAttribute("user", newUser);
            session.setAttribute("cartCount", 0);

            String redirectUrl = request.getContextPath() + "/homepage";
            if (ajax) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().print("{\"status\":\"success\",\"message\":\"Đăng ký tài khoản thành công!\",\"redirect\":\"" + redirectUrl + "\",\"fullName\":\"" + escapeJson(newUser.getFullName()) + "\"}");
            } else {
                response.sendRedirect(redirectUrl);
            }
        } else {
            if (ajax) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().print("{\"status\":\"error\",\"message\":\"Đăng ký thất bại. Vui lòng thử lại!\"}");
            } else {
                request.setAttribute("error", "Đăng ký thất bại. Vui lòng thử lại!");
                request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
            }
        }
    }

    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\b", "\\b")
                  .replace("\f", "\\f")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
}
