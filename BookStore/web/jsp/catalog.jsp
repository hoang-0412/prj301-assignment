<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Tủ Sách & Bộ Lọc Nâng Cao - Tiệm sách Hoàng Trịnh</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/homepage.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/components/header/header.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/components/footer/footer.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/catalog.css">
    </head>
    <body data-context-path="${pageContext.request.contextPath}">

        <!-- HEADER -->
        <jsp:include page="/components/header/header.jsp" />

        <!-- CATALOG HERO BANNER -->
        <section class="cat-hero">
            <div class="container cat-hero-inner">
                <div>
                    <span class="cat-hero-badge">KHO SÁCH BẢN QUYỀN & CHO THUÊ</span>
                    <h1>Khám Phá Tủ Sách Hoàng Trịnh</h1>
                    <p>Tìm kiếm chuyên sâu theo thể loại, khoảng giá và hình thức sách Mua hoặc Thuê</p>
                </div>

                <!-- Status Quick Tabs (Bán / Cho thuê / Đã thuê) -->
                <div class="cat-status-pills">
                    <a href="${pageContext.request.contextPath}/catalog"
                       class="cat-status-pill ${empty selectedStatus ? 'active' : ''}">
                        <span>Tất cả sách</span>
                        <strong>${allCount}</strong>
                    </a>
                    <a href="${pageContext.request.contextPath}/catalog?status=B%C3%A1n"
                       class="cat-status-pill ${selectedStatus == 'Bán' ? 'active' : ''}">
                        <span>● Sách Bán</span>
                        <strong>${saleCount}</strong>
                    </a>
                    <a href="${pageContext.request.contextPath}/catalog?status=Cho+thu%C3%AA"
                       class="cat-status-pill ${selectedStatus == 'Cho thuê' ? 'active' : ''}">
                        <span>● Sách Cho Thuê</span>
                        <strong>${rentCount}</strong>
                    </a>
                    <a href="${pageContext.request.contextPath}/catalog?status=%C4%90%C3%A3+thu%C3%AA"
                       class="cat-status-pill ${selectedStatus == 'Đã thuê' ? 'active' : ''}">
                        <span>● Đã Thuê</span>
                        <strong>${rentedCount}</strong>
                    </a>
                </div>
            </div>
        </section>

        <main class="container cat-main-layout">

            <!-- LEFT SIDEBAR FILTER -->
            <aside class="cat-sidebar">
                <form action="${pageContext.request.contextPath}/catalog" method="GET" class="cat-filter-card">
                    <div class="cat-filter-head">
                        <h3>Bộ Lọc Tìm Kiếm</h3>
                        <a href="${pageContext.request.contextPath}/catalog" class="cat-reset-link">Đặt lại</a>
                    </div>

                    <!-- 1. Keyword -->
                    <div class="cat-filter-group">
                        <label for="catKeyword">Từ khóa</label>
                        <input type="text" name="keyword" id="catKeyword" class="cat-input"
                               placeholder="Tên sách, tác giả, NXB..."
                               value="${fn:escapeXml(keyword)}">
                    </div>

                    <!-- 2. Book Status (Mua / Thuê) -->
                    <div class="cat-filter-group">
                        <label for="catStatusSelect">Hình thức sách</label>
                        <select name="status" id="catStatusSelect" class="cat-select">
                            <option value="">-- Tất cả hình thức --</option>
                            <option value="Bán" ${selectedStatus == 'Bán' ? 'selected' : ''}>Sách Bán (${saleCount})</option>
                            <option value="Cho thuê" ${selectedStatus == 'Cho thuê' ? 'selected' : ''}>Sách Cho thuê (${rentCount})</option>
                            <option value="Đã thuê" ${selectedStatus == 'Đã thuê' ? 'selected' : ''}>Sách Đã thuê (${rentedCount})</option>
                        </select>
                    </div>

                    <!-- 3. Category -->
                    <div class="cat-filter-group">
                        <label>Thể loại sách</label>
                        <div class="cat-radio-list">
                            <label class="cat-radio-item">
                                <input type="radio" name="categoryId" value="" ${empty selectedCategoryId ? 'checked' : ''}>
                                <span>Tất cả thể loại</span>
                                <small>${allCount}</small>
                            </label>
                            <c:forEach items="${categories}" var="c">
                                <label class="cat-radio-item">
                                    <input type="radio" name="categoryId" value="${c.categoryId}" ${selectedCategoryId == c.categoryId ? 'checked' : ''}>
                                    <span>${fn:escapeXml(c.categoryName)}</span>
                                    <small>${c.bookCount}</small>
                                </label>
                            </c:forEach>
                        </div>
                    </div>

                    <!-- 4. Price Range -->
                    <div class="cat-filter-group">
                        <label>Khoảng giá (VNĐ)</label>
                        <div class="cat-price-inputs">
                            <input type="number" name="minPrice" id="catMinPrice" class="cat-input"
                                   placeholder="Từ (0)" min="0" step="10000" value="${minPrice}">
                            <span>—</span>
                            <input type="number" name="maxPrice" id="catMaxPrice" class="cat-input"
                                   placeholder="Đến" min="0" step="10000" value="${maxPrice}">
                        </div>
                        <div class="cat-price-presets">
                            <button type="button" class="cat-preset-btn" data-min="0" data-max="100000">&lt; 100k</button>
                            <button type="button" class="cat-preset-btn" data-min="100000" data-max="250000">100k - 250k</button>
                            <button type="button" class="cat-preset-btn" data-min="250000" data-max="500000">250k - 500k</button>
                            <button type="button" class="cat-preset-btn" data-min="500000" data-max="">&gt; 500k</button>
                        </div>
                    </div>

                    <!-- 5. Stock Availability -->
                    <div class="cat-filter-group">
                        <label class="cat-checkbox-item">
                            <input type="checkbox" name="inStock" value="true" ${inStockOnly ? 'checked' : ''}>
                            <span>Chỉ hiển thị sách còn hàng</span>
                        </label>
                    </div>

                    <!-- 6. Sort -->
                    <div class="cat-filter-group">
                        <label for="catSortSelect">Sắp xếp theo</label>
                        <select name="sort" id="catSortSelect" class="cat-select">
                            <option value="newest" ${selectedSort == 'newest' ? 'selected' : ''}>Mới nhất</option>
                            <option value="price-asc" ${selectedSort == 'price-asc' ? 'selected' : ''}>Giá: Thấp đến Cao</option>
                            <option value="price-desc" ${selectedSort == 'price-desc' ? 'selected' : ''}>Giá: Cao đến Thấp</option>
                            <option value="title-asc" ${selectedSort == 'title-asc' ? 'selected' : ''}>Tên sách: A - Z</option>
                            <option value="oldest" ${selectedSort == 'oldest' ? 'selected' : ''}>Cũ nhất</option>
                        </select>
                    </div>

                    <button type="submit" class="cat-submit-btn">Áp Dụng Bộ Lọc</button>
                </form>
            </aside>

            <!-- RIGHT BOOKS RESULTS -->
            <section class="cat-results-col">
                <div class="cat-results-toolbar">
                    <div class="cat-results-count">
                        Tìm thấy <strong>${totalBooks}</strong> đầu sách phù hợp
                        <c:if test="${not empty selectedStatus}">
                            • Hình thức: <span class="cat-active-tag">${fn:escapeXml(selectedStatus)}</span>
                        </c:if>
                        <c:if test="${not empty keyword}">
                            • Từ khóa: <span class="cat-active-tag">"${fn:escapeXml(keyword)}"</span>
                        </c:if>
                    </div>
                </div>

                <c:choose>
                    <c:when test="${not empty books}">
                        <div class="cat-books-grid">
                            <c:forEach items="${books}" var="b">
                                <article class="book-card"
                                         data-id="${b.bookId}"
                                         data-title="${fn:escapeXml(b.title)}">
                                    <div class="card-image-wrap">
                                        <div class="card-badges">
                                            <span class="badge-cat">${fn:escapeXml(b.categoryName)}</span>
                                            <span class="cat-book-status ${b.bookStatus == 'Bán' ? 'sale' : (b.bookStatus == 'Cho thuê' ? 'rent' : 'rented')}">
                                                ${fn:escapeXml(b.bookStatus)}
                                            </span>
                                        </div>
                                        <a href="${pageContext.request.contextPath}/book-detail?id=${b.bookId}">
                                            <img src="${pageContext.request.contextPath}/book-image?bookId=${b.bookId}"
                                                 alt="${fn:escapeXml(b.title)}" loading="lazy">
                                        </a>
                                    </div>
                                    <div class="card-body">
                                        <h3 class="card-title" title="${fn:escapeXml(b.title)}">
                                            <a href="${pageContext.request.contextPath}/book-detail?id=${b.bookId}" style="color:inherit; text-decoration:none;">
                                                ${fn:escapeXml(b.title)}
                                            </a>
                                        </h3>
                                        <p class="card-author">Tác giả: <strong>${fn:escapeXml(b.author)}</strong></p>
                                        <div class="card-rating">
                                            <span>Kho: <strong>${b.stockQuantity} cuốn</strong></span>
                                            <span class="review-count">• NXB ${fn:escapeXml(b.publisher)}</span>
                                        </div>
                                        <div class="card-footer-price">
                                            <div class="price-box">
                                                <span class="current-price">${b.formattedPrice}</span>
                                            </div>
                                            <div style="display:flex; gap:6px;">
                                                <button type="button" class="btn-add-cart-mini js-cat-add-cart"
                                                        data-id="${b.bookId}"
                                                        data-title="${fn:escapeXml(b.title)}"
                                                        title="Thêm vào giỏ">
                                                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><path d="M6 2L3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4z"/><line x1="3" y1="6" x2="21" y2="6"/><path d="M16 10a4 4 0 0 1-8 0"/></svg>
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                </article>
                            </c:forEach>
                        </div>

                        <!-- Pagination -->
                        <c:if test="${totalPages > 1}">
                            <nav class="pagination-wrap" style="margin-top:32px;">
                                <c:forEach begin="1" end="${totalPages}" var="p">
                                    <a href="${pageContext.request.contextPath}/catalog?page=${p}&sort=${selectedSort}&status=${fn:escapeXml(selectedStatus)}&categoryId=${selectedCategoryId}&keyword=${fn:escapeXml(keyword)}&minPrice=${minPrice}&maxPrice=${maxPrice}&inStock=${inStockOnly}"
                                       class="page-btn ${p == currentPage ? 'active' : ''}">
                                        ${p}
                                    </a>
                                </c:forEach>
                            </nav>
                        </c:if>
                    </c:when>

                    <c:otherwise>
                        <div class="empty-state">
                            <h3>Không tìm thấy cuốn sách nào khớp với bộ lọc</h3>
                            <p>Hãy thử nới rộng khoảng giá hoặc đặt lại các tiêu chí tìm kiếm.</p>
                            <a href="${pageContext.request.contextPath}/catalog" class="btn-primary" style="display:inline-flex; margin-top:12px;">
                                Xem Tất Cả Sách
                            </a>
                        </div>
                    </c:otherwise>
                </c:choose>
            </section>
        </main>

        <div class="toast-container" id="toastContainer" aria-live="polite"></div>

        <!-- FOOTER -->
        <jsp:include page="/components/footer/footer.jsp" />

        <script src="${pageContext.request.contextPath}/components/header/header.js"></script>
        <script src="${pageContext.request.contextPath}/components/footer/footer.js"></script>
        <script>
            document.addEventListener('DOMContentLoaded', () => {
                const ctx = document.body.dataset.contextPath || '';
                const minInput = document.getElementById('catMinPrice');
                const maxInput = document.getElementById('catMaxPrice');

                document.querySelectorAll('.cat-preset-btn').forEach(btn => {
                    btn.addEventListener('click', () => {
                        if (minInput) minInput.value = btn.dataset.min || '';
                        if (maxInput) maxInput.value = btn.dataset.max || '';
                    });
                });

                document.querySelectorAll('.js-cat-add-cart').forEach(btn => {
                    btn.addEventListener('click', () => {
                        const bookId = btn.dataset.id;
                        const params = new URLSearchParams();
                        params.append('action', 'add');
                        params.append('bookId', bookId);
                        params.append('quantity', '1');
                        params.append('ajax', 'true');

                        fetch(ctx + '/cart', {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
                                'X-Requested-With': 'XMLHttpRequest'
                            },
                            body: params.toString()
                        })
                        .then(r => r.json())
                        .then(data => {
                            const badge = document.getElementById('navCartBadge');
                            if (badge && typeof data.cartCount === 'number') {
                                badge.textContent = data.cartCount;
                                badge.style.display = data.cartCount > 0 ? 'flex' : 'none';
                            }
                            const container = document.getElementById('toastContainer');
                            if (container) {
                                const t = document.createElement('div');
                                t.className = 'toast show';
                                t.innerHTML = '<div class="toast-icon">✓</div><div class="toast-msg"><h6>Giỏ hàng</h6><p>' + (data.message || '') + '</p></div>';
                                container.appendChild(t);
                                setTimeout(() => t.remove(), 3000);
                            }
                        });
                    });
                });
            });
        </script>
    </body>
</html>

