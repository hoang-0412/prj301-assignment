/**
 * BOOK DETAIL JAVASCRIPT (bookdetail.js)
 * Manages quantity controls, AJAX add-to-cart, and toast notifications.
 */

document.addEventListener('DOMContentLoaded', () => {
    const qtyInput = document.getElementById('bdQtyInput');
    const minusBtn = document.getElementById('bdQtyMinus');
    const plusBtn = document.getElementById('bdQtyPlus');
    const addAjaxBtn = document.getElementById('bdAddToCartAjaxBtn');

    if (qtyInput && minusBtn && plusBtn) {
        const maxStock = parseInt(qtyInput.max, 10) || 99;
        minusBtn.addEventListener('click', () => {
            let v = parseInt(qtyInput.value, 10) || 1;
            if (v > 1) qtyInput.value = v - 1;
        });
        plusBtn.addEventListener('click', () => {
            let v = parseInt(qtyInput.value, 10) || 1;
            if (v < maxStock) qtyInput.value = v + 1;
        });
    }

    if (addAjaxBtn) {
        addAjaxBtn.addEventListener('click', () => {
            const bookId = addAjaxBtn.dataset.id;
            const ctx = addAjaxBtn.dataset.context || '';
            const qty = qtyInput ? (parseInt(qtyInput.value, 10) || 1) : 1;

            const params = new URLSearchParams();
            params.append('action', 'add');
            params.append('bookId', bookId);
            params.append('quantity', String(qty));
            params.append('ajax', 'true');

            fetch(ctx + '/cart', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
                    'X-Requested-With': 'XMLHttpRequest'
                },
                body: params.toString()
            })
            .then(res => res.json())
            .then(data => {
                const badge = document.getElementById('navCartBadge');
                if (badge && typeof data.cartCount === 'number') {
                    badge.textContent = data.cartCount;
                    badge.classList.toggle('is-hidden', data.cartCount <= 0);
                    badge.style.display = data.cartCount > 0 ? 'flex' : 'none';
                }
                showDetailToast(data.success ? 'Đã thêm vào giỏ hàng!' : 'Thông báo', data.message || '');
            });
        });
    }

    function showDetailToast(title, message) {
        const container = document.getElementById('toastContainer');
        if (!container) return;
        const toast = document.createElement('div');
        toast.className = 'toast show';
        toast.innerHTML = '<div class="toast-icon">✓</div><div class="toast-msg"><h6>' + title + '</h6><p>' + message + '</p></div>';
        container.appendChild(toast);
        setTimeout(() => {
            toast.classList.remove('show');
            setTimeout(() => toast.remove(), 300);
        }, 3000);
    }
});

