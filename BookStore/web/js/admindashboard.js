/**
 * ADMIN DASHBOARD JAVASCRIPT (admindashboard.js)
 * Manages tabs, CRUD modals (Books, Categories, Orders, Users), and image preview.
 */

document.addEventListener('DOMContentLoaded', () => {
    initAdminTabs();
    initBookModal();
    initCategoryModal();
    initAddOrderModal();
    initOrderModal();
    initUserModal();
    initDeleteModal();
    initModalClosers();
    initQuickTableSearch();
    initAlertClosers();
});

function initAlertClosers() {
    document.querySelectorAll('.admin-alert-close').forEach(btn => {
        btn.addEventListener('click', () => {
            const alertBox = btn.closest('.admin-alert');
            if (alertBox) alertBox.remove();
        });
    });
}

/* 1. Tab switching */
function initAdminTabs() {
    const tabBtns = document.querySelectorAll('.admin-tab-btn');
    const panels = document.querySelectorAll('.admin-panel');

    tabBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            const targetTab = btn.dataset.tab;
            if (!targetTab) return;

            tabBtns.forEach(b => b.classList.remove('active'));
            btn.classList.add('active');

            panels.forEach(panel => {
                if (panel.id === 'panel-' + targetTab) {
                    panel.classList.add('active');
                } else {
                    panel.classList.remove('active');
                }
            });

            // Update URL query parameter without reload
            const url = new URL(window.location.href);
            url.searchParams.set('tab', targetTab);
            window.history.replaceState({}, '', url.toString());
        });
    });
}

/* 2. Book Add / Edit Modal */
function initBookModal() {
    const modal = document.getElementById('bookModal');
    const openAddBtn = document.getElementById('btnOpenAddBook');
    const openAddHeroBtn = document.getElementById('btnHeroAddBook');
    const coverInput = document.getElementById('bookCoverFile');
    const coverPreview = document.getElementById('bookCoverPreview');

    if (!modal) return;

    function openAddBookModal() {
        document.getElementById('bookModalTitle').textContent = 'Thêm Sách Mới';
        document.getElementById('bookFormAction').value = 'addBook';
        document.getElementById('bookFormId').value = '';
        document.getElementById('bookTitle').value = '';
        document.getElementById('bookAuthor').value = '';
        document.getElementById('bookPublisher').value = '';
        document.getElementById('bookYear').value = '2026';
        document.getElementById('bookIsbn').value = '';
        document.getElementById('bookPrice').value = '';
        document.getElementById('bookStock').value = '20';
        document.getElementById('bookStatus').value = 'Bán';
        document.getElementById('bookDescription').value = '';
        if (coverInput) coverInput.value = '';
        if (coverPreview) {
            coverPreview.src = modal.dataset.defaultCover || '';
        }
        openModal(modal);
    }

    if (openAddBtn) openAddBtn.addEventListener('click', openAddBookModal);
    if (openAddHeroBtn) openAddHeroBtn.addEventListener('click', openAddBookModal);

    // Edit buttons on book rows
    document.querySelectorAll('.js-edit-book').forEach(btn => {
        btn.addEventListener('click', () => {
            document.getElementById('bookModalTitle').textContent = 'Cập Nhật Sách #' + btn.dataset.id;
            document.getElementById('bookFormAction').value = 'updateBook';
            document.getElementById('bookFormId').value = btn.dataset.id || '';
            document.getElementById('bookTitle').value = btn.dataset.title || '';
            document.getElementById('bookAuthor').value = btn.dataset.author || '';
            document.getElementById('bookCategoryId').value = btn.dataset.categoryid || '1';
            document.getElementById('bookPublisher').value = btn.dataset.publisher || '';
            document.getElementById('bookYear').value = btn.dataset.year || '2025';
            document.getElementById('bookIsbn').value = btn.dataset.isbn || '';
            document.getElementById('bookPrice').value = btn.dataset.price || '0';
            document.getElementById('bookStock').value = btn.dataset.stock || '0';
            document.getElementById('bookStatus').value = btn.dataset.status || 'Bán';
            document.getElementById('bookDescription').value = btn.dataset.desc || '';
            if (coverInput) coverInput.value = '';
            if (coverPreview && btn.dataset.image) {
                coverPreview.src = btn.dataset.image;
            }
            openModal(modal);
        });
    });

    // Live preview for uploaded cover image
    if (coverInput && coverPreview) {
        coverInput.addEventListener('change', () => {
            const file = coverInput.files && coverInput.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = (e) => {
                    coverPreview.src = e.target.result;
                };
                reader.readAsDataURL(file);
            }
        });
    }
}

/* 3. Category Add / Edit Modal */
function initCategoryModal() {
    const modal = document.getElementById('categoryModal');
    const openAddBtn = document.getElementById('btnOpenAddCategory');
    if (!modal) return;

    if (openAddBtn) {
        openAddBtn.addEventListener('click', () => {
            document.getElementById('categoryModalTitle').textContent = 'Thêm Thể Loại Mới';
            document.getElementById('categoryFormAction').value = 'addCategory';
            document.getElementById('categoryFormId').value = '';
            document.getElementById('categoryNameInput').value = '';
            document.getElementById('categoryDescInput').value = '';
            openModal(modal);
        });
    }

    document.querySelectorAll('.js-edit-category').forEach(btn => {
        btn.addEventListener('click', () => {
            document.getElementById('categoryModalTitle').textContent = 'Cập Nhật Thể Loại #' + btn.dataset.id;
            document.getElementById('categoryFormAction').value = 'updateCategory';
            document.getElementById('categoryFormId').value = btn.dataset.id || '';
            document.getElementById('categoryNameInput').value = btn.dataset.name || '';
            document.getElementById('categoryDescInput').value = btn.dataset.desc || '';
            openModal(modal);
        });
    });
}

/* 4A. Proactive Order Creation Modal (Add Order) */
function initAddOrderModal() {
    const modal = document.getElementById('addOrderModal');
    const openBtn = document.getElementById('btnOpenAddOrder');
    const userSelect = document.getElementById('addOrderUserSelect');
    const phoneInput = document.getElementById('addOrderPhoneInput');
    const addressInput = document.getElementById('addOrderAddressInput');
    const itemsContainer = document.getElementById('orderItemsContainer');
    const addRowBtn = document.getElementById('btnAddOrderItemRow');
    const totalPreview = document.getElementById('addOrderTotalPreview');

    if (!modal || !itemsContainer) return;

    function formatCurrencyVND(amount) {
        const rounded = Math.round(amount || 0);
        return rounded.toString().replace(/\B(?=(\d{3})+(?!\d))/g, '.') + ' đ';
    }

    function recalculateOrderTotal() {
        let total = 0;
        const rows = itemsContainer.querySelectorAll('.order-item-row');
        rows.forEach(row => {
            const selectEl = row.querySelector('.js-order-book-select');
            const qtyEl = row.querySelector('.js-order-qty-input');
            if (!selectEl || !qtyEl) return;

            const selectedOpt = selectEl.options[selectEl.selectedIndex];
            const price = selectedOpt ? parseFloat(selectedOpt.dataset.price || '0') : 0;
            const stock = selectedOpt ? parseInt(selectedOpt.dataset.stock || '999', 10) : 999;

            if (stock > 0) {
                qtyEl.max = String(stock);
            }
            let qty = parseInt(qtyEl.value || '0', 10);
            if (isNaN(qty) || qty < 1) {
                qty = 0;
            }
            total += price * qty;
        });

        if (totalPreview) {
            totalPreview.textContent = formatCurrencyVND(total);
        }
    }

    function bindOrderRowEvents(row) {
        const selectEl = row.querySelector('.js-order-book-select');
        const qtyEl = row.querySelector('.js-order-qty-input');
        const removeBtn = row.querySelector('.js-remove-order-row');

        if (selectEl) {
            selectEl.addEventListener('change', recalculateOrderTotal);
        }
        if (qtyEl) {
            qtyEl.addEventListener('input', recalculateOrderTotal);
            qtyEl.addEventListener('change', recalculateOrderTotal);
        }
        if (removeBtn) {
            removeBtn.addEventListener('click', () => {
                const allRows = itemsContainer.querySelectorAll('.order-item-row');
                if (allRows.length > 1) {
                    row.remove();
                } else {
                    if (selectEl) selectEl.value = '';
                    if (qtyEl) qtyEl.value = '1';
                }
                recalculateOrderTotal();
            });
        }
    }

    // Bind initial row
    itemsContainer.querySelectorAll('.order-item-row').forEach(bindOrderRowEvents);

    // Add new book row
    if (addRowBtn) {
        addRowBtn.addEventListener('click', () => {
            const firstRow = itemsContainer.querySelector('.order-item-row');
            if (!firstRow) return;
            const clone = firstRow.cloneNode(true);
            const selectEl = clone.querySelector('.js-order-book-select');
            const qtyEl = clone.querySelector('.js-order-qty-input');
            if (selectEl) selectEl.value = '';
            if (qtyEl) {
                qtyEl.value = '1';
                qtyEl.max = '999';
            }
            bindOrderRowEvents(clone);
            itemsContainer.appendChild(clone);
            recalculateOrderTotal();
        });
    }

    // Auto-fill customer phone & address when customer is chosen
    if (userSelect) {
        userSelect.addEventListener('change', () => {
            const opt = userSelect.options[userSelect.selectedIndex];
            if (!opt || !opt.value) return;
            const phone = opt.dataset.phone || '';
            const address = opt.dataset.address || '';
            if (phoneInput && phone) phoneInput.value = phone;
            if (addressInput && address) addressInput.value = address;
        });
    }

    // Open modal and reset form
    if (openBtn) {
        openBtn.addEventListener('click', () => {
            const form = document.getElementById('addOrderForm');
            if (form) form.reset();

            // Keep only 1 row when opening fresh
            const allRows = itemsContainer.querySelectorAll('.order-item-row');
            allRows.forEach((r, idx) => {
                if (idx > 0) {
                    r.remove();
                } else {
                    const sel = r.querySelector('.js-order-book-select');
                    const qty = r.querySelector('.js-order-qty-input');
                    if (sel) sel.value = '';
                    if (qty) qty.value = '1';
                }
            });

            recalculateOrderTotal();
            openModal(modal);
        });
    }
}

/* 4B. Order Edit Modal */
function initOrderModal() {
    const modal = document.getElementById('orderModal');
    if (!modal) return;

    document.querySelectorAll('.js-edit-order').forEach(btn => {
        btn.addEventListener('click', () => {
            document.getElementById('orderModalTitle').textContent = 'Cập Nhật Đơn Hàng #' + btn.dataset.id;
            document.getElementById('orderFormId').value = btn.dataset.id || '';
            document.getElementById('orderStatusInput').value = btn.dataset.orderstatus || 'Pending';
            document.getElementById('paymentStatusInput').value = btn.dataset.paymentstatus || 'Unpaid';
            document.getElementById('orderPhoneInput').value = btn.dataset.phone || '';
            document.getElementById('orderAddressInput').value = btn.dataset.address || '';
            openModal(modal);
        });
    });
}

/* 5. User Add / Edit Modal */
function initUserModal() {
    const modal = document.getElementById('userModal');
    const openAddBtn = document.getElementById('btnOpenAddUser');
    if (!modal) return;

    if (openAddBtn) {
        openAddBtn.addEventListener('click', () => {
            document.getElementById('userModalTitle').textContent = 'Thêm Người Dùng Mới';
            document.getElementById('userFormAction').value = 'addUser';
            document.getElementById('userFormId').value = '';
            document.getElementById('userFullNameInput').value = '';
            document.getElementById('userEmailInput').value = '';
            document.getElementById('userPasswordInput').value = '';
            document.getElementById('userPasswordInput').required = true;
            document.getElementById('userPwdHint').textContent = '(Bắt buộc)';
            document.getElementById('userPhoneInput').value = '';
            document.getElementById('userAddressInput').value = '';
            document.getElementById('userRoleInput').value = '1';
            openModal(modal);
        });
    }

    document.querySelectorAll('.js-edit-user').forEach(btn => {
        btn.addEventListener('click', () => {
            document.getElementById('userModalTitle').textContent = 'Cập Nhật Tài Khoản #' + btn.dataset.id;
            document.getElementById('userFormAction').value = 'updateUser';
            document.getElementById('userFormId').value = btn.dataset.id || '';
            document.getElementById('userFullNameInput').value = btn.dataset.fullname || '';
            document.getElementById('userEmailInput').value = btn.dataset.email || '';
            document.getElementById('userPasswordInput').value = '';
            document.getElementById('userPasswordInput').required = false;
            document.getElementById('userPwdHint').textContent = '(Để trống nếu giữ nguyên mật khẩu cũ)';
            document.getElementById('userPhoneInput').value = btn.dataset.phone || '';
            document.getElementById('userAddressInput').value = btn.dataset.address || '';
            document.getElementById('userRoleInput').value = btn.dataset.roleid || '1';
            openModal(modal);
        });
    });
}

/* 6. Delete Confirmation Modal */
function initDeleteModal() {
    const modal = document.getElementById('deleteConfirmModal');
    if (!modal) return;

    document.querySelectorAll('.js-delete-item').forEach(btn => {
        btn.addEventListener('click', () => {
            document.getElementById('deleteActionInput').value = btn.dataset.action || '';
            document.getElementById('deleteIdInput').name = btn.dataset.idfield || 'id';
            document.getElementById('deleteIdInput').value = btn.dataset.id || '';
            document.getElementById('deleteConfirmText').textContent =
                btn.dataset.label || ('Mục #' + btn.dataset.id);
            openModal(modal);
        });
    });
}

/* 7. Generic Modal Open/Close */
function openModal(modal) {
    if (!modal) return;
    modal.classList.add('active');
    document.body.style.overflow = 'hidden';
}

function closeModal(modal) {
    if (!modal) return;
    modal.classList.remove('active');
    document.body.style.overflow = '';
}

function initModalClosers() {
    document.querySelectorAll('[data-close-modal]').forEach(btn => {
        btn.addEventListener('click', () => {
            const overlay = btn.closest('.adm-modal-overlay');
            closeModal(overlay);
        });
    });

    document.querySelectorAll('.adm-modal-overlay').forEach(overlay => {
        overlay.addEventListener('click', (e) => {
            if (e.target === overlay) {
                closeModal(overlay);
            }
        });
    });

    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') {
            document.querySelectorAll('.adm-modal-overlay.active').forEach(overlay => {
                closeModal(overlay);
            });
        }
    });
}

/* 8. Client-side quick search for Categories, Orders, Users tables */
function initQuickTableSearch() {
    document.querySelectorAll('.js-table-filter').forEach(input => {
        const tableId = input.dataset.target;
        const table = document.getElementById(tableId);
        if (!table) return;

        input.addEventListener('input', () => {
            const kw = input.value.trim().toLowerCase();
            const rows = table.querySelectorAll('tbody tr.data-row');
            rows.forEach(row => {
                const text = row.textContent.toLowerCase();
                row.style.display = text.includes(kw) ? '' : 'none';
            });
        });
    });
}

