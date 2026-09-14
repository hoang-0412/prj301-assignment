package dal;

import java.util.Vector;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.BookImages;

public class BookImagesDAO extends DBContext {

    public Vector<BookImages> getAllBookImages() {
        Vector<BookImages> vector = new Vector<>();
        String sql = "select * from BookImages";
        try {
            PreparedStatement ptm = connection.prepareStatement(sql);
            ResultSet rs = ptm.executeQuery();
            while (rs.next()) {
                BookImages bi = new BookImages(rs.getInt(1),
                        rs.getInt(2),
                        rs.getBytes(3),
                        rs.getString(4),
                        rs.getInt(5),
                        rs.getDate(6));
                vector.add(bi);
            }
        } catch (SQLException e) {
            e.getStackTrace();
        }
        return vector;
    }

    public static void main(String[] args) {
        BookImagesDAO dao = new BookImagesDAO();
        Vector<BookImages> vector = dao.getAllBookImages();
        for (BookImages bi : vector) {
            System.out.println(bi);
        }
    }
}
