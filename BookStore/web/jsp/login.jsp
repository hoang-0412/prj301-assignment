<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Đăng nhập - Tiệm sách Hoàng Trịnh</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/components/header/header.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/components/footer/footer.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/login.css">
    </head>
    <body>

        <!-- HEADER -->
        <jsp:include page="/components/header/header.jsp" />

        <!-- STANDALONE LOGIN CARD -->
        <main class="standalone-login-wrapper">
            <div class="auth-modal-card">
                <!-- Circular Logo in center -->
                <div class="auth-modal-avatar">
                    <svg width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="#f43f5e" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"></path>
                        <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"></path>
                    </svg>
                </div>

                <!-- Server Error Alert if any -->
                <c:if test="${not empty error}">
                    <div class="auth-feedback error">
                        ${error}
                    </div>
                </c:if>

                <!-- ================= BƯỚC 1: NHẬP SỐ ĐIỆN THOẠI HOẶC EMAIL ================= -->
                <div class="auth-step-panel" id="pageAuthStep1Panel">
                    <h3 class="auth-title">Đăng nhập</h3>
                    <p class="auth-subtitle">Nhập số điện thoại hoặc email hoặc đăng ký tài khoản mới</p>

                    <div class="auth-feedback" id="pageAuthFeedback1"></div>

                    <form id="pageAuthStep1Form">
                        <div class="auth-form-group">
                            <input type="text" id="pageAuthIdentifierInput" class="auth-input" 
                                   placeholder="Nhập số điện thoại hoặc email" 
                                   value="${identifier}" autocomplete="username" required>
                        </div>

                        <button type="submit" class="auth-btn-primary" id="pageAuthStep1SubmitBtn">
                            <span>Tiếp tục</span>
                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.3" stroke-linecap="round" stroke-linejoin="round">
                                <line x1="5" y1="12" x2="19" y2="12"></line>
                                <polyline points="12 5 19 12 12 19"></polyline>
                            </svg>
                        </button>
                    </form>
                    <div class="auth-register-prompt">
                        Chưa có tài khoản? <a href="${pageContext.request.contextPath}/register" class="auth-register-link">Đăng ký tài khoản mới →</a>
                    </div>
                </div>

                <!-- ================= BƯỚC 2: NHẬP MẬT KHẨU ================= -->
                <div class="auth-step-panel auth-step-hidden" id="pageAuthStep2Panel">
                    <h3 class="auth-title">Đăng nhập</h3>

                    <div class="auth-account-badge">
                        <span class="auth-badge-id" id="pageAuthBadgeIdentifier">nguyenvana@gmail.com</span>
                        <button type="button" class="auth-badge-change" id="pageAuthBackToStep1Btn">Thay đổi</button>
                    </div>

                    <div class="auth-feedback" id="pageAuthFeedback2"></div>

                    <form id="pageAuthStep2Form">
                        <div class="auth-form-group">
                            <label class="auth-label" for="pageAuthPasswordInput">Nhập mật khẩu</label>
                            <div class="auth-input-wrapper">
                                <input type="password" id="pageAuthPasswordInput" class="auth-input" 
                                       placeholder="Nhập mật khẩu" 
                                       autocomplete="current-password" required>
                                <button type="button" class="auth-eye-btn" id="pageAuthEyeBtn" aria-label="Hiện/ẩn mật khẩu">
                                    <svg class="eye-icon-show" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                        <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                                        <circle cx="12" cy="12" r="3"></circle>
                                    </svg>
                                    <svg class="eye-icon-hide" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                        <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"></path>
                                        <line x1="1" y1="1" x2="23" y2="23"></line>
                                    </svg>
                                </button>
                            </div>
                            <div class="auth-link-row">
                                <a href="#" class="auth-forgot-link" id="pageAuthForgotLink">Quên mật khẩu?</a>
                            </div>
                        </div>

                        <button type="submit" class="auth-btn-primary" id="pageAuthLoginSubmitBtn">
                            <span>Tiếp tục</span>
                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.3" stroke-linecap="round" stroke-linejoin="round">
                                <line x1="5" y1="12" x2="19" y2="12"></line>
                                <polyline points="12 5 19 12 12 19"></polyline>
                            </svg>
                        </button>
                    </form>
                </div>

                <!-- ================= BƯỚC 3: ĐĂNG KÝ ================= -->
                <div class="auth-step-panel auth-step-hidden" id="pageAuthStepRegisterPanel">
                    <h3 class="auth-title">Đăng ký tài khoản</h3>
                    <p class="auth-subtitle">Tài khoản chưa tồn tại. Tạo mật khẩu để bắt đầu mua sắm</p>

                    <div class="auth-feedback" id="pageAuthFeedbackRegister"></div>

                    <form id="pageAuthRegisterForm">
                        <div class="auth-form-group">
                            <label class="auth-label" for="pageAuthRegisterFullName">Họ và tên</label>
                            <input type="text" id="pageAuthRegisterFullName" class="auth-input" 
                                   placeholder="Nhập họ và tên của bạn" required>
                        </div>

                        <div class="auth-form-group auth-form-group-spaced">
                            <label class="auth-label" for="pageAuthRegisterPassword">Tạo mật khẩu</label>
                            <div class="auth-input-wrapper">
                                <input type="password" id="pageAuthRegisterPassword" class="auth-input" 
                                       placeholder="Mật khẩu tối thiểu 6 ký tự" required>
                                <button type="button" class="auth-eye-btn" id="pageAuthRegisterEyeBtn" aria-label="Hiện/ẩn mật khẩu">
                                    <svg class="eye-icon-show" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                        <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                                        <circle cx="12" cy="12" r="3"></circle>
                                    </svg>
                                    <svg class="eye-icon-hide" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                        <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"></path>
                                        <line x1="1" y1="1" x2="23" y2="23"></line>
                                    </svg>
                                </button>
                            </div>
                        </div>

                        <button type="submit" class="auth-btn-primary" id="pageAuthRegisterSubmitBtn">
                            <span>Đăng ký & Đăng nhập</span>
                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.3" stroke-linecap="round" stroke-linejoin="round">
                                <line x1="5" y1="12" x2="19" y2="12"></line>
                                <polyline points="12 5 19 12 12 19"></polyline>
                            </svg>
                        </button>
                    </form>

                    <div class="auth-back-wrap">
                        <button type="button" class="auth-link-back" id="pageAuthCancelRegisterBtn">← Sử dụng tài khoản khác</button>
                    </div>
                </div>

                <!-- ================= BƯỚC 4: QUÊN MẬT KHẨU ================= -->
                <div class="auth-step-panel auth-step-hidden" id="pageAuthStepForgotPanel">
                    <h3 class="auth-title">Quên mật khẩu?</h3>
                    <p class="auth-subtitle">Để bảo mật thông tin tài khoản, vui lòng liên hệ trực tiếp để được xác minh và cấp lại mật khẩu:</p>
                    
                    <div class="auth-contact-box">
                        <div class="contact-item">
                            <span class="contact-lbl">Hotline hỗ trợ:</span>
                            <strong class="contact-val">1900 8888 (8:00 - 21:00)</strong>
                        </div>
                        <div class="contact-item">
                            <span class="contact-lbl">Email hỗ trợ:</span>
                            <strong class="contact-val">support@bookstore.vn</strong>
                        </div>
                    </div>

                    <button type="button" class="auth-btn-primary auth-btn-back-forgot" id="pageAuthBackFromForgotBtn">
                        <span>← Quay lại đăng nhập</span>
                    </button>
                </div>
            </div>
        </main>

        <!-- FOOTER -->
        <jsp:include page="/components/footer/footer.jsp" />

        <script src="${pageContext.request.contextPath}/components/header/header.js"></script>
        <script src="${pageContext.request.contextPath}/components/footer/footer.js"></script>
        <script src="${pageContext.request.contextPath}/js/login.js"></script>
    </body>
</html>
