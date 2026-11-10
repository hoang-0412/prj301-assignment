<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Giỏ Hàng & Thanh Toán - Tiệm sách Hoàng Trịnh</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/homepage.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/components/header/header.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/components/footer/footer.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/cart.css">
    </head>
    <body>

        <!-- HEADER -->
        <jsp:include page="/components/header/header.jsp" />

        <!-- PAGE BANNER -->
        <section class="cart-hero">
            <div class="container">
                <h1>Giỏ Hàng Của Bạn</h1>
                <p>Kiểm tra danh sách sách đã chọn và hoàn tất thông tin giao hàng</p>
            </div>
        </section>

        <main class="container cart-main-container">

            <!-- FLASH ALERTS -->
            <c:if test="${not empty successMsg}">
                <div class="cart-alert success">
                    <span>✓ ${fn:escapeXml(successMsg)}</span>
                    <button type="button" onclick="this.parentElement.remove()">✕</button>
                </div>
            </c:if>
            <c:if test="${not empty errorMsg}">
                <div class="cart-alert error">
                    <span>⚠ ${fn:escapeXml(errorMsg)}</span>
                    <button type="button" onclick="this.parentElement.remove()">✕</button>
                </div>
            </c:if>

            <c:choose>
                <c:when test="${not empty cartItems}">
                    <div class="cart-layout-grid">
                        <!-- LEFT: CART ITEMS LIST -->
                        <div class="cart-items-panel">
                            <div class="cart-panel-header">
                                <h3>Sản Phẩm Trong Giỏ (${cartCount} cuốn)</h3>
                                <form action="${pageContext.request.contextPath}/cart" method="POST" onsubmit="return confirm('Bạn muốn xóa toàn bộ sản phẩm trong giỏ hàng?');">
                                    <input type="hidden" name="action" value="clear">
                                    <button type="submit" class="btn-clear-cart">Xóa tất cả</button>
                                </form>
                            </div>

                            <div class="cart-items-list">
                                <c:forEach items="${cartItems}" var="ci">
                                    <c:set var="b" value="${ci.book}" />
                                    <div class="cart-item-card">
                                        <a href="${pageContext.request.contextPath}/book-detail?id=${b.bookId}" class="cart-item-thumb-link">
                                            <img src="${pageContext.request.contextPath}/book-image?bookId=${b.bookId}"
                                                 alt="${fn:escapeXml(b.title)}" class="cart-item-thumb">
                                        </a>

                                        <div class="cart-item-info">
                                            <span class="cart-item-cat">${fn:escapeXml(b.categoryName)}</span>
                                            <h4>
                                                <a href="${pageContext.request.contextPath}/book-detail?id=${b.bookId}">
                                                    ${fn:escapeXml(b.title)}
                                                </a>
                                            </h4>
                                            <p class="cart-item-author">Tác giả: <strong>${fn:escapeXml(b.author)}</strong></p>
                                            <div class="cart-item-unit-price">
                                                Đơn giá: <strong>${b.formattedPrice}</strong>
                                                <span class="stock-note">(Kho còn: ${b.stockQuantity})</span>
                                            </div>
                                        </div>

                                        <!-- Quantity Update Form -->
                                        <form action="${pageContext.request.contextPath}/cart" method="POST" class="cart-qty-form">
                                            <input type="hidden" name="action" value="update">
                                            <input type="hidden" name="bookId" value="${b.bookId}">
                                            <div class="cart-qty-box">
                                                <button type="submit" name="quantity" value="${ci.quantity - 1}" class="cart-qty-btn" title="Giảm">−</button>
                                                <span class="cart-qty-val">${ci.quantity}</span>
                                                <button type="submit" name="quantity" value="${ci.quantity + 1}" class="cart-qty-btn"
                                                        ${ci.quantity >= b.stockQuantity ? 'disabled' : ''} title="Tăng">+</button>
                                            </div>
                                        </form>

                                        <div class="cart-item-subtotal">
                                            <span class="lbl">Thành tiền</span>
                                            <strong>${ci.formattedSubtotal}</strong>
                                        </div>

                                        <!-- Remove Item Form -->
                                        <form action="${pageContext.request.contextPath}/cart" method="POST">
                                            <input type="hidden" name="action" value="remove">
                                            <input type="hidden" name="bookId" value="${b.bookId}">
                                            <button type="submit" class="cart-remove-btn" title="Xóa khỏi giỏ">✕</button>
                                        </form>
                                    </div>
                                </c:forEach>
                            </div>

                            <div class="cart-panel-footer">
                                <a href="${pageContext.request.contextPath}/home" class="btn-continue-shopping">
                                    ← Tiếp tục chọn thêm sách
                                </a>
                            </div>
                        </div>

                        <!-- RIGHT: CHECKOUT & ORDER SUMMARY -->
                        <aside class="cart-checkout-panel">
                            <div class="checkout-card">
                                <h3>Thông Tin Đặt Hàng</h3>
                                <p class="checkout-sub">Điền thông tin nhận hàng để hoàn tất đơn đặt sách</p>

                                <form action="${pageContext.request.contextPath}/cart" method="POST" class="checkout-form">
                                    <input type="hidden" name="action" value="checkout">

                                    <div class="checkout-field">
                                        <label>Người nhận hàng</label>
                                        <input type="text" class="checkout-input readonly"
                                               value="${not empty sessionScope.user ? fn:escapeXml(sessionScope.user.fullName) : 'Khách vãng lai (Chưa đăng nhập)'}"
                                               readonly>
                                    </div>

                                    <div class="checkout-field">
                                        <label for="checkoutPhone">Số điện thoại nhận hàng <span class="req">*</span></label>
                                        <input type="text" name="recipientPhone" id="checkoutPhone" class="checkout-input"
                                               placeholder="Nhập SĐT liên hệ giao hàng (VD: 0987654321)"
                                               value="${not empty sessionScope.user ? fn:escapeXml(sessionScope.user.phoneNumber) : ''}"
                                               required>
                                    </div>

                                    <div class="checkout-field">
                                        <label for="checkoutAddress">Địa chỉ giao hàng chi tiết <span class="req">*</span></label>
                                        <textarea name="shippingAddress" id="checkoutAddress" class="checkout-textarea"
                                                  placeholder="Số nhà, đường/phố, phường/xã, quận/huyện, tỉnh/thành phố..."
                                                  required>${not empty sessionScope.user ? fn:escapeXml(sessionScope.user.address) : ''}</textarea>
                                    </div>

                                    <div class="checkout-field">
                                        <label for="checkoutPayment">Phương thức thanh toán</label>
                                        <select name="paymentMethod" id="checkoutPayment" class="checkout-select">
                                            <option value="COD">Thanh toán khi nhận hàng (COD)</option>
                                            <option value="Chuyển khoản">Chuyển khoản ngân hàng</option>
                                            <option value="VNPay">Thanh toán qua Ví VNPay</option>
                                            <option value="MoMo">Thanh toán qua Ví MoMo</option>
                                        </select>
                                    </div>

                                    <div class="checkout-summary-box">
                                        <div class="summary-row">
                                            <span>Tổng số lượng sách:</span>
                                            <strong>${cartCount} cuốn</strong>
                                        </div>
                                        <div class="summary-row">
                                            <span>Tạm tính:</span>
                                            <strong>${formattedTotal}</strong>
                                        </div>
                                        <div class="summary-row">
                                            <span>Phí vận chuyển:</span>
                                            <strong class="free-ship">Miễn phí</strong>
                                        </div>
                                        <div class="summary-divider"></div>
                                        <div class="summary-row total">
                                            <span>Tổng thanh toán:</span>
                                            <strong class="total-price">${formattedTotal}</strong>
                                        </div>
                                    </div>

                                    <c:choose>
                                        <c:when test="${not empty sessionScope.user}">
                                            <button type="submit" class="btn-checkout-submit">
                                                <span>Đặt Hàng Ngay</span>
                                                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4"><path d="M5 12h14"/><path d="M12 5l7 7-7 7"/></svg>
                                            </button>
                                        </c:when>
                                        <c:otherwise>
                                            <a href="${pageContext.request.contextPath}/login" class="btn-checkout-submit login-required">
                                                <span>Đăng Nhập Để Đặt Hàng</span>
                                                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4"><path d="M15 3h4a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-4"/><polyline points="10 17 15 12 10 7"/><line x1="15" y1="12" x2="3" y2="12"/></svg>
                                            </a>
                                        </c:otherwise>
                                    </c:choose>
                                </form>
                            </div>
                        </aside>
                    </div>
                </c:when>

                <c:otherwise>
                    <!-- EMPTY CART STATE -->
                    <div class="cart-empty-card">
                        <div class="cart-empty-icon">🛒</div>
                        <h2>Giỏ hàng của bạn đang trống</h2>
                        <p>Hãy khám phá kho sách phong phú của Tiệm sách Hoàng Trịnh và thêm những cuốn sách yêu thích vào giỏ nhé!</p>
                        <div class="cart-empty-actions">
                            <a href="${pageContext.request.contextPath}/home" class="btn-checkout-submit" style="display:inline-flex; width:auto; padding: 12px 28px;">
                                Khám Phá Kho Sách Ngay
                            </a>
                            <c:if test="${not empty sessionScope.user}">
                                <a href="${pageContext.request.contextPath}/my-orders" class="btn-continue-shopping" style="padding: 12px 22px;">
                                    Xem Đơn Mua Của Tôi →
                                </a>
                            </c:if>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </main>

        <!-- FOOTER -->
        <jsp:include page="/components/footer/footer.jsp" />

        <script src="${pageContext.request.contextPath}/components/header/header.js"></script>
        <script src="${pageContext.request.contextPath}/components/footer/footer.js"></script>
    </body>
</html>

