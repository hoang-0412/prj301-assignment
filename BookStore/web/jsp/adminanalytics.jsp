<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Thống Kê & Báo Cáo Doanh Thu - Tiệm sách Hoàng Trịnh</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/components/header/header.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/components/footer/footer.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admindashboard.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/adminanalytics.css">
    </head>
    <body>

        <!-- HEADER -->
        <jsp:include page="/components/header/header.jsp" />

        <!-- HERO BANNER -->
        <section class="admin-hero">
            <div class="admin-container">
                <div class="admin-hero-inner">
                    <div class="admin-hero-left">
                        <span class="admin-badge-top">
                            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.3"><line x1="18" y1="20" x2="18" y2="10"/><line x1="12" y1="20" x2="12" y2="4"/><line x1="6" y1="20" x2="6" y2="14"/></svg>
                            <span>BÁO CÁO PHÂN TÍCH & DOANH THU HỆ THỐNG</span>
                        </span>
                        <h1 class="admin-hero-title">Thống Kê Kinh Doanh & Kho Sách</h1>
                        <p class="admin-hero-sub">
                            Tổng hợp dữ liệu thời gian thực từ bảng Order, OrderItem, Book, Category và User.
                        </p>
                    </div>
                    <div class="admin-hero-actions">
                        <a href="${pageContext.request.contextPath}/admindashboard" class="btn-hero-action btn-hero-outline">
                            ← Quay Lại Bảng Quản Trị
                        </a>
                        <button type="button" class="btn-hero-action btn-hero-primary" onclick="window.print()">
                            🖨 Xuất Báo Cáo
                        </button>
                    </div>
                </div>
            </div>
        </section>

        <main class="admin-container ana-main-wrap">

            <!-- 1. KPI OVERVIEW CARDS -->
            <div class="kpi-grid">
                <div class="kpi-card">
                    <div class="kpi-info">
                        <h4>Tổng Doanh Thu Đơn</h4>
                        <div class="kpi-value">${formattedTotalRevenue}</div>
                        <div class="kpi-sub">
                            Đã thu: <strong>${formattedPaidRevenue}</strong> (${validOrdersCount} đơn hợp lệ)
                        </div>
                    </div>
                    <div class="kpi-icon amber">
                        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><line x1="12" y1="1" x2="12" y2="23"/><path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/></svg>
                    </div>
                </div>

                <div class="kpi-card">
                    <div class="kpi-info">
                        <h4>Giá Trị Đơn Trung Bình</h4>
                        <div class="kpi-value">${formattedAvgOrderValue}</div>
                        <div class="kpi-sub">
                            Tổng số sách bán ra: <strong>${totalItemsSold} cuốn</strong>
                        </div>
                    </div>
                    <div class="kpi-icon indigo">
                        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/></svg>
                    </div>
                </div>

                <div class="kpi-card">
                    <div class="kpi-info">
                        <h4>Tổng Giá Trị Kho Sách</h4>
                        <div class="kpi-value">${formattedInventoryValue}</div>
                        <div class="kpi-sub">
                            <strong>${totalStockUnits} cuốn</strong> thuộc ${totalBooksCount} đầu sách
                        </div>
                    </div>
                    <div class="kpi-icon emerald">
                        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/><path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/></svg>
                    </div>
                </div>

                <div class="kpi-card">
                    <div class="kpi-info">
                        <h4>Cảnh Báo Sắp Hết Kho</h4>
                        <div class="kpi-value">${fn:length(lowStockBooks)} đầu sách</div>
                        <div class="kpi-sub">
                            Số lượng tồn kho ≤ 15 cuốn cần nhập thêm
                        </div>
                    </div>
                    <div class="kpi-icon purple">
                        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>
                    </div>
                </div>
            </div>

            <!-- 2. CATEGORY PERFORMANCE & ORDER STATUS BREAKDOWN -->
            <div class="ana-two-col">
                <!-- Category Breakdown -->
                <section class="ana-card">
                    <div class="ana-card-head">
                        <h3>Phân Bổ Kho & Doanh Thu Theo Thể Loại</h3>
                        <p>Thống kê số đầu sách, tồn kho, giá trị kho và doanh thu theo từng Category</p>
                    </div>
                    <div class="ana-cat-list">
                        <c:forEach items="${categoryStats}" var="cs">
                            <c:set var="cat" value="${cs.category}" />
                            <div class="ana-cat-item">
                                <div class="ana-cat-top">
                                    <div>
                                        <strong>${fn:escapeXml(cat.categoryName)}</strong>
                                        <span class="ana-cat-sub">(${cat.bookCount} đầu sách • Tồn: ${cs.totalStock} cuốn)</span>
                                    </div>
                                    <div class="ana-cat-vals">
                                        <span>Giá trị kho: <strong>${cs.formattedInvValue}</strong></span>
                                        <span class="sep">|</span>
                                        <span class="rev">Doanh thu: <strong>${cs.formattedRevenue}</strong> (${cs.soldQty} đã bán)</span>
                                    </div>
                                </div>
                                <div class="ana-progress-track">
                                    <div class="ana-progress-fill" style="width: ${cs.stockPercent > 5 ? cs.stockPercent : 5}%;"></div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </section>

                <!-- Order Status & Payment Method Breakdown -->
                <section class="ana-card">
                    <div class="ana-card-head">
                        <h3>Tỷ Lệ Trạng Thái Đơn Hàng</h3>
                        <p>Tổng cộng ${totalOrdersCount} đơn hàng trong hệ thống</p>
                    </div>

                    <div class="ana-status-list">
                        <c:forEach items="${statusCounts}" var="entry">
                            <div class="ana-status-row">
                                <span class="ana-status-name">${fn:escapeXml(entry.key)}</span>
                                <strong class="ana-status-val">${entry.value} đơn</strong>
                            </div>
                        </c:forEach>
                    </div>

                    <div class="ana-card-head" style="margin-top:24px;">
                        <h3>Phương Thức Thanh Toán</h3>
                        <p>Phân bổ theo hình thức thanh toán của khách hàng</p>
                    </div>
                    <div class="ana-status-list">
                        <c:forEach items="${paymentMethodCounts}" var="pmEntry">
                            <div class="ana-status-row">
                                <span class="ana-status-name">${fn:escapeXml(pmEntry.key)}</span>
                                <strong class="ana-status-val">${pmEntry.value} đơn</strong>
                            </div>
                        </c:forEach>
                        <c:if test="${empty paymentMethodCounts}">
                            <p style="font-size:13px; color:#64748b;">Chưa có dữ liệu thanh toán.</p>
                        </c:if>
                    </div>
                </section>
            </div>

            <!-- 3. TOP SELLING BOOKS & LOW STOCK ALERTS -->
            <div class="ana-two-col">
                <!-- Top Selling Books -->
                <section class="ana-card">
                    <div class="ana-card-head">
                        <h3>Top Đầu Sách Tiêu Biểu & Bán Chạy</h3>
                        <p>Xếp hạng theo số lượng bán ra từ bảng OrderItem và doanh thu mang lại</p>
                    </div>
                    <div class="table-responsive">
                        <table class="admin-table">
                            <thead>
                                <tr>
                                    <th>Hạng</th>
                                    <th>Đầu Sách</th>
                                    <th>Giá Bán</th>
                                    <th>Đã Bán</th>
                                    <th>Doanh Thu</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${topSellingBooks}" var="row" varStatus="st">
                                    <c:set var="b" value="${row.book}" />
                                    <tr>
                                        <td><span class="ana-rank-badge">#${st.index + 1}</span></td>
                                        <td>
                                            <div class="book-cell-info" style="min-width:200px;">
                                                <img src="${pageContext.request.contextPath}/book-image?bookId=${b.bookId}"
                                                     alt="${fn:escapeXml(b.title)}" class="book-thumb">
                                                <div class="book-cell-text">
                                                    <h4>
                                                        <a href="${pageContext.request.contextPath}/book-detail?id=${b.bookId}" style="color:inherit; text-decoration:none;">
                                                            ${fn:escapeXml(b.title)}
                                                        </a>
                                                    </h4>
                                                    <p>${fn:escapeXml(b.author)}</p>
                                                </div>
                                            </div>
                                        </td>
                                        <td>${b.formattedPrice}</td>
                                        <td><strong>${row.soldQty} cuốn</strong></td>
                                        <td><span class="price-text">${row.formattedRevenue}</span></td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </section>

                <!-- Low Stock Books & VIP Customers -->
                <div style="display:flex; flex-direction:column; gap:24px;">
                    <section class="ana-card">
                        <div class="ana-card-head">
                            <h3>Cảnh Báo Sách Sắp Hết Hàng (Tồn ≤ 15)</h3>
                            <p>Các đầu sách cần ưu tiên nhập bổ sung kho</p>
                        </div>
                        <div class="table-responsive">
                            <table class="admin-table">
                                <thead>
                                    <tr>
                                        <th>Mã</th>
                                        <th>Tên Sách</th>
                                        <th>Tồn Kho</th>
                                        <th>Trạng Thái</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:choose>
                                        <c:when test="${not empty lowStockBooks}">
                                            <c:forEach items="${lowStockBooks}" var="lb">
                                                <tr>
                                                    <td><strong>#${lb.bookId}</strong></td>
                                                    <td><strong>${fn:escapeXml(lb.title)}</strong></td>
                                                    <td>
                                                        <span class="stock-pill ${lb.stockQuantity <= 0 ? 'stock-out' : 'stock-low'}">
                                                            ${lb.stockQuantity} cuốn
                                                        </span>
                                                    </td>
                                                    <td>${fn:escapeXml(lb.bookStatus)}</td>
                                                </tr>
                                            </c:forEach>
                                        </c:when>
                                        <c:otherwise>
                                            <tr>
                                                <td colspan="4" style="text-align:center; color:#64748b;">Tất cả đầu sách đều có tồn kho dồi dào (&gt; 15 cuốn).</td>
                                            </tr>
                                        </c:otherwise>
                                    </c:choose>
                                </tbody>
                            </table>
                        </div>
                    </section>

                    <section class="ana-card">
                        <div class="ana-card-head">
                            <h3>Xếp Hạng Khách Hàng Chi Tiêu Cao Nhất</h3>
                            <p>Thống kê theo tổng số đơn hàng hợp lệ và tổng chi tiêu</p>
                        </div>
                        <div class="table-responsive">
                            <table class="admin-table">
                                <thead>
                                    <tr>
                                        <th>Khách Hàng</th>
                                        <th>Email / SĐT</th>
                                        <th>Số Đơn</th>
                                        <th>Tổng Chi Tiêu</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${topCustomers}" var="tc">
                                        <c:set var="u" value="${tc.user}" />
                                        <tr>
                                            <td><strong>${fn:escapeXml(u.fullName)}</strong></td>
                                            <td>
                                                <div>${fn:escapeXml(u.email)}</div>
                                                <small style="color:#64748b;">${fn:escapeXml(u.phoneNumber)}</small>
                                            </td>
                                            <td><strong>${tc.orderCount} đơn</strong></td>
                                            <td><span class="price-text">${tc.formattedSpent}</span></td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </section>
                </div>
            </div>

        </main>

        <!-- FOOTER -->
        <jsp:include page="/components/footer/footer.jsp" />

        <script src="${pageContext.request.contextPath}/components/header/header.js"></script>
        <script src="${pageContext.request.contextPath}/components/footer/footer.js"></script>
    </body>
</html>

