package dal;

import java.util.Vector;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Category;

public class CategoryDAO extends DBContext {

    public Vector<Category> getAllCategory() {
        Vector<Category> vector = new Vector<>();
        String sql = "select * from Category";
        try {
            PreparedStatement ptm = connection.prepareStatement(sql);
            ResultSet rs = ptm.executeQuery();
            while (rs.next()) {
                Category c = new Category(rs.getInt(1), rs.getString(2), rs.getString(3));
                vector.add(c);
            }
        } catch (SQLException ex) {
            ex.getStackTrace();
        }
        return vector;
    }

    public Category searchCategory(int cartItemId) {
        String sql = "select * from Category\n"
                + "where categoryId = ?";
        try {
            PreparedStatement ptm = connection.prepareStatement(sql);
            ptm.setInt(1, cartItemId);
            ResultSet rs = ptm.executeQuery();
            if (rs.next()) {
                Category c = new Category(rs.getInt(1), rs.getString(2), rs.getString(3));
                return c;
            }
        } catch (SQLException ex) {
            ex.getStackTrace();
        }
        return null;
    }

    public int insertCategory(Category c) {
        String sql = "INSERT INTO [dbo].[Category]\n"
                + "           ([categoryName]\n"
                + "           ,[description])\n"
                + "     VALUES\n"
                + "           (?,?)";
        int n = 0;
        try {
            PreparedStatement ptm = connection.prepareStatement(sql);
            ptm.setString(1, c.getCategoryName());
            ptm.setString(2, c.getDescription());
            n = ptm.executeUpdate();
        } catch (SQLException ex) {
            ex.getStackTrace();
        }
        return n;
    }

    public int updateCategory(Category c) {
        String sql = "UPDATE [dbo].[Category]\n"
                + "   SET [categoryName] = ?\n"
                + "      ,[description] = ?\n"
                + " WHERE categoryId = ?";
        int n = 0;
        try {
            PreparedStatement ptm = connection.prepareStatement(sql);
            ptm.setString(1, c.getCategoryName());
            ptm.setString(2, c.getDescription());
            ptm.setInt(3, c.getCategoryId());
            n = ptm.executeUpdate();
        } catch (SQLException ex) {
            ex.getStackTrace();
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
