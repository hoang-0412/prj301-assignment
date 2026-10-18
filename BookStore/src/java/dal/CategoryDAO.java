package dal;

import java.util.Vector;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Category;

public class CategoryDAO extends DBContext {

    private static final Logger LOGGER = Logger.getLogger(CategoryDAO.class.getName());

    public Vector<Category> getAllCategory() {
        Vector<Category> vector = new Vector<>();
        String sql = "SELECT c.categoryId, c.categoryName, c.description, COUNT(b.bookId) AS bookCount "
                   + "FROM Category c "
                   + "LEFT JOIN Book b ON c.categoryId = b.categoryId "
                   + "GROUP BY c.categoryId, c.categoryName, c.description "
                   + "ORDER BY c.categoryId ASC";
        try (PreparedStatement ptm = connection.prepareStatement(sql);
             ResultSet rs = ptm.executeQuery()) {
            while (rs.next()) {
                Category c = new Category(rs.getInt(1), rs.getString(2), rs.getString(3));
                c.setBookCount(rs.getInt(4));
                vector.add(c);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy danh sách thể loại", ex);
        }
        return vector;
    }

    public Category searchCategory(int categoryId) {
        String sql = "SELECT categoryId, categoryName, description FROM Category WHERE categoryId = ?";
        try (PreparedStatement ptm = connection.prepareStatement(sql)) {
            ptm.setInt(1, categoryId);
            try (ResultSet rs = ptm.executeQuery()) {
                if (rs.next()) {
                    return new Category(rs.getInt(1), rs.getString(2), rs.getString(3));
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tìm thể loại theo id: " + categoryId, ex);
        }
        return null;
    }

    public int insertCategory(Category c) {
        String sql = "INSERT INTO [dbo].[Category] ([categoryName], [description]) VALUES (?, ?)";
        int n = 0;
        try (PreparedStatement ptm = connection.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ptm.setString(1, c.getCategoryName());
            ptm.setString(2, c.getDescription());
            n = ptm.executeUpdate();
            if (n > 0) {
                try (ResultSet generatedKeys = ptm.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        c.setCategoryId(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi thêm thể loại mới", ex);
        }
        return n;
    }

    public int updateCategory(Category c) {
        String sql = "UPDATE [dbo].[Category] SET [categoryName] = ?, [description] = ? WHERE categoryId = ?";
        int n = 0;
        try (PreparedStatement ptm = connection.prepareStatement(sql)) {
            ptm.setString(1, c.getCategoryName());
            ptm.setString(2, c.getDescription());
            ptm.setInt(3, c.getCategoryId());
            n = ptm.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi cập nhật thể loại", ex);
        }
        return n;
    }

    public static void main(String[] args) {
        CategoryDAO dao = new CategoryDAO();
        Vector<Category> vector = dao.getAllCategory();
        for (Category c : vector) {
            System.out.println(c);
        }
    }
}
