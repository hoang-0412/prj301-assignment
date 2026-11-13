<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Tài Khoản & Đơn Mua - Tiệm sách Hoàng Trịnh</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/homepage.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/components/header/header.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/components/footer/footer.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/profile.css">
    </head>
    <body>

        <!-- HEADER -->
        <jsp:include page="/components/header/header.jsp" />

        <!-- TOP PROFILE BANNER -->
        <section class="prf-hero">
            <div class="container prf-hero-inner">
                <div class="prf-user-head">
                    <div class="prf-avatar">
                        ${fn:substring(userProfile.fullName, 0, 1)}
                    </div>
                    <div>
                        <span class="prf-role-tag">
                            ${userProfile.roleId == 2 ? '★ Quản Trị Viên' : 'Khách Hàng Thành Viên'}
                        </span>
                        <h1>${fn:escapeXml(userProfile.fullName)}</h1>
                        <p>${fn:escapeXml(userProfile.email)} • Thành viên từ ${userProfile.createdAt}</p>
                    </div>
                </div>
                <div class="prf-hero-stats">
                    <div class="prf-stat-box">
                        <span>Tổng đơn mua</span>
                        <strong>${totalOrdersCount}</strong>
                    </div>
                    <div class="prf-stat-box">
                        <span>Chờ xử lý</span>
                        <strong>${pendingOrdersCount}</strong>
                    </div>
                    <div class="prf-stat-box">
                        <span>Hoàn thành</span>
                        <strong>${completedOrdersCount}</strong>
                    </div>
                    <div class="prf-stat-box highlight">
                        <span>Tổng chi tiêu</span>
                        <strong>${formattedTotalSpent}</strong>
                    </div>
                </div>
            </div>
        </section>

        <main class="container prf-main-wrap">

            <!-- FLASH ALERTS -->
            <c:if test="${not empty successMsg}">
                <div class="prf-alert success">
                    <span>✓ ${fn:escapeXml(successMsg)}</span>
                    <button type="button" class="js-prf-alert-close">✕</button>
                </div>
            </c:if>
            <c:if test="${not empty errorMsg}">
                <div class="prf-alert error">
                    <span>⚠ ${fn:escapeXml(errorMsg)}</span>
                    <button type="button" class="js-prf-alert-close">✕</button>
                </div>
            </c:if>

            <div class="prf-grid">
                <!-- LEFT SIDEBAR MENU -->
                <aside class="prf-sidebar">
                    <nav class="prf-nav">
                        <button type="button" class="prf-nav-btn ${activeTab == 'profile' ? 'active' : ''}" data-tab="profile">
                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
                            <span>Thông Tin Hồ Sơ</span>
                        </button>
                        <button type="button" class="prf-nav-btn ${activeTab == 'orders' ? 'active' : ''}" data-tab="orders">
                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><path d="M6 2L3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4z"/><line x1="3" y1="6" x2="21" y2="6"/><path d="M16 10a4 4 0 0 1-8 0"/></svg>
                            <span>Đơn Mua Của Tôi</span>
                            <span class="prf-count">${totalOrdersCount}</span>
                        </button>
                        <button type="button" class="prf-nav-btn ${activeTab == 'password' ? 'active' : ''}" data-tab="password">
                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
                            <span>Đổi Mật Khẩu</span>
                        </button>
                        <c:if test="${userProfile.roleId == 2}">
                            <a href="${pageContext.request.contextPath}/admindashboard" class="prf-nav-btn link">
                                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>
                                <span>Bảng Điều Khiển Admin</span>
                            </a>
                        </c:if>
                        <a href="${pageContext.request.contextPath}/logout" class="prf-nav-btn logout">
                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>
                            <span>Đăng Xuất</span>
                        </a>
                    </nav>
                </aside>

                <!-- RIGHT CONTENT PANELS -->
                <div class="prf-content">

                    <!-- PANEL 1: THÔNG TIN HỒ SƠ -->
                    <section class="prf-panel ${activeTab == 'profile' ? 'active' : ''}" id="prf-panel-profile">
                        <div class="prf-panel-head">
                            <h2>Hồ Sơ Cá Nhân</h2>
                            <p>Quản lý thông tin liên hệ và địa chỉ mặc định khi nhận sách</p>
                        </div>

                        <form action="${pageContext.request.contextPath}/profile" method="POST" class="prf-form">
                            <input type="hidden" name="action" value="updateProfile">

                            <div class="prf-form-grid">
                                <div class="prf-field">
                                    <label for="prfFullName">Họ và tên <span class="req">*</span></label>
                                    <input type="text" name="fullName" id="prfFullName" class="prf-input"
                                           value="${fn:escapeXml(userProfile.fullName)}" required>
                                </div>

                                <div class="prf-field">
                                    <label for="prfEmail">Địa chỉ Email <span class="req">*</span></label>
                                    <input type="email" name="email" id="prfEmail" class="prf-input"
                                           value="${fn:escapeXml(userProfile.email)}" required>
                                </div>

                                <div class="prf-field">
                                    <label for="prfPhone">Số điện thoại liên hệ</label>
                                    <input type="text" name="phoneNumber" id="prfPhone" class="prf-input"
                                           placeholder="09xxxxxxxx"
                                           value="${fn:escapeXml(userProfile.phoneNumber)}">
                                </div>

                                <div class="prf-field">
                                    <label>Vai trò tài khoản</label>
                                    <input type="text" class="prf-input readonly"
                                           value="${userProfile.roleId == 2 ? 'Quản trị viên (Admin)' : 'Khách hàng (Customer)'}" readonly>
                                </div>

                                <div class="prf-field full">
                                    <label for="prfAddress">Địa chỉ giao hàng mặc định</label>
                                    <textarea name="address" id="prfAddress" class="prf-textarea"
                                              placeholder="Nhập địa chỉ nhận hàng của bạn...">${fn:escapeXml(userProfile.address)}</textarea>
                                </div>
                            </div>

                            <div class="prf-form-actions">
                                <button type="submit" class="prf-btn-primary">Lưu Thay Đổi Hồ Sơ</button>
                            </div>
                        </form>
                    </section>

                    <!-- PANEL 2: ĐƠN MUA CỦA TÔI -->
                    <section class="prf-panel ${activeTab == 'orders' ? 'active' : ''}" id="prf-panel-orders">
                        <div class="prf-panel-head">
                            <h2>Lịch Sử Đơn Mua Hàng</h2>
                            <p>Theo dõi tiến độ giao hàng và chi tiết các cuốn sách bạn đã đặt</p>
                        </div>

                        <c:choose>
                            <c:when test="${not empty myOrders}">
                                <div class="prf-orders-list">
                                    <c:forEach items="${myOrders}" var="o">
                                        <div class="prf-order-card">
                                            <div class="prf-order-top">
                                                <div class="prf-order-id-group">
                                                    <strong>
                                                        <a href="${pageContext.request.contextPath}/order-detail?id=${o.orderId}" class="prf-order-link">
                                                            Đơn hàng #${o.orderId}
                                                        </a>
                                                    </strong>
                                                    <span class="prf-order-date">Ngày đặt: ${o.createdAt}</span>
                                                </div>
                                                <div class="prf-order-badges">
                                                    <span class="prf-badge ${o.paymentStatus == 'Paid' ? 'paid' : 'unpaid'}">
                                                        ${o.paymentMethod} • ${o.paymentStatus == 'Paid' ? 'Đã thanh toán' : 'Chưa thanh toán'}
                                                    </span>
                                                    <span class="prf-badge status-${fn:toLowerCase(o.orderStatus)}">
                                                        <c:choose>
                                                            <c:when test="${o.orderStatus == 'Pending'}">⏳ Chờ xử lý</c:when>
                                                            <c:when test="${o.orderStatus == 'Processing'}">📦 Đang chuẩn bị</c:when>
                                                            <c:when test="${o.orderStatus == 'Shipped'}">🚚 Đang giao hàng</c:when>
                                                            <c:when test="${o.orderStatus == 'Completed'}">✓ Hoàn thành</c:when>
                                                            <c:when test="${o.orderStatus == 'Cancelled'}">✕ Đã hủy</c:when>
                                                            <c:otherwise>${fn:escapeXml(o.orderStatus)}</c:otherwise>
                                                        </c:choose>
                                                    </span>
                                                </div>
                                            </div>

                                            <!-- Order Items -->
                                            <div class="prf-order-items">
                                                <c:forEach items="${o.items}" var="item">
                                                    <div class="prf-order-item-row">
                                                        <img src="${pageContext.request.contextPath}/book-image?bookId=${item.bookId}"
                                                             alt="${fn:escapeXml(item.bookTitle)}" class="prf-oi-thumb">
                                                        <div class="prf-oi-info">
                                                            <h4>
                                                                <a href="${pageContext.request.contextPath}/book-detail?id=${item.bookId}">
                                                                    ${not empty item.bookTitle ? fn:escapeXml(item.bookTitle) : ('Sách #' += item.bookId)}
                                                                </a>
                                                            </h4>
                                                            <span>Đơn giá: ${item.formattedUnitPrice} × <strong>${item.quantity}</strong></span>
                                                        </div>
                                                        <div class="prf-oi-subtotal">
                                                            ${item.formattedSubtotal}
                                                        </div>
                                                    </div>
                                                </c:forEach>
                                            </div>

                                            <div class="prf-order-bottom">
                                                <div class="prf-order-delivery">
                                                    <span>📍 <strong>Giao tới:</strong> ${fn:escapeXml(o.shippingAddress)} (SĐT: ${fn:escapeXml(o.recipientPhone)})</span>
                                                </div>
                                                <div class="prf-order-total-wrap">
                                                    <span class="prf-total-lbl">Tổng tiền: <strong>${o.formattedTotalAmount}</strong></span>
                                                    <a href="${pageContext.request.contextPath}/order-detail?id=${o.orderId}"
                                                       class="prf-btn-detail">
                                                        Xem Chi Tiết / Hóa Đơn
                                                    </a>
                                                    <c:if test="${o.orderStatus == 'Pending'}">
                                                        <form action="${pageContext.request.contextPath}/profile" method="POST"
                                                              class="js-prf-cancel-order" data-order-id="${o.orderId}">
                                                            <input type="hidden" name="action" value="cancelOrder">
                                                            <input type="hidden" name="orderId" value="${o.orderId}">
                                                            <button type="submit" class="prf-btn-cancel">Hủy Đơn Hàng</button>
                                                        </form>
                                                    </c:if>
                                                </div>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="prf-empty-orders">
                                    <p>Bạn chưa có đơn đặt hàng nào.</p>
                                    <a href="${pageContext.request.contextPath}/home" class="prf-btn-primary prf-btn-shop-now">
                                        Mua Sắm Ngay
                                    </a>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </section>

                    <!-- PANEL 3: ĐỔI MẬT KHẨU -->
                    <section class="prf-panel ${activeTab == 'password' ? 'active' : ''}" id="prf-panel-password">
                        <div class="prf-panel-head">
                            <h2>Đổi Mật Khẩu Bảo Mật</h2>
                            <p>Cập nhật mật khẩu định kỳ để bảo vệ tài khoản của bạn</p>
                        </div>

                        <form action="${pageContext.request.contextPath}/profile" method="POST" class="prf-form prf-form-narrow">
                            <input type="hidden" name="action" value="changePassword">

                            <div class="prf-field prf-field-spaced">
                                <label for="prfCurrentPwd">Mật khẩu hiện tại <span class="req">*</span></label>
                                <input type="password" name="currentPassword" id="prfCurrentPwd" class="prf-input"
                                       placeholder="Nhập mật khẩu đang sử dụng..." required>
                            </div>

                            <div class="prf-field prf-field-spaced">
                                <label for="prfNewPwd">Mật khẩu mới (Tối thiểu 6 ký tự) <span class="req">*</span></label>
                                <input type="password" name="newPassword" id="prfNewPwd" class="prf-input"
                                       placeholder="Nhập mật khẩu mới..." minlength="6" required>
                            </div>

                            <div class="prf-field prf-field-spaced-lg">
                                <label for="prfConfirmPwd">Xác nhận mật khẩu mới <span class="req">*</span></label>
                                <input type="password" name="confirmPassword" id="prfConfirmPwd" class="prf-input"
                                       placeholder="Nhập lại mật khẩu mới..." minlength="6" required>
                            </div>

                            <div class="prf-form-actions">
                                <button type="submit" class="prf-btn-primary">Cập Nhật Mật Khẩu</button>
                            </div>
                        </form>
                    </section>

                </div>
            </div>
        </main>

        <!-- FOOTER -->
        <jsp:include page="/components/footer/footer.jsp" />

        <script src="${pageContext.request.contextPath}/components/header/header.js"></script>
        <script src="${pageContext.request.contextPath}/components/footer/footer.js"></script>
        <script src="${pageContext.request.contextPath}/js/profile.js"></script>
    </body>
</html>

