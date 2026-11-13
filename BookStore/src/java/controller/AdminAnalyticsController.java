package controller;

import dal.BookDAO;
import dal.CategoryDAO;
import dal.OrderDAO;
import dal.UserDAO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Book;
import model.Category;
import model.Order;
import model.OrderItem;
import model.User;

@WebServlet(name = "AdminAnalyticsController", urlPatterns = {"/admin-analytics"})
public class AdminAnalyticsController extends HttpServlet {

    private final BookDAO bookDAO = new BookDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final OrderDAO orderDAO = new OrderDAO();
    private final UserDAO userDAO = new UserDAO();

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String service = request.getParameter("service");
        if (service == null || service.trim().isEmpty()) {
            service = request.getParameter("action");
        }
        if (service == null || service.trim().isEmpty()) {
            service = "viewAnalytics";
        }
        service = service.trim();

        if ("viewAnalytics".equals(service)) {

        HttpSession session = request.getSession(true);
        User currentUser = (User) session.getAttribute("user");

        // Nếu đang đăng nhập bằng tài khoản Customer (roleId != 2) thì chuyển về trang chủ
        if (currentUser != null && currentUser.getRoleId() != 2) {
            response.sendRedirect(request.getContextPath() + "/homepage");
            return;
        }

        Vector<Book> allBooks = bookDAO.getBooksWithDetails(null, null, "newest", 1, 1000);
        Vector<Category> categories = categoryDAO.getAllCategory();
        Vector<Order> orders = orderDAO.getAllOrder();
        Vector<User> users = userDAO.getAllUser();

        Map<Integer, Book> bookById = new HashMap<>();
        double totalInventoryValue = 0;
        int totalStockUnits = 0;
        for (Book b : allBooks) {
            bookById.put(b.getBookId(), b);
            totalInventoryValue += b.getPrice() * b.getStockQuantity();
            totalStockUnits += b.getStockQuantity();
        }

        // 1. Thống kê đơn hàng & doanh thu
        double totalRevenue = 0;
        double paidRevenue = 0;
        int validOrdersCount = 0;
        int totalItemsSold = 0;

        Map<String, Integer> statusCounts = new LinkedHashMap<>();
        statusCounts.put("Pending", 0);
        statusCounts.put("Processing", 0);
        statusCounts.put("Shipped", 0);
        statusCounts.put("Completed", 0);
        statusCounts.put("Cancelled", 0);

        Map<String, Integer> paymentMethodCounts = new LinkedHashMap<>();
        Map<Integer, Integer> soldQtyByBook = new HashMap<>();
        Map<Integer, Double> revenueByBook = new HashMap<>();
        Map<Integer, Integer> orderCountByUser = new HashMap<>();
        Map<Integer, Double> spentByUser = new HashMap<>();

        for (Order o : orders) {
            String st = o.getOrderStatus() != null ? o.getOrderStatus().trim() : "Pending";
            statusCounts.put(st, statusCounts.getOrDefault(st, 0) + 1);

            String pm = o.getPaymentMethod() != null ? o.getPaymentMethod().trim() : "COD";
            paymentMethodCounts.put(pm, paymentMethodCounts.getOrDefault(pm, 0) + 1);

            if (!"Cancelled".equalsIgnoreCase(st)) {
                totalRevenue += o.getTotalAmount();
                validOrdersCount++;
                if ("Paid".equalsIgnoreCase(o.getPaymentStatus())) {
                    paidRevenue += o.getTotalAmount();
                }

                orderCountByUser.put(o.getUserId(), orderCountByUser.getOrDefault(o.getUserId(), 0) + 1);
                spentByUser.put(o.getUserId(), spentByUser.getOrDefault(o.getUserId(), 0.0) + o.getTotalAmount());

                if (o.getItems() != null) {
                    for (OrderItem item : o.getItems()) {
                        totalItemsSold += item.getQuantity();
                        soldQtyByBook.put(item.getBookId(), soldQtyByBook.getOrDefault(item.getBookId(), 0) + item.getQuantity());
                        revenueByBook.put(item.getBookId(), revenueByBook.getOrDefault(item.getBookId(), 0.0) + (item.getQuantity() * item.getUnitPrice()));
                    }
                }
            }
        }

        double avgOrderValue = validOrdersCount > 0 ? (totalRevenue / validOrdersCount) : 0;

        // 2. Top Sách Bán Chạy Nhất
        List<Map<String, Object>> topSellingBooks = new ArrayList<>();
        for (Book b : allBooks) {
            int sold = soldQtyByBook.getOrDefault(b.getBookId(), 0);
            double rev = revenueByBook.getOrDefault(b.getBookId(), 0.0);
            Map<String, Object> row = new HashMap<>();
            row.put("book", b);
            row.put("soldQty", sold);
            row.put("revenue", rev);
            row.put("formattedRevenue", formatVND(rev));
            topSellingBooks.add(row);
        }
        topSellingBooks.sort((a, b) -> {
            int cmp = Integer.compare((Integer) b.get("soldQty"), (Integer) a.get("soldQty"));
            if (cmp != 0) return cmp;
            return Double.compare((Double) b.get("revenue"), (Double) a.get("revenue"));
        });
        if (topSellingBooks.size() > 6) {
            topSellingBooks = topSellingBooks.subList(0, 6);
        }

        // 3. Thống kê theo Thể Loại (Category)
        List<Map<String, Object>> categoryStats = new ArrayList<>();
        for (Category c : categories) {
            int catStock = 0;
            double catInvValue = 0;
            int catSold = 0;
            double catRevenue = 0;

            for (Book b : allBooks) {
                if (b.getCategoryId() == c.getCategoryId()) {
                    catStock += b.getStockQuantity();
                    catInvValue += b.getPrice() * b.getStockQuantity();
                    catSold += soldQtyByBook.getOrDefault(b.getBookId(), 0);
                    catRevenue += revenueByBook.getOrDefault(b.getBookId(), 0.0);
                }
            }

            int stockPercent = totalStockUnits > 0 ? (int) Math.round((catStock * 100.0) / totalStockUnits) : 0;
            Map<String, Object> row = new HashMap<>();
            row.put("category", c);
            row.put("totalStock", catStock);
            row.put("formattedInvValue", formatVND(catInvValue));
            row.put("soldQty", catSold);
            row.put("formattedRevenue", formatVND(catRevenue));
            row.put("stockPercent", stockPercent);
            categoryStats.add(row);
        }

        // 4. Top Khách Hàng (VIP Customers)
        List<Map<String, Object>> topCustomers = new ArrayList<>();
        for (User u : users) {
            int oCount = orderCountByUser.getOrDefault(u.getUserId(), 0);
            double spent = spentByUser.getOrDefault(u.getUserId(), 0.0);
            Map<String, Object> row = new HashMap<>();
            row.put("user", u);
            row.put("orderCount", oCount);
            row.put("totalSpent", spent);
            row.put("formattedSpent", formatVND(spent));
            topCustomers.add(row);
        }
        topCustomers.sort((a, b) -> Double.compare((Double) b.get("totalSpent"), (Double) a.get("totalSpent")));

        // 5. Cảnh báo sách tồn kho thấp (<= 15 cuốn)
        Vector<Book> lowStockBooks = new Vector<>();
        for (Book b : allBooks) {
            if (b.getStockQuantity() <= 15) {
                lowStockBooks.add(b);
            }
        }
        lowStockBooks.sort(Comparator.comparingInt(Book::getStockQuantity));

        request.setAttribute("formattedTotalRevenue", formatVND(totalRevenue));
        request.setAttribute("formattedPaidRevenue", formatVND(paidRevenue));
        request.setAttribute("formattedAvgOrderValue", formatVND(avgOrderValue));
        request.setAttribute("formattedInventoryValue", formatVND(totalInventoryValue));
        request.setAttribute("totalOrdersCount", orders.size());
        request.setAttribute("validOrdersCount", validOrdersCount);
        request.setAttribute("totalItemsSold", totalItemsSold);
        request.setAttribute("totalStockUnits", totalStockUnits);
        request.setAttribute("totalBooksCount", allBooks.size());

        request.setAttribute("statusCounts", statusCounts);
        request.setAttribute("paymentMethodCounts", paymentMethodCounts);
        request.setAttribute("topSellingBooks", topSellingBooks);
        request.setAttribute("categoryStats", categoryStats);
        request.setAttribute("topCustomers", topCustomers);
        request.setAttribute("lowStockBooks", lowStockBooks);
        request.setAttribute("categories", categories);

        request.getRequestDispatcher("/jsp/adminanalytics.jsp").forward(request, response);
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

    private String formatVND(double amount) {
        return String.format("%,.0f đ", amount).replace(',', '.');
    }
}

