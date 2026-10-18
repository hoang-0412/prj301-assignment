/**
 * HEADER COMPONENT JAVASCRIPT (header.js)
 * Manages search popup modal, clear button, scroll effect, and nav active state.
 */

document.addEventListener('DOMContentLoaded', () => {
    initSearchPopup();
    initHeaderSearch();
    initHeaderScrollEffect();
    initHeaderActiveNav();
    initAuthModal();
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

/* ==========================================================
   AUTH / LOGIN MODAL CONTROLLER (TULALOVE.VN UX)
   ========================================================== */
function initAuthModal() {
    const overlay = document.getElementById('authModalOverlay');
    const loginBtn = document.getElementById('loginBtn');
    const closeBtn = document.getElementById('authModalCloseBtn');

    if (!overlay) return;

    // Panels
    const step1Panel = document.getElementById('authStep1Panel');
    const step2Panel = document.getElementById('authStep2Panel');
    const registerPanel = document.getElementById('authStepRegisterPanel');
    const forgotPanel = document.getElementById('authStepForgotPanel');

    // Forms & Inputs
    const step1Form = document.getElementById('authStep1Form');
    const idInput = document.getElementById('authIdentifierInput');
    const step1SubmitBtn = document.getElementById('authStep1SubmitBtn');
    const feedback1 = document.getElementById('authFeedback1');

    const step2Form = document.getElementById('authStep2Form');
    const badgeId = document.getElementById('authBadgeIdentifier');
    const backToStep1Btn = document.getElementById('authBackToStep1Btn');
    const pwdInput = document.getElementById('authPasswordInput');
    const eyeBtn = document.getElementById('authEyeBtn');
    const forgotLink = document.getElementById('authForgotLink');
    const loginSubmitBtn = document.getElementById('authLoginSubmitBtn');
    const feedback2 = document.getElementById('authFeedback2');

    const registerForm = document.getElementById('authRegisterForm');
    const regFullNameInput = document.getElementById('authRegisterFullName');
    const regPwdInput = document.getElementById('authRegisterPassword');
    const regEyeBtn = document.getElementById('authRegisterEyeBtn');
    const cancelRegBtn = document.getElementById('authCancelRegisterBtn');
    const feedbackReg = document.getElementById('authFeedbackRegister');

    const backFromForgotBtn = document.getElementById('authBackFromForgotBtn');

    // Context path detection
    const contextPath = window.location.pathname.substring(0, window.location.pathname.indexOf('/', 2)) || '';
    const authUrl = (contextPath ? contextPath : '') + '/login';

    function showPanel(panelToShow) {
        [step1Panel, step2Panel, registerPanel, forgotPanel].forEach(p => {
            if (p) p.style.display = 'none';
        });
        if (panelToShow) {
            panelToShow.style.display = 'block';
        }
        clearFeedbacks();
    }

    function clearFeedbacks() {
        [feedback1, feedback2, feedbackReg].forEach(f => {
            if (f) {
                f.style.display = 'none';
                f.className = 'auth-feedback';
                f.innerHTML = '';
            }
        });
    }

    function showFeedback(element, message, type) {
        if (!element) return;
        element.className = 'auth-feedback ' + (type || 'error');
        element.innerHTML = message;
        element.style.display = 'block';
    }

    function openModal() {
        showPanel(step1Panel);
        overlay.classList.add('active');
        document.body.style.overflow = 'hidden';
        setTimeout(() => {
            if (idInput) {
                idInput.focus();
                idInput.select();
            }
        }, 120);
    }

    function closeModal() {
        overlay.classList.remove('active');
        document.body.style.overflow = '';
        setTimeout(() => {
            showPanel(step1Panel);
            if (pwdInput) pwdInput.value = '';
        }, 250);
    }

    // Modal Trigger listeners
    if (loginBtn) {
        loginBtn.addEventListener('click', (e) => {
            e.preventDefault();
            openModal();
        });
    }

    if (closeBtn) {
        closeBtn.addEventListener('click', closeModal);
    }

    overlay.addEventListener('click', (e) => {
        if (e.target === overlay) {
            closeModal();
        }
    });

    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape' && overlay.classList.contains('active')) {
            closeModal();
        }
    });

    // Check if URL has #login or ?login=true to auto open
    if (window.location.hash === '#login' || window.location.search.includes('login=true')) {
        openModal();
    }

    // ----------------------------------------------------
    // STEP 1: SUBMIT IDENTIFIER (PHONE OR EMAIL)
    // ----------------------------------------------------
    async function handleStep1Submit() {
        const identifier = idInput ? idInput.value.trim() : '';
        if (!identifier) {
            showFeedback(feedback1, 'Vui lòng nhập số điện thoại hoặc email!', 'error');
            if (idInput) idInput.focus();
            return;
        }

        step1SubmitBtn.disabled = true;
        step1SubmitBtn.querySelector('span').textContent = 'Đang kiểm tra...';

        try {
            const formData = new URLSearchParams();
            formData.append('action', 'check');
            formData.append('identifier', identifier);
            formData.append('ajax', 'true');

            const response = await fetch(authUrl, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8',
                    'X-Requested-With': 'XMLHttpRequest'
                },
                body: formData.toString()
            });

            const data = await response.json();

            if (data.status === 'exists') {
                // User exists -> Go to step 2 (Enter Password)
                if (badgeId) badgeId.textContent = identifier;
                showPanel(step2Panel);
                if (pwdInput) {
                    pwdInput.value = '';
                    pwdInput.focus();
                }
            } else {
                // User does not exist -> Switch to Register step
                showPanel(registerPanel);
                showFeedback(feedbackReg, 'Tài khoản chưa tồn tại. Vui lòng tạo mật khẩu để hoàn tất đăng ký!', 'info');
                if (regFullNameInput) regFullNameInput.focus();
            }
        } catch (err) {
            // Fallback: If network issue or error checking, still let user try password
            if (badgeId) badgeId.textContent = identifier;
            showPanel(step2Panel);
            if (pwdInput) {
                pwdInput.value = '';
                pwdInput.focus();
            }
        } finally {
            step1SubmitBtn.disabled = false;
            step1SubmitBtn.querySelector('span').textContent = 'Tiếp tục';
        }
    }

    if (step1Form) {
        step1Form.addEventListener('submit', (e) => {
            e.preventDefault();
            handleStep1Submit();
        });
    }

    if (step1SubmitBtn) {
        step1SubmitBtn.addEventListener('click', handleStep1Submit);
    }

    // ----------------------------------------------------
    // STEP 2: SUBMIT PASSWORD
    // ----------------------------------------------------
    async function handleStep2Submit() {
        const identifier = idInput ? idInput.value.trim() : '';
        const password = pwdInput ? pwdInput.value.trim() : '';

        if (!password) {
            showFeedback(feedback2, 'Vui lòng nhập mật khẩu của bạn!', 'error');
            if (pwdInput) pwdInput.focus();
            return;
        }

        loginSubmitBtn.disabled = true;
        loginSubmitBtn.querySelector('span').textContent = 'Đang đăng nhập...';

        try {
            const formData = new URLSearchParams();
            formData.append('action', 'login');
            formData.append('identifier', identifier);
            formData.append('password', password);
            formData.append('ajax', 'true');

            const response = await fetch(authUrl, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8',
                    'X-Requested-With': 'XMLHttpRequest'
                },
                body: formData.toString()
            });

            const data = await response.json();

            if (data.status === 'success') {
                showFeedback(feedback2, 'Đăng nhập thành công! Đang chuyển hướng...', 'success');
                setTimeout(() => {
                    window.location.reload();
                }, 400);
            } else {
                showFeedback(feedback2, data.message || 'Mật khẩu không chính xác. Vui lòng thử lại!', 'error');
                if (pwdInput) {
                    pwdInput.focus();
                    pwdInput.select();
                }
            }
        } catch (err) {
            showFeedback(feedback2, 'Đã xảy ra lỗi kết nối. Vui lòng thử lại sau!', 'error');
        } finally {
            loginSubmitBtn.disabled = false;
            loginSubmitBtn.querySelector('span').textContent = 'Tiếp tục';
        }
    }

    if (step2Form) {
        step2Form.addEventListener('submit', (e) => {
            e.preventDefault();
            handleStep2Submit();
        });
    }

    if (loginSubmitBtn) {
        loginSubmitBtn.addEventListener('click', handleStep2Submit);
    }

    // Back to Step 1 from Step 2
    if (backToStep1Btn) {
        backToStep1Btn.addEventListener('click', () => {
            showPanel(step1Panel);
            if (idInput) {
                idInput.focus();
                idInput.select();
            }
        });
    }

    // ----------------------------------------------------
    // STEP 3: REGISTER NEW ACCOUNT
    // ----------------------------------------------------
    async function handleRegisterSubmit() {
        const identifier = idInput ? idInput.value.trim() : '';
        const fullName = regFullNameInput ? regFullNameInput.value.trim() : '';
        const password = regPwdInput ? regPwdInput.value.trim() : '';

        if (!fullName) {
            showFeedback(feedbackReg, 'Vui lòng nhập họ và tên của bạn!', 'error');
            if (regFullNameInput) regFullNameInput.focus();
            return;
        }

        if (!password || password.length < 6) {
            showFeedback(feedbackReg, 'Mật khẩu phải chứa ít nhất 6 ký tự!', 'error');
            if (regPwdInput) regPwdInput.focus();
            return;
        }

        const submitBtn = document.getElementById('authRegisterSubmitBtn');
        if (submitBtn) {
            submitBtn.disabled = true;
            submitBtn.querySelector('span').textContent = 'Đang khởi tạo tài khoản...';
        }

        try {
            const formData = new URLSearchParams();
            formData.append('action', 'register');
            formData.append('identifier', identifier);
            formData.append('fullName', fullName);
            formData.append('password', password);
            formData.append('ajax', 'true');

            const response = await fetch(authUrl, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8',
                    'X-Requested-With': 'XMLHttpRequest'
                },
                body: formData.toString()
            });

            const data = await response.json();

            if (data.status === 'success') {
                showFeedback(feedbackReg, 'Đăng ký thành công! Đang đăng nhập...', 'success');
                setTimeout(() => {
                    window.location.reload();
                }, 500);
            } else {
                showFeedback(feedbackReg, data.message || 'Đăng ký thất bại. Vui lòng thử lại!', 'error');
            }
        } catch (err) {
            showFeedback(feedbackReg, 'Lỗi kết nối khi đăng ký. Vui lòng thử lại sau!', 'error');
        } finally {
            if (submitBtn) {
                submitBtn.disabled = false;
                submitBtn.querySelector('span').textContent = 'Đăng ký & Đăng nhập';
            }
        }
    }

    if (registerForm) {
        registerForm.addEventListener('submit', (e) => {
            e.preventDefault();
            handleRegisterSubmit();
        });
    }

    const regSubmitBtn = document.getElementById('authRegisterSubmitBtn');
    if (regSubmitBtn) {
        regSubmitBtn.addEventListener('click', handleRegisterSubmit);
    }

    if (cancelRegBtn) {
        cancelRegBtn.addEventListener('click', () => {
            showPanel(step1Panel);
            if (idInput) {
                idInput.focus();
                idInput.select();
            }
        });
    }

    // ----------------------------------------------------
    // EYE TOGGLE VISIBILITY
    // ----------------------------------------------------
    function setupPasswordToggle(button, inputField) {
        if (!button || !inputField) return;
        button.addEventListener('click', () => {
            const isPassword = inputField.type === 'password';
            inputField.type = isPassword ? 'text' : 'password';

            const showIcon = button.querySelector('.eye-icon-show');
            const hideIcon = button.querySelector('.eye-icon-hide');
            if (showIcon && hideIcon) {
                showIcon.style.display = isPassword ? 'none' : 'block';
                hideIcon.style.display = isPassword ? 'block' : 'none';
            }
        });
    }

    setupPasswordToggle(eyeBtn, pwdInput);
    setupPasswordToggle(regEyeBtn, regPwdInput);

    // ----------------------------------------------------
    // FORGOT PASSWORD NAVIGATION
    // ----------------------------------------------------
    if (forgotLink) {
        forgotLink.addEventListener('click', (e) => {
            e.preventDefault();
            showPanel(forgotPanel);
        });
    }

    if (backFromForgotBtn) {
        backFromForgotBtn.addEventListener('click', () => {
            showPanel(step2Panel);
            if (pwdInput) pwdInput.focus();
        });
    }
}

