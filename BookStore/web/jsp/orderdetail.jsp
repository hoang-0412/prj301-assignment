<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Chi Tiết Đơn Hàng #${order.orderId} - Tiệm sách Hoàng Trịnh</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/homepage.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/components/header/header.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/components/footer/footer.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/orderdetail.css">
    </head>
    <body>

        <!-- HEADER (Hidden when printing) -->
        <div class="no-print">
            <jsp:include page="/components/header/header.jsp" />
        </div>

        <main class="container od-main-wrap">

            <!-- TOP ACTION BAR -->
            <div class="od-top-bar no-print">
                <div class="od-back-links">
                    <c:choose>
                        <c:when test="${sessionScope.user.roleId == 2}">
                            <a href="${pageContext.request.contextPath}/admindashboard?tab=orders" class="od-btn od-btn-secondary">
                                ← Quay lại Quản lý Đơn Hàng
                            </a>
                        </c:when>
                        <c:otherwise>
                            <a href="${pageContext.request.contextPath}/my-orders" class="od-btn od-btn-secondary">
                                ← Quay lại Đơn Mua Của Tôi
                            </a>
                        </c:otherwise>
                    </c:choose>
                </div>

                <div class="od-right-actions">
                    <button type="button" class="od-btn od-btn-print" onclick="window.print()">
                        <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><polyline points="6 9 6 2 18 2 18 9"/><path d="M6 18H4a2 2 0 0 1-2-2v-5a2 2 0 0 1 2-2h16a2 2 0 0 1 2 2v5a2 2 0 0 1-2 2h-2"/><rect x="6" y="14" width="12" height="8"/></svg>
                        <span>In Hóa Đơn / Xuất PDF</span>
                    </button>
                </div>
            </div>

            <!-- FLASH ALERTS -->
            <c:if test="${not empty successMsg}">
                <div class="od-alert success no-print">
                    <span>✓ ${fn:escapeXml(successMsg)}</span>
                    <button type="button" onclick="this.parentElement.remove()">✕</button>
                </div>
            </c:if>
            <c:if test="${not empty errorMsg}">
                <div class="od-alert error no-print">
                    <span>⚠ ${fn:escapeXml(errorMsg)}</span>
                    <button type="button" onclick="this.parentElement.remove()">✕</button>
                </div>
            </c:if>

            <!-- INVOICE / ORDER CARD -->
            <div class="od-invoice-card">

                <!-- 1. INVOICE HEADER -->
                <div class="od-invoice-header">
                    <div class="od-brand-block">
                        <div class="od-brand-logo">Hoàng<span>Trịnh</span> BookStore</div>
                        <p>TIỆM SÁCH TRỰC TUYẾN HOÀNG TRỊNH</p>
                        <small>Địa chỉ: FPT University, Hà Nội • Hotline: 0965 551 664</small>
                    </div>
                    <div class="od-order-meta">
                        <span class="od-invoice-label">HÓA ĐƠN ĐẶT HÀNG</span>
                        <h1>Mã đơn: #${order.orderId}</h1>
                        <p>Ngày tạo đơn: <strong>${order.createdAt}</strong></p>
                    </div>
                </div>

                <!-- 2. STEP PROGRESS TRACKER -->
                <c:set var="stepLevel" value="${order.orderStatus == 'Pending' ? 1 : (order.orderStatus == 'Processing' ? 2 : (order.orderStatus == 'Shipped' ? 3 : (order.orderStatus == 'Completed' ? 4 : 0)))}" />
                <div class="od-stepper-section no-print">
                    <c:choose>
                        <c:when test="${order.orderStatus == 'Cancelled'}">
                            <div class="od-cancelled-banner">
                                <span>✕ Đơn hàng #${order.orderId} đã bị hủy (Cancelled) — Số lượng sách đã được hoàn lại vào kho.</span>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="od-stepper">
                                <div class="od-step ${stepLevel >= 1 ? 'active' : ''}">
                                    <div class="od-step-circle">1</div>
                                    <div class="od-step-text">
                                        <strong>Chờ Xử Lý</strong>
                                        <span>Pending</span>
                                    </div>
                                </div>
                                <div class="od-step-line ${stepLevel >= 2 ? 'active' : ''}"></div>
                                <div class="od-step ${stepLevel >= 2 ? 'active' : ''}">
                                    <div class="od-step-circle">2</div>
                                    <div class="od-step-text">
                                        <strong>Đang Chuẩn Bị</strong>
                                        <span>Processing</span>
                                    </div>
                                </div>
                                <div class="od-step-line ${stepLevel >= 3 ? 'active' : ''}"></div>
                                <div class="od-step ${stepLevel >= 3 ? 'active' : ''}">
                                    <div class="od-step-circle">3</div>
                                    <div class="od-step-text">
                                        <strong>Đang Giao Hàng</strong>
                                        <span>Shipped</span>
                                    </div>
                                </div>
                                <div class="od-step-line ${stepLevel >= 4 ? 'active' : ''}"></div>
                                <div class="od-step ${stepLevel >= 4 ? 'active' : ''}">
                                    <div class="od-step-circle">4</div>
                                    <div class="od-step-text">
                                        <strong>Hoàn Thành</strong>
                                        <span>Completed</span>
                                    </div>
                                </div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

                <!-- 3. CUSTOMER & PAYMENT INFO GRID -->
                <div class="od-info-grid">
                    <div class="od-info-box">
                        <h3>Thông Tin Người Nhận</h3>
                        <div class="od-info-row">
                            <span>Khách hàng:</span>
                            <strong>${not empty customer ? fn:escapeXml(customer.fullName) : ('User #' += order.userId)}</strong>
                        </div>
                        <c:if test="${not empty customer}">
                            <div class="od-info-row">
                                <span>Email tài khoản:</span>
                                <strong>${fn:escapeXml(customer.email)}</strong>
                            </div>
                        </c:if>
                        <div class="od-info-row">
                            <span>Số điện thoại nhận:</span>
                            <strong>${fn:escapeXml(order.recipientPhone)}</strong>
                        </div>
                        <div class="od-info-row">
                            <span>Địa chỉ giao hàng:</span>
                            <strong>${fn:escapeXml(order.shippingAddress)}</strong>
                        </div>
                    </div>

                    <div class="od-info-box">
                        <h3>Thanh Toán & Vận Chuyển</h3>
                        <div class="od-info-row">
                            <span>Phương thức thanh toán:</span>
                            <strong>${fn:escapeXml(order.paymentMethod)}</strong>
                        </div>
                        <div class="od-info-row">
                            <span>Trạng thái thanh toán:</span>
                            <span class="od-badge ${order.paymentStatus == 'Paid' ? 'paid' : 'unpaid'}">
                                ${order.paymentStatus == 'Paid' ? 'Đã thanh toán (Paid)' : fn:escapeXml(order.paymentStatus)}
                            </span>
                        </div>
                        <div class="od-info-row">
                            <span>Trạng thái đơn hàng:</span>
                            <span class="od-badge status-${fn:toLowerCase(order.orderStatus)}">
                                ${fn:escapeXml(order.orderStatus)}
                            </span>
                        </div>
                        <div class="od-info-row">
                            <span>Tổng số lượng sách:</span>
                            <strong>${totalQty} cuốn</strong>
                        </div>
                    </div>
                </div>

                <!-- 4. ORDER ITEMS TABLE -->
                <div class="od-items-section">
                    <h3>Danh Sách Sản Phẩm Trong Đơn</h3>
                    <div class="od-table-wrap">
                        <table class="od-table">
                            <thead>
                                <tr>
                                    <th>STT</th>
                                    <th>Sản Phẩm Sách</th>
                                    <th>Mã ISBN / NXB</th>
                                    <th>Đơn Giá</th>
                                    <th>Số Lượng</th>
                                    <th style="text-align:right;">Thành Tiền</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${order.items}" var="item" varStatus="st">
                                    <c:set var="b" value="${bookMap[item.bookId]}" />
                                    <tr>
                                        <td>${st.index + 1}</td>
                                        <td>
                                            <div class="od-book-cell">
                                                <img src="${pageContext.request.contextPath}/book-image?bookId=${item.bookId}"
                                                     alt="${fn:escapeXml(item.bookTitle)}" class="od-book-thumb no-print">
                                                <div>
                                                    <a href="${pageContext.request.contextPath}/book-detail?id=${item.bookId}" class="od-book-title">
                                                        ${not empty item.bookTitle ? fn:escapeXml(item.bookTitle) : ('Sách #' += item.bookId)}
                                                    </a>
                                                    <c:if test="${not empty b}">
                                                        <div class="od-book-sub">Tác giả: ${fn:escapeXml(b.author)} • ${fn:escapeXml(b.categoryName)}</div>
                                                    </c:if>
                                                </div>
                                            </div>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty b}">
                                                    <code>${fn:escapeXml(b.isbn)}</code>
                                                    <div class="od-book-sub">${fn:escapeXml(b.publisher)}</div>
                                                </c:when>
                                                <c:otherwise>—</c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>${item.formattedUnitPrice}</td>
                                        <td><strong>x${item.quantity}</strong></td>
                                        <td style="text-align:right;"><strong class="od-price">${item.formattedSubtotal}</strong></td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>

                <!-- 5. SUMMARY & TOTALS -->
                <div class="od-summary-footer">
                    <div class="od-note-box">
                        <strong>Ghi chú đơn hàng:</strong>
                        <p>Cảm ơn quý khách đã mua sắm tại Tiệm sách Hoàng Trịnh. Vui lòng kiểm tra tình trạng sách khi nhận hàng.</p>
                    </div>
                    <div class="od-totals-box">
                        <div class="od-total-line">
                            <span>Tạm tính (${totalQty} cuốn):</span>
                            <strong>${order.formattedTotalAmount}</strong>
                        </div>
                        <div class="od-total-line">
                            <span>Phí vận chuyển:</span>
                            <strong style="color:#059669;">Miễn phí (0 đ)</strong>
                        </div>
                        <div class="od-total-line grand">
                            <span>Tổng cộng thanh toán:</span>
                            <strong>${order.formattedTotalAmount}</strong>
                        </div>
                    </div>
                </div>

                <!-- 6. QUICK ACTIONS FOR ADMIN OR CUSTOMER (Hidden when printing) -->
                <div class="od-control-bar no-print">
                    <c:if test="${sessionScope.user.roleId == 2}">
                        <form action="${pageContext.request.contextPath}/order-detail" method="POST" class="od-admin-form">
                            <input type="hidden" name="action" value="adminUpdate">
                            <input type="hidden" name="orderId" value="${order.orderId}">
                            <span class="od-admin-lbl">Cập nhật nhanh (Admin):</span>
                            <select name="orderStatus" class="od-select">
                                <option value="Pending" ${order.orderStatus == 'Pending' ? 'selected' : ''}>Pending (Chờ xử lý)</option>
                                <option value="Processing" ${order.orderStatus == 'Processing' ? 'selected' : ''}>Processing (Đang chuẩn bị)</option>
                                <option value="Shipped" ${order.orderStatus == 'Shipped' ? 'selected' : ''}>Shipped (Đang giao)</option>
                                <option value="Completed" ${order.orderStatus == 'Completed' ? 'selected' : ''}>Completed (Hoàn thành)</option>
                                <option value="Cancelled" ${order.orderStatus == 'Cancelled' ? 'selected' : ''}>Cancelled (Đã hủy)</option>
                            </select>
                            <select name="paymentStatus" class="od-select">
                                <option value="Unpaid" ${order.paymentStatus == 'Unpaid' ? 'selected' : ''}>Unpaid (Chưa thanh toán)</option>
                                <option value="Paid" ${order.paymentStatus == 'Paid' ? 'selected' : ''}>Paid (Đã thanh toán)</option>
                                <option value="Refunded" ${order.paymentStatus == 'Refunded' ? 'selected' : ''}>Refunded (Hoàn tiền)</option>
                            </select>
                            <button type="submit" class="od-btn od-btn-primary">Lưu Trạng Thái</button>
                        </form>
                    </c:if>

                    <c:if test="${sessionScope.user.userId == order.userId and order.orderStatus == 'Pending'}">
                        <form action="${pageContext.request.contextPath}/order-detail" method="POST"
                              onsubmit="return confirm('Bạn có chắc chắn muốn hủy đơn hàng #${order.orderId}?');">
                            <input type="hidden" name="action" value="customerCancel">
                            <input type="hidden" name="orderId" value="${order.orderId}">
                            <button type="submit" class="od-btn od-btn-danger">Hủy Đơn Hàng Này</button>
                        </form>
                    </c:if>
                </div>

            </div>
        </main>

        <!-- FOOTER (Hidden when printing) -->
        <div class="no-print">
            <jsp:include page="/components/footer/footer.jsp" />
        </div>

        <script src="${pageContext.request.contextPath}/components/header/header.js"></script>
        <script src="${pageContext.request.contextPath}/components/footer/footer.js"></script>
    </body>
</html>

