<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!-- STICKY HEADER (CĂN CÙNG MỘT DÒNG) -->
<header class="header-sticky" id="mainHeader">
    <div class="container header-container">
        <!-- 1. Logo -->
        <a href="${pageContext.request.contextPath}/home" class="brand-logo" id="brandLogo">
            <span>Book<span class="accent">Store</span></span>
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
                    <a href="${pageContext.request.contextPath}/profile" class="header-icon-btn user-btn" id="userProfileBtn" title="${sessionScope.user.fullName}" aria-label="Hồ sơ tài khoản">
                        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                            <circle cx="12" cy="7" r="4"></circle>
                        </svg>
                    </a>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/login.jsp" class="header-icon-btn" id="loginBtn" title="Đăng nhập tài khoản" aria-label="Đăng nhập">
                        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                            <circle cx="12" cy="7" r="4"></circle>
                        </svg>
                    </a>
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
