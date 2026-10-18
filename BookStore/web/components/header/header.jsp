<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!-- STICKY HEADER (CĂN CÙNG MỘT DÒNG) -->
<header class="header-sticky" id="mainHeader">
    <div class="container header-container">
        <!-- 1. Logo -->
        <a href="${pageContext.request.contextPath}/home" class="brand-logo" id="brandLogo">
            <span>Hoàng<span class="accent">Trịnh</span></span>
        </a>

        <!-- 2. Các container task (Chính giữa màn hình căn dọc) -->
        <nav class="nav-task-links" aria-label="Menu chính">
            <a href="${pageContext.request.contextPath}/home" class="nav-link active">Trang Chủ</a>
            <a href="${pageContext.request.contextPath}/home#featured" class="nav-link">Sách Nổi Bật</a>
            <a href="${pageContext.request.contextPath}/home#catalog" class="nav-link">Tất Cả Danh Mục</a>
            <a href="${pageContext.request.contextPath}/home#newsletter" class="nav-link">Khuyến Mãi</a>
            <a href="${pageContext.request.contextPath}/home#about" class="nav-link">Về Chúng Tôi</a>
        </nav>

        <!-- 3. Chức năng dạng Icon: Button tìm kiếm -> Giỏ hàng -> Login -->
        <div class="nav-action-icons">
            <!-- 3.1 Button Tìm Kiếm (Pop up) -->
            <button type="button" class="header-icon-btn" id="searchToggleBtn" title="Tìm kiếm sách" aria-label="Mở tìm kiếm">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
                    <circle cx="11" cy="11" r="8"></circle>
                    <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
                </svg>
            </button>

            <!-- 3.2 Giỏ Hàng -->
            <a href="${pageContext.request.contextPath}/cart" class="header-icon-btn cart-btn" id="navCartBtn" title="Giỏ hàng" aria-label="Giỏ hàng">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
                    <circle cx="9" cy="21" r="1"></circle>
                    <circle cx="20" cy="21" r="1"></circle>
                    <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"></path>
                </svg>
                <span class="cart-badge" id="navCartBadge" style="${cartCount > 0 ? '' : 'display:none;'}">
                    ${cartCount}
                </span>
            </a>

            <!-- 3.3 Login / Tài khoản -->
            <c:choose>
                <c:when test="${not empty sessionScope.user}">
                    <div class="header-user-wrap" id="headerUserWrap">
                        <button type="button" class="header-icon-btn user-btn" id="userProfileBtn" title="${sessionScope.user.fullName}" aria-label="Tài khoản">
                            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
                                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                                <circle cx="12" cy="7" r="4"></circle>
                            </svg>
                        </button>
                        <div class="header-user-dropdown" id="headerUserDropdown">
                            <div class="user-dropdown-info">
                                <div class="user-dropdown-name">${sessionScope.user.fullName}</div>
                                <div class="user-dropdown-email">${sessionScope.user.email}</div>
                            </div>
                            <div class="user-dropdown-divider"></div>
                            <c:if test="${sessionScope.user.roleId == 2}">
                                <a href="${pageContext.request.contextPath}/admin" class="user-dropdown-link">
                                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 20h9"></path><path d="M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z"></path></svg>
                                    <span>Trang Quản Trị</span>
                                </a>
                            </c:if>
                            <a href="${pageContext.request.contextPath}/logout" class="user-dropdown-link logout">
                                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path><polyline points="16 17 21 12 16 7"></polyline><line x1="21" y1="12" x2="9" y2="12"></line></svg>
                                <span>Đăng Xuất</span>
                            </a>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <button type="button" class="header-icon-btn" id="loginBtn" title="Đăng nhập tài khoản" aria-label="Đăng nhập">
                        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                            <circle cx="12" cy="7" r="4"></circle>
                        </svg>
                    </button>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</header>

<!-- POPUP TÌM KIẾM (TRUY VẤN GIỮ NGUYÊN) -->
<div class="search-popup-overlay" id="searchPopupOverlay" role="dialog" aria-modal="true" aria-labelledby="searchModalTitle">
    <div class="search-popup-card">
        <div class="search-popup-header">
            <h4 id="searchModalTitle">Tìm kiếm sách</h4>
            <button type="button" class="search-popup-close-btn" id="searchPopupCloseBtn" aria-label="Đóng tìm kiếm">✕</button>
        </div>
        <form action="${pageContext.request.contextPath}/home" method="GET" class="search-popup-form" id="searchForm" role="search">
            <!-- Giữ nguyên các tham số truy vấn categoryId và sort -->
            <c:if test="${not empty selectedCategoryId}">
                <input type="hidden" name="categoryId" value="${selectedCategoryId}">
            </c:if>
            <c:if test="${not empty selectedSort}">
                <input type="hidden" name="sort" value="${selectedSort}">
            </c:if>

            <div class="search-popup-box">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#64748b" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
                    <circle cx="11" cy="11" r="8"></circle>
                    <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
                </svg>
                <input type="text" name="keyword" id="searchInput" class="search-popup-input" 
                       placeholder="Nhập tên sách, tác giả, nhà xuất bản..." 
                       value="${fn:escapeXml(keyword)}" autocomplete="off">
                <button type="button" id="searchClearBtn" class="search-clear-btn" aria-label="Xóa từ khóa">✕</button>
                <button type="submit" class="search-popup-submit-btn" id="searchSubmitBtn">Tìm Kiếm</button>
            </div>
            <div class="search-popup-tags">
                <span class="tags-label">Gợi ý nhanh:</span>
                <a href="${pageContext.request.contextPath}/home?keyword=Java" class="search-tag">Java</a>
                <a href="${pageContext.request.contextPath}/home?keyword=Python" class="search-tag">Python</a>
                <a href="${pageContext.request.contextPath}/home?keyword=Kinh tế" class="search-tag">Kinh tế</a>
                <a href="${pageContext.request.contextPath}/home?keyword=Tâm lý" class="search-tag">Tâm lý</a>
            </div>
        </form>
    </div>
</div>

<!-- POPUP ĐĂNG NHẬP (MODAL DIALOG CHUẨN GIAO DIỆN TULALOVE.VN) -->
<div class="auth-modal-overlay" id="authModalOverlay" role="dialog" aria-modal="true" aria-labelledby="authModalTitle">
    <div class="auth-modal-card">
        <!-- Nút Đóng góc trên bên phải -->
        <button type="button" class="auth-modal-close" id="authModalCloseBtn" aria-label="Đóng cửa sổ">✕</button>

        <!-- Logo tròn chính giữa bên trên -->
        <div class="auth-modal-avatar">
            <svg width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="#f43f5e" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"></path>
                <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"></path>
            </svg>
        </div>

        <!-- ================= BƯỚC 1: NHẬP SỐ ĐIỆN THOẠI HOẶC EMAIL ================= -->
        <div class="auth-step-panel" id="authStep1Panel">
            <h3 class="auth-title" id="authModalTitle">Đăng nhập</h3>
            <p class="auth-subtitle">Nhập số điện thoại hoặc email hoặc đăng ký tài khoản mới</p>

            <div class="auth-feedback" id="authFeedback1" style="display: none;"></div>

            <form id="authStep1Form" onsubmit="return false;">
                <div class="auth-form-group">
                    <input type="text" id="authIdentifierInput" class="auth-input" 
                           placeholder="Nhập số điện thoại hoặc email" 
                           autocomplete="username" required>
                </div>

                <button type="submit" class="auth-btn-primary" id="authStep1SubmitBtn">
                    <span>Tiếp tục</span>
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.3" stroke-linecap="round" stroke-linejoin="round">
                        <line x1="5" y1="12" x2="19" y2="12"></line>
                        <polyline points="12 5 19 12 12 19"></polyline>
                    </svg>
                </button>
            </form>
        </div>

        <!-- ================= BƯỚC 2: NHẬP MẬT KHẨU ================= -->
        <div class="auth-step-panel" id="authStep2Panel" style="display: none;">
            <h3 class="auth-title">Đăng nhập</h3>

            <!-- Thẻ hiển thị tài khoản vừa nhập kèm nút đổi -->
            <div class="auth-account-badge">
                <span class="auth-badge-id" id="authBadgeIdentifier">nguyenvana@gmail.com</span>
                <button type="button" class="auth-badge-change" id="authBackToStep1Btn">Thay đổi</button>
            </div>

            <div class="auth-feedback" id="authFeedback2" style="display: none;"></div>

            <form id="authStep2Form" onsubmit="return false;">
                <div class="auth-form-group">
                    <label class="auth-label" for="authPasswordInput">Nhập mật khẩu</label>
                    <div class="auth-input-wrapper">
                        <input type="password" id="authPasswordInput" class="auth-input" 
                               placeholder="Nhập mật khẩu" 
                               autocomplete="current-password" required>
                        <button type="button" class="auth-eye-btn" id="authEyeBtn" aria-label="Hiện/ẩn mật khẩu">
                            <svg class="eye-icon-show" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                                <circle cx="12" cy="12" r="3"></circle>
                            </svg>
                            <svg class="eye-icon-hide" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="display: none;">
                                <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"></path>
                                <line x1="1" y1="1" x2="23" y2="23"></line>
                            </svg>
                        </button>
                    </div>
                    <div class="auth-link-row">
                        <a href="javascript:void(0)" class="auth-forgot-link" id="authForgotLink">Quên mật khẩu?</a>
                    </div>
                </div>

                <button type="submit" class="auth-btn-primary" id="authLoginSubmitBtn">
                    <span>Tiếp tục</span>
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.3" stroke-linecap="round" stroke-linejoin="round">
                        <line x1="5" y1="12" x2="19" y2="12"></line>
                        <polyline points="12 5 19 12 12 19"></polyline>
                    </svg>
                </button>
            </form>
        </div>

        <!-- ================= BƯỚC 3: ĐĂNG KÝ TÀI KHOẢN MỚI ================= -->
        <div class="auth-step-panel" id="authStepRegisterPanel" style="display: none;">
            <h3 class="auth-title">Đăng ký tài khoản</h3>
            <p class="auth-subtitle">Tài khoản chưa tồn tại. Tạo mật khẩu để bắt đầu mua sắm</p>

            <div class="auth-feedback" id="authFeedbackRegister" style="display: none;"></div>

            <form id="authRegisterForm" onsubmit="return false;">
                <div class="auth-form-group">
                    <label class="auth-label" for="authRegisterFullName">Họ và tên</label>
                    <input type="text" id="authRegisterFullName" class="auth-input" 
                           placeholder="Nhập họ và tên của bạn" required>
                </div>

                <div class="auth-form-group" style="margin-top: 14px;">
                    <label class="auth-label" for="authRegisterPassword">Tạo mật khẩu</label>
                    <div class="auth-input-wrapper">
                        <input type="password" id="authRegisterPassword" class="auth-input" 
                               placeholder="Mật khẩu tối thiểu 6 ký tự" required>
                        <button type="button" class="auth-eye-btn" id="authRegisterEyeBtn" aria-label="Hiện/ẩn mật khẩu">
                            <svg class="eye-icon-show" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                                <circle cx="12" cy="12" r="3"></circle>
                            </svg>
                            <svg class="eye-icon-hide" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="display: none;">
                                <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"></path>
                                <line x1="1" y1="1" x2="23" y2="23"></line>
                            </svg>
                        </button>
                    </div>
                </div>

                <button type="submit" class="auth-btn-primary" id="authRegisterSubmitBtn">
                    <span>Đăng ký & Đăng nhập</span>
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.3" stroke-linecap="round" stroke-linejoin="round">
                        <line x1="5" y1="12" x2="19" y2="12"></line>
                        <polyline points="12 5 19 12 12 19"></polyline>
                    </svg>
                </button>
            </form>

            <div style="text-align: center; margin-top: 14px;">
                <button type="button" class="auth-link-back" id="authCancelRegisterBtn">← Sử dụng tài khoản khác</button>
            </div>
        </div>

        <!-- ================= BƯỚC 4: QUÊN MẬT KHẨU ================= -->
        <div class="auth-step-panel" id="authStepForgotPanel" style="display: none;">
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

            <button type="button" class="auth-btn-primary" id="authBackFromForgotBtn" style="background: #334155; margin-top: 18px;">
                <span>← Quay lại đăng nhập</span>
            </button>
        </div>
    </div>
</div>

