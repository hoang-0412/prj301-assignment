/**
 * Dedicated Homepage JavaScript (homepage.js)
 * Manages search, sorting, quick-view modal, and add-to-cart notifications
 */

document.addEventListener('DOMContentLoaded', () => {
    initSearchBox();
    initSortDropdown();
    initQuickViewModal();
    initAddToCart();
    initQuantityControls();
});

/* Search Box functionality */
function initSearchBox() {
    const searchInput = document.getElementById('searchInput');
    const clearBtn = document.getElementById('searchClearBtn');

    if (!searchInput || !clearBtn) return;

    function toggleClearBtn() {
        if (searchInput.value.trim().length > 0) {
            clearBtn.style.display = 'block';
        } else {
            clearBtn.style.display = 'none';
        }
    }

    searchInput.addEventListener('input', toggleClearBtn);
    toggleClearBtn();

    clearBtn.addEventListener('click', () => {
        searchInput.value = '';
        toggleClearBtn();
        searchInput.focus();
    });
}

/* Sort dropdown auto reload */
function initSortDropdown() {
    const sortSelect = document.getElementById('sortSelect');
    if (!sortSelect) return;

    sortSelect.addEventListener('change', () => {
        const url = new URL(window.location.href);
        url.searchParams.set('sort', sortSelect.value);
        url.searchParams.set('page', '1');
        window.location.href = url.toString();
    });
}

/* Quick View Modal */
function initQuickViewModal() {
    const modalOverlay = document.getElementById('quickViewModal');
    const closeBtn = document.getElementById('modalCloseBtn');
    if (!modalOverlay || !closeBtn) return;

    // Open modal on click
    document.querySelectorAll('.btn-quick-view').forEach(btn => {
        btn.addEventListener('click', (e) => {
            e.preventDefault();
            const card = btn.closest('.book-card');
            if (!card) return;

            const id = card.dataset.id;
            const title = card.dataset.title || 'Chi tiết sách';
            const author = card.dataset.author || 'Đang cập nhật';
            const category = card.dataset.category || 'Sách';
            const publisher = card.dataset.publisher || 'N/A';
            const year = card.dataset.year || 'N/A';
            const isbn = card.dataset.isbn || 'N/A';
            const price = card.dataset.price || '0 đ';
            const desc = card.dataset.desc || 'Chưa có thông tin mô tả chi tiết cho cuốn sách này.';
            const image = card.dataset.image || ('book-image?bookId=' + id);

            document.getElementById('modalBookTitle').textContent = title;
            document.getElementById('modalBookAuthor').textContent = author;
            document.getElementById('modalBookCategory').textContent = category;
            document.getElementById('modalBookPublisher').textContent = publisher;
            document.getElementById('modalBookYear').textContent = year;
            document.getElementById('modalBookIsbn').textContent = isbn;
            document.getElementById('modalBookPrice').textContent = price;
            document.getElementById('modalBookDesc').textContent = desc;
            document.getElementById('modalBookImg').src = image;
            document.getElementById('modalBookImg').alt = title;

            const modalAddCartBtn = document.getElementById('modalAddToCartBtn');
            if (modalAddCartBtn) {
                modalAddCartBtn.dataset.id = id;
                modalAddCartBtn.dataset.title = title;
            }

            // Reset quantity to 1
            const qtyInput = document.getElementById('modalQtyInput');
            if (qtyInput) qtyInput.value = '1';

            modalOverlay.classList.add('active');
            document.body.style.overflow = 'hidden';
        });
    });

    // Close modal
    function closeModal() {
        modalOverlay.classList.remove('active');
        document.body.style.overflow = '';
    }

    closeBtn.addEventListener('click', closeModal);
    modalOverlay.addEventListener('click', (e) => {
        if (e.target === modalOverlay) closeModal();
    });

    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape' && modalOverlay.classList.contains('active')) {
            closeModal();
        }
    });
}

/* Add to Cart Simulation & Toast */
function initAddToCart() {
    const cartBadge = document.getElementById('navCartBadge');

    function handleAddToCart(bookTitle) {
        if (cartBadge) {
            let count = parseInt(cartBadge.textContent.trim()) || 0;
            count += 1;
            cartBadge.textContent = count;
            cartBadge.style.display = 'flex';
            cartBadge.animate([
                { transform: 'scale(1)' },
                { transform: 'scale(1.4)' },
                { transform: 'scale(1)' }
            ], { duration: 300 });
        }

        showToast('Đã thêm vào giỏ hàng!', bookTitle);
    }

    // Mini buttons on cards
    document.querySelectorAll('.btn-add-cart-mini').forEach(btn => {
        btn.addEventListener('click', (e) => {
            e.preventDefault();
            const card = btn.closest('.book-card');
            const title = card ? card.dataset.title : 'Cuốn sách';
            handleAddToCart(title);
        });
    });

    // Modal Add To Cart button
    const modalAddCartBtn = document.getElementById('modalAddToCartBtn');
    if (modalAddCartBtn) {
        modalAddCartBtn.addEventListener('click', () => {
            const title = modalAddCartBtn.dataset.title || 'Cuốn sách';
            handleAddToCart(title);
            
            const modalOverlay = document.getElementById('quickViewModal');
            if (modalOverlay) {
                modalOverlay.classList.remove('active');
                document.body.style.overflow = '';
            }
        });
    }
}

/* Toast Message Notification */
function showToast(title, message) {
    let container = document.getElementById('toastContainer');
    if (!container) {
        container = document.createElement('div');
        container.id = 'toastContainer';
        container.className = 'toast-container';
        document.body.appendChild(container);
    }

    const toast = document.createElement('div');
    toast.className = 'toast';
    toast.innerHTML = `
        <div class="toast-icon">✓</div>
        <div class="toast-msg">
            <h6>${escapeHtml(title)}</h6>
            <p>${escapeHtml(message)}</p>
        </div>
    `;

    container.appendChild(toast);

    setTimeout(() => toast.classList.add('show'), 10);

    setTimeout(() => {
        toast.classList.remove('show');
        setTimeout(() => toast.remove(), 300);
    }, 3200);
}

/* Quantity controls in modal */
function initQuantityControls() {
    const minusBtn = document.getElementById('qtyMinusBtn');
    const plusBtn = document.getElementById('qtyPlusBtn');
    const qtyInput = document.getElementById('modalQtyInput');

    if (!minusBtn || !plusBtn || !qtyInput) return;

    minusBtn.addEventListener('click', () => {
        let val = parseInt(qtyInput.value) || 1;
        if (val > 1) qtyInput.value = val - 1;
    });

    plusBtn.addEventListener('click', () => {
        let val = parseInt(qtyInput.value) || 1;
        if (val < 99) qtyInput.value = val + 1;
    });
}

function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/&/g, '&amp;')
              .replace(/</g, '&lt;')
              .replace(/>/g, '&gt;')
              .replace(/"/g, '&quot;')
              .replace(/'/g, '&#039;');
}
