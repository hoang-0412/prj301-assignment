/**
 * STANDALONE LOGIN PAGE JAVASCRIPT (login.js)
 * Manages the multi-step authentication flow on the dedicated /login page.
 */

document.addEventListener('DOMContentLoaded', () => {
    const wrapper = document.querySelector('.standalone-login-wrapper');
    if (!wrapper) return;

    const step1Panel = wrapper.querySelector('#pageAuthStep1Panel');
    const step2Panel = wrapper.querySelector('#pageAuthStep2Panel');
    const registerPanel = wrapper.querySelector('#pageAuthStepRegisterPanel');
    const forgotPanel = wrapper.querySelector('#pageAuthStepForgotPanel');

    const step1Form = wrapper.querySelector('#pageAuthStep1Form');
    const idInput = wrapper.querySelector('#pageAuthIdentifierInput');
    const step1SubmitBtn = wrapper.querySelector('#pageAuthStep1SubmitBtn');
    const feedback1 = wrapper.querySelector('#pageAuthFeedback1');

    const step2Form = wrapper.querySelector('#pageAuthStep2Form');
    const badgeId = wrapper.querySelector('#pageAuthBadgeIdentifier');
    const backToStep1Btn = wrapper.querySelector('#pageAuthBackToStep1Btn');
    const pwdInput = wrapper.querySelector('#pageAuthPasswordInput');
    const eyeBtn = wrapper.querySelector('#pageAuthEyeBtn');
    const forgotLink = wrapper.querySelector('#pageAuthForgotLink');
    const loginSubmitBtn = wrapper.querySelector('#pageAuthLoginSubmitBtn');
    const feedback2 = wrapper.querySelector('#pageAuthFeedback2');

    const registerForm = wrapper.querySelector('#pageAuthRegisterForm');
    const regFullNameInput = wrapper.querySelector('#pageAuthRegisterFullName');
    const regPwdInput = wrapper.querySelector('#pageAuthRegisterPassword');
    const regEyeBtn = wrapper.querySelector('#pageAuthRegisterEyeBtn');
    const cancelRegBtn = wrapper.querySelector('#pageAuthCancelRegisterBtn');
    const feedbackReg = wrapper.querySelector('#pageAuthFeedbackRegister');
    const regSubmitBtn = wrapper.querySelector('#pageAuthRegisterSubmitBtn');

    const backFromForgotBtn = wrapper.querySelector('#pageAuthBackFromForgotBtn');

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

    async function handleStep1Submit() {
        const identifier = idInput ? idInput.value.trim() : '';
        if (!identifier) {
            showFeedback(feedback1, 'Vui lòng nhập số điện thoại hoặc email!', 'error');
            if (idInput) idInput.focus();
            return;
        }

        if (step1SubmitBtn) {
            step1SubmitBtn.disabled = true;
            step1SubmitBtn.querySelector('span').textContent = 'Đang kiểm tra...';
        }

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
                if (badgeId) badgeId.textContent = identifier;
                showPanel(step2Panel);
                if (pwdInput) {
                    pwdInput.value = '';
                    pwdInput.focus();
                }
            } else {
                showPanel(registerPanel);
                showFeedback(feedbackReg, 'Tài khoản chưa tồn tại. Vui lòng tạo mật khẩu để hoàn tất đăng ký!', 'info');
                if (regFullNameInput) regFullNameInput.focus();
            }
        } catch (err) {
            if (badgeId) badgeId.textContent = identifier;
            showPanel(step2Panel);
            if (pwdInput) {
                pwdInput.value = '';
                pwdInput.focus();
            }
        } finally {
            if (step1SubmitBtn) {
                step1SubmitBtn.disabled = false;
                step1SubmitBtn.querySelector('span').textContent = 'Tiếp tục';
            }
        }
    }

    if (step1Form) {
        step1Form.addEventListener('submit', (e) => {
            e.preventDefault();
            handleStep1Submit();
        });
    }

    async function handleStep2Submit() {
        const identifier = idInput ? idInput.value.trim() : '';
        const password = pwdInput ? pwdInput.value.trim() : '';

        if (!password) {
            showFeedback(feedback2, 'Vui lòng nhập mật khẩu của bạn!', 'error');
            if (pwdInput) pwdInput.focus();
            return;
        }

        if (loginSubmitBtn) {
            loginSubmitBtn.disabled = true;
            loginSubmitBtn.querySelector('span').textContent = 'Đang đăng nhập...';
        }

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
                    window.location.href = data.redirect || (contextPath + '/home');
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
            if (loginSubmitBtn) {
                loginSubmitBtn.disabled = false;
                loginSubmitBtn.querySelector('span').textContent = 'Tiếp tục';
            }
        }
    }

    if (step2Form) {
        step2Form.addEventListener('submit', (e) => {
            e.preventDefault();
            handleStep2Submit();
        });
    }

    if (backToStep1Btn) {
        backToStep1Btn.addEventListener('click', () => {
            showPanel(step1Panel);
            if (idInput) {
                idInput.focus();
                idInput.select();
            }
        });
    }

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

        if (regSubmitBtn) {
            regSubmitBtn.disabled = true;
            regSubmitBtn.querySelector('span').textContent = 'Đang khởi tạo tài khoản...';
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
                    window.location.href = data.redirect || (contextPath + '/home');
                }, 500);
            } else {
                showFeedback(feedbackReg, data.message || 'Đăng ký thất bại. Vui lòng thử lại!', 'error');
            }
        } catch (err) {
            showFeedback(feedbackReg, 'Lỗi kết nối khi đăng ký. Vui lòng thử lại sau!', 'error');
        } finally {
            if (regSubmitBtn) {
                regSubmitBtn.disabled = false;
                regSubmitBtn.querySelector('span').textContent = 'Đăng ký & Đăng nhập';
            }
        }
    }

    if (registerForm) {
        registerForm.addEventListener('submit', (e) => {
            e.preventDefault();
            handleRegisterSubmit();
        });
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
});

