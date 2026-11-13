<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>${fn:escapeXml(book.title)} - Tiệm sách Hoàng Trịnh</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/homepage.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/components/header/header.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/components/footer/footer.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bookdetail.css">
    </head>
    <body>

        <!-- HEADER -->
        <jsp:include page="/components/header/header.jsp" />

        <!-- BREADCRUMB -->
        <div class="bd-breadcrumb-bar">
            <div class="container bd-breadcrumb">
                <a href="${pageContext.request.contextPath}/home">Trang Chủ</a>
                <span class="sep">/</span>
                <a href="${pageContext.request.contextPath}/home?categoryId=${book.categoryId}">${fn:escapeXml(book.categoryName)}</a>
                <span class="sep">/</span>
                <span class="current">${fn:escapeXml(book.title)}</span>
            </div>
        </div>

        <!-- MAIN BOOK DETAIL SECTION -->
        <main class="container bd-main-wrap">
            <div class="bd-card">
                <!-- Left Column: Cover Image -->
                <div class="bd-gallery-col">
                    <div class="bd-cover-frame">
                        <span class="bd-status-pill ${book.bookStatus == 'Bán' ? 'sale' : 'rent'}">
                            ● ${not empty book.bookStatus ? fn:escapeXml(book.bookStatus) : 'Bán'}
                        </span>
                        <img src="${pageContext.request.contextPath}/book-image?bookId=${book.bookId}"
                             alt="${fn:escapeXml(book.title)}" class="bd-cover-img">
                    </div>
                </div>

                <!-- Right Column: Book Information & Purchase Actions -->
                <div class="bd-info-col">
                    <div class="bd-category-tag">
                        <a href="${pageContext.request.contextPath}/home?categoryId=${book.categoryId}">
                            ${fn:escapeXml(book.categoryName)}
                        </a>
                    </div>

                    <h1 class="bd-title">${fn:escapeXml(book.title)}</h1>

                    <div class="bd-sub-meta">
                        <span>Tác giả: <strong>${fn:escapeXml(book.author)}</strong></span>
                        <span class="dot">•</span>
                        <span>NXB: <strong>${fn:escapeXml(book.publisher)}</strong></span>
                        <span class="dot">•</span>
                        <span>Mã ISBN: <code>${fn:escapeXml(book.isbn)}</code></span>
                    </div>

                    <div class="bd-price-panel">
                        <div class="bd-price-main">${book.formattedPrice}</div>
                        <div class="bd-stock-status">
                            <c:choose>
                                <c:when test="${book.stockQuantity > 0}">
                                    <span class="bd-stock-badge in-stock">✓ Còn hàng (${book.stockQuantity} cuốn có sẵn)</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="bd-stock-badge out-stock">✕ Tạm hết hàng</span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>

                    <p class="bd-short-desc">
                        ${not empty book.description ? fn:escapeXml(book.description) : 'Ấn phẩm chính hãng tại Tiệm sách Hoàng Trịnh, in ấn sắc nét, nội dung chọn lọc dành cho độc giả.'}
                    </p>

                    <!-- Add to Cart / Buy Now Form -->
                    <c:choose>
                        <c:when test="${book.stockQuantity > 0}">
                            <form action="${pageContext.request.contextPath}/cart" method="POST" class="bd-purchase-form" id="bdPurchaseForm">
                                <input type="hidden" name="action" value="add">
                                <input type="hidden" name="bookId" value="${book.bookId}">

                                <div class="bd-qty-row">
                                    <label for="bdQtyInput">Số lượng:</label>
                                    <div class="bd-qty-control">
                                        <button type="button" class="bd-qty-btn" id="bdQtyMinus">−</button>
                                        <input type="number" name="quantity" id="bdQtyInput" class="bd-qty-input"
                                               value="1" min="1" max="${book.stockQuantity}">
                                        <button type="button" class="bd-qty-btn" id="bdQtyPlus">+</button>
                                    </div>
                                    <span class="bd-qty-hint">${book.stockQuantity} cuốn có sẵn trong kho</span>
                                </div>

                                <div class="bd-action-buttons">
                                    <button type="button" class="bd-btn bd-btn-outline" id="bdAddToCartAjaxBtn"
                                            data-id="${book.bookId}"
                                            data-title="${fn:escapeXml(book.title)}"
                                            data-context="${pageContext.request.contextPath}">
                                        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><circle cx="9" cy="21" r="1"/><circle cx="20" cy="21" r="1"/><path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"/></svg>
                                        <span>Thêm Vào Giỏ Hàng</span>
                                    </button>
                                    <button type="submit" class="bd-btn bd-btn-primary">
                                        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><path d="M5 12h14"/><path d="M12 5l7 7-7 7"/></svg>
                                        <span>Mua Ngay</span>
                                    </button>
                                </div>
                            </form>
                        </c:when>
                        <c:otherwise>
                            <div class="bd-out-of-stock-notice">
                                Cuốn sách này hiện đang tạm hết hàng. Vui lòng quay lại sau hoặc tham khảo các đầu sách cùng thể loại bên dưới.
                            </div>
                        </c:otherwise>
                    </c:choose>

                    <!-- Service Guarantees -->
                    <div class="bd-guarantees">
                        <div class="bd-guarantee-item">
                            <span class="icon">✓</span>
                            <div>
                                <strong>Sách thật 100%</strong>
                                <p>Bản quyền chính hãng từ NXB</p>
                            </div>
                        </div>
                        <div class="bd-guarantee-item">
                            <span class="icon">🚚</span>
                            <div>
                                <strong>Giao hàng toàn quốc</strong>
                                <p>Đóng gói chống sốc 3 lớp</p>
                            </div>
                        </div>
                        <div class="bd-guarantee-item">
                            <span class="icon">↺</span>
                            <div>
                                <strong>Đổi trả miễn phí</strong>
                                <p>Trong 7 ngày nếu lỗi in ấn</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- SPECIFICATIONS & DESCRIPTION GRID -->
            <div class="bd-details-grid">
                <section class="bd-spec-box">
                    <h3>Thông Tin Chi Tiết</h3>
                    <table class="bd-spec-table">
                        <tbody>
                            <tr>
                                <th>Mã sản phẩm</th>
                                <td>#${book.bookId}</td>
                            </tr>
                            <tr>
                                <th>Tên sách</th>
                                <td><strong>${fn:escapeXml(book.title)}</strong></td>
                            </tr>
                            <tr>
                                <th>Tác giả</th>
                                <td>${fn:escapeXml(book.author)}</td>
                            </tr>
                            <tr>
                                <th>Thể loại</th>
                                <td>${fn:escapeXml(book.categoryName)}</td>
                            </tr>
                            <tr>
                                <th>Nhà xuất bản</th>
                                <td>${fn:escapeXml(book.publisher)}</td>
                            </tr>
                            <tr>
                                <th>Năm xuất bản</th>
                                <td>${book.publicationYear}</td>
                            </tr>
                            <tr>
                                <th>Mã ISBN</th>
                                <td><code>${fn:escapeXml(book.isbn)}</code></td>
                            </tr>
                            <tr>
                                <th>Hình thức</th>
                                <td>${not empty book.bookStatus ? fn:escapeXml(book.bookStatus) : 'Bán'}</td>
                            </tr>
                        </tbody>
                    </table>
                </section>

                <section class="bd-desc-box">
                    <h3>Giới Thiệu Nội Dung Sách</h3>
                    <div class="bd-desc-content">
                        <p>${not empty book.description ? fn:escapeXml(book.description) : 'Chưa có bài giới thiệu chi tiết cho cuốn sách này.'}</p>
                        <p class="bd-desc-meta-note">
                            Ấn phẩm <strong>${fn:escapeXml(book.title)}</strong> của tác giả <strong>${fn:escapeXml(book.author)}</strong> (xuất bản năm ${book.publicationYear} bởi ${fn:escapeXml(book.publisher)}) thuộc danh mục <em>${fn:escapeXml(book.categoryName)}</em>, hiện đang được phân phối trực tiếp tại hệ thống Tiệm sách Hoàng Trịnh.
                        </p>
                    </div>
                </section>
            </div>

            <!-- RELATED BOOKS -->
            <c:if test="${not empty relatedBooks}">
                <section class="bd-related-section">
                    <div class="section-header">
                        <div class="section-title-wrap">
                            <h2>Sách Cùng Thể Loại "${fn:escapeXml(book.categoryName)}"</h2>
                            <p>Gợi ý các đầu sách tương tự có thể bạn quan tâm</p>
                        </div>
                        <a href="${pageContext.request.contextPath}/home?categoryId=${book.categoryId}" class="bd-view-all-link">
                            Xem tất cả →
                        </a>
                    </div>

                    <div class="book-grid">
                        <c:forEach items="${relatedBooks}" var="rb">
                            <article class="book-card"
                                     data-id="${rb.bookId}"
                                     data-title="${fn:escapeXml(rb.title)}">
                                <div class="card-image-wrap">
                                    <div class="card-badges">
                                        <span class="badge-cat">${fn:escapeXml(rb.categoryName)}</span>
                                    </div>
                                    <a href="${pageContext.request.contextPath}/book-detail?id=${rb.bookId}">
                                        <img src="${pageContext.request.contextPath}/book-image?bookId=${rb.bookId}" alt="${fn:escapeXml(rb.title)}" loading="lazy">
                                    </a>
                                </div>
                                <div class="card-body">
                                    <h3 class="card-title" title="${fn:escapeXml(rb.title)}">
                                        <a href="${pageContext.request.contextPath}/book-detail?id=${rb.bookId}">
                                            ${fn:escapeXml(rb.title)}
                                        </a>
                                    </h3>
                                    <p class="card-author">Tác giả: <strong>${fn:escapeXml(rb.author)}</strong></p>
                                    <div class="card-footer-price">
                                        <div class="price-box">
                                            <span class="current-price">${rb.formattedPrice}</span>
                                        </div>
                                        <a href="${pageContext.request.contextPath}/book-detail?id=${rb.bookId}" class="btn-add-cart-mini" title="Xem chi tiết">
                                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><path d="M5 12h14"/><path d="M12 5l7 7-7 7"/></svg>
                                        </a>
                                    </div>
                                </div>
                            </article>
                        </c:forEach>
                    </div>
                </section>
            </c:if>
        </main>

        <!-- TOAST CONTAINER -->
        <div class="toast-container" id="toastContainer" aria-live="polite"></div>

        <!-- FOOTER -->
        <jsp:include page="/components/footer/footer.jsp" />

        <script src="${pageContext.request.contextPath}/components/header/header.js"></script>
        <script src="${pageContext.request.contextPath}/components/footer/footer.js"></script>
        <script src="${pageContext.request.contextPath}/js/bookdetail.js"></script>
    </body>
</html>

