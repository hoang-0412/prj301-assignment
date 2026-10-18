/**
 * FOOTER COMPONENT JAVASCRIPT (footer.js)
 * Manages dynamic copyright year, smooth link navigation, and footer interactions.
 */

document.addEventListener('DOMContentLoaded', () => {
    initFooterYear();
    initFooterSmoothScroll();
});

/* Automatically keep copyright year updated */
function initFooterYear() {
    const yearSpan = document.getElementById('footerCurrentYear');
    if (yearSpan) {
        yearSpan.textContent = new Date().getFullYear();
    }
}

/* Smooth scrolling for in-page footer links (e.g., #about, #contact) */
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
