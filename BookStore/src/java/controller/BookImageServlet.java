package controller;

import dal.BookDAO;
import dal.BookImagesDAO;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Book;
import model.BookImages;

@WebServlet(name = "BookImageServlet", urlPatterns = {"/book-image"})
public class BookImageServlet extends HttpServlet {

    private final BookImagesDAO bookImagesDAO = new BookImagesDAO();
    private final BookDAO bookDAO = new BookDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String bookIdParam = request.getParameter("bookId");
        String imageIdParam = request.getParameter("id");

        BookImages bookImage = null;
        int bookId = 0;

        try {
            if (bookIdParam != null && !bookIdParam.trim().isEmpty()) {
                bookId = Integer.parseInt(bookIdParam.trim());
                bookImage = bookImagesDAO.getCoverImageByBookId(bookId);
            } else if (imageIdParam != null && !imageIdParam.trim().isEmpty()) {
                int imageId = Integer.parseInt(imageIdParam.trim());
                bookImage = bookImagesDAO.getImageById(imageId);
                if (bookImage != null) {
                    bookId = bookImage.getBookId();
                }
            }
        } catch (NumberFormatException ignored) {
        }

        // If a valid real image exists (> 50 bytes)
        if (bookImage != null && bookImage.getImageData() != null && bookImage.getImageData().length > 50) {
            String mime = bookImage.getMimeType();
            if (mime == null || mime.trim().isEmpty()) {
                mime = "image/jpeg";
            }
            response.setContentType(mime);
            response.setContentLength(bookImage.getImageData().length);
            response.setHeader("Cache-Control", "public, max-age=86400"); // Cache 1 day

            try (OutputStream os = response.getOutputStream()) {
                os.write(bookImage.getImageData());
                os.flush();
            }
            return;
        }

        // Generate dynamic modern SVG book cover
        Book book = null;
        if (bookId > 0) {
            book = bookDAO.searchBook(bookId);
        }

        String svgContent = generateSvgCover(book, bookId);
        byte[] svgBytes = svgContent.getBytes(StandardCharsets.UTF_8);

        response.setContentType("image/svg+xml;charset=UTF-8");
        response.setContentLength(svgBytes.length);
        response.setHeader("Cache-Control", "public, max-age=86400");

        try (OutputStream os = response.getOutputStream()) {
            os.write(svgBytes);
            os.flush();
        }
    }

    private String generateSvgCover(Book book, int bookId) {
        String title = (book != null && book.getTitle() != null) ? book.getTitle() : "Sách Hay Tri Thức #" + bookId;
        String author = (book != null && book.getAuthor() != null) ? book.getAuthor() : "Tác Giả Nổi Tiếng";
        String category = (book != null && book.getCategoryName() != null) ? book.getCategoryName() : "Sách Chọn Lọc";
        int categoryId = (book != null) ? book.getCategoryId() : 1;

        // Escape XML entities
        title = escapeXml(title);
        author = escapeXml(author);
        category = escapeXml(category);

        // Color palettes based on category
        String gradientStart;
        String gradientEnd;
        String accentColor;

        switch (categoryId) {
            case 1: // CNTT - Tech Blue/Cyan
                gradientStart = "#0f172a";
                gradientEnd = "#0369a1";
                accentColor = "#38bdf8";
                break;
            case 2: // Kinh te - Emerald Green
                gradientStart = "#064e3b";
                gradientEnd = "#047857";
                accentColor = "#34d399";
                break;
            case 3: // Van hoc - Rich Indigo/Violet
                gradientStart = "#311042";
                gradientEnd = "#6d28d9";
                accentColor = "#c084fc";
                break;
            case 4: // Tam ly - Sunset Coral/Rose
                gradientStart = "#881337";
                gradientEnd = "#be123c";
                accentColor = "#fb7185";
                break;
            case 5: // Thieu nhi - Amber/Orange
                gradientStart = "#78350f";
                gradientEnd = "#b45309";
                accentColor = "#fcd34d";
                break;
            default:
                gradientStart = "#1e1b4b";
                gradientEnd = "#4338ca";
                accentColor = "#818cf8";
                break;
        }

        // Split title into 2-3 lines if long
        String[] lines = wrapTitle(title, 22);
        StringBuilder titleSvg = new StringBuilder();
        int startY = 175;
        for (int i = 0; i < lines.length && i < 3; i++) {
            titleSvg.append("<text x='150' y='").append(startY + (i * 24))
                    .append("' text-anchor='middle' font-family='sans-serif' font-size='16' font-weight='700' fill='#ffffff'>")
                    .append(lines[i]).append("</text>");
        }

        return "<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 300 420' width='300' height='420'>"
                + "<defs>"
                + "  <linearGradient id='bgGrad' x1='0%' y1='0%' x2='100%' y2='100%'>"
                + "    <stop offset='0%' stop-color='" + gradientStart + "' />"
                + "    <stop offset='100%' stop-color='" + gradientEnd + "' />"
                + "  </linearGradient>"
                + "  <linearGradient id='spineGrad' x1='0%' y1='0%' x2='100%' y2='0%'>"
                + "    <stop offset='0%' stop-color='rgba(0,0,0,0.5)' />"
                + "    <stop offset='70%' stop-color='rgba(255,255,255,0.1)' />"
                + "    <stop offset='100%' stop-color='rgba(0,0,0,0.2)' />"
                + "  </linearGradient>"
                + "  <filter id='shadow' x='-10%' y='-10%' width='120%' height='120%'>"
                + "    <feDropShadow dx='0' dy='8' stdDeviation='10' flood-color='rgba(0,0,0,0.35)'/>"
                + "  </filter>"
                + "</defs>"
                + "<!-- Book Body -->"
                + "<rect x='0' y='0' width='300' height='420' rx='10' fill='url(#bgGrad)' />"
                + "<!-- Inner Frame -->"
                + "<rect x='16' y='16' width='268' height='388' rx='6' fill='none' stroke='" + accentColor + "' stroke-width='1.5' stroke-opacity='0.4' />"
                + "<!-- Spine Effect -->"
                + "<rect x='0' y='0' width='16' height='420' rx='10' fill='url(#spineGrad)' />"
                + "<line x1='16' y1='0' x2='16' y2='420' stroke='rgba(255,255,255,0.15)' stroke-width='1' />"
                + "<!-- Category Badge -->"
                + "<rect x='40' y='42' width='220' height='26' rx='13' fill='" + accentColor + "' fill-opacity='0.2' />"
                + "<text x='150' y='59' text-anchor='middle' font-family='sans-serif' font-size='11' font-weight='600' fill='" + accentColor + "' letter-spacing='1'>"
                + category.toUpperCase() + "</text>"
                + "<!-- Book Icon -->"
                + "<circle cx='150' cy='115' r='28' fill='rgba(255,255,255,0.08)' />"
                + "<path d='M138 107h10a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3h-11zm24 0h-10a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h11z' fill='" + accentColor + "' />"
                + "<!-- Title -->"
                + titleSvg.toString()
                + "<!-- Divider -->"
                + "<line x1='110' y1='270' x2='190' y2='270' stroke='" + accentColor + "' stroke-width='2' stroke-linecap='round' stroke-opacity='0.6' />"
                + "<!-- Author -->"
                + "<text x='150' y='305' text-anchor='middle' font-family='sans-serif' font-size='13' font-style='italic' fill='rgba(255,255,255,0.85)'>"
                + author + "</text>"
                + "<!-- Brand / Bottom Badge -->"
                + "<rect x='45' y='360' width='210' height='24' rx='6' fill='rgba(0,0,0,0.3)' />"
                + "<text x='150' y='376' text-anchor='middle' font-family='sans-serif' font-size='10' font-weight='500' fill='rgba(255,255,255,0.6)' letter-spacing='1.5'>"
                + "BOOKSTORE • OFFICIAL EDITION</text>"
                + "</svg>";
    }

    private String[] wrapTitle(String text, int maxCharsPerLine) {
        if (text == null) return new String[]{""};
        String[] words = text.split("\\s+");
        StringBuilder line = new StringBuilder();
        java.util.List<String> result = new java.util.ArrayList<>();

        for (String word : words) {
            if (line.length() + word.length() + 1 > maxCharsPerLine) {
                if (line.length() > 0) {
                    result.add(line.toString());
                    line = new StringBuilder();
                }
            }
            if (line.length() > 0) {
                line.append(" ");
            }
            line.append(word);
        }
        if (line.length() > 0) {
            result.add(line.toString());
        }
        return result.toArray(new String[0]);
    }

    private String escapeXml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
