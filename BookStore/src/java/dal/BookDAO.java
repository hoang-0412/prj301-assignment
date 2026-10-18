package dal;

import java.util.Vector;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.List;
import model.Book;
import model.BookImages;

public class BookDAO extends DBContext {

    private static final Logger LOGGER = Logger.getLogger(BookDAO.class.getName());

    private Book mapResultSetWithDetails(ResultSet rs) throws SQLException {
        Book b = new Book(
                rs.getInt("bookId"),
                rs.getInt("categoryId"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getString("publisher"),
                rs.getInt("publicationYear"),
                rs.getString("isbn"),
                rs.getDouble("price"),
                rs.getInt("stockQuantity"),
                rs.getString("description"),
                rs.getDate("createdAt")
        );
        try {
            b.setCategoryName(rs.getString("categoryName"));
        } catch (SQLException ignored) {
        }

        try {
            int imageId = rs.getInt("imageId");
            if (!rs.wasNull() && imageId > 0) {
                byte[] imgData = rs.getBytes("imageData");
                String mimeType = rs.getString("mimeType");
                int isCover = rs.getInt("isCover");
                java.sql.Date imgCreatedAt = rs.getDate("imgCreatedAt");
                BookImages cover = new BookImages(imageId, b.getBookId(), imgData, mimeType, isCover, imgCreatedAt);
                b.setCoverImage(cover);
            }
        } catch (SQLException ignored) {
        }

        return b;
    }

    public Vector<Book> getAllBook() {
        Vector<Book> vector = new Vector<>();
        String sql = "select * from Book";
        try {
            PreparedStatement ptm = connection.prepareStatement(sql);
            ResultSet rs = ptm.executeQuery();
            while (rs.next()) {
                Book b = new Book(rs.getInt(1),
                        rs.getInt(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getInt(6),
                        rs.getString(7),
                        rs.getDouble(8),
                        rs.getInt(9),
                        rs.getString(10),
                        rs.getDate(11));
                vector.add(b);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return vector;
    }

    public Vector<Book> getBooksWithDetails(String keyword, Integer categoryId, String sortBy, int page, int pageSize) {
        Vector<Book> vector = new Vector<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT b.bookId, b.categoryId, b.title, b.author, b.publisher, ")
           .append("b.publicationYear, b.isbn, b.price, b.stockQuantity, b.description, b.createdAt, ")
           .append("c.categoryName, ")
           .append("img.imageId, img.imageData, img.mimeType, img.isCover, img.createdAt AS imgCreatedAt ")
           .append("FROM Book b ")
           .append("LEFT JOIN Category c ON b.categoryId = c.categoryId ")
           .append("OUTER APPLY ( ")
           .append("    SELECT TOP 1 imageId, imageData, mimeType, isCover, createdAt ")
           .append("    FROM BookImages ")
           .append("    WHERE bookId = b.bookId ")
           .append("    ORDER BY isCover DESC, imageId ASC ")
           .append(") img ")
           .append("WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (b.title LIKE ? OR b.author LIKE ? OR b.publisher LIKE ?) ");
            String kw = "%" + keyword.trim() + "%";
            params.add(kw);
            params.add(kw);
            params.add(kw);
        }

        if (categoryId != null && categoryId > 0) {
            sql.append("AND b.categoryId = ? ");
            params.add(categoryId);
        }

        // Sorting
        if ("price-asc".equalsIgnoreCase(sortBy)) {
            sql.append("ORDER BY b.price ASC ");
        } else if ("price-desc".equalsIgnoreCase(sortBy)) {
            sql.append("ORDER BY b.price DESC ");
        } else if ("title-asc".equalsIgnoreCase(sortBy)) {
            sql.append("ORDER BY b.title ASC ");
        } else if ("oldest".equalsIgnoreCase(sortBy)) {
            sql.append("ORDER BY b.bookId ASC ");
        } else {
            // Default newest
            sql.append("ORDER BY b.bookId DESC ");
        }

        // Pagination
        if (pageSize > 0 && page > 0) {
            sql.append("OFFSET ? ROWS FETCH NEXT ? ROWS ONLY ");
            int offset = (page - 1) * pageSize;
            params.add(offset);
            params.add(pageSize);
        }

        try (PreparedStatement ptm = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ptm.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ptm.executeQuery()) {
                while (rs.next()) {
                    vector.add(mapResultSetWithDetails(rs));
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi truy vấn danh sách sách chi tiết", ex);
        }
        return vector;
    }

    public int getTotalBooksCount(String keyword, Integer categoryId) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) FROM Book b WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (b.title LIKE ? OR b.author LIKE ? OR b.publisher LIKE ?) ");
            String kw = "%" + keyword.trim() + "%";
            params.add(kw);
            params.add(kw);
            params.add(kw);
        }

        if (categoryId != null && categoryId > 0) {
            sql.append("AND b.categoryId = ? ");
            params.add(categoryId);
        }

        try (PreparedStatement ptm = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ptm.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ptm.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi đếm tổng số sách", ex);
        }
        return 0;
    }

    public Vector<Book> getFeaturedBooks(int limit) {
        Vector<Book> vector = new Vector<>();
        String sql = "SELECT TOP (" + limit + ") b.bookId, b.categoryId, b.title, b.author, b.publisher, "
                + "b.publicationYear, b.isbn, b.price, b.stockQuantity, b.description, b.createdAt, "
                + "c.categoryName, "
                + "img.imageId, img.imageData, img.mimeType, img.isCover, img.createdAt AS imgCreatedAt "
                + "FROM Book b "
                + "LEFT JOIN Category c ON b.categoryId = c.categoryId "
                + "OUTER APPLY ( "
                + "    SELECT TOP 1 imageId, imageData, mimeType, isCover, createdAt "
                + "    FROM BookImages "
                + "    WHERE bookId = b.bookId "
                + "    ORDER BY isCover DESC, imageId ASC "
                + ") img "
                + "ORDER BY b.stockQuantity DESC, b.bookId DESC";
        try (PreparedStatement ptm = connection.prepareStatement(sql);
             ResultSet rs = ptm.executeQuery()) {
            while (rs.next()) {
                vector.add(mapResultSetWithDetails(rs));
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy danh sách sách nổi bật", ex);
        }
        return vector;
    }

    public Vector<Book> getNewestBooks(int limit) {
        Vector<Book> vector = new Vector<>();
        String sql = "SELECT TOP (" + limit + ") b.bookId, b.categoryId, b.title, b.author, b.publisher, "
                + "b.publicationYear, b.isbn, b.price, b.stockQuantity, b.description, b.createdAt, "
                + "c.categoryName, "
                + "img.imageId, img.imageData, img.mimeType, img.isCover, img.createdAt AS imgCreatedAt "
                + "FROM Book b "
                + "LEFT JOIN Category c ON b.categoryId = c.categoryId "
                + "OUTER APPLY ( "
                + "    SELECT TOP 1 imageId, imageData, mimeType, isCover, createdAt "
                + "    FROM BookImages "
                + "    WHERE bookId = b.bookId "
                + "    ORDER BY isCover DESC, imageId ASC "
                + ") img "
                + "ORDER BY b.bookId DESC";
        try (PreparedStatement ptm = connection.prepareStatement(sql);
             ResultSet rs = ptm.executeQuery()) {
            while (rs.next()) {
                vector.add(mapResultSetWithDetails(rs));
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy danh sách sách mới nhất", ex);
        }
        return vector;
    }

    public Book searchBook(int bookId) {
        String sql = "SELECT b.bookId, b.categoryId, b.title, b.author, b.publisher, "
                + "b.publicationYear, b.isbn, b.price, b.stockQuantity, b.description, b.createdAt, "
                + "c.categoryName, "
                + "img.imageId, img.imageData, img.mimeType, img.isCover, img.createdAt AS imgCreatedAt "
                + "FROM Book b "
                + "LEFT JOIN Category c ON b.categoryId = c.categoryId "
                + "OUTER APPLY ( "
                + "    SELECT TOP 1 imageId, imageData, mimeType, isCover, createdAt "
                + "    FROM BookImages "
                + "    WHERE bookId = b.bookId "
                + "    ORDER BY isCover DESC, imageId ASC "
                + ") img "
                + "WHERE b.bookId = ?";
        try {
            PreparedStatement ptm = connection.prepareStatement(sql);
            ptm.setInt(1, bookId);
            ResultSet rs = ptm.executeQuery();
            if (rs.next()) {
                return mapResultSetWithDetails(rs);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public int insertBook(Book b) {
        String sql = "INSERT INTO [dbo].[Book]\n"
                + "           ([categoryId]\n"
                + "           ,[title]\n"
                + "           ,[author]\n"
                + "           ,[publisher]\n"
                + "           ,[publicationYear]\n"
                + "           ,[isbn]\n"
                + "           ,[price]\n"
                + "           ,[stockQuantity]\n"
                + "           ,[description]\n"
                + "           ,[createdAt])\n"
                + "     VALUES\n"
                + "           (?,?,?,?,?,?,?,?,?,?)";
        int n = 0;
        try {
            PreparedStatement ptm = connection.prepareStatement(sql);
            ptm.setInt(1, b.getCategoryId());
            ptm.setString(2, b.getTitle());
            ptm.setString(3, b.getAuthor());
            ptm.setString(4, b.getPublisher());
            ptm.setInt(5, b.getPublicationYear());
            ptm.setString(6, b.getIsbn());
            ptm.setDouble(7, b.getPrice());
            ptm.setInt(8, b.getStockQuantity());
            ptm.setString(9, b.getDescription());
            ptm.setDate(10, b.getCreatedAt() != null ? b.getCreatedAt() : new java.sql.Date(System.currentTimeMillis()));
            n = ptm.executeUpdate();
            if (n > 0) {
                try (ResultSet generatedKeys = ptm.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        b.setBookId(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi thêm sách mới", ex);
        }
        return n;
    }

    public int updateBook(Book b) {
        String sql = "UPDATE [dbo].[Book]\n"
                + "   SET [categoryId] = ?\n"
                + "      ,[title] = ?\n"
                + "      ,[author] = ?\n"
                + "      ,[publisher] = ?\n"
                + "      ,[publicationYear] = ?\n"
                + "      ,[isbn] = ?\n"
                + "      ,[price] = ?\n"
                + "      ,[stockQuantity] = ?\n"
                + "      ,[description] = ?\n"
                + "      ,[createdAt] = ?\n"
                + " WHERE bookId = ?";
        int n = 0;
        try (PreparedStatement ptm = connection.prepareStatement(sql)) {
            ptm.setInt(1, b.getCategoryId());
            ptm.setString(2, b.getTitle());
            ptm.setString(3, b.getAuthor());
            ptm.setString(4, b.getPublisher());
            ptm.setInt(5, b.getPublicationYear());
            ptm.setString(6, b.getIsbn());
            ptm.setDouble(7, b.getPrice());
            ptm.setInt(8, b.getStockQuantity());
            ptm.setString(9, b.getDescription());
            ptm.setDate(10, b.getCreatedAt() != null ? b.getCreatedAt() : new java.sql.Date(System.currentTimeMillis()));
            ptm.setInt(11, b.getBookId());
            n = ptm.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi cập nhật thông tin sách", ex);
        }
        return n;
    }

    public int deleteBook(int bookId) {
        String sql = "DELETE FROM Book WHERE bookId = ?";
        int n = 0;
        try (PreparedStatement ptm = connection.prepareStatement(sql)) {
            ptm.setInt(1, bookId);
            n = ptm.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi xóa sách có bookId: " + bookId, ex);
        }
        return n;
    }

    public static void main(String[] args) {
        BookDAO dao = new BookDAO();
        Vector<Book> vector = dao.getBooksWithDetails(null, null, "newest", 1, 10);
        System.out.println("Tổng số sách tìm thấy: " + vector.size());
        for (Book b : vector) {
            System.out.println(b + " | Category: " + b.getCategoryName() + " | Price: " + b.getFormattedPrice());
        }
    }
}
