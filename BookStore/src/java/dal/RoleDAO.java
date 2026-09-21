package dal;

import java.util.Vector;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Role;

public class RoleDAO extends DBContext {

    public Vector<Role> getAllRole() {
        Vector<Role> vector = new Vector<>();
        String sql = "select * from Role";
        try {
            PreparedStatement ptm = connection.prepareStatement(sql);
            ResultSet rs = ptm.executeQuery();
            while (rs.next()) {
                Role r = new Role(rs.getInt(1), rs.getString(2));
                vector.add(r);
            }
        } catch (SQLException ex) {
            ex.getStackTrace();
        }
        return vector;
    }

    public Role searchRole(int roleId) {
        String sql = "select * from Role\n"
                + "where roleId = ?";
        try {
            PreparedStatement ptm = connection.prepareStatement(sql);
            ptm.setInt(1, roleId);
            ResultSet rs = ptm.executeQuery();
            if (rs.next()) {
                Role r = new Role(rs.getInt(1), rs.getString(2));
                return r;
            }
        } catch (SQLException ex) {
            ex.getStackTrace();
        }
        return null;
    }

    public int insertRole(Role r) {
        String sql = "INSERT INTO [dbo].[Role]\n"
                + "           ([roleName])\n"
                + "     VALUES\n"
                + "           (?)";
        int n = 0;
        try {
            PreparedStatement ptm = connection.prepareStatement(sql);
            ptm.setString(1, r.getRoleName());
            n = ptm.executeUpdate();
        } catch (SQLException ex) {
            ex.getStackTrace();
        }
        return n;
    }

    public int updateRole(Role r) {
        String sql = "UPDATE [dbo].[Role]\n"
                + "   SET [roleName] = ?\n"
                + " WHERE roleId = ?";
        int n = 0;
        try {
            PreparedStatement ptm = connection.prepareStatement(sql);
            ptm.setString(1, r.getRoleName());
            n = ptm.executeUpdate();
        } catch (SQLException ex) {
            ex.getStackTrace();
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
