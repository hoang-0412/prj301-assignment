/**
 * USER PROFILE JAVASCRIPT (profile.js)
 * Manages profile tabs, alert dismissal, and order cancellation confirmation.
 */

document.addEventListener('DOMContentLoaded', () => {
    const navBtns = document.querySelectorAll('.prf-nav-btn[data-tab]');
    const panels = document.querySelectorAll('.prf-panel');

    navBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            const tab = btn.dataset.tab;
            navBtns.forEach(b => b.classList.remove('active'));
            btn.classList.add('active');

            panels.forEach(p => {
                if (p.id === 'prf-panel-' + tab) {
                    p.classList.add('active');
                } else {
                    p.classList.remove('active');
                }
            });

            const url = new URL(window.location.href);
            url.searchParams.set('tab', tab);
            window.history.replaceState({}, '', url.toString());
        });
    });

    document.querySelectorAll('.js-prf-alert-close').forEach(btn => {
        btn.addEventListener('click', () => {
            const alertEl = btn.closest('.prf-alert');
            if (alertEl) alertEl.remove();
        });
    });

    document.querySelectorAll('.js-prf-cancel-order').forEach(form => {
        form.addEventListener('submit', (e) => {
            const orderId = form.dataset.orderId || '';
            if (!window.confirm('Bạn có chắc chắn muốn hủy đơn hàng #' + orderId + '?')) {
                e.preventDefault();
            }
        });
    });
});

