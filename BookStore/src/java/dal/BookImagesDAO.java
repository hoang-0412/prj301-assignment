package dal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.BookImages;

public class BookImagesDAO extends DBContext {

    private static final Logger LOGGER = Logger.getLogger(BookImagesDAO.class.getName());

    /**
     * Đọc toàn bộ dữ liệu byte của ảnh từ ResultSet.
     * Sử dụng InputStream và ByteArrayOutputStream để đọc tuần tự từng buffer chunk,
     * đảm bảo đọc trọn vẹn 100% dung lượng ảnh mà không bị cắt xén hay tràn bộ đệm
     * (đặc biệt quan trọng với cột VARBINARY(MAX) trong SQL Server).
     */
    private byte[] readFullImageData(ResultSet rs, String columnName) throws SQLException {
        try (InputStream is = rs.getBinaryStream(columnName)) {
            if (is == null) {
                return null;
            }
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] temp = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(temp)) != -1) {
                buffer.write(temp, 0, bytesRead);
            }
            return buffer.toByteArray();
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Không thể đọc qua binary stream, fallback sang rs.getBytes(): {0}", ex.getMessage());
            return rs.getBytes(columnName);
        }
    }

    /**
     * Chuyển đổi dòng hiện tại của ResultSet thành đối tượng BookImages
     */
    private BookImages mapResultSetToBookImage(ResultSet rs) throws SQLException {
        int imageId = rs.getInt("imageId");
        int bookId = rs.getInt("bookId");
        byte[] imageData = readFullImageData(rs, "imageData");
        String mimeType = rs.getString("mimeType");
        int isCover = rs.getInt("isCover");
        java.sql.Date createdAt = rs.getDate("createdAt");

        return new BookImages(imageId, bookId, imageData, mimeType, isCover, createdAt);
    }

    /**
     * Lấy tất cả ảnh sách trong database
     */
    public Vector<BookImages> getAllBookImages() {
        Vector<BookImages> vector = new Vector<>();
        String sql = "SELECT imageId, bookId, imageData, mimeType, isCover, createdAt FROM BookImages";
        try (PreparedStatement ptm = connection.prepareStatement(sql);
             ResultSet rs = ptm.executeQuery()) {
            while (rs.next()) {
                vector.add(mapResultSetToBookImage(rs));
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy toàn bộ danh sách ảnh sách", ex);
        }
        return vector;
    }

    /**
     * Lấy tất cả ảnh của một cuốn sách cụ thể
     */
    public Vector<BookImages> getImagesByBookId(int bookId) {
        Vector<BookImages> vector = new Vector<>();
        String sql = "SELECT imageId, bookId, imageData, mimeType, isCover, createdAt FROM BookImages WHERE bookId = ? ORDER BY isCover DESC, imageId ASC";
        try (PreparedStatement ptm = connection.prepareStatement(sql)) {
            ptm.setInt(1, bookId);
            try (ResultSet rs = ptm.executeQuery()) {
                while (rs.next()) {
                    vector.add(mapResultSetToBookImage(rs));
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy danh sách ảnh theo bookId: " + bookId, ex);
        }
        return vector;
    }

    /**
     * Lấy ảnh bìa (cover image) của cuốn sách
     */
    public BookImages getCoverImageByBookId(int bookId) {
        String sql = "SELECT TOP 1 imageId, bookId, imageData, mimeType, isCover, createdAt FROM BookImages WHERE bookId = ? AND isCover = 1";
        try (PreparedStatement ptm = connection.prepareStatement(sql)) {
            ptm.setInt(1, bookId);
            try (ResultSet rs = ptm.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBookImage(rs);
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy ảnh bìa theo bookId: " + bookId, ex);
        }
        return null;
    }

    /**
     * Lấy một ảnh cụ thể theo imageId
     */
    public BookImages getImageById(int imageId) {
        String sql = "SELECT imageId, bookId, imageData, mimeType, isCover, createdAt FROM BookImages WHERE imageId = ?";
        try (PreparedStatement ptm = connection.prepareStatement(sql)) {
            ptm.setInt(1, imageId);
            try (ResultSet rs = ptm.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBookImage(rs);
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy ảnh theo imageId: " + imageId, ex);
        }
        return null;
    }

    /**
     * Thêm mới một ảnh vào database
     */
    public int insertBookImage(BookImages bi) {
        String sql = "INSERT INTO BookImages (bookId, imageData, mimeType, isCover) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ptm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ptm.setInt(1, bi.getBookId());
            ptm.setBytes(2, bi.getImageData());
            ptm.setString(3, bi.getMimeType());
            ptm.setInt(4, bi.getIsCover());

            int affectedRows = ptm.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ptm.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        bi.setImageId(generatedKeys.getInt(1));
                    }
                }
            }
            return affectedRows;
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi thêm ảnh mới", ex);
        }
        return 0;
    }

    /**
     * Xóa ảnh theo imageId
     */
    public int deleteBookImage(int imageId) {
        String sql = "DELETE FROM BookImages WHERE imageId = ?";
        try (PreparedStatement ptm = connection.prepareStatement(sql)) {
            ptm.setInt(1, imageId);
            return ptm.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi xóa ảnh có imageId: " + imageId, ex);
        }
        return 0;
    }

    public static void main(String[] args) {
        BookImagesDAO dao = new BookImagesDAO();
        System.out.println("Kiểm tra kết nối DB: " + dao.isConnected());
        Vector<BookImages> vector = dao.getAllBookImages();
        System.out.println("Tổng số ảnh đọc được: " + vector.size());
        for (BookImages bi : vector) {
            System.out.println(bi);
            System.out.println(" -> Độ dài mảng byte: " + (bi.getImageData() != null ? bi.getImageData().length : 0));
            System.out.println(" -> Base64 URI: " + 
                (bi.getBase64Src().length() > 50 ? bi.getBase64Src().substring(0, 50) + "..." : bi.getBase64Src()));
        }
    }
}
