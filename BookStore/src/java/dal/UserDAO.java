package dal;

import java.util.Vector;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.User;

public class UserDAO extends DBContext {

    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());

    public Vector<User> getAllUser() {
        Vector<User> vector = new Vector<>();
        String sql = "SELECT userId, roleId, fullName, email, passwordHash, phoneNumber, address, createdAt FROM [User]";
        try (PreparedStatement ptm = connection.prepareStatement(sql);
             ResultSet rs = ptm.executeQuery()) {
            while (rs.next()) {
                User u = new User(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getString(7),
                        rs.getDate(8)
                );
                vector.add(u);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy toàn bộ danh sách người dùng", ex);
        }
        return vector;
    }

    public User searchUser(int userId) {
        String sql = "SELECT userId, roleId, fullName, email, passwordHash, phoneNumber, address, createdAt FROM [User] WHERE userId = ?";
        try (PreparedStatement ptm = connection.prepareStatement(sql)) {
            ptm.setInt(1, userId);
            try (ResultSet rs = ptm.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt(1),
                            rs.getInt(2),
                            rs.getString(3),
                            rs.getString(4),
                            rs.getString(5),
                            rs.getString(6),
                            rs.getString(7),
                            rs.getDate(8)
                    );
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tìm người dùng theo id: " + userId, ex);
        }
        return null;
    }

    public User getUserByEmail(String email) {
        String sql = "SELECT userId, roleId, fullName, email, passwordHash, phoneNumber, address, createdAt FROM [User] WHERE email = ?";
        try (PreparedStatement ptm = connection.prepareStatement(sql)) {
            ptm.setString(1, email);
            try (ResultSet rs = ptm.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt(1),
                            rs.getInt(2),
                            rs.getString(3),
                            rs.getString(4),
                            rs.getString(5),
                            rs.getString(6),
                            rs.getString(7),
                            rs.getDate(8)
                    );
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tìm người dùng theo email: " + email, ex);
        }
        return null;
    }

    public User getUserByEmailOrPhone(String identifier) {
        if (identifier == null || identifier.trim().isEmpty()) {
            return null;
        }
        String idClean = identifier.trim();
        String sql = "SELECT userId, roleId, fullName, email, passwordHash, phoneNumber, address, createdAt FROM [User] WHERE email = ? OR phoneNumber = ?";
        try (PreparedStatement ptm = connection.prepareStatement(sql)) {
            ptm.setString(1, idClean);
            ptm.setString(2, idClean);
            try (ResultSet rs = ptm.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt(1),
                            rs.getInt(2),
                            rs.getString(3),
                            rs.getString(4),
                            rs.getString(5),
                            rs.getString(6),
                            rs.getString(7),
                            rs.getDate(8)
                    );
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tìm người dùng theo email hoặc SĐT: " + idClean, ex);
        }
        return null;
    }

    public boolean checkPassword(User user, String rawPassword) {
        if (user == null || rawPassword == null) {
            return false;
        }
        String hash = user.getPasswordHash();
        if (hash == null) {
            return false;
        }
        return hash.equals(rawPassword)
                || hash.equals("hashed_pwd_" + rawPassword)
                || ("hashed_pwd_" + hash).equals(rawPassword);
    }

    public int insertUser(User u) {
        String sql = "INSERT INTO [dbo].[User] ([roleId], [fullName], [email], [passwordHash], [phoneNumber], [address], [createdAt]) VALUES (?, ?, ?, ?, ?, ?, ?)";
        int n = 0;
        try (PreparedStatement ptm = connection.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ptm.setInt(1, u.getRoleId());
            ptm.setString(2, u.getFullName());
            ptm.setString(3, u.getEmail());
            ptm.setString(4, u.getPasswordHash());
            ptm.setString(5, u.getPhoneNumber());
            ptm.setString(6, u.getAddress());
            ptm.setDate(7, u.getCreatedAt());
            n = ptm.executeUpdate();
            if (n > 0) {
                try (ResultSet generatedKeys = ptm.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        u.setUserId(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi thêm người dùng mới", ex);
        }
        return n;
    }

    public int updateUser(User u) {
        String sql = "UPDATE [dbo].[User] SET [roleId] = ?, [fullName] = ?, [email] = ?, [passwordHash] = ?, [phoneNumber] = ?, [address] = ?, [createdAt] = ? WHERE userId = ?";
        int n = 0;
        try (PreparedStatement ptm = connection.prepareStatement(sql)) {
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
            LOGGER.log(Level.SEVERE, "Lỗi khi cập nhật thông tin người dùng", ex);
        }
        return n;
    }

    public static void main(String[] args) {
        UserDAO dao = new UserDAO();
        Vector<User> vector = dao.getAllUser();
        for (User u : vector) {
            System.out.println(u);
        }
    }
}