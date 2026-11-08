<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Bảng Điều Khiển Quản Trị - Tiệm sách Hoàng Trịnh</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/components/header/header.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/components/footer/footer.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admindashboard.css">
    </head>
    <body>

        <!-- HEADER COMPONENT -->
        <jsp:include page="/components/header/header.jsp" />

        <!-- 1. ADMIN TOP HERO BANNER -->
        <section class="admin-hero">
            <div class="admin-container">
                <div class="admin-hero-inner">
                    <div class="admin-hero-left">
                        <span class="admin-badge-top">
                            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.3"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>
                            <span>HỆ THỐNG QUẢN TRỊ BOOKSTORE</span>
                        </span>
                        <h1 class="admin-hero-title">Bảng Điều Khiển Quản Trị</h1>
                        <p class="admin-hero-sub">
                            Xin chào <strong>${not empty sessionScope.user ? fn:escapeXml(sessionScope.user.fullName) : 'Quản trị viên'}</strong> — Quản lý kho sách, thể loại, đơn hàng và tài khoản khách hàng trực tiếp từ CSDL.
                        </p>
                    </div>
                    <div class="admin-hero-actions">
                        <a href="${pageContext.request.contextPath}/homepage" class="btn-hero-action btn-hero-outline">
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>
                            <span>Xem Trang Chủ</span>
                        </a>
                        <button type="button" class="btn-hero-action btn-hero-primary" id="btnHeroAddBook">
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
                            <span>Thêm Sách Mới</span>
                        </button>
                    </div>
                </div>
            </div>
        </section>

        <!-- MAIN CONTENT CONTAINER -->
        <main class="admin-container">

            <!-- 2. KPI SUMMARY CARDS -->
            <div class="kpi-grid">
                <!-- Card 1: Books & Stock -->
                <div class="kpi-card">
                    <div class="kpi-info">
                        <h4>Tổng Đầu Sách</h4>
                        <div class="kpi-value">${allBooksCount}</div>
                        <div class="kpi-sub">
                            Tồn kho: <strong>${totalStockCount} cuốn</strong> (${booksForSaleCount} bán • ${booksForRentCount} thuê)
                        </div>
                    </div>
                    <div class="kpi-icon indigo">
                        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/><path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/></svg>
                    </div>
                </div>

                <!-- Card 2: Categories -->
                <div class="kpi-card">
                    <div class="kpi-info">
                        <h4>Thể Loại Sách</h4>
                        <div class="kpi-value">${fn:length(categories)}</div>
                        <div class="kpi-sub">
                            Phân loại danh mục đang hoạt động
                        </div>
                    </div>
                    <div class="kpi-icon emerald">
                        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/></svg>
                    </div>
                </div>

                <!-- Card 3: Orders & Revenue -->
                <div class="kpi-card">
                    <div class="kpi-info">
                        <h4>Tổng Doanh Thu</h4>
                        <div class="kpi-value">${formattedRevenue}</div>
                        <div class="kpi-sub">
                            <strong>${fn:length(orders)} đơn hàng</strong> (${pendingOrdersCount} chờ xử lý)
                        </div>
                    </div>
                    <div class="kpi-icon amber">
                        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><line x1="12" y1="1" x2="12" y2="23"/><path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/></svg>
                    </div>
                </div>

                <!-- Card 4: Users -->
                <div class="kpi-card">
                    <div class="kpi-info">
                        <h4>Tài Khoản Hệ Thống</h4>
                        <div class="kpi-value">${fn:length(users)}</div>
                        <div class="kpi-sub">
                            Quản trị viên & Khách hàng thành viên
                        </div>
                    </div>
                    <div class="kpi-icon purple">
                        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
                    </div>
                </div>
            </div>

            <!-- 3. FLASH MESSAGES -->
            <c:if test="${not empty successMsg}">
                <div class="admin-alert success">
                    <span>✓ ${fn:escapeXml(successMsg)}</span>
                    <button type="button" class="admin-alert-close" onclick="this.parentElement.remove()">✕</button>
                </div>
            </c:if>
            <c:if test="${not empty errorMsg}">
                <div class="admin-alert error">
                    <span>⚠ ${fn:escapeXml(errorMsg)}</span>
                    <button type="button" class="admin-alert-close" onclick="this.parentElement.remove()">✕</button>
                </div>
            </c:if>

            <!-- 4. NAVIGATION TABS -->
            <div class="admin-tabs-bar">
                <div class="admin-tabs-list" role="tablist">
                    <button type="button" class="admin-tab-btn ${activeTab == 'books' ? 'active' : ''}" data-tab="books">
                        <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/><path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/></svg>
                        <span>Quản Lý Sách</span>
                        <span class="tab-count-pill">${allBooksCount}</span>
                    </button>
                    <button type="button" class="admin-tab-btn ${activeTab == 'categories' ? 'active' : ''}" data-tab="categories">
                        <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/></svg>
                        <span>Thể Loại</span>
                        <span class="tab-count-pill">${fn:length(categories)}</span>
                    </button>
                    <button type="button" class="admin-tab-btn ${activeTab == 'orders' ? 'active' : ''}" data-tab="orders">
                        <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><path d="M6 2L3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4z"/><line x1="3" y1="6" x2="21" y2="6"/><path d="M16 10a4 4 0 0 1-8 0"/></svg>
                        <span>Đơn Hàng</span>
                        <span class="tab-count-pill">${fn:length(orders)}</span>
                    </button>
                    <button type="button" class="admin-tab-btn ${activeTab == 'users' ? 'active' : ''}" data-tab="users">
                        <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
                        <span>Người Dùng</span>
                        <span class="tab-count-pill">${fn:length(users)}</span>
                    </button>
                </div>
            </div>

            <!-- ==========================================================
                 PANEL 1: QUẢN LÝ SÁCH (BOOKS)
                 ========================================================== -->
            <section class="admin-panel ${activeTab == 'books' ? 'active' : ''}" id="panel-books">
                <div class="panel-header">
                    <div class="panel-title-group">
                        <h2>Danh Sách Đầu Sách Trong Kho</h2>
                        <p>Hiển thị ${fn:length(books)} trên tổng số ${allBooksCount} đầu sách (đồng bộ bảng Book & BookImages)</p>
                    </div>
                    <button type="button" class="btn-admin btn-admin-primary" id="btnOpenAddBook">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
                        <span>Thêm Sách Mới</span>
                    </button>
                </div>

                <!-- Filter & Search Toolbar -->
                <div class="panel-toolbar">
                    <form action="${pageContext.request.contextPath}/admindashboard" method="GET" class="filter-form">
                        <input type="hidden" name="tab" value="books">
                        <input type="text" name="keyword" class="filter-input"
                               placeholder="Tìm theo tên sách, tác giả, NXB..."
                               value="${fn:escapeXml(keyword)}">

                        <select name="categoryId" class="filter-select">
                            <option value="">-- Tất cả thể loại --</option>
                            <c:forEach items="${categories}" var="cat">
                                <option value="${cat.categoryId}" ${selectedCategoryId == cat.categoryId ? 'selected' : ''}>
                                    ${fn:escapeXml(cat.categoryName)} (${cat.bookCount})
                                </option>
                            </c:forEach>
                        </select>

                        <select name="status" class="filter-select">
                            <option value="">-- Tất cả trạng thái --</option>
                            <option value="Bán" ${selectedStatus == 'Bán' ? 'selected' : ''}>Bán</option>
                            <option value="Cho thuê" ${selectedStatus == 'Cho thuê' ? 'selected' : ''}>Cho thuê</option>
                            <option value="Đã thuê" ${selectedStatus == 'Đã thuê' ? 'selected' : ''}>Đã thuê</option>
                            <option value="Ngừng bán" ${selectedStatus == 'Ngừng bán' ? 'selected' : ''}>Ngừng bán</option>
                        </select>

                        <select name="sort" class="filter-select">
                            <option value="newest" ${selectedSort == 'newest' ? 'selected' : ''}>Mới nhất</option>
                            <option value="oldest" ${selectedSort == 'oldest' ? 'selected' : ''}>Cũ nhất</option>
                            <option value="price-asc" ${selectedSort == 'price-asc' ? 'selected' : ''}>Giá tăng dần</option>
                            <option value="price-desc" ${selectedSort == 'price-desc' ? 'selected' : ''}>Giá giảm dần</option>
                            <option value="title-asc" ${selectedSort == 'title-asc' ? 'selected' : ''}>Tên A - Z</option>
                        </select>

                        <button type="submit" class="btn-admin btn-admin-primary">Lọc Sách</button>
                        <c:if test="${not empty keyword or not empty selectedCategoryId or not empty selectedStatus}">
                            <a href="${pageContext.request.contextPath}/admindashboard?tab=books" class="btn-admin btn-admin-secondary">Xóa lọc</a>
                        </c:if>
                    </form>
                </div>

                <!-- Books Table -->
                <div class="table-responsive">
                    <table class="admin-table">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Thông Tin Sách</th>
                                <th>Thể Loại</th>
                                <th>NXB / Năm</th>
                                <th>ISBN</th>
                                <th>Giá Bán</th>
                                <th>Tồn Kho</th>
                                <th>Trạng Thái</th>
                                <th>Thao Tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${not empty books}">
                                    <c:forEach items="${books}" var="b">
                                        <tr>
                                            <td><strong>#${b.bookId}</strong></td>
                                            <td>
                                                <div class="book-cell-info">
                                                    <img src="${pageContext.request.contextPath}/book-image?bookId=${b.bookId}"
                                                         alt="${fn:escapeXml(b.title)}" class="book-thumb" loading="lazy">
                                                    <div class="book-cell-text">
                                                        <h4>${fn:escapeXml(b.title)}</h4>
                                                        <p>Tác giả: <strong>${fn:escapeXml(b.author)}</strong></p>
                                                    </div>
                                                </div>
                                            </td>
                                            <td>${fn:escapeXml(b.categoryName)}</td>
                                            <td>
                                                <div>${fn:escapeXml(b.publisher)}</div>
                                                <small style="color:#64748b;">Năm: ${b.publicationYear}</small>
                                            </td>
                                            <td><code>${fn:escapeXml(b.isbn)}</code></td>
                                            <td><span class="price-text">${b.formattedPrice}</span></td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${b.stockQuantity <= 0}">
                                                        <span class="stock-pill stock-out">Hết hàng (0)</span>
                                                    </c:when>
                                                    <c:when test="${b.stockQuantity <= 10}">
                                                        <span class="stock-pill stock-low">${b.stockQuantity} cuốn</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="stock-pill stock-ok">${b.stockQuantity} cuốn</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${b.bookStatus == 'Bán'}">
                                                        <span class="status-badge status-sale">● Bán</span>
                                                    </c:when>
                                                    <c:when test="${b.bookStatus == 'Cho thuê'}">
                                                        <span class="status-badge status-rent">● Cho thuê</span>
                                                    </c:when>
                                                    <c:when test="${b.bookStatus == 'Đã thuê'}">
                                                        <span class="status-badge status-rented">● Đã thuê</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="status-badge status-stopped">● ${fn:escapeXml(b.bookStatus)}</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <div class="action-cell">
                                                    <button type="button" class="btn-admin btn-icon-sm btn-edit js-edit-book"
                                                            data-id="${b.bookId}"
                                                            data-title="${fn:escapeXml(b.title)}"
                                                            data-author="${fn:escapeXml(b.author)}"
                                                            data-categoryid="${b.categoryId}"
                                                            data-publisher="${fn:escapeXml(b.publisher)}"
                                                            data-year="${b.publicationYear}"
                                                            data-isbn="${fn:escapeXml(b.isbn)}"
                                                            data-price="${b.price}"
                                                            data-stock="${b.stockQuantity}"
                                                            data-status="${fn:escapeXml(b.bookStatus)}"
                                                            data-desc="${fn:escapeXml(b.description)}"
                                                            data-image="${pageContext.request.contextPath}/book-image?bookId=${b.bookId}">
                                                        Sửa
                                                    </button>
                                                    <button type="button" class="btn-admin btn-icon-sm btn-delete js-delete-item"
                                                            data-action="deleteBook"
                                                            data-idfield="bookId"
                                                            data-id="${b.bookId}"
                                                            data-label="Sách #${b.bookId} - ${fn:escapeXml(b.title)}">
                                                        Xóa
                                                    </button>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <td colspan="9" class="empty-table-box">
                                            Không tìm thấy cuốn sách nào khớp với điều kiện lọc.
                                        </td>
                                    </tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
            </section>

            <!-- ==========================================================
                 PANEL 2: QUẢN LÝ THỂ LOẠI (CATEGORIES)
                 ========================================================== -->
            <section class="admin-panel ${activeTab == 'categories' ? 'active' : ''}" id="panel-categories">
                <div class="panel-header">
                    <div class="panel-title-group">
                        <h2>Quản Lý Thể Loại Sách</h2>
                        <p>Danh sách các thể loại trong bảng Category và số lượng đầu sách tương ứng</p>
                    </div>
                    <button type="button" class="btn-admin btn-admin-primary" id="btnOpenAddCategory">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
                        <span>Thêm Thể Loại</span>
                    </button>
                </div>

                <div class="panel-toolbar">
                    <input type="text" class="filter-input js-table-filter" data-target="categoriesTable"
                           placeholder="Tìm nhanh thể loại...">
                </div>

                <div class="table-responsive">
                    <table class="admin-table" id="categoriesTable">
                        <thead>
                            <tr>
                                <th>Mã Thể Loại</th>
                                <th>Tên Thể Loại</th>
                                <th>Mô Tả Chi Tiết</th>
                                <th>Số Đầu Sách</th>
                                <th>Thao Tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${categories}" var="cat">
                                <tr class="data-row">
                                    <td><strong>#${cat.categoryId}</strong></td>
                                    <td><strong>${fn:escapeXml(cat.categoryName)}</strong></td>
                                    <td>${fn:escapeXml(cat.description)}</td>
                                    <td>
                                        <span class="status-badge status-rent">${cat.bookCount} cuốn sách</span>
                                    </td>
                                    <td>
                                        <div class="action-cell">
                                            <button type="button" class="btn-admin btn-icon-sm btn-edit js-edit-category"
                                                    data-id="${cat.categoryId}"
                                                    data-name="${fn:escapeXml(cat.categoryName)}"
                                                    data-desc="${fn:escapeXml(cat.description)}">
                                                Sửa
                                            </button>
                                            <button type="button" class="btn-admin btn-icon-sm btn-delete js-delete-item"
                                                    data-action="deleteCategory"
                                                    data-idfield="categoryId"
                                                    data-id="${cat.categoryId}"
                                                    data-label="Thể loại #${cat.categoryId} - ${fn:escapeXml(cat.categoryName)}">
                                                Xóa
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </section>

            <!-- ==========================================================
                 PANEL 3: QUẢN LÝ ĐƠN HÀNG (ORDERS)
                 ========================================================== -->
            <section class="admin-panel ${activeTab == 'orders' ? 'active' : ''}" id="panel-orders">
                <div class="panel-header">
                    <div class="panel-title-group">
                        <h2>Quản Lý Đơn Đặt Hàng</h2>
                        <p>Theo dõi và cập nhật trạng thái đơn hàng, trạng thái thanh toán từ bảng Order</p>
                    </div>
                </div>

                <div class="panel-toolbar">
                    <input type="text" class="filter-input js-table-filter" data-target="ordersTable"
                           placeholder="Tìm đơn hàng theo mã, tên khách, SĐT, địa chỉ...">
                </div>

                <div class="table-responsive">
                    <table class="admin-table" id="ordersTable">
                        <thead>
                            <tr>
                                <th>Mã Đơn</th>
                                <th>Khách Hàng</th>
                                <th>SĐT Nhận</th>
                                <th>Địa Chỉ Giao Hàng</th>
                                <th>Tổng Tiền</th>
                                <th>Thanh Toán</th>
                                <th>Trạng Thái Đơn</th>
                                <th>Ngày Đặt</th>
                                <th>Thao Tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${not empty orders}">
                                    <c:forEach items="${orders}" var="o">
                                        <tr class="data-row">
                                            <td><strong>#${o.orderId}</strong></td>
                                            <td>
                                                <strong>${not empty o.customerName ? fn:escapeXml(o.customerName) : 'User #' += o.userId}</strong>
                                                <div style="font-size:12px; color:#64748b;">ID: #${o.userId}</div>
                                            </td>
                                            <td>${fn:escapeXml(o.recipientPhone)}</td>
                                            <td>${fn:escapeXml(o.shippingAddress)}</td>
                                            <td><span class="price-text">${o.formattedTotalAmount}</span></td>
                                            <td>
                                                <div><strong>${fn:escapeXml(o.paymentMethod)}</strong></div>
                                                <span class="status-badge ${o.paymentStatus == 'Paid' ? 'status-paid' : 'status-unpaid'}">
                                                    ${fn:escapeXml(o.paymentStatus)}
                                                </span>
                                            </td>
                                            <td>
                                                <span class="status-badge ${o.orderStatus == 'Completed' ? 'status-completed' : (o.orderStatus == 'Cancelled' ? 'status-cancelled' : (o.orderStatus == 'Pending' ? 'status-pending' : 'status-processing'))}">
                                                    ${fn:escapeXml(o.orderStatus)}
                                                </span>
                                            </td>
                                            <td>${o.createdAt}</td>
                                            <td>
                                                <div class="action-cell">
                                                    <button type="button" class="btn-admin btn-icon-sm btn-edit js-edit-order"
                                                            data-id="${o.orderId}"
                                                            data-orderstatus="${fn:escapeXml(o.orderStatus)}"
                                                            data-paymentstatus="${fn:escapeXml(o.paymentStatus)}"
                                                            data-phone="${fn:escapeXml(o.recipientPhone)}"
                                                            data-address="${fn:escapeXml(o.shippingAddress)}">
                                                        Cập nhật
                                                    </button>
                                                    <button type="button" class="btn-admin btn-icon-sm btn-delete js-delete-item"
                                                            data-action="deleteOrder"
                                                            data-idfield="orderId"
                                                            data-id="${o.orderId}"
                                                            data-label="Đơn hàng #${o.orderId}">
                                                        Xóa
                                                    </button>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <td colspan="9" class="empty-table-box">
                                            Chưa có đơn đặt hàng nào trong cơ sở dữ liệu.
                                        </td>
                                    </tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
            </section>

            <!-- ==========================================================
                 PANEL 4: QUẢN LÝ NGƯỜI DÙNG (USERS)
                 ========================================================== -->
            <section class="admin-panel ${activeTab == 'users' ? 'active' : ''}" id="panel-users">
                <div class="panel-header">
                    <div class="panel-title-group">
                        <h2>Quản Lý Tài Khoản Người Dùng</h2>
                        <p>Danh sách tài khoản từ bảng User và phân quyền Role (Admin / Customer)</p>
                    </div>
                    <button type="button" class="btn-admin btn-admin-primary" id="btnOpenAddUser">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
                        <span>Thêm Tài Khoản</span>
                    </button>
                </div>

                <div class="panel-toolbar">
                    <input type="text" class="filter-input js-table-filter" data-target="usersTable"
                           placeholder="Tìm theo họ tên, email, số điện thoại, địa chỉ...">
                </div>

                <div class="table-responsive">
                    <table class="admin-table" id="usersTable">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Họ Và Tên</th>
                                <th>Email</th>
                                <th>Số Điện Thoại</th>
                                <th>Địa Chỉ</th>
                                <th>Vai Trò</th>
                                <th>Ngày Tạo</th>
                                <th>Thao Tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${users}" var="u">
                                <tr class="data-row">
                                    <td><strong>#${u.userId}</strong></td>
                                    <td><strong>${fn:escapeXml(u.fullName)}</strong></td>
                                    <td>${fn:escapeXml(u.email)}</td>
                                    <td>${not empty u.phoneNumber ? fn:escapeXml(u.phoneNumber) : '—'}</td>
                                    <td>${not empty u.address ? fn:escapeXml(u.address) : '—'}</td>
                                    <td>
                                        <span class="status-badge ${u.roleId == 2 ? 'role-badge-admin' : 'role-badge-customer'}">
                                            ${u.roleId == 2 ? '★ Admin' : 'Customer'}
                                        </span>
                                    </td>
                                    <td>${u.createdAt}</td>
                                    <td>
                                        <div class="action-cell">
                                            <c:choose>
                                                <c:when test="${u.roleId == 2}">
                                                    <button type="button" class="btn-admin btn-icon-sm btn-edit js-edit-user"
                                                            data-id="${u.userId}"
                                                            data-fullname="${fn:escapeXml(u.fullName)}"
                                                            data-email="${fn:escapeXml(u.email)}"
                                                            data-phone="${fn:escapeXml(u.phoneNumber)}"
                                                            data-address="${fn:escapeXml(u.address)}"
                                                            data-roleid="${u.roleId}">
                                                        Sửa
                                                    </button>
                                                </c:when>
                                                <c:otherwise>
                                                    <span style="font-size:12px; color:#64748b; padding:4px 8px; background:#f1f5f9; border-radius:6px;" title="Admin không được phép sửa thông tin khách hàng">
                                                        Chỉ xem
                                                    </span>
                                                </c:otherwise>
                                            </c:choose>
                                            <c:if test="${empty sessionScope.user or sessionScope.user.userId != u.userId}">
                                                <button type="button" class="btn-admin btn-icon-sm btn-delete js-delete-item"
                                                        data-action="deleteUser"
                                                        data-idfield="userId"
                                                        data-id="${u.userId}"
                                                        data-label="Tài khoản #${u.userId} - ${fn:escapeXml(u.fullName)}">
                                                    Xóa
                                                </button>
                                            </c:if>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </section>

        </main>

        <!-- ==========================================================
             MODAL 1: THÊM / SỬA SÁCH (BOOK MODAL)
             ========================================================== -->
        <div class="adm-modal-overlay" id="bookModal" data-default-cover="${pageContext.request.contextPath}/book-image?bookId=1">
            <div class="adm-modal-card">
                <div class="adm-modal-header">
                    <h3 id="bookModalTitle">Thêm Sách Mới</h3>
                    <button type="button" class="adm-modal-close" data-close-modal>✕</button>
                </div>
                <form action="${pageContext.request.contextPath}/admindashboard" method="POST" enctype="multipart/form-data">
                    <input type="hidden" name="action" id="bookFormAction" value="addBook">
                    <input type="hidden" name="bookId" id="bookFormId" value="">
                    <div class="adm-modal-body">
                        <div class="adm-form-grid">
                            <div class="adm-form-group full-width">
                                <label for="bookTitle">Tên cuốn sách <span class="req">*</span></label>
                                <input type="text" name="title" id="bookTitle" class="adm-input" placeholder="Nhập tên sách..." required>
                            </div>

                            <div class="adm-form-group">
                                <label for="bookAuthor">Tác giả <span class="req">*</span></label>
                                <input type="text" name="author" id="bookAuthor" class="adm-input" placeholder="Nhập tên tác giả..." required>
                            </div>

                            <div class="adm-form-group">
                                <label for="bookCategoryId">Thể loại <span class="req">*</span></label>
                                <select name="categoryId" id="bookCategoryId" class="adm-select" required>
                                    <c:forEach items="${categories}" var="cat">
                                        <option value="${cat.categoryId}">${fn:escapeXml(cat.categoryName)}</option>
                                    </c:forEach>
                                </select>
                            </div>

                            <div class="adm-form-group">
                                <label for="bookPublisher">Nhà xuất bản</label>
                                <input type="text" name="publisher" id="bookPublisher" class="adm-input" placeholder="VD: NXB Trẻ">
                            </div>

                            <div class="adm-form-group">
                                <label for="bookYear">Năm xuất bản</label>
                                <input type="number" name="publicationYear" id="bookYear" class="adm-input" value="2026" min="1000" max="2100">
                            </div>

                            <div class="adm-form-group">
                                <label for="bookIsbn">Mã ISBN (Duy nhất)</label>
                                <input type="text" name="isbn" id="bookIsbn" class="adm-input" placeholder="VD: 9786041182011">
                            </div>

                            <div class="adm-form-group">
                                <label for="bookStatus">Trạng thái sách (bookStatus) <span class="req">*</span></label>
                                <select name="bookStatus" id="bookStatus" class="adm-select" required>
                                    <option value="Bán">Bán</option>
                                    <option value="Cho thuê">Cho thuê</option>
                                    <option value="Đã thuê">Đã thuê</option>
                                    <option value="Ngừng bán">Ngừng bán</option>
                                </select>
                            </div>

                            <div class="adm-form-group">
                                <label for="bookPrice">Giá bán (VNĐ) <span class="req">*</span></label>
                                <input type="number" name="price" id="bookPrice" class="adm-input" step="1000" min="0" placeholder="VD: 150000" required>
                            </div>

                            <div class="adm-form-group">
                                <label for="bookStock">Số lượng tồn kho <span class="req">*</span></label>
                                <input type="number" name="stockQuantity" id="bookStock" class="adm-input" min="0" value="20" required>
                            </div>

                            <div class="adm-form-group full-width">
                                <label for="bookCoverFile">Ảnh bìa sách (Tùy chọn tải ảnh mới)</label>
                                <div class="cover-upload-box">
                                    <img src="${pageContext.request.contextPath}/book-image?bookId=1" alt="Preview" id="bookCoverPreview" class="cover-preview-img">
                                    <div style="flex:1;">
                                        <input type="file" name="coverFile" id="bookCoverFile" accept="image/*" class="adm-input" style="width:100%;">
                                        <small style="color:#64748b; display:block; margin-top:4px;">Hỗ trợ JPG, PNG, WEBP. Lưu trực tiếp vào bảng BookImages.</small>
                                    </div>
                                </div>
                            </div>

                            <div class="adm-form-group full-width">
                                <label for="bookDescription">Mô tả nội dung sách</label>
                                <textarea name="description" id="bookDescription" class="adm-textarea" placeholder="Nhập mô tả tóm tắt nội dung cuốn sách..."></textarea>
                            </div>
                        </div>
                    </div>
                    <div class="adm-modal-footer">
                        <button type="button" class="btn-admin btn-admin-secondary" data-close-modal>Hủy</button>
                        <button type="submit" class="btn-admin btn-admin-primary">Lưu Thông Tin Sách</button>
                    </div>
                </form>
            </div>
        </div>

        <!-- ==========================================================
             MODAL 2: THÊM / SỬA THỂ LOẠI (CATEGORY MODAL)
             ========================================================== -->
        <div class="adm-modal-overlay" id="categoryModal">
            <div class="adm-modal-card modal-sm">
                <div class="adm-modal-header">
                    <h3 id="categoryModalTitle">Thêm Thể Loại Mới</h3>
                    <button type="button" class="adm-modal-close" data-close-modal>✕</button>
                </div>
                <form action="${pageContext.request.contextPath}/admindashboard" method="POST">
                    <input type="hidden" name="action" id="categoryFormAction" value="addCategory">
                    <input type="hidden" name="categoryId" id="categoryFormId" value="">
                    <div class="adm-modal-body">
                        <div class="adm-form-grid">
                            <div class="adm-form-group full-width">
                                <label for="categoryNameInput">Tên thể loại <span class="req">*</span></label>
                                <input type="text" name="categoryName" id="categoryNameInput" class="adm-input" placeholder="Nhập tên thể loại..." required>
                            </div>
                            <div class="adm-form-group full-width">
                                <label for="categoryDescInput">Mô tả thể loại</label>
                                <textarea name="description" id="categoryDescInput" class="adm-textarea" placeholder="Nhập mô tả cho thể loại sách..."></textarea>
                            </div>
                        </div>
                    </div>
                    <div class="adm-modal-footer">
                        <button type="button" class="btn-admin btn-admin-secondary" data-close-modal>Hủy</button>
                        <button type="submit" class="btn-admin btn-admin-primary">Lưu Thể Loại</button>
                    </div>
                </form>
            </div>
        </div>

        <!-- ==========================================================
             MODAL 3: CẬP NHẬT ĐƠN HÀNG (ORDER MODAL)
             ========================================================== -->
        <div class="adm-modal-overlay" id="orderModal">
            <div class="adm-modal-card modal-sm">
                <div class="adm-modal-header">
                    <h3 id="orderModalTitle">Cập Nhật Đơn Hàng</h3>
                    <button type="button" class="adm-modal-close" data-close-modal>✕</button>
                </div>
                <form action="${pageContext.request.contextPath}/admindashboard" method="POST">
                    <input type="hidden" name="action" value="updateOrder">
                    <input type="hidden" name="orderId" id="orderFormId" value="">
                    <div class="adm-modal-body">
                        <div class="adm-form-grid">
                            <div class="adm-form-group">
                                <label for="orderStatusInput">Trạng thái đơn hàng</label>
                                <select name="orderStatus" id="orderStatusInput" class="adm-select">
                                    <option value="Pending">Pending (Chờ xử lý)</option>
                                    <option value="Processing">Processing (Đang chuẩn bị)</option>
                                    <option value="Shipped">Shipped (Đang giao)</option>
                                    <option value="Completed">Completed (Hoàn thành)</option>
                                    <option value="Cancelled">Cancelled (Đã hủy)</option>
                                </select>
                            </div>
                            <div class="adm-form-group">
                                <label for="paymentStatusInput">Trạng thái thanh toán</label>
                                <select name="paymentStatus" id="paymentStatusInput" class="adm-select">
                                    <option value="Unpaid">Unpaid (Chưa thanh toán)</option>
                                    <option value="Paid">Paid (Đã thanh toán)</option>
                                    <option value="Refunded">Refunded (Đã hoàn tiền)</option>
                                </select>
                            </div>
                            <div class="adm-form-group full-width">
                                <label for="orderPhoneInput">Số điện thoại nhận hàng</label>
                                <input type="text" name="recipientPhone" id="orderPhoneInput" class="adm-input" required>
                            </div>
                            <div class="adm-form-group full-width">
                                <label for="orderAddressInput">Địa chỉ giao hàng</label>
                                <input type="text" name="shippingAddress" id="orderAddressInput" class="adm-input" required>
                            </div>
                        </div>
                    </div>
                    <div class="adm-modal-footer">
                        <button type="button" class="btn-admin btn-admin-secondary" data-close-modal>Hủy</button>
                        <button type="submit" class="btn-admin btn-admin-primary">Lưu Đơn Hàng</button>
                    </div>
                </form>
            </div>
        </div>

        <!-- ==========================================================
             MODAL 4: THÊM / SỬA NGƯỜI DÙNG (USER MODAL)
             ========================================================== -->
        <div class="adm-modal-overlay" id="userModal">
            <div class="adm-modal-card">
                <div class="adm-modal-header">
                    <h3 id="userModalTitle">Thêm Người Dùng Mới</h3>
                    <button type="button" class="adm-modal-close" data-close-modal>✕</button>
                </div>
                <form action="${pageContext.request.contextPath}/admindashboard" method="POST">
                    <input type="hidden" name="action" id="userFormAction" value="addUser">
                    <input type="hidden" name="userId" id="userFormId" value="">
                    <div class="adm-modal-body">
                        <div class="adm-form-grid">
                            <div class="adm-form-group">
                                <label for="userFullNameInput">Họ và tên <span class="req">*</span></label>
                                <input type="text" name="fullName" id="userFullNameInput" class="adm-input" placeholder="Nhập họ và tên..." required>
                            </div>
                            <div class="adm-form-group">
                                <label for="userRoleInput">Vai trò (Role) <span class="req">*</span></label>
                                <select name="roleId" id="userRoleInput" class="adm-select">
                                    <option value="1">Customer (Khách hàng)</option>
                                    <option value="2">Admin (Quản trị viên)</option>
                                </select>
                            </div>
                            <div class="adm-form-group">
                                <label for="userEmailInput">Địa chỉ Email <span class="req">*</span></label>
                                <input type="email" name="email" id="userEmailInput" class="adm-input" placeholder="example@gmail.com" required>
                            </div>
                            <div class="adm-form-group">
                                <label for="userPhoneInput">Số điện thoại</label>
                                <input type="text" name="phoneNumber" id="userPhoneInput" class="adm-input" placeholder="09xxxxxxxx">
                            </div>
                            <div class="adm-form-group full-width">
                                <label for="userPasswordInput">Mật khẩu <small id="userPwdHint" style="color:#64748b; font-weight:400;">(Bắt buộc)</small></label>
                                <input type="password" name="password" id="userPasswordInput" class="adm-input" placeholder="Nhập mật khẩu...">
                            </div>
                            <div class="adm-form-group full-width">
                                <label for="userAddressInput">Địa chỉ</label>
                                <input type="text" name="address" id="userAddressInput" class="adm-input" placeholder="Nhập địa chỉ liên hệ...">
                            </div>
                        </div>
                    </div>
                    <div class="adm-modal-footer">
                        <button type="button" class="btn-admin btn-admin-secondary" data-close-modal>Hủy</button>
                        <button type="submit" class="btn-admin btn-admin-primary">Lưu Tài Khoản</button>
                    </div>
                </form>
            </div>
        </div>

        <!-- ==========================================================
             MODAL 5: XÁC NHẬN XÓA (DELETE CONFIRM MODAL)
             ========================================================== -->
        <div class="adm-modal-overlay" id="deleteConfirmModal">
            <div class="adm-modal-card modal-sm">
                <div class="adm-modal-header">
                    <h3>Xác Nhận Xóa Dữ Liệu</h3>
                    <button type="button" class="adm-modal-close" data-close-modal>✕</button>
                </div>
                <form action="${pageContext.request.contextPath}/admindashboard" method="POST">
                    <input type="hidden" name="action" id="deleteActionInput" value="">
                    <input type="hidden" name="id" id="deleteIdInput" value="">
                    <div class="adm-modal-body">
                        <p style="font-size:14.5px; color:#334155; margin-bottom:8px;">
                            Bạn có chắc chắn muốn xóa mục sau khỏi cơ sở dữ liệu?
                        </p>
                        <div style="padding:12px 14px; background:#fef2f2; border:1px solid #fecaca; border-radius:8px; color:#991b1b; font-weight:700;" id="deleteConfirmText">
                            ...
                        </div>
                    </div>
                    <div class="adm-modal-footer">
                        <button type="button" class="btn-admin btn-admin-secondary" data-close-modal>Hủy Bỏ</button>
                        <button type="submit" class="btn-admin btn-admin-danger">Xác Nhận Xóa</button>
                    </div>
                </form>
            </div>
        </div>

        <!-- FOOTER COMPONENT -->
        <jsp:include page="/components/footer/footer.jsp" />

        <script src="${pageContext.request.contextPath}/components/header/header.js"></script>
        <script src="${pageContext.request.contextPath}/js/admindashboard.js"></script>
        <script src="${pageContext.request.contextPath}/components/footer/footer.js"></script>
    </body>
</html>

