package controller;

import dal.BookDAO;
import dal.BookImagesDAO;
import dal.CategoryDAO;
import dal.OrderDAO;
import dal.OrderItemDAO;
import dal.RoleDAO;
import dal.UserDAO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import model.Book;
import model.Category;
import model.Order;
import model.OrderItem;
import model.Role;
import model.User;

@WebServlet(name = "AdminDashboardController", urlPatterns = {"/admindashboard", "/admin"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,      // 1 MB
        maxFileSize = 1024 * 1024 * 10,       // 10 MB
        maxRequestSize = 1024 * 1024 * 15     // 15 MB
)
public class AdminDashboardController extends HttpServlet {

    private final BookDAO bookDAO = new BookDAO();
    private final BookImagesDAO bookImagesDAO = new BookImagesDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final OrderDAO orderDAO = new OrderDAO();
    private final OrderItemDAO orderItemDAO = new OrderItemDAO();
    private final UserDAO userDAO = new UserDAO();
    private final RoleDAO roleDAO = new RoleDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        // Nếu truy cập qua /admin cũ thì chuyển hướng sang /admindashboard
        String servletPath = request.getServletPath();
        if ("/admin".equalsIgnoreCase(servletPath)) {
            response.sendRedirect(request.getContextPath() + "/admindashboard");
            return;
        }

        HttpSession session = request.getSession(true);
        User currentUser = (User) session.getAttribute("user");

        // Nếu đang đăng nhập bằng tài khoản Customer (roleId != 2) thì chuyển về trang chủ
        if (currentUser != null && currentUser.getRoleId() != 2) {
            response.sendRedirect(request.getContextPath() + "/homepage");
            return;
        }

        // Lấy thông báo flash từ session (nếu có sau khi POST)
        String successMsg = (String) session.getAttribute("adminSuccessMsg");
        String errorMsg = (String) session.getAttribute("adminErrorMsg");
        if (successMsg != null) {
            request.setAttribute("successMsg", successMsg);
            session.removeAttribute("adminSuccessMsg");
        }
        if (errorMsg != null) {
            request.setAttribute("errorMsg", errorMsg);
            session.removeAttribute("adminErrorMsg");
        }

        String activeTab = request.getParameter("tab");
        if (activeTab == null || activeTab.trim().isEmpty()) {
            activeTab = "books";
        }

        // Tham số lọc sách
        String keyword = request.getParameter("keyword");
        if (keyword != null) {
            keyword = keyword.trim();
        }
        Integer categoryId = parseIntOrNull(request.getParameter("categoryId"));
        String statusFilter = request.getParameter("status");
        if (statusFilter != null) {
            statusFilter = statusFilter.trim();
        }
        String sort = request.getParameter("sort");
        if (sort == null || sort.trim().isEmpty()) {
            sort = "newest";
        }

        // Truy vấn dữ liệu từ Database
        Vector<Book> allBooks = bookDAO.getBooksWithDetails(null, null, "newest", 1, 1000);
        Vector<Book> filteredBooks = bookDAO.getBooksWithDetails(keyword, categoryId, sort, 1, 1000);

        if (statusFilter != null && !statusFilter.isEmpty()) {
            Vector<Book> statusFiltered = new Vector<>();
            for (Book b : filteredBooks) {
                if (b.getBookStatus() != null && b.getBookStatus().equalsIgnoreCase(statusFilter)) {
                    statusFiltered.add(b);
                }
            }
            filteredBooks = statusFiltered;
        }

        Vector<Category> categories = categoryDAO.getAllCategory();
        Vector<Order> orders = orderDAO.getAllOrder();
        Vector<User> users = userDAO.getAllUser();
        Vector<Role> roles = roleDAO.getAllRole();

        // Tính toán thống kê KPI
        int totalBooksCount = allBooks.size();
        int totalStockCount = 0;
        int booksForSaleCount = 0;
        int booksForRentCount = 0;
        for (Book b : allBooks) {
            totalStockCount += b.getStockQuantity();
            String st = b.getBookStatus() != null ? b.getBookStatus().trim() : "";
            if ("Bán".equalsIgnoreCase(st)) {
                booksForSaleCount++;
            } else {
                booksForRentCount++;
            }
        }

        double totalRevenue = 0;
        int pendingOrdersCount = 0;
        for (Order o : orders) {
            if (!"Cancelled".equalsIgnoreCase(o.getOrderStatus())) {
                totalRevenue += o.getTotalAmount();
            }
            if ("Pending".equalsIgnoreCase(o.getOrderStatus())) {
                pendingOrdersCount++;
            }
        }

        String formattedRevenue = String.format("%,.0f đ", totalRevenue).replace(',', '.');

        // Đặt thuộc tính sang JSP
        request.setAttribute("activeTab", activeTab);
        request.setAttribute("books", filteredBooks);
        request.setAttribute("allBooksList", allBooks);
        request.setAttribute("allBooksCount", totalBooksCount);
        request.setAttribute("totalStockCount", totalStockCount);
        request.setAttribute("booksForSaleCount", booksForSaleCount);
        request.setAttribute("booksForRentCount", booksForRentCount);
        request.setAttribute("categories", categories);
        request.setAttribute("orders", orders);
        request.setAttribute("pendingOrdersCount", pendingOrdersCount);
        request.setAttribute("formattedRevenue", formattedRevenue);
        request.setAttribute("users", users);
        request.setAttribute("roles", roles);

        request.setAttribute("keyword", keyword);
        request.setAttribute("selectedCategoryId", categoryId);
        request.setAttribute("selectedStatus", statusFilter);
        request.setAttribute("selectedSort", sort);

        request.getRequestDispatcher("/jsp/admindashboard.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(true);
        User currentUser = (User) session.getAttribute("user");
        if (currentUser != null && currentUser.getRoleId() != 2) {
            response.sendRedirect(request.getContextPath() + "/homepage");
            return;
        }

        String action = request.getParameter("action");
        if (action == null) {
            action = "";
        }

        String redirectTab = "books";

        try {
            switch (action) {
                case "addBook":
                    handleAddBook(request, session);
                    redirectTab = "books";
                    break;
                case "updateBook":
                    handleUpdateBook(request, session);
                    redirectTab = "books";
                    break;
                case "deleteBook":
                    handleDeleteBook(request, session);
                    redirectTab = "books";
                    break;
                case "addCategory":
                    handleAddCategory(request, session);
                    redirectTab = "categories";
                    break;
                case "updateCategory":
                    handleUpdateCategory(request, session);
                    redirectTab = "categories";
                    break;
                case "deleteCategory":
                    handleDeleteCategory(request, session);
                    redirectTab = "categories";
                    break;
                case "addOrder":
                    handleAddOrder(request, session);
                    redirectTab = "orders";
                    break;
                case "updateOrder":
                    handleUpdateOrder(request, session);
                    redirectTab = "orders";
                    break;
                case "deleteOrder":
                    handleDeleteOrder(request, session);
                    redirectTab = "orders";
                    break;
                case "addUser":
                    handleAddUser(request, session);
                    redirectTab = "users";
                    break;
                case "updateUser":
                    handleUpdateUser(request, session);
                    redirectTab = "users";
                    break;
                case "deleteUser":
                    handleDeleteUser(request, session);
                    redirectTab = "users";
                    break;
                default:
                    break;
            }
        } catch (Exception ex) {
            session.setAttribute("adminErrorMsg", "Đã xảy ra lỗi xử lý: " + ex.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/admindashboard?tab=" + redirectTab);
    }

    // ==================== BOOK HANDLERS ====================

    private void handleAddBook(HttpServletRequest request, HttpSession session) throws IOException, ServletException {
        String title = trimParam(request.getParameter("title"));
        String author = trimParam(request.getParameter("author"));
        int categoryId = parseIntOrDefault(request.getParameter("categoryId"), 1);
        String publisher = trimParam(request.getParameter("publisher"));
        int publicationYear = parseIntOrDefault(request.getParameter("publicationYear"), 2025);
        String isbn = trimParam(request.getParameter("isbn"));
        double price = parseDoubleOrDefault(request.getParameter("price"), 0);
        int stockQuantity = parseIntOrDefault(request.getParameter("stockQuantity"), 0);
        String bookStatus = trimParam(request.getParameter("bookStatus"));
        String description = trimParam(request.getParameter("description"));

        if (title.isEmpty() || author.isEmpty()) {
            session.setAttribute("adminErrorMsg", "Tên sách và tác giả không được để trống!");
            return;
        }
        if (bookStatus.isEmpty()) {
            bookStatus = "Bán";
        }
        if (isbn.isEmpty()) {
            isbn = "ISBN-" + System.currentTimeMillis();
        }

        Book newBook = new Book(
                categoryId,
                title,
                author,
                publisher,
                publicationYear,
                isbn,
                price,
                stockQuantity,
                description,
                new Date(System.currentTimeMillis()),
                bookStatus
        );

        int n = bookDAO.insertBook(newBook);
        if (n > 0) {
            saveUploadedCoverIfPresent(request, newBook.getBookId());
            session.setAttribute("adminSuccessMsg", "Đã thêm sách mới \"" + title + "\" thành công!");
        } else {
            session.setAttribute("adminErrorMsg", "Không thể thêm sách mới (kiểm tra mã ISBN có bị trùng không).");
        }
    }

    private void handleUpdateBook(HttpServletRequest request, HttpSession session) throws IOException, ServletException {
        int bookId = parseIntOrDefault(request.getParameter("bookId"), 0);
        if (bookId <= 0) {
            session.setAttribute("adminErrorMsg", "Mã sách không hợp lệ!");
            return;
        }

        Book existing = bookDAO.searchBook(bookId);
        if (existing == null) {
            session.setAttribute("adminErrorMsg", "Không tìm thấy sách cần cập nhật!");
            return;
        }

        String title = trimParam(request.getParameter("title"));
        String author = trimParam(request.getParameter("author"));
        int categoryId = parseIntOrDefault(request.getParameter("categoryId"), existing.getCategoryId());
        String publisher = trimParam(request.getParameter("publisher"));
        int publicationYear = parseIntOrDefault(request.getParameter("publicationYear"), existing.getPublicationYear());
        String isbn = trimParam(request.getParameter("isbn"));
        double price = parseDoubleOrDefault(request.getParameter("price"), existing.getPrice());
        int stockQuantity = parseIntOrDefault(request.getParameter("stockQuantity"), existing.getStockQuantity());
        String bookStatus = trimParam(request.getParameter("bookStatus"));
        String description = trimParam(request.getParameter("description"));

        if (bookStatus.isEmpty()) {
            bookStatus = existing.getBookStatus() != null ? existing.getBookStatus() : "Bán";
        }

        Book updated = new Book(
                bookId,
                categoryId,
                title.isEmpty() ? existing.getTitle() : title,
                author.isEmpty() ? existing.getAuthor() : author,
                publisher,
                publicationYear,
                isbn.isEmpty() ? existing.getIsbn() : isbn,
                price,
                stockQuantity,
                description,
                existing.getCreatedAt() != null ? existing.getCreatedAt() : new Date(System.currentTimeMillis()),
                bookStatus
        );

        int n = bookDAO.updateBook(updated);
        if (n > 0) {
            saveUploadedCoverIfPresent(request, bookId);
            session.setAttribute("adminSuccessMsg", "Đã cập nhật thông tin sách #" + bookId + " thành công!");
        } else {
            session.setAttribute("adminErrorMsg", "Cập nhật sách thất bại (kiểm tra mã ISBN có bị trùng không).");
        }
    }

    private void handleDeleteBook(HttpServletRequest request, HttpSession session) {
        int bookId = parseIntOrDefault(request.getParameter("bookId"), 0);
        if (bookId <= 0) {
            session.setAttribute("adminErrorMsg", "Mã sách cần xóa không hợp lệ!");
            return;
        }
        int n = bookDAO.deleteBook(bookId);
        if (n > 0) {
            session.setAttribute("adminSuccessMsg", "Đã xóa cuốn sách #" + bookId + " khỏi hệ thống!");
        } else {
            session.setAttribute("adminErrorMsg", "Không thể xóa sách #" + bookId + " (sách có thể đang nằm trong đơn hàng).");
        }
    }

    private void saveUploadedCoverIfPresent(HttpServletRequest request, int bookId) {
        try {
            Part coverPart = request.getPart("coverFile");
            if (coverPart != null && coverPart.getSize() > 0) {
                String contentType = coverPart.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    contentType = "image/jpeg";
                }
                byte[] imageBytes = readPartBytes(coverPart);
                if (imageBytes != null && imageBytes.length > 0) {
                    bookImagesDAO.saveOrUpdateCoverImage(bookId, imageBytes, contentType);
                }
            }
        } catch (Exception ignored) {
        }
    }

    private byte[] readPartBytes(Part part) throws IOException {
        try (InputStream is = part.getInputStream();
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            byte[] temp = new byte[8192];
            int read;
            while ((read = is.read(temp)) != -1) {
                buffer.write(temp, 0, read);
            }
            return buffer.toByteArray();
        }
    }

    // ==================== CATEGORY HANDLERS ====================

    private void handleAddCategory(HttpServletRequest request, HttpSession session) {
        String categoryName = trimParam(request.getParameter("categoryName"));
        String description = trimParam(request.getParameter("description"));

        if (categoryName.isEmpty()) {
            session.setAttribute("adminErrorMsg", "Tên thể loại không được để trống!");
            return;
        }

        Category cat = new Category(categoryName, description);
        int n = categoryDAO.insertCategory(cat);
        if (n > 0) {
            session.setAttribute("adminSuccessMsg", "Đã thêm thể loại \"" + categoryName + "\" thành công!");
        } else {
            session.setAttribute("adminErrorMsg", "Thêm thể loại thất bại (tên thể loại có thể đã tồn tại).");
        }
    }

    private void handleUpdateCategory(HttpServletRequest request, HttpSession session) {
        int categoryId = parseIntOrDefault(request.getParameter("categoryId"), 0);
        String categoryName = trimParam(request.getParameter("categoryName"));
        String description = trimParam(request.getParameter("description"));

        if (categoryId <= 0 || categoryName.isEmpty()) {
            session.setAttribute("adminErrorMsg", "Thông tin thể loại không hợp lệ!");
            return;
        }

        Category cat = new Category(categoryId, categoryName, description);
        int n = categoryDAO.updateCategory(cat);
        if (n > 0) {
            session.setAttribute("adminSuccessMsg", "Đã cập nhật thể loại #" + categoryId + " thành công!");
        } else {
            session.setAttribute("adminErrorMsg", "Cập nhật thể loại thất bại!");
        }
    }

    private void handleDeleteCategory(HttpServletRequest request, HttpSession session) {
        int categoryId = parseIntOrDefault(request.getParameter("categoryId"), 0);
        if (categoryId <= 0) {
            session.setAttribute("adminErrorMsg", "Mã thể loại không hợp lệ!");
            return;
        }
        int bookCount = bookDAO.getTotalBooksCount(null, categoryId);
        if (bookCount > 0) {
            session.setAttribute("adminErrorMsg", "Không thể xóa thể loại này vì đang có " + bookCount + " cuốn sách thuộc thể loại!");
            return;
        }
        int n = categoryDAO.deleteCategory(categoryId);
        if (n > 0) {
            session.setAttribute("adminSuccessMsg", "Đã xóa thể loại #" + categoryId + " thành công!");
        } else {
            session.setAttribute("adminErrorMsg", "Không thể xóa thể loại #" + categoryId + "!");
        }
    }

    // ==================== ORDER HANDLERS ====================

    private void handleAddOrder(HttpServletRequest request, HttpSession session) {
        int userId = parseIntOrDefault(request.getParameter("userId"), 0);
        User customer = userDAO.searchUser(userId);
        if (customer == null) {
            session.setAttribute("adminErrorMsg", "Vui lòng chọn khách hàng hợp lệ để tạo đơn hàng!");
            return;
        }

        String recipientPhone = trimParam(request.getParameter("recipientPhone"));
        String shippingAddress = trimParam(request.getParameter("shippingAddress"));
        String paymentMethod = trimParam(request.getParameter("paymentMethod"));
        String paymentStatus = trimParam(request.getParameter("paymentStatus"));
        String orderStatus = trimParam(request.getParameter("orderStatus"));

        if (recipientPhone.isEmpty()) {
            recipientPhone = customer.getPhoneNumber() != null ? customer.getPhoneNumber() : "";
        }
        if (shippingAddress.isEmpty()) {
            shippingAddress = customer.getAddress() != null ? customer.getAddress() : "";
        }
        if (recipientPhone.isEmpty() || shippingAddress.isEmpty()) {
            session.setAttribute("adminErrorMsg", "Vui lòng nhập đầy đủ số điện thoại và địa chỉ giao hàng!");
            return;
        }
        if (paymentMethod.isEmpty()) {
            paymentMethod = "COD";
        }
        if (paymentStatus.isEmpty()) {
            paymentStatus = "Unpaid";
        }
        if (orderStatus.isEmpty()) {
            orderStatus = "Pending";
        }

        String[] bookIdParams = request.getParameterValues("orderBookId");
        String[] qtyParams = request.getParameterValues("orderQuantity");

        if (bookIdParams == null || qtyParams == null || bookIdParams.length == 0) {
            session.setAttribute("adminErrorMsg", "Vui lòng chọn ít nhất 1 cuốn sách cho đơn hàng!");
            return;
        }

        // Gộp số lượng nếu chọn trùng cùng 1 đầu sách ở nhiều dòng
        Map<Integer, Integer> bookQtyMap = new LinkedHashMap<>();
        int len = Math.min(bookIdParams.length, qtyParams.length);
        for (int i = 0; i < len; i++) {
            int bId = parseIntOrDefault(bookIdParams[i], 0);
            int qty = parseIntOrDefault(qtyParams[i], 0);
            if (bId > 0 && qty > 0) {
                bookQtyMap.put(bId, bookQtyMap.getOrDefault(bId, 0) + qty);
            }
        }

        if (bookQtyMap.isEmpty()) {
            session.setAttribute("adminErrorMsg", "Danh sách sản phẩm trong đơn hàng không hợp lệ!");
            return;
        }

        // Kiểm tra tồn kho và tính tổng tiền
        Map<Integer, Book> bookMap = new LinkedHashMap<>();
        double totalAmount = 0;
        for (Map.Entry<Integer, Integer> entry : bookQtyMap.entrySet()) {
            int bId = entry.getKey();
            int qty = entry.getValue();
            Book book = bookDAO.searchBook(bId);
            if (book == null) {
                session.setAttribute("adminErrorMsg", "Không tìm thấy sách #" + bId + " trong kho!");
                return;
            }
            if (book.getStockQuantity() < qty) {
                session.setAttribute("adminErrorMsg", "Sách \"" + book.getTitle() + "\" chỉ còn " + book.getStockQuantity() + " cuốn trong kho (yêu cầu " + qty + " cuốn)!");
                return;
            }
            bookMap.put(bId, book);
            totalAmount += book.getPrice() * qty;
        }

        Order newOrder = new Order(
                userId,
                totalAmount,
                orderStatus,
                shippingAddress,
                recipientPhone,
                paymentMethod,
                paymentStatus,
                new Date(System.currentTimeMillis())
        );

        int n = orderDAO.insertOrder(newOrder);
        if (n > 0 && newOrder.getOrderId() > 0) {
            int orderId = newOrder.getOrderId();
            for (Map.Entry<Integer, Integer> entry : bookQtyMap.entrySet()) {
                int bId = entry.getKey();
                int qty = entry.getValue();
                Book book = bookMap.get(bId);

                OrderItem item = new OrderItem(0, orderId, bId, qty, book.getPrice());
                orderItemDAO.insertOrderItem(item);

                // Trừ tồn kho
                book.setStockQuantity(Math.max(0, book.getStockQuantity() - qty));
                bookDAO.updateBook(book);
            }
            session.setAttribute("adminSuccessMsg", "Đã tạo chủ động đơn hàng #" + orderId + " cho khách hàng \"" + customer.getFullName() + "\" thành công!");
        } else {
            session.setAttribute("adminErrorMsg", "Không thể tạo đơn hàng mới vào cơ sở dữ liệu!");
        }
    }

    private void handleUpdateOrder(HttpServletRequest request, HttpSession session) {
        int orderId = parseIntOrDefault(request.getParameter("orderId"), 0);
        Order existing = orderDAO.searchOrder(orderId);
        if (existing == null) {
            session.setAttribute("adminErrorMsg", "Không tìm thấy đơn hàng #" + orderId + "!");
            return;
        }

        String orderStatus = trimParam(request.getParameter("orderStatus"));
        String paymentStatus = trimParam(request.getParameter("paymentStatus"));
        String shippingAddress = trimParam(request.getParameter("shippingAddress"));
        String recipientPhone = trimParam(request.getParameter("recipientPhone"));

        if (!orderStatus.isEmpty()) {
            existing.setOrderStatus(orderStatus);
        }
        if (!paymentStatus.isEmpty()) {
            existing.setPaymentStatus(paymentStatus);
        }
        if (!shippingAddress.isEmpty()) {
            existing.setShippingAddress(shippingAddress);
        }
        if (!recipientPhone.isEmpty()) {
            existing.setRecipientPhone(recipientPhone);
        }

        int n = orderDAO.updateOrder(existing);
        if (n > 0) {
            session.setAttribute("adminSuccessMsg", "Đã cập nhật trạng thái đơn hàng #" + orderId + " thành công!");
        } else {
            session.setAttribute("adminErrorMsg", "Cập nhật đơn hàng #" + orderId + " thất bại!");
        }
    }

    private void handleDeleteOrder(HttpServletRequest request, HttpSession session) {
        int orderId = parseIntOrDefault(request.getParameter("orderId"), 0);
        if (orderId <= 0) {
            session.setAttribute("adminErrorMsg", "Mã đơn hàng không hợp lệ!");
            return;
        }
        int n = orderDAO.deleteOrder(orderId);
        if (n > 0) {
            session.setAttribute("adminSuccessMsg", "Đã xóa đơn hàng #" + orderId + " thành công!");
        } else {
            session.setAttribute("adminErrorMsg", "Không thể xóa đơn hàng #" + orderId + "!");
        }
    }

    // ==================== USER HANDLERS ====================

    private void handleAddUser(HttpServletRequest request, HttpSession session) {
        String fullName = trimParam(request.getParameter("fullName"));
        String email = trimParam(request.getParameter("email"));
        String password = trimParam(request.getParameter("password"));
        String phoneNumber = trimParam(request.getParameter("phoneNumber"));
        String address = trimParam(request.getParameter("address"));
        int roleId = parseIntOrDefault(request.getParameter("roleId"), 1);

        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            session.setAttribute("adminErrorMsg", "Vui lòng nhập đầy đủ Họ tên, Email và Mật khẩu!");
            return;
        }

        if (userDAO.getUserByEmail(email) != null) {
            session.setAttribute("adminErrorMsg", "Email \"" + email + "\" đã tồn tại trên hệ thống!");
            return;
        }

        User newUser = new User(
                roleId,
                fullName,
                email,
                password,
                phoneNumber,
                address.isEmpty() ? "Chưa cập nhật" : address,
                new Date(System.currentTimeMillis())
        );

        int n = userDAO.insertUser(newUser);
        if (n > 0) {
            session.setAttribute("adminSuccessMsg", "Đã thêm tài khoản \"" + fullName + "\" thành công!");
        } else {
            session.setAttribute("adminErrorMsg", "Thêm người dùng thất bại!");
        }
    }

    private void handleUpdateUser(HttpServletRequest request, HttpSession session) {
        int userId = parseIntOrDefault(request.getParameter("userId"), 0);
        User existing = userDAO.searchUser(userId);
        if (existing == null) {
            session.setAttribute("adminErrorMsg", "Không tìm thấy người dùng #" + userId + "!");
            return;
        }

        if (existing.getRoleId() == 1) {
            session.setAttribute("adminErrorMsg", "Quản trị viên không được phép chỉnh sửa tài khoản của Khách hàng!");
            return;
        }

        String fullName = trimParam(request.getParameter("fullName"));
        String email = trimParam(request.getParameter("email"));
        String password = trimParam(request.getParameter("password"));
        String phoneNumber = trimParam(request.getParameter("phoneNumber"));
        String address = trimParam(request.getParameter("address"));
        int roleId = parseIntOrDefault(request.getParameter("roleId"), existing.getRoleId());

        if (!fullName.isEmpty()) {
            existing.setFullName(fullName);
        }
        if (!email.isEmpty()) {
            existing.setEmail(email);
        }
        if (!password.isEmpty()) {
            existing.setPasswordHash(password);
        }
        existing.setPhoneNumber(phoneNumber);
        existing.setAddress(address);
        existing.setRoleId(roleId);

        int n = userDAO.updateUser(existing);
        if (n > 0) {
            // Cập nhật lại session nếu admin tự sửa chính mình
            User currentUser = (User) session.getAttribute("user");
            if (currentUser != null && currentUser.getUserId() == userId) {
                session.setAttribute("user", existing);
            }
            session.setAttribute("adminSuccessMsg", "Đã cập nhật tài khoản #" + userId + " thành công!");
        } else {
            session.setAttribute("adminErrorMsg", "Cập nhật tài khoản thất bại (Email có thể bị trùng)!");
        }
    }

    private void handleDeleteUser(HttpServletRequest request, HttpSession session) {
        int userId = parseIntOrDefault(request.getParameter("userId"), 0);
        User currentUser = (User) session.getAttribute("user");
        if (currentUser != null && currentUser.getUserId() == userId) {
            session.setAttribute("adminErrorMsg", "Không thể tự xóa tài khoản Quản trị viên đang đăng nhập!");
            return;
        }
        int n = userDAO.deleteUser(userId);
        if (n > 0) {
            session.setAttribute("adminSuccessMsg", "Đã xóa người dùng #" + userId + " khỏi hệ thống!");
        } else {
            session.setAttribute("adminErrorMsg", "Không thể xóa người dùng #" + userId + " (người dùng đã có đơn hàng).");
        }
    }

    // ==================== HELPERS ====================

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

    private double parseDoubleOrDefault(String val, double defaultVal) {
        if (val == null || val.trim().isEmpty()) {
            return defaultVal;
        }
        try {
            return Double.parseDouble(val.trim());
        } catch (NumberFormatException ex) {
            return defaultVal;
        }
    }
}

