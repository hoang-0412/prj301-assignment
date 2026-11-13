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

@WebServlet(name = "CatalogController", urlPatterns = {"/catalog", "/books"})
public class CatalogController extends HttpServlet {

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
            service = "listCatalog";
        }
        service = service.trim();

        if ("listCatalog".equals(service) || "filterCatalog".equals(service)) {

        String keyword = trimParam(request.getParameter("keyword"));
        Integer categoryId = parseIntOrNull(request.getParameter("categoryId"));
        String bookStatus = trimParam(request.getParameter("status"));
        Double minPrice = parseDoubleOrNull(request.getParameter("minPrice"));
        Double maxPrice = parseDoubleOrNull(request.getParameter("maxPrice"));
        boolean inStockOnly = "true".equalsIgnoreCase(request.getParameter("inStock"))
                || "1".equals(request.getParameter("inStock"));
        String sort = trimParam(request.getParameter("sort"));
        if (sort.isEmpty()) {
            sort = "newest";
        }

        int page = 1;
        Integer pageParsed = parseIntOrNull(request.getParameter("page"));
        if (pageParsed != null && pageParsed > 0) {
            page = pageParsed;
        }
        int pageSize = 9; // Lưới 3 cột x 3 hàng bên cạnh Sidebar

        // Lấy toàn bộ sách để thống kê số lượng theo hình thức (Bán / Cho thuê / Đã thuê)
        Vector<Book> allBooks = bookDAO.getBooksWithDetails(null, null, "newest", 1, 1000);
        int allCount = allBooks.size();
        int saleCount = 0;
        int rentCount = 0;
        int rentedCount = 0;
        for (Book b : allBooks) {
            String st = b.getBookStatus() != null ? b.getBookStatus().trim() : "";
            if ("Bán".equalsIgnoreCase(st)) {
                saleCount++;
            } else if ("Cho thuê".equalsIgnoreCase(st)) {
                rentCount++;
            } else if ("Đã thuê".equalsIgnoreCase(st)) {
                rentedCount++;
            }
        }

        // Lọc theo keyword, categoryId và sort từ DAO
        Vector<Book> rawFiltered = bookDAO.getBooksWithDetails(
                keyword.isEmpty() ? null : keyword,
                categoryId,
                sort,
                1,
                1000
        );

        // Áp dụng bộ lọc nâng cao: bookStatus, minPrice, maxPrice, inStockOnly
        Vector<Book> filteredBooks = new Vector<>();
        for (Book b : rawFiltered) {
            if (!bookStatus.isEmpty()) {
                String st = b.getBookStatus() != null ? b.getBookStatus().trim() : "";
                if (!st.equalsIgnoreCase(bookStatus)) {
                    continue;
                }
            }
            if (minPrice != null && b.getPrice() < minPrice) {
                continue;
            }
            if (maxPrice != null && b.getPrice() > maxPrice) {
                continue;
            }
            if (inStockOnly && b.getStockQuantity() <= 0) {
                continue;
            }
            filteredBooks.add(b);
        }

        int totalFiltered = filteredBooks.size();
        int totalPages = (int) Math.ceil((double) totalFiltered / pageSize);
        if (totalPages <= 0) {
            totalPages = 1;
        }
        if (page > totalPages) {
            page = totalPages;
        }

        int fromIndex = (page - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, totalFiltered);
        Vector<Book> pagedBooks = new Vector<>();
        if (fromIndex < totalFiltered) {
            for (int i = fromIndex; i < toIndex; i++) {
                pagedBooks.add(filteredBooks.get(i));
            }
        }

        Vector<Category> categories = categoryDAO.getAllCategory();

        // Tính số lượng giỏ hàng
        HttpSession session = request.getSession(true);
        int cartCount = computeCartCount(session);

        request.setAttribute("books", pagedBooks);
        request.setAttribute("totalBooks", totalFiltered);
        request.setAttribute("allCount", allCount);
        request.setAttribute("saleCount", saleCount);
        request.setAttribute("rentCount", rentCount);
        request.setAttribute("rentedCount", rentedCount);
        request.setAttribute("categories", categories);
        request.setAttribute("cartCount", cartCount);

        request.setAttribute("keyword", keyword);
        request.setAttribute("selectedCategoryId", categoryId);
        request.setAttribute("selectedStatus", bookStatus);
        request.setAttribute("minPrice", minPrice != null ? String.format("%.0f", minPrice) : "");
        request.setAttribute("maxPrice", maxPrice != null ? String.format("%.0f", maxPrice) : "");
        request.setAttribute("inStockOnly", inStockOnly);
        request.setAttribute("selectedSort", sort);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);

        request.getRequestDispatcher("/jsp/catalog.jsp").forward(request, response);
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
                    for (CartItem item : items) {
                        cartCount += item.getQuantity();
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

    private String trimParam(String val) {
        return val != null ? val.trim() : "";
    }

    private Integer parseIntOrNull(String val) {
        if (val == null || val.trim().isEmpty()) {
            return null;
        }
        try {
            int parsed = Integer.parseInt(val.trim());
            return parsed > 0 ? parsed : null;
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private Double parseDoubleOrNull(String val) {
        if (val == null || val.trim().isEmpty()) {
            return null;
        }
        try {
            double parsed = Double.parseDouble(val.trim());
            return parsed >= 0 ? parsed : null;
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}

