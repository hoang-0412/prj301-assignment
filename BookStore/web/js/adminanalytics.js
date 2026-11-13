/**
 * ADMIN ANALYTICS JAVASCRIPT (adminanalytics.js)
 * Handles report printing and dynamic progress bar widths.
 */

document.addEventListener('DOMContentLoaded', () => {
    const printBtn = document.getElementById('btnPrintAnalytics');
    if (printBtn) {
        printBtn.addEventListener('click', () => {
            window.print();
        });
    }

    document.querySelectorAll('.ana-progress-fill[data-width]').forEach(bar => {
        const w = parseFloat(bar.dataset.width || '0');
        bar.style.width = (w > 0 ? w : 0) + '%';
    });
});

