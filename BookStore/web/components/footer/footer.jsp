<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<footer class="main-footer" id="about">
    <div class="container">
        <div class="footer-grid">
            <!-- Brand info -->
            <div class="footer-col">
                <div class="footer-brand">
                    Hoàng<span>Trịnh</span>
                </div>
                <p class="footer-desc">
                    TIỆM SÁCH TNHH MỘT MÌNH TÔI
                </p>
                <div class="payment-badges">
                    <span class="payment-pill">VISA</span>
                    <span class="payment-pill">Mastercard</span>
                    <span class="payment-pill">VNPay</span>
                    <span class="payment-pill">MoMo</span>
                    <span class="payment-pill">COD</span>
                </div>
            </div>

            <!-- Categories list -->
            <div class="footer-col">
                <h5>Thể Loại Nổi Bật</h5>
                <ul class="footer-links">
                    <c:choose>
                        <c:when test="${not empty categories}">
                            <c:forEach items="${categories}" var="c" begin="0" end="4">
                                <li>
                                    <a href="${pageContext.request.contextPath}/home?categoryId=${c.categoryId}">${c.categoryName}</a>
                                </li>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <li><a href="${pageContext.request.contextPath}/home?categoryId=1">Công nghệ thông tin</a></li>
                            <li><a href="${pageContext.request.contextPath}/home?categoryId=2">Kinh tế - Khởi nghiệp</a></li>
                            <li><a href="${pageContext.request.contextPath}/home?categoryId=3">Văn học - Nghệ thuật</a></li>
                            <li><a href="${pageContext.request.contextPath}/home?categoryId=4">Tâm lý - Kỹ năng sống</a></li>
                            <li><a href="${pageContext.request.contextPath}/home?categoryId=5">Thiếu nhi - Giáo dục</a></li>
                        </c:otherwise>
                    </c:choose>
                </ul>
            </div>

            <!-- Quick links -->
            <div class="footer-col">
                <h5>Chính Sách & Hỗ Trợ</h5>
                <ul class="footer-links">
                    <li><a href="#">Điều khoản sử dụng</a></li>
                    <li><a href="#">Chính sách bảo mật</a></li>
                    <li><a href="#">Chính sách đổi trả hoàn tiền</a></li>
                    <li><a href="#">Phương thức vận chuyển</a></li>
                    <li><a href="#">Hướng dẫn mua hàng</a></li>
                </ul>
            </div>

            <!-- Contact -->
            <div class="footer-col" id="contact">
                <h5>Thông Tin Liên Hệ</h5>
                <div class="footer-contact-item">
                    <span>Địa chỉ: FPT UNIVERSITY, HA NOI, VIET NAM</span>
                </div>
                <div class="footer-contact-item">
                    <span>Hotline: 0965 551 664</span>
                </div>
                <div class="footer-contact-item">
                    <span>Email: hoangth04122005@gmail.com</span>
                </div>
            </div>
        </div>

        <div class="footer-bottom">
            <p>© <span id="footerCurrentYear">2026</span> BookStore Inc. Mọi quyền được bảo lưu. Dự án môn học PRJ301.</p>
            <p>Designed for Vietnamese Book Lovers</p>
        </div>
    </div>
</footer>
