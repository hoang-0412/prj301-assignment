<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Đăng Ký Tài Khoản Thành Viên - Tiệm sách Hoàng Trịnh</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/components/header/header.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/components/footer/footer.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/register.css">
    </head>
    <body>

        <!-- HEADER -->
        <jsp:include page="/components/header/header.jsp" />

        <main class="reg-wrapper">
            <div class="reg-card">
                <div class="reg-header">
                    <span class="reg-badge">THÀNH VIÊN HOÀNG TRỊNH BOOKSTORE</span>
                    <h1>Đăng Ký Tài Khoản Mới</h1>
                    <p>Trở thành thành viên để đặt mua, thuê sách và theo dõi đơn hàng dễ dàng</p>
                </div>

                <c:if test="${not empty errorMsg}">
                    <div class="reg-alert error">
                        <span>⚠ ${fn:escapeXml(errorMsg)}</span>
                    </div>
                </c:if>

                <form action="${pageContext.request.contextPath}/register" method="POST" class="reg-form">
                    <div class="reg-grid">
                        <div class="reg-field full">
                            <label for="regFullName">Họ và tên đầy đủ <span class="req">*</span></label>
                            <input type="text" name="fullName" id="regFullName" class="reg-input"
                                   placeholder="VD: Nguyễn Văn A"
                                   value="${fn:escapeXml(fullName)}" required>
                        </div>

                        <div class="reg-field">
                            <label for="regEmail">Địa chỉ Email <span class="req">*</span></label>
                            <input type="email" name="email" id="regEmail" class="reg-input"
                                   placeholder="example@gmail.com"
                                   value="${fn:escapeXml(email)}" required>
                        </div>

                        <div class="reg-field">
                            <label for="regPhone">Số điện thoại <span class="req">*</span></label>
                            <input type="text" name="phoneNumber" id="regPhone" class="reg-input"
                                   placeholder="09xxxxxxxx"
                                   value="${fn:escapeXml(phoneNumber)}" required>
                        </div>

                        <div class="reg-field full">
                            <label for="regAddress">Địa chỉ nhận hàng mặc định</label>
                            <input type="text" name="address" id="regAddress" class="reg-input"
                                   placeholder="Số nhà, đường, phường/xã, quận/huyện, tỉnh/thành phố..."
                                   value="${fn:escapeXml(address)}">
                        </div>

                        <div class="reg-field">
                            <label for="regPassword">Mật khẩu <span class="req">*</span></label>
                            <input type="password" name="password" id="regPassword" class="reg-input"
                                   placeholder="Tối thiểu 6 ký tự" minlength="6" required>
                        </div>

                        <div class="reg-field">
                            <label for="regConfirmPassword">Xác nhận mật khẩu <span class="req">*</span></label>
                            <input type="password" name="confirmPassword" id="regConfirmPassword" class="reg-input"
                                   placeholder="Nhập lại mật khẩu" minlength="6" required>
                        </div>
                    </div>

                    <button type="submit" class="reg-submit-btn">
                        <span>Hoàn Tất Đăng Ký</span>
                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.3"><line x1="5" y1="12" x2="19" y2="12"/><polyline points="12 5 19 12 12 19"/></svg>
                    </button>
                </form>

                <div class="reg-footer-links">
                    <span>Bạn đã có tài khoản?</span>
                    <a href="${pageContext.request.contextPath}/login">Đăng nhập ngay →</a>
                </div>
            </div>
        </main>

        <!-- FOOTER -->
        <jsp:include page="/components/footer/footer.jsp" />

        <script src="${pageContext.request.contextPath}/components/header/header.js"></script>
        <script src="${pageContext.request.contextPath}/components/footer/footer.js"></script>
        <script src="${pageContext.request.contextPath}/js/register.js"></script>
    </body>
</html>

