package controller;

import dal.BookDAO;
import dal.CartDAO;
import dal.CartItemDAO;
import dal.CategoryDAO;
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
import model.User;

@WebServlet(name = "HomeController", urlPatterns = {"/homepage"})
public class HomeController extends HttpServlet {

    private final BookDAO bookDAO = new BookDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final CartDAO cartDAO = new CartDAO();
    private final CartItemDAO cartItemDAO = new CartItemDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        // 1. Lấy các tham số lọc, tìm kiếm, sắp xếp và phân trang
        String keyword = request.getParameter("keyword");
        if (keyword != null) {
            keyword = keyword.trim();
        }

        Integer categoryId = null;
        String catParam = request.getParameter("categoryId");
        if (catParam != null && !catParam.trim().isEmpty()) {
            try {
                int cid = Integer.parseInt(catParam.trim());
                if (cid > 0) {
                    categoryId = cid;
                }
            } catch (NumberFormatException ignored) {
            }
        }

        String sort = request.getParameter("sort");
        if (sort == null || sort.trim().isEmpty()) {
            sort = "newest";
        }

        int page = 1;
        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.trim().isEmpty()) {
            try {
                int p = Integer.parseInt(pageParam.trim());
                if (p > 0) {
                    page = p;
                }
            } catch (NumberFormatException ignored) {
            }
        }

        int pageSize = 8; // 8 cuốn sách mỗi trang hiển thị lưới 4 cột x 2 hàng

        // 2. Truy vấn dữ liệu từ DAL
        Vector<Category> categories = categoryDAO.getAllCategory();
        Vector<Book> featuredBooks = bookDAO.getFeaturedBooks(4);
        Vector<Book> newestBooks = bookDAO.getNewestBooks(4);

        int totalBooks = bookDAO.getTotalBooksCount(keyword, categoryId);
        int totalPages = (int) Math.ceil((double) totalBooks / pageSize);
        if (totalPages <= 0) {
            totalPages = 1;
        }
        if (page > totalPages) {
            page = totalPages;
        }

        Vector<Book> books = bookDAO.getBooksWithDetails(keyword, categoryId, sort, page, pageSize);

        // Tính tổng toàn bộ sách trong cửa hàng
        int totalAllBooks = 0;
        for (Category cat : categories) {
            totalAllBooks += cat.getBookCount();
        }

        // 3. Xử lý số lượng giỏ hàng nếu có User trong session
        HttpSession session = request.getSession();
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
            Object sessionCartCount = session.getAttribute("cartCount");
            if (sessionCartCount instanceof Integer) {
                cartCount = (Integer) sessionCartCount;
            }
        }
        request.setAttribute("cartCount", cartCount);

        // 4. Đặt thuộc tính request
        request.setAttribute("categories", categories);
        request.setAttribute("featuredBooks", featuredBooks);
        request.setAttribute("newestBooks", newestBooks);
        request.setAttribute("books", books);
        request.setAttribute("keyword", keyword);
        request.setAttribute("selectedCategoryId", categoryId);
        request.setAttribute("selectedSort", sort);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalBooks", totalBooks);
        request.setAttribute("totalAllBooks", totalAllBooks);

        // 5. Chuyển hướng sang /jsp/homepage.jsp
        request.getRequestDispatcher("/jsp/homepage.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
