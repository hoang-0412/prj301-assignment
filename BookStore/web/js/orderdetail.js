/**
 * ORDER DETAIL JAVASCRIPT (orderdetail.js)
 * Handles invoice printing, alert dismissal, and order cancellation confirmation.
 */

document.addEventListener('DOMContentLoaded', () => {
    const printBtn = document.getElementById('btnPrintInvoice');
    if (printBtn) {
        printBtn.addEventListener('click', () => {
            window.print();
        });
    }

    document.querySelectorAll('.js-od-alert-close').forEach(btn => {
        btn.addEventListener('click', () => {
            const alertEl = btn.closest('.od-alert');
            if (alertEl) alertEl.remove();
        });
    });

    document.querySelectorAll('.js-cancel-order-form').forEach(form => {
        form.addEventListener('submit', (e) => {
            const orderId = form.dataset.orderId || '';
            if (!window.confirm('Bạn có chắc chắn muốn hủy đơn hàng #' + orderId + '?')) {
                e.preventDefault();
            }
        });
    });
});

