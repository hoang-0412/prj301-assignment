package dal;

import java.util.Vector;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Role;

public class RoleDAO extends DBContext {

    private static final Logger LOGGER = Logger.getLogger(RoleDAO.class.getName());

    public Vector<Role> getAllRole() {
        Vector<Role> vector = new Vector<>();
        String sql = "SELECT roleId, roleName FROM [Role]";
        try (PreparedStatement ptm = connection.prepareStatement(sql);
             ResultSet rs = ptm.executeQuery()) {
            while (rs.next()) {
                Role r = new Role(rs.getInt(1), rs.getString(2));
                vector.add(r);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy danh sách vai trò", ex);
        }
        return vector;
    }

    public Role searchRole(int roleId) {
        String sql = "SELECT roleId, roleName FROM [Role] WHERE roleId = ?";
        try (PreparedStatement ptm = connection.prepareStatement(sql)) {
            ptm.setInt(1, roleId);
            try (ResultSet rs = ptm.executeQuery()) {
                if (rs.next()) {
                    return new Role(rs.getInt(1), rs.getString(2));
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tìm vai trò theo id: " + roleId, ex);
        }
        return null;
    }

    public int insertRole(Role r) {
        String sql = "INSERT INTO [dbo].[Role] ([roleName]) VALUES (?)";
        int n = 0;
        try (PreparedStatement ptm = connection.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ptm.setString(1, r.getRoleName());
            n = ptm.executeUpdate();
            if (n > 0) {
                try (ResultSet generatedKeys = ptm.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        r.setRoleId(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi thêm vai trò mới", ex);
        }
        return n;
    }

    public int updateRole(Role r) {
        String sql = "UPDATE [dbo].[Role] SET [roleName] = ? WHERE roleId = ?";
        int n = 0;
        try (PreparedStatement ptm = connection.prepareStatement(sql)) {
            ptm.setString(1, r.getRoleName());
            ptm.setInt(2, r.getRoleId());
            n = ptm.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi cập nhật vai trò", ex);
        }
        return n;
    }

    public static void main(String[] args) {
        RoleDAO dao = new RoleDAO();
        Vector<Role> vector = dao.getAllRole();
        for (Role r : vector) {
            System.out.println(r);
        }
    }
}
