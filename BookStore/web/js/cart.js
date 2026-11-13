/**
 * SHOPPING CART JAVASCRIPT (cart.js)
 * Handles alert dismissal and clear-cart confirmation.
 */

document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('.js-cart-alert-close').forEach(btn => {
        btn.addEventListener('click', () => {
            const alertEl = btn.closest('.cart-alert');
            if (alertEl) alertEl.remove();
        });
    });

    const clearCartForm = document.getElementById('clearCartForm');
    if (clearCartForm) {
        clearCartForm.addEventListener('submit', (e) => {
            if (!window.confirm('Bạn muốn xóa toàn bộ sản phẩm trong giỏ hàng?')) {
                e.preventDefault();
            }
        });
    }
});

