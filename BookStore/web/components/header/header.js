/**
 * HEADER COMPONENT JAVASCRIPT (header.js)
 * Manages search popup modal, clear button, scroll effect, and nav active state.
 */

document.addEventListener('DOMContentLoaded', () => {
    initSearchPopup();
    initHeaderSearch();
    initHeaderScrollEffect();
    initHeaderActiveNav();
});

/* Search Popup Modal toggle logic */
function initSearchPopup() {
    const toggleBtn = document.getElementById('searchToggleBtn');
    const overlay = document.getElementById('searchPopupOverlay');
    const closeBtn = document.getElementById('searchPopupCloseBtn');
    const searchInput = document.getElementById('searchInput');

    if (!toggleBtn || !overlay) return;

    function openSearchPopup() {
        overlay.classList.add('active');
        document.body.style.overflow = 'hidden';
        setTimeout(() => {
            if (searchInput) {
                searchInput.focus();
                // Select all text if there is an existing keyword
                searchInput.select();
            }
        }, 120);
    }

    function closeSearchPopup() {
        overlay.classList.remove('active');
        document.body.style.overflow = '';
    }

    toggleBtn.addEventListener('click', (e) => {
        e.preventDefault();
        openSearchPopup();
    });

    if (closeBtn) {
        closeBtn.addEventListener('click', closeSearchPopup);
    }

    overlay.addEventListener('click', (e) => {
        if (e.target === overlay) {
            closeSearchPopup();
        }
    });

    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape' && overlay.classList.contains('active')) {
            closeSearchPopup();
        }
        // Pressing '/' opens the search popup when not already in an input/textarea
        if (e.key === '/' && !overlay.classList.contains('active')) {
            const activeTag = document.activeElement ? document.activeElement.tagName.toLowerCase() : '';
            if (activeTag !== 'input' && activeTag !== 'textarea') {
                e.preventDefault();
                openSearchPopup();
            }
        }
    });
}

/* Search input clear button and keyboard behavior */
function initHeaderSearch() {
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

/* Add shadow and glass effect when scrolling */
function initHeaderScrollEffect() {
    const header = document.getElementById('mainHeader');
    if (!header) return;

    const handleScroll = () => {
        if (window.scrollY > 20) {
            header.classList.add('is-scrolled');
        } else {
            header.classList.remove('is-scrolled');
        }
    };

    window.addEventListener('scroll', handleScroll, { passive: true });
    handleScroll();
}

/* Highlight current active nav item based on URL or hash */
function initHeaderActiveNav() {
    const navLinks = document.querySelectorAll('.nav-task-links .nav-link, .nav-sub-menu .nav-link');
    if (!navLinks || navLinks.length === 0) return;

    const currentHash = window.location.hash;

    navLinks.forEach(link => {
        const href = link.getAttribute('href');
        if (!href) return;

        // If URL has hash and link matches hash
        if (currentHash && href.includes(currentHash)) {
            navLinks.forEach(l => l.classList.remove('active'));
            link.classList.add('active');
        }
    });

    // Update active on hashchange
    window.addEventListener('hashchange', () => {
        const hash = window.location.hash;
        navLinks.forEach(link => {
            const href = link.getAttribute('href');
            if (href && href.endsWith(hash)) {
                navLinks.forEach(l => l.classList.remove('active'));
                link.classList.add('active');
            }
        });
    });
}
