package controller;

import dal.BookDAO;
import dal.CartDAO;
import dal.CartItemDAO;
import dal.CategoryDAO;
import java.io.IOException;
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
import model.User;

@WebServlet(name = "BookDetailController", urlPatterns = {"/book-detail", "/book"})
public class BookDetailController extends HttpServlet {

    private final BookDAO bookDAO = new BookDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final CartDAO cartDAO = new CartDAO();
    private final CartItemDAO cartItemDAO = new CartItemDAO();

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String service = request.getParameter("service");
        if (service == null || service.trim().isEmpty()) {
            service = request.getParameter("action");
        }
        if (service == null || service.trim().isEmpty()) {
            service = "viewDetail";
        }
        service = service.trim();

        if ("viewDetail".equals(service)) {

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            idParam = request.getParameter("bookId");
        }

        int bookId = 0;
        if (idParam != null) {
            try {
                bookId = Integer.parseInt(idParam.trim());
            } catch (NumberFormatException ignored) {
            }
        }

        if (bookId <= 0) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        Book book = bookDAO.searchBook(bookId);
        if (book == null) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        // Lấy danh sách sách liên quan cùng thể loại
        Vector<Book> sameCategoryBooks = bookDAO.getBooksWithDetails(null, book.getCategoryId(), "newest", 1, 8);
        Vector<Book> relatedBooks = new Vector<>();
        for (Book b : sameCategoryBooks) {
            if (b.getBookId() != book.getBookId()) {
                relatedBooks.add(b);
                if (relatedBooks.size() >= 4) {
                    break;
                }
            }
        }

        Vector<Category> categories = categoryDAO.getAllCategory();

        // Tính số lượng giỏ hàng
        HttpSession session = request.getSession(true);
        int cartCount = computeCartCount(session);

        request.setAttribute("book", book);
        request.setAttribute("relatedBooks", relatedBooks);
        request.setAttribute("categories", categories);
        request.setAttribute("cartCount", cartCount);

        request.getRequestDispatcher("/jsp/bookdetail.jsp").forward(request, response);
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

    private int computeCartCount(HttpSession session) {
        int cartCount = 0;
        User currentUser = (User) session.getAttribute("user");
        if (currentUser != null) {
            try {
                Cart cart = cartDAO.getCartByUserId(currentUser.getUserId());
                if (cart != null) {
                    Vector<CartItem> items = cartItemDAO.getCartItemsByCartId(cart.getCartId());
                    if (items != null) {
                        for (CartItem item : items) {
                            cartCount += item.getQuantity();
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        } else {
            @SuppressWarnings("unchecked")
            Map<Integer, Integer> guestCart = (Map<Integer, Integer>) session.getAttribute("guestCart");
            if (guestCart != null) {
                for (Integer qty : guestCart.values()) {
                    if (qty != null && qty > 0) {
                        cartCount += qty;
                    }
                }
            }
        }
        session.setAttribute("cartCount", cartCount);
        return cartCount;
    }
}

