package dal;

import java.util.Vector;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Book;

public class BookDAO extends DBContext {

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
    
    public static void main(String[] args) {
        BookDAO dao = new BookDAO();
        Vector<Book> vector = dao.getAllBook();
        for (Book b : vector) {
            System.out.println(b);
        }
    }
}
