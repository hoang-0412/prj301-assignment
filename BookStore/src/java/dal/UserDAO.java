package dal;

import java.util.Vector;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.User;

public class UserDAO extends DBContext {

    public Vector<User> getAllUser() {
        Vector<User> vector = new Vector<>();
        String sql = "select * from User";
        try {
            PreparedStatement ptm = connection.prepareStatement(sql);
            ResultSet rs = ptm.executeQuery();
            while (rs.next()) {
                User u = new User(rs.getInt(1),
                        rs.getInt(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getString(7),
                        rs.getDate(8));
                vector.add(u);
            }
        } catch (SQLException ex) {
            ex.getStackTrace();
        }
        return vector;
    }

    public User searchUser(int userId) {
        String sql = "select * from User\n"
                + "where userId = ?";
        try {
            PreparedStatement ptm = connection.prepareStatement(sql);
            ptm.setInt(1, userId);
            ResultSet rs = ptm.executeQuery();
            if (rs.next()) {
                User u = new User(rs.getInt(1),
                        rs.getInt(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getString(7),
                        rs.getDate(8));
                return u;
            }
        } catch (SQLException ex) {
            ex.getStackTrace();
        }
        return null;
    }

    public int insertUser(User u) {
        String sql = "INSERT INTO [dbo].[User]\n"
                + "           ([roleId]\n"
                + "           ,[fullName]\n"
                + "           ,[email]\n"
                + "           ,[passwordHash]\n"
                + "           ,[phoneNumber]\n"
                + "           ,[address]\n"
                + "           ,[createdAt])\n"
                + "     VALUES\n"
                + "           (?,?,?,?,?,?,?)";
        int n = 0;
        try {
            PreparedStatement ptm = connection.prepareStatement(sql);
            ptm.setInt(1, u.getRoleId());
            ptm.setString(2, u.getFullName());
            ptm.setString(3, u.getEmail());
            ptm.setString(4, u.getPasswordHash());
            ptm.setString(5, u.getPhoneNumber());
            ptm.setString(6, u.getAddress());
            ptm.setDate(7, u.getCreatedAt());
            n = ptm.executeUpdate();
        } catch (SQLException ex) {
            ex.getStackTrace();
        }
        return n;
    }

    public int updateUser(User u) {
        String sql = "UPDATE [dbo].[User]\n"
                + "   SET [roleId] = ?\n"
                + "      ,[fullName] = ?\n"
                + "      ,[email] = ?\n"
                + "      ,[passwordHash] = ?\n"
                + "      ,[phoneNumber] = ?\n"
                + "      ,[address] = ?\n"
                + "      ,[createdAt] = ?\n"
                + " WHERE userId = ?";
        int n = 0;
        try {
            PreparedStatement ptm = connection.prepareStatement(sql);
            ptm.setInt(1, u.getRoleId());
            ptm.setString(2, u.getFullName());
            ptm.setString(3, u.getEmail());
            ptm.setString(4, u.getPasswordHash());
            ptm.setString(5, u.getPhoneNumber());
            ptm.setString(6, u.getAddress());
            ptm.setDate(7, u.getCreatedAt());
            ptm.setInt(8, u.getUserId());
            n = ptm.executeUpdate();
        } catch (SQLException ex) {
            ex.getStackTrace();
        }
        return n;
    }

    public static void main(String[] args) {
        BookDAO dao = new BookDAO();
        Vector<Book> vector = dao.getAllBook();
        for (Book b : vector) {
            System.out.println(b);
        }
    }
}

}
