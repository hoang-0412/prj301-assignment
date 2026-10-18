<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Tiệm sách Hoàng Trịnh</title>
        <meta name="description" content="Nhà sách trực tuyến uy tín hàng đầu, cung cấp hàng ngàn tựa sách công nghệ, kinh tế, văn học, kỹ năng sống chính hãng với ưu đãi hấp dẫn.">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/homepage.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/components/header/header.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/components/footer/footer.css">
    </head>
    <body>

        <!-- HEADER COMPONENT -->
        <jsp:include page="/components/header/header.jsp" />

        <!-- 3. HERO BANNER SECTION -->
        <section class="hero-section" id="heroBanner">
            <div class="hero-glow-1"></div>
            <div class="hero-glow-2"></div>
            <div class="container">
                <div class="hero-layout">
                    <!-- Left Hero Content -->
                    <div class="hero-text-content">
                        <div class="hero-tag">
                            Nhà Sách Trực Tuyến Hàng Đầu Việt Nam
                        </div>
                        <h1 class="hero-title">
                            Khơi Nguồn <span class="highlight">Tri Thức</span><br>
                            Nâng Tầm Tương Lai
                        </h1>
                        <p class="hero-description">
                            Khám phá hơn 10.000+ tựa sách chọn lọc từ các tác giả danh tiếng thế giới và Việt Nam. 
                            Giao sách hỏa tốc, bao bọc cẩn thận, ưu đãi thành viên lên tới 30%.
                        </p>
                        <div class="hero-cta-group">
                            <a href="#catalog" class="btn-primary" id="heroExploreBtn">
                                <span>Khám Phá Sách Ngay</span>
                            </a>
                            <a href="#featured" class="btn-secondary" id="heroPromoBtn">
                                <span>Xem Sách Bán Chạy</span>
                            </a>
                        </div>
                        <div class="hero-stats">
                            <div class="stat-item">
                                <h4>10.000+</h4>
                                <p>Đầu sách bản quyền</p>
                            </div>
                            <div class="stat-item">
                                <h4>50.000+</h4>
                                <p>Độc giả tin dùng</p>
                            </div>
                            <div class="stat-item">
                                <h4>99.8%</h4>
                                <p>Đánh giá hài lòng</p>
                            </div>
                        </div>
                    </div>

                    <!-- Right Hero Spotlight Book Card -->
                    <c:if test="${not empty featuredBooks}">
                        <c:set var="spotlightBook" value="${featuredBooks[0]}" />
                        <div class="hero-card-spotlight">
                            <span class="spotlight-badge">SÁCH TIÊU BIỂU TUẦN NÀY</span>
                            <div class="spotlight-book-grid">
                                <div class="spotlight-cover">
                                    <img src="${pageContext.request.contextPath}/book-image?bookId=${spotlightBook.bookId}" 
                                         alt="${fn:escapeXml(spotlightBook.title)}" loading="eager">
                                </div>
                                <div class="spotlight-info">
                                    <h3>${spotlightBook.title}</h3>
                                    <p class="author">Tác giả: <strong>${spotlightBook.author}</strong></p>
                                    <div class="spotlight-rating">
                                        <span>Đánh giá: 5.0 (320+ lượt mua)</span>
                                    </div>
                                    <div class="spotlight-price">${spotlightBook.formattedPrice}</div>
                                    <p style="font-size:12px; color:#cbd5e1; margin-top:8px;">
                                        ${fn:substring(spotlightBook.description, 0, 85)}...
                                    </p>
                                </div>
                            </div>
                        </div>
                    </c:if>
                </div>
            </div>
        </section>


        <!-- 5. CATEGORY QUICK FILTER CHIPS (Dữ liệu từ bảng Category trong CSDL) -->
        <section class="category-filter-section" aria-label="Bộ lọc danh mục">
            <div class="container">
                <div class="category-scroll-container">
                    <a href="${pageContext.request.contextPath}/home?sort=${selectedSort}${not empty keyword ? '&keyword=' : ''}${fn:escapeXml(keyword)}" 
                       class="cat-chip ${empty selectedCategoryId ? 'active' : ''}" id="catAllBtn">
                        <span>Tất Cả Thể Loại</span>
                        <span class="cat-count">${totalAllBooks}</span>
                    </a>

                    <c:forEach items="${categories}" var="cat">
                        <a href="${pageContext.request.contextPath}/home?categoryId=${cat.categoryId}&sort=${selectedSort}${not empty keyword ? '&keyword=' : ''}${fn:escapeXml(keyword)}" 
                           class="cat-chip ${selectedCategoryId == cat.categoryId ? 'active' : ''}">
                            <span>${cat.categoryName}</span>
                            <span class="cat-count">${cat.bookCount}</span>
                        </a>
                    </c:forEach>
                </div>
            </div>
        </section>

        <!-- 6. FEATURED BOOKS SHOWCASE -->
        <c:if test="${empty keyword and (empty selectedCategoryId or selectedCategoryId == 0)}">
            <section class="container" id="featured" style="margin-bottom: 48px;">
                <div class="section-header">
                    <div class="section-title-wrap">
                        <h2>
                            <span>Sách Nổi Bật Bán Chạy</span>
                            <span class="section-badge-hot">BÁN CHẠY</span>
                        </h2>
                        <p>Những tựa sách được độc giả quan tâm nhất</p>
                    </div>
                </div>

                <div class="book-grid">
                    <c:forEach items="${featuredBooks}" var="b">
                        <article class="book-card" 
                                 data-id="${b.bookId}"
                                 data-title="${fn:escapeXml(b.title)}"
                                 data-author="${fn:escapeXml(b.author)}"
                                 data-category="${fn:escapeXml(b.categoryName)}"
                                 data-publisher="${fn:escapeXml(b.publisher)}"
                                 data-year="${b.publicationYear}"
                                 data-isbn="${b.isbn}"
                                 data-price="${b.formattedPrice}"
                                 data-desc="${fn:escapeXml(b.description)}"
                                 data-image="${pageContext.request.contextPath}/book-image?bookId=${b.bookId}">
                            <div class="card-image-wrap">
                                <div class="card-badges">
                                    <span class="badge-cat">${b.categoryName}</span>
                                </div>
                                <img src="${pageContext.request.contextPath}/book-image?bookId=${b.bookId}" alt="${fn:escapeXml(b.title)}" loading="lazy">
                                <div class="card-quick-actions">
                                    <button type="button" class="action-btn-quick btn-quick-view" aria-label="Xem nhanh">
                                        <span>Xem Nhanh</span>
                                    </button>
                                </div>
                            </div>
                            <div class="card-body">
                                <h3 class="card-title" title="${fn:escapeXml(b.title)}">${b.title}</h3>
                                <p class="card-author">
                                    Tác giả: <strong>${b.author}</strong>
                                </p>
                                <div class="card-rating">
                                    <span>Đánh giá: 5.0</span>
                                    <span class="review-count">(${b.stockQuantity + 45} lượt mua)</span>
                                </div>
                                <div class="card-footer-price">
                                    <div class="price-box">
                                        <span class="current-price">${b.formattedPrice}</span>
                                    </div>
                                    <button type="button" class="btn-add-cart-mini" title="Thêm vào giỏ" aria-label="Thêm vào giỏ">
                                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><path d="M6 2L3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4z"></path><line x1="3" y1="6" x2="21" y2="6"></line><path d="M16 10a4 4 0 0 1-8 0"></path></svg>
                                    </button>
                                </div>
                            </div>
                        </article>
                    </c:forEach>
                </div>
            </section>
        </c:if>

        <!-- 7. MAIN CATALOG & SEARCH RESULTS (Dữ liệu từ bảng Book trong CSDL) -->
        <main class="container" id="catalog">
            <div class="section-header">
                <div class="section-title-wrap">
                    <h2>
                        <c:choose>
                            <c:when test="${not empty keyword}">
                                <span>Kết quả tìm kiếm: "${fn:escapeXml(keyword)}"</span>
                            </c:when>
                            <c:when test="${not empty selectedCategoryId}">
                                <c:forEach items="${categories}" var="c">
                                    <c:if test="${c.categoryId == selectedCategoryId}">
                                        <span>Thể Loại: ${c.categoryName}</span>
                                    </c:if>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <span>Danh Mục Sách</span>
                            </c:otherwise>
                        </c:choose>
                    </h2>
                    <p>Khám phá kho sách phong phú từ cơ sở dữ liệu hệ thống</p>
                </div>
            </div>

            <!-- Toolbar: Results Info & Sort Selector -->
            <div class="catalog-toolbar">
                <div class="toolbar-info">
                    Hiển thị <strong>${fn:length(books)}</strong> trên tổng số <strong>${totalBooks}</strong> cuốn sách
                    <c:if test="${not empty keyword or not empty selectedCategoryId}">
                        <span>• <a href="${pageContext.request.contextPath}/home" style="color:var(--primary); font-weight:600;">[Xóa bộ lọc]</a></span>
                    </c:if>
                </div>
                <div class="toolbar-actions">
                    <div class="sort-select-wrap">
                        <label for="sortSelect">Sắp xếp:</label>
                        <select id="sortSelect" class="sort-select">
                            <option value="newest" ${selectedSort == 'newest' ? 'selected' : ''}>Mới nhất</option>
                            <option value="price-asc" ${selectedSort == 'price-asc' ? 'selected' : ''}>Giá: Thấp đến Cao</option>
                            <option value="price-desc" ${selectedSort == 'price-desc' ? 'selected' : ''}>Giá: Cao đến Thấp</option>
                            <option value="title-asc" ${selectedSort == 'title-asc' ? 'selected' : ''}>Tên sách: A - Z</option>
                            <option value="oldest" ${selectedSort == 'oldest' ? 'selected' : ''}>Cũ nhất</option>
                        </select>
                    </div>
                </div>
            </div>

            <!-- Books Grid -->
            <c:choose>
                <c:when test="${not empty books}">
                    <div class="book-grid">
                        <c:forEach items="${books}" var="b">
                            <article class="book-card" 
                                     data-id="${b.bookId}"
                                     data-title="${fn:escapeXml(b.title)}"
                                     data-author="${fn:escapeXml(b.author)}"
                                     data-category="${fn:escapeXml(b.categoryName)}"
                                     data-publisher="${fn:escapeXml(b.publisher)}"
                                     data-year="${b.publicationYear}"
                                     data-isbn="${b.isbn}"
                                     data-price="${b.formattedPrice}"
                                     data-desc="${fn:escapeXml(b.description)}"
                                     data-image="${pageContext.request.contextPath}/book-image?bookId=${b.bookId}">
                                <div class="card-image-wrap">
                                    <div class="card-badges">
                                        <span class="badge-cat">${b.categoryName}</span>
                                        <c:if test="${b.stockQuantity <= 0}">
                                            <span class="badge-stock-out">Hết hàng</span>
                                        </c:if>
                                    </div>
                                    <img src="${pageContext.request.contextPath}/book-image?bookId=${b.bookId}" alt="${fn:escapeXml(b.title)}" loading="lazy">
                                    <div class="card-quick-actions">
                                        <button type="button" class="action-btn-quick btn-quick-view" aria-label="Xem nhanh">
                                            <span>Xem Nhanh</span>
                                        </button>
                                    </div>
                                </div>
                                <div class="card-body">
                                    <h3 class="card-title" title="${fn:escapeXml(b.title)}">${b.title}</h3>
                                    <p class="card-author">
                                        Tác giả: <strong>${b.author}</strong>
                                    </p>
                                    <div class="card-rating">
                                        <span>Đánh giá: 4.9</span>
                                        <span class="review-count">(${b.stockQuantity + 20} lượt mua)</span>
                                    </div>
                                    <div class="card-footer-price">
                                        <div class="price-box">
                                            <span class="current-price">${b.formattedPrice}</span>
                                        </div>
                                        <button type="button" class="btn-add-cart-mini" title="Thêm vào giỏ" aria-label="Thêm vào giỏ">
                                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><path d="M6 2L3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4z"></path><line x1="3" y1="6" x2="21" y2="6"></line><path d="M16 10a4 4 0 0 1-8 0"></path></svg>
                                        </button>
                                    </div>
                                </div>
                            </article>
                        </c:forEach>
                    </div>

                    <!-- Pagination -->
                    <c:if test="${totalPages > 1}">
                        <nav class="pagination-wrap" aria-label="Phân trang sách">
                            <!-- Previous button -->
                            <a href="${pageContext.request.contextPath}/home?page=${currentPage - 1}&sort=${selectedSort}${not empty selectedCategoryId ? '&categoryId=' : ''}${selectedCategoryId}${not empty keyword ? '&keyword=' : ''}${fn:escapeXml(keyword)}"
                               class="page-btn ${currentPage <= 1 ? 'disabled' : ''}" aria-label="Trang trước">
                                ❮
                            </a>

                            <!-- Page numbers -->
                            <c:forEach begin="1" end="${totalPages}" var="p">
                                <a href="${pageContext.request.contextPath}/home?page=${p}&sort=${selectedSort}${not empty selectedCategoryId ? '&categoryId=' : ''}${selectedCategoryId}${not empty keyword ? '&keyword=' : ''}${fn:escapeXml(keyword)}"
                                   class="page-btn ${p == currentPage ? 'active' : ''}">
                                    ${p}
                                </a>
                            </c:forEach>

                            <!-- Next button -->
                            <a href="${pageContext.request.contextPath}/home?page=${currentPage + 1}&sort=${selectedSort}${not empty selectedCategoryId ? '&categoryId=' : ''}${selectedCategoryId}${not empty keyword ? '&keyword=' : ''}${fn:escapeXml(keyword)}"
                               class="page-btn ${currentPage >= totalPages ? 'disabled' : ''}" aria-label="Trang sau">
                                ❯
                            </a>
                        </nav>
                    </c:if>
                </c:when>

                <c:otherwise>
                    <!-- Empty State -->
                    <div class="empty-state">
                        <div class="empty-icon">
                            <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                                <circle cx="11" cy="11" r="8"></circle>
                                <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
                                <line x1="8" y1="11" x2="14" y2="11"></line>
                            </svg>
                        </div>
                        <h3>Không tìm thấy cuốn sách nào phù hợp</h3>
                        <p>Hãy thử tìm kiếm với từ khóa khác hoặc xóa bớt các tiêu chí lọc.</p>
                        <a href="${pageContext.request.contextPath}/home" class="btn-primary" style="display:inline-flex;">Xem Toàn Bộ Sách</a>
                    </div>
                </c:otherwise>
            </c:choose>
        </main>

        <!-- 8. PROMOTIONAL / NEWSLETTER SECTION -->
        <section class="container" id="newsletter">
            <div class="newsletter-section">
                <div class="newsletter-content">
                    <div class="newsletter-text">
                        <h3>Đăng Ký Nhận Bản Tin Tri Thức</h3>
                        <p>Nhận ngay mã giảm giá <strong>15%</strong> cho đơn hàng đầu tiên cùng những gợi ý sách hay tuyển chọn hàng tuần.</p>
                    </div>
                    <form class="newsletter-form" onsubmit="event.preventDefault(); showToast('Đăng ký thành công!', 'Cảm ơn bạn đã đăng ký nhận bản tin!');">
                        <input type="email" class="newsletter-input" placeholder="Nhập địa chỉ email của bạn..." required>
                        <button type="submit" class="newsletter-btn">Đăng Ký</button>
                    </form>
                </div>
            </div>
        </section>

        <!-- FOOTER COMPONENT -->
        <jsp:include page="/components/footer/footer.jsp" />

        <!-- 10. QUICK VIEW MODAL -->
        <div class="modal-overlay" id="quickViewModal" role="dialog" aria-modal="true" aria-labelledby="modalBookTitle">
            <div class="modal-container">
                <button type="button" class="modal-close-btn" id="modalCloseBtn" aria-label="Đóng cửa sổ">✕</button>
                <div class="modal-content-grid">
                    <div class="modal-image-box">
                        <img id="modalBookImg" src="" alt="Book Cover">
                    </div>
                    <div class="modal-details">
                        <span class="badge-cat" id="modalBookCategory" style="display:inline-block; margin-bottom:8px;">Thể Loại</span>
                        <h3 id="modalBookTitle">Tên Cuốn Sách</h3>
                        <div class="modal-meta">
                            <span>Tác giả: <strong id="modalBookAuthor">...</strong></span>
                            <span>NXB: <strong id="modalBookPublisher">...</strong></span>
                            <span>Năm XB: <strong id="modalBookYear">...</strong></span>
                            <span>ISBN: <strong id="modalBookIsbn">...</strong></span>
                        </div>
                        <div class="modal-price-box" id="modalBookPrice">0 đ</div>
                        <div class="modal-description" id="modalBookDesc">
                            Mô tả chi tiết cuốn sách...
                        </div>
                        <div class="modal-actions">
                            <div class="quantity-control">
                                <button type="button" class="qty-btn" id="qtyMinusBtn">-</button>
                                <input type="number" id="modalQtyInput" class="qty-input" value="1" min="1" max="99" readonly>
                                <button type="button" class="qty-btn" id="qtyPlusBtn">+</button>
                            </div>
                            <button type="button" class="btn-primary" id="modalAddToCartBtn" style="padding: 10px 24px;">
                                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><path d="M6 2L3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4z"></path><line x1="3" y1="6" x2="21" y2="6"></line><path d="M16 10a4 4 0 0 1-8 0"></path></svg>
                                <span>Thêm Vào Giỏ</span>
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- 11. TOAST NOTIFICATION CONTAINER -->
        <div class="toast-container" id="toastContainer" aria-live="polite"></div>

        <!-- 12. SCRIPTS -->
        <script src="${pageContext.request.contextPath}/components/header/header.js"></script>
        <script src="${pageContext.request.contextPath}/js/homepage.js"></script>
        <script src="${pageContext.request.contextPath}/components/footer/footer.js"></script>
    </body>
</html>
