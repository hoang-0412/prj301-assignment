package dal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBContext {

    private static final Logger LOGGER = Logger.getLogger(DBContext.class.getName());
    protected Connection connection;

    public DBContext() {
        try {
            Properties properties = new Properties();
            InputStream inputStream = null;

            // 1. Thử đọc từ ClassLoader ("ConnectDB.properties" trong classpath)
            inputStream = getClass().getClassLoader().getResourceAsStream("ConnectDB.properties");

            // 2. Thử đọc từ Class ("../ConnectDB.properties" hoặc "/ConnectDB.properties")
            if (inputStream == null) {
                inputStream = getClass().getResourceAsStream("/ConnectDB.properties");
            }
            if (inputStream == null) {
                inputStream = getClass().getClassLoader().getResourceAsStream("../ConnectDB.properties");
            }

            // 3. Thử đọc từ file vật lý nếu đang chạy từ thư mục web
            if (inputStream == null) {
                try {
                    File propFile = new File("web/WEB-INF/ConnectDB.properties");
                    if (propFile.exists()) {
                        inputStream = new FileInputStream(propFile);
                    } else {
                        File propFileAlt = new File("../web/WEB-INF/ConnectDB.properties");
                        if (propFileAlt.exists()) {
                            inputStream = new FileInputStream(propFileAlt);
                        }
                    }
                } catch (java.io.FileNotFoundException ignored) {
                }
            }

            if (inputStream != null) {
                try {
                    properties.load(inputStream);
                } catch (IOException ex) {
                    LOGGER.log(Level.WARNING, "Không thể đọc nội dung file ConnectDB.properties: " + ex.getMessage());
                } finally {
                    try {
                        inputStream.close();
                    } catch (IOException ignored) {
                    }
                }
            }

            String user = properties.getProperty("userID");
            String pass = properties.getProperty("password");
            String url = properties.getProperty("url");

            // Giá trị mặc định nếu file properties rỗng hoặc thiếu
            if (user == null || user.trim().isEmpty()) {
                user = "sa";
            }
            if (pass == null || pass.trim().isEmpty()) {
                pass = "123";
            }
            if (url == null || url.trim().isEmpty()) {
                url = "jdbc:sqlserver://localhost:1433;databaseName=BookStoreDB;encrypt=true;trustServerCertificate=true;";
            }

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            // Thử kết nối theo URL cấu hình
            try {
                connection = DriverManager.getConnection(url, user, pass);
            } catch (SQLException ex) {
                // Fallback nếu kết nối localhost:1433 thất bại, thử instance TRINHHUYHOANG\\SQLEXPRESS
                LOGGER.log(Level.WARNING, "Kết nối URL chính thất bại, thử URL dự phòng: " + ex.getMessage());
                String fallbackUrl = "jdbc:sqlserver://TRINHHUYHOANG\\SQLEXPRESS:1433;databaseName=BookStoreDB;encrypt=true;trustServerCertificate=true;";
                if (!fallbackUrl.equalsIgnoreCase(url)) {
                    connection = DriverManager.getConnection(fallbackUrl, user, pass);
                } else {
                    throw ex;
                }
            }

        } catch (ClassNotFoundException | SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi kết nối cơ sở dữ liệu: " + ex.getMessage(), ex);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public ResultSet getData(String sql) {
        ResultSet rs = null;
        try {
            Statement st = connection.createStatement(
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE
            );
            rs = st.executeQuery(sql);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi thực thi câu truy vấn SQL: " + sql, ex);
        }
        return rs;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        }
    }

    public static void main(String[] args) {
        DBContext dbContext = new DBContext();
        if (dbContext.isConnected()) {
            System.out.println("Kết nối cơ sở dữ liệu thành công!");
        } else {
            System.out.println("Kết nối cơ sở dữ liệu thất bại.");
        }
    }
}
