/**
 * REGISTRATION PAGE JAVASCRIPT (register.js)
 * Validates password confirmation before submitting the registration form.
 */

document.addEventListener('DOMContentLoaded', () => {
    const form = document.querySelector('.reg-form');
    const pwdInput = document.getElementById('regPassword');
    const confirmInput = document.getElementById('regConfirmPassword');

    if (form && pwdInput && confirmInput) {
        confirmInput.addEventListener('input', () => {
            if (confirmInput.value && pwdInput.value !== confirmInput.value) {
                confirmInput.setCustomValidity('Mật khẩu xác nhận không khớp!');
            } else {
                confirmInput.setCustomValidity('');
            }
        });

        pwdInput.addEventListener('input', () => {
            if (confirmInput.value && pwdInput.value !== confirmInput.value) {
                confirmInput.setCustomValidity('Mật khẩu xác nhận không khớp!');
            } else {
                confirmInput.setCustomValidity('');
            }
        });
    }
});

