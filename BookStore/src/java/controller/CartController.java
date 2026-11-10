package controller;

import dal.BookDAO;
import dal.CartDAO;
import dal.CartItemDAO;
import dal.CategoryDAO;
import dal.OrderDAO;
import dal.OrderItemDAO;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.util.LinkedHashMap;
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

@WebServlet(name = "CartController", urlPatterns = {"/cart", "/checkout"})
public class CartController extends HttpServlet {

    private final BookDAO bookDAO = new BookDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final CartDAO cartDAO = new CartDAO();
    private final CartItemDAO cartItemDAO = new CartItemDAO();
    private final OrderDAO orderDAO = new OrderDAO();
    private final OrderItemDAO orderItemDAO = new OrderItemDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(true);
        User currentUser = (User) session.getAttribute("user");

        // Nếu người dùng vừa đăng nhập mà có giỏ hàng tạm trong session thì gộp vào DB
        if (currentUser != null) {
            mergeGuestCartIntoDb(session, currentUser.getUserId());
        }

        // Flash messages
        String successMsg = (String) session.getAttribute("cartSuccessMsg");
        String errorMsg = (String) session.getAttribute("cartErrorMsg");
        if (successMsg != null) {
            request.setAttribute("successMsg", successMsg);
            session.removeAttribute("cartSuccessMsg");
        }
        if (errorMsg != null) {
            request.setAttribute("errorMsg", errorMsg);
            session.removeAttribute("cartErrorMsg");
        }

        Vector<CartItem> cartItems = loadCartItemsWithBooks(session, currentUser);
        int cartCount = 0;
        double totalAmount = 0;
        for (CartItem ci : cartItems) {
            cartCount += ci.getQuantity();
            totalAmount += ci.getSubtotal();
        }
        session.setAttribute("cartCount", cartCount);

        String formattedTotal = String.format("%,.0f đ", totalAmount).replace(',', '.');
        Vector<Category> categories = categoryDAO.getAllCategory();

        request.setAttribute("cartItems", cartItems);
        request.setAttribute("cartCount", cartCount);
        request.setAttribute("totalAmount", totalAmount);
        request.setAttribute("formattedTotal", formattedTotal);
        request.setAttribute("categories", categories);

        request.getRequestDispatcher("/jsp/cart.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(true);
        User currentUser = (User) session.getAttribute("user");
        if (currentUser != null) {
            mergeGuestCartIntoDb(session, currentUser.getUserId());
        }

        String action = request.getParameter("action");
        if (action == null || action.trim().isEmpty()) {
            action = "add";
        }

        boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"))
                || "true".equalsIgnoreCase(request.getParameter("ajax"));

        switch (action) {
            case "add":
                handleAddToCart(request, response, session, currentUser, isAjax);
                break;
            case "update":
                handleUpdateCartItem(request, session, currentUser);
                response.sendRedirect(request.getContextPath() + "/cart");
                break;
            case "remove":
                handleRemoveCartItem(request, session, currentUser);
                response.sendRedirect(request.getContextPath() + "/cart");
                break;
            case "clear":
                handleClearCart(session, currentUser);
                response.sendRedirect(request.getContextPath() + "/cart");
                break;
            case "checkout":
                handleCheckout(request, response, session, currentUser);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/cart");
                break;
        }
    }

    private void handleAddToCart(HttpServletRequest request, HttpServletResponse response,
                                 HttpSession session, User currentUser, boolean isAjax) throws IOException {
        int bookId = parseIntOrDefault(request.getParameter("bookId"), 0);
        int quantity = parseIntOrDefault(request.getParameter("quantity"), 1);
        if (quantity < 1) {
            quantity = 1;
        }

        Book book = bookDAO.searchBook(bookId);
        if (book == null || book.getStockQuantity() <= 0) {
            String msg = "Sách hiện đã hết hàng hoặc không tồn tại!";
            if (isAjax) {
                sendJson(response, false, getCartCount(session, currentUser), msg);
            } else {
                session.setAttribute("cartErrorMsg", msg);
                response.sendRedirect(request.getContextPath() + "/cart");
            }
            return;
        }

        if (currentUser != null) {
            Cart cart = getOrCreateUserCart(currentUser.getUserId());
            if (cart != null) {
                CartItem existing = cartItemDAO.getCartItemByCartAndBook(cart.getCartId(), bookId);
                if (existing != null) {
                    int newQty = Math.min(book.getStockQuantity(), existing.getQuantity() + quantity);
                    existing.setQuantity(newQty);
                    cartItemDAO.updateCartItem(existing);
                } else {
                    int addQty = Math.min(book.getStockQuantity(), quantity);
                    CartItem newItem = new CartItem(cart.getCartId(), bookId, addQty, new Date(System.currentTimeMillis()));
                    cartItemDAO.insertCartItem(newItem);
                }
                cart.setUpdatedAt(new Date(System.currentTimeMillis()));
                cartDAO.updateCart(cart);
            }
        } else {
            Map<Integer, Integer> guestCart = getOrCreateGuestCart(session);
            int currentQty = guestCart.getOrDefault(bookId, 0);
            int newQty = Math.min(book.getStockQuantity(), currentQty + quantity);
            guestCart.put(bookId, newQty);
        }

        int newCartCount = getCartCount(session, currentUser);
        session.setAttribute("cartCount", newCartCount);
        String msg = "Đã thêm \"" + book.getTitle() + "\" vào giỏ hàng!";

        if (isAjax) {
            sendJson(response, true, newCartCount, msg);
        } else {
            session.setAttribute("cartSuccessMsg", msg);
            response.sendRedirect(request.getContextPath() + "/cart");
        }
    }

    private void handleUpdateCartItem(HttpServletRequest request, HttpSession session, User currentUser) {
        int bookId = parseIntOrDefault(request.getParameter("bookId"), 0);
        int quantity = parseIntOrDefault(request.getParameter("quantity"), 1);

        Book book = bookDAO.searchBook(bookId);
        if (book == null) {
            return;
        }

        if (currentUser != null) {
            Cart cart = getOrCreateUserCart(currentUser.getUserId());
            if (cart != null) {
                CartItem existing = cartItemDAO.getCartItemByCartAndBook(cart.getCartId(), bookId);
                if (existing != null) {
                    if (quantity <= 0) {
                        cartItemDAO.deleteCartItem(existing.getCartItemId());
                    } else {
                        int capped = Math.min(quantity, Math.max(1, book.getStockQuantity()));
                        existing.setQuantity(capped);
                        cartItemDAO.updateCartItem(existing);
                    }
                }
            }
        } else {
            Map<Integer, Integer> guestCart = getOrCreateGuestCart(session);
            if (quantity <= 0) {
                guestCart.remove(bookId);
            } else {
                int capped = Math.min(quantity, Math.max(1, book.getStockQuantity()));
                guestCart.put(bookId, capped);
            }
        }
        session.setAttribute("cartCount", getCartCount(session, currentUser));
    }

    private void handleRemoveCartItem(HttpServletRequest request, HttpSession session, User currentUser) {
        int bookId = parseIntOrDefault(request.getParameter("bookId"), 0);
        if (currentUser != null) {
            Cart cart = getOrCreateUserCart(currentUser.getUserId());
            if (cart != null) {
                CartItem existing = cartItemDAO.getCartItemByCartAndBook(cart.getCartId(), bookId);
                if (existing != null) {
                    cartItemDAO.deleteCartItem(existing.getCartItemId());
                }
            }
        } else {
            Map<Integer, Integer> guestCart = getOrCreateGuestCart(session);
            guestCart.remove(bookId);
        }
        session.setAttribute("cartCount", getCartCount(session, currentUser));
        session.setAttribute("cartSuccessMsg", "Đã xóa sản phẩm khỏi giỏ hàng.");
    }

    private void handleClearCart(HttpSession session, User currentUser) {
        if (currentUser != null) {
            Cart cart = cartDAO.getCartByUserId(currentUser.getUserId());
            if (cart != null) {
                cartItemDAO.clearCart(cart.getCartId());
            }
        } else {
            session.removeAttribute("guestCart");
        }
        session.setAttribute("cartCount", 0);
        session.setAttribute("cartSuccessMsg", "Đã làm trống giỏ hàng.");
    }

    private void handleCheckout(HttpServletRequest request, HttpServletResponse response,
                                HttpSession session, User currentUser) throws IOException {
        if (currentUser == null) {
            session.setAttribute("cartErrorMsg", "Vui lòng đăng nhập tài khoản trước khi đặt hàng!");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Vector<CartItem> cartItems = loadCartItemsWithBooks(session, currentUser);
        if (cartItems.isEmpty()) {
            session.setAttribute("cartErrorMsg", "Giỏ hàng của bạn đang trống!");
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        String recipientPhone = trimParam(request.getParameter("recipientPhone"));
        String shippingAddress = trimParam(request.getParameter("shippingAddress"));
        String paymentMethod = trimParam(request.getParameter("paymentMethod"));

        if (recipientPhone.isEmpty()) {
            recipientPhone = currentUser.getPhoneNumber() != null ? currentUser.getPhoneNumber().trim() : "";
        }
        if (shippingAddress.isEmpty()) {
            shippingAddress = currentUser.getAddress() != null ? currentUser.getAddress().trim() : "";
        }
        if (recipientPhone.isEmpty() || shippingAddress.isEmpty()) {
            session.setAttribute("cartErrorMsg", "Vui lòng nhập đầy đủ số điện thoại và địa chỉ giao hàng!");
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }
        if (paymentMethod.isEmpty()) {
            paymentMethod = "COD";
        }

        // Kiểm tra tồn kho và tính tổng tiền
        double totalAmount = 0;
        for (CartItem ci : cartItems) {
            Book b = ci.getBook();
            if (b == null) {
                session.setAttribute("cartErrorMsg", "Có sản phẩm trong giỏ không còn tồn tại!");
                response.sendRedirect(request.getContextPath() + "/cart");
                return;
            }
            if (b.getStockQuantity() < ci.getQuantity()) {
                session.setAttribute("cartErrorMsg", "Sách \"" + b.getTitle() + "\" chỉ còn " + b.getStockQuantity() + " cuốn trong kho!");
                response.sendRedirect(request.getContextPath() + "/cart");
                return;
            }
            totalAmount += b.getPrice() * ci.getQuantity();
        }

        Order newOrder = new Order(
                currentUser.getUserId(),
                totalAmount,
                "Pending",
                shippingAddress,
                recipientPhone,
                paymentMethod,
                "Unpaid",
                new Date(System.currentTimeMillis())
        );

        int n = orderDAO.insertOrder(newOrder);
        if (n > 0 && newOrder.getOrderId() > 0) {
            int orderId = newOrder.getOrderId();
            for (CartItem ci : cartItems) {
                Book b = ci.getBook();
                OrderItem oi = new OrderItem(0, orderId, b.getBookId(), ci.getQuantity(), b.getPrice());
                orderItemDAO.insertOrderItem(oi);

                // Trừ tồn kho
                b.setStockQuantity(Math.max(0, b.getStockQuantity() - ci.getQuantity()));
                bookDAO.updateBook(b);
            }

            // Xóa giỏ hàng sau khi đặt đơn thành công
            Cart cart = cartDAO.getCartByUserId(currentUser.getUserId());
            if (cart != null) {
                cartItemDAO.clearCart(cart.getCartId());
            }
            session.setAttribute("cartCount", 0);
            session.setAttribute("profileSuccessMsg", "Đặt hàng thành công! Đơn hàng #" + orderId + " của bạn đã được ghi nhận.");
            response.sendRedirect(request.getContextPath() + "/my-orders");
        } else {
            session.setAttribute("cartErrorMsg", "Đặt hàng thất bại, vui lòng thử lại!");
            response.sendRedirect(request.getContextPath() + "/cart");
        }
    }

    // ==================== HELPERS ====================

    private Cart getOrCreateUserCart(int userId) {
        Cart cart = cartDAO.getCartByUserId(userId);
        if (cart == null) {
            Date now = new Date(System.currentTimeMillis());
            cart = new Cart(userId, now, now);
            cartDAO.insertCart(cart);
            if (cart.getCartId() <= 0) {
                cart = cartDAO.getCartByUserId(userId);
            }
        }
        return cart;
    }

    @SuppressWarnings("unchecked")
    private Map<Integer, Integer> getOrCreateGuestCart(HttpSession session) {
        Map<Integer, Integer> guestCart = (Map<Integer, Integer>) session.getAttribute("guestCart");
        if (guestCart == null) {
            guestCart = new LinkedHashMap<>();
            session.setAttribute("guestCart", guestCart);
        }
        return guestCart;
    }

    private void mergeGuestCartIntoDb(HttpSession session, int userId) {
        @SuppressWarnings("unchecked")
        Map<Integer, Integer> guestCart = (Map<Integer, Integer>) session.getAttribute("guestCart");
        if (guestCart == null || guestCart.isEmpty()) {
            return;
        }
        Cart cart = getOrCreateUserCart(userId);
        if (cart != null) {
            for (Map.Entry<Integer, Integer> entry : guestCart.entrySet()) {
                int bookId = entry.getKey();
                int qty = entry.getValue();
                if (bookId > 0 && qty > 0) {
                    Book book = bookDAO.searchBook(bookId);
                    if (book != null && book.getStockQuantity() > 0) {
                        CartItem existing = cartItemDAO.getCartItemByCartAndBook(cart.getCartId(), bookId);
                        if (existing != null) {
                            existing.setQuantity(Math.min(book.getStockQuantity(), existing.getQuantity() + qty));
                            cartItemDAO.updateCartItem(existing);
                        } else {
                            CartItem ci = new CartItem(cart.getCartId(), bookId, Math.min(book.getStockQuantity(), qty), new Date(System.currentTimeMillis()));
                            cartItemDAO.insertCartItem(ci);
                        }
                    }
                }
            }
        }
        session.removeAttribute("guestCart");
    }

    private Vector<CartItem> loadCartItemsWithBooks(HttpSession session, User currentUser) {
        Vector<CartItem> result = new Vector<>();
        if (currentUser != null) {
            Cart cart = cartDAO.getCartByUserId(currentUser.getUserId());
            if (cart != null) {
                Vector<CartItem> rawItems = cartItemDAO.getCartItemsByCartId(cart.getCartId());
                for (CartItem ci : rawItems) {
                    Book b = bookDAO.searchBook(ci.getBookId());
                    if (b != null) {
                        ci.setBook(b);
                        result.add(ci);
                    }
                }
            }
        } else {
            @SuppressWarnings("unchecked")
            Map<Integer, Integer> guestCart = (Map<Integer, Integer>) session.getAttribute("guestCart");
            if (guestCart != null) {
                for (Map.Entry<Integer, Integer> entry : guestCart.entrySet()) {
                    Book b = bookDAO.searchBook(entry.getKey());
                    if (b != null) {
                        CartItem ci = new CartItem(0, 0, b.getBookId(), entry.getValue(), new Date(System.currentTimeMillis()));
                        ci.setBook(b);
                        result.add(ci);
                    }
                }
            }
        }
        return result;
    }

    private int getCartCount(HttpSession session, User currentUser) {
        int count = 0;
        Vector<CartItem> items = loadCartItemsWithBooks(session, currentUser);
        for (CartItem ci : items) {
            count += ci.getQuantity();
        }
        return count;
    }

    private void sendJson(HttpServletResponse response, boolean success, int cartCount, String message) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            String safeMsg = message != null ? message.replace("\\", "\\\\").replace("\"", "\\\"") : "";
            out.print("{\"success\":" + success + ",\"cartCount\":" + cartCount + ",\"message\":\"" + safeMsg + "\"}");
            out.flush();
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

