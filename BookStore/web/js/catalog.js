/**
 * CATALOG PAGE JAVASCRIPT (catalog.js)
 * Handles price preset buttons and AJAX add-to-cart actions.
 */

document.addEventListener('DOMContentLoaded', () => {
    const ctx = document.body.dataset.contextPath || '';
    const minInput = document.getElementById('catMinPrice');
    const maxInput = document.getElementById('catMaxPrice');
    const presetBtns = document.querySelectorAll('.cat-preset-btn');
    const filterForm = document.querySelector('.cat-filter-card');
    const sortSelect = document.getElementById('catSortSelect');
    const statusSelect = document.getElementById('catStatusSelect');

    function syncActivePreset() {
        const curMin = minInput ? minInput.value.trim() : '';
        const curMax = maxInput ? maxInput.value.trim() : '';
        presetBtns.forEach(btn => {
            const bMin = btn.dataset.min || '';
            const bMax = btn.dataset.max || '';
            btn.classList.toggle('active', curMin === bMin && curMax === bMax && (curMin !== '' || curMax !== ''));
        });
    }

    presetBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            if (minInput) minInput.value = btn.dataset.min || '';
            if (maxInput) maxInput.value = btn.dataset.max || '';
            syncActivePreset();
        });
    });

    if (minInput) minInput.addEventListener('input', syncActivePreset);
    if (maxInput) maxInput.addEventListener('input', syncActivePreset);
    syncActivePreset();

    if (filterForm && sortSelect) {
        sortSelect.addEventListener('change', () => filterForm.submit());
    }
    if (filterForm && statusSelect) {
        statusSelect.addEventListener('change', () => filterForm.submit());
    }

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
                    badge.classList.toggle('is-hidden', data.cartCount <= 0);
                    badge.style.display = data.cartCount > 0 ? 'flex' : 'none';
                }
                const container = document.getElementById('toastContainer');
                if (container) {
                    const t = document.createElement('div');
                    t.className = 'toast show';
                    t.innerHTML = '<div class="toast-icon">✓</div><div class="toast-msg"><h6>Đã thêm vào giỏ hàng!</h6><p>' + (data.message || '') + '</p></div>';
                    container.appendChild(t);
                    setTimeout(() => t.remove(), 3000);
                }
            });
        });
    });
});

