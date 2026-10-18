document.addEventListener('DOMContentLoaded', () => {
    initFooterYear();
    initFooterSmoothScroll();
});

function initFooterYear() {
    const yearSpan = document.getElementById('footerCurrentYear');
    if (yearSpan) {
        yearSpan.textContent = new Date().getFullYear();
    }
}

function initFooterSmoothScroll() {
    const footerLinks = document.querySelectorAll('.footer-links a[href^="#"], .footer-col a[href^="#"]');
    footerLinks.forEach(link => {
        link.addEventListener('click', (e) => {
            const targetId = link.getAttribute('href');
            if (targetId && targetId !== '#') {
                const targetEl = document.querySelector(targetId);
                if (targetEl) {
                    e.preventDefault();
                    targetEl.scrollIntoView({ behavior: 'smooth' });
                }
            }
        });
    });
}
