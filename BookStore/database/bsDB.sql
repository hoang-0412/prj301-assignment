-- ==========================================================
-- SCRIPT TẠO DATABASE QUẢN LÝ WEBSITE BÁN SÁCH TRÊN SSMS 21
-- ==========================================================

USE master;
GO

-- 1. Xóa Database nếu đã tồn tại để tránh xung đột
IF EXISTS (SELECT name FROM sys.databases WHERE name = N'BookStoreDB')
BEGIN
    ALTER DATABASE BookStoreDB SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE BookStoreDB;
END
GO

-- 2. Tạo Database mới
CREATE DATABASE BookStoreDB;
GO

USE BookStoreDB;
GO

-- ----------------------------------------------------------
-- BẢNG 1: [Role] (Vai trò người dùng)
-- ----------------------------------------------------------
CREATE TABLE [Role] (
    [roleId] INT IDENTITY(1,1) PRIMARY KEY,
    [roleName] NVARCHAR(50) NOT NULL UNIQUE
);
GO

-- ----------------------------------------------------------
-- BẢNG 2: [User] (Người dùng / Khách hàng)
-- ----------------------------------------------------------
CREATE TABLE [User] (
    [userId] INT IDENTITY(1,1) PRIMARY KEY,
    [roleId] INT NOT NULL,
    [fullName] NVARCHAR(100) NOT NULL,
    [email] VARCHAR(150) NOT NULL UNIQUE,
    [passwordHash] VARCHAR(255) NOT NULL,
    [phoneNumber] VARCHAR(20) NULL,
    [address] NVARCHAR(255) NULL,
    [createdAt] DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT [FK_User_Role] FOREIGN KEY ([roleId]) 
        REFERENCES [Role]([roleId]) ON DELETE NO ACTION
);
GO

-- ----------------------------------------------------------
-- BẢNG 3: [Category] (Thể loại sách)
-- ----------------------------------------------------------
CREATE TABLE [Category] (
    [categoryId] INT IDENTITY(1,1) PRIMARY KEY,
    [categoryName] NVARCHAR(100) NOT NULL UNIQUE,
    [description] NVARCHAR(MAX) NULL
);
GO

-- ----------------------------------------------------------
-- BẢNG 4: [Book] (Thông tin sách)
-- ----------------------------------------------------------
CREATE TABLE [Book] (
    [bookId] INT IDENTITY(1,1) PRIMARY KEY,
    [categoryId] INT NOT NULL,
    [title] NVARCHAR(255) NOT NULL,
    [author] NVARCHAR(150) NOT NULL,
    [publisher] NVARCHAR(150) NULL,
    [publicationYear] INT NULL,
    [isbn] VARCHAR(20) NULL UNIQUE,
    [price] DECIMAL(18, 2) NOT NULL CHECK ([price] >= 0),
    [stockQuantity] INT NOT NULL DEFAULT 0 CHECK ([stockQuantity] >= 0),
    [description] NVARCHAR(MAX) NULL,
    [createdAt] DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT [FK_Book_Category] FOREIGN KEY ([categoryId]) 
        REFERENCES [Category]([categoryId]) ON DELETE NO ACTION
);
GO

-- ----------------------------------------------------------
-- BẢNG 5: [BookImages] (Lưu ảnh dưới dạng mảng Byte)
-- ----------------------------------------------------------
CREATE TABLE [BookImages] (
    [imageId] INT IDENTITY(1,1) PRIMARY KEY,
    [bookId] INT NOT NULL,
    [imageData] VARBINARY(MAX) NOT NULL, -- Lưu trữ dữ liệu byte của ảnh
    [mimeType] VARCHAR(50) NOT NULL,    -- Ví dụ: 'image/jpeg', 'image/png'
    [isCover] BIT NOT NULL DEFAULT 0,
    [createdAt] DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT [FK_BookImages_Book] FOREIGN KEY ([bookId]) 
        REFERENCES [Book]([bookId]) ON DELETE CASCADE
);
GO

-- ----------------------------------------------------------
-- BẢNG 6: [Cart] (Giỏ hàng người dùng lưu khi chưa thanh toán)
-- Mỗi User có đúng 1 giỏ hàng hoạt động
-- ----------------------------------------------------------
CREATE TABLE [Cart] (
    [cartId] INT IDENTITY(1,1) PRIMARY KEY,
    [userId] INT NOT NULL UNIQUE,
    [createdAt] DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    [updatedAt] DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT [FK_Cart_User] FOREIGN KEY ([userId]) 
        REFERENCES [User]([userId]) ON DELETE CASCADE
);
GO

-- ----------------------------------------------------------
-- BẢNG 7: [CartItem] (Chi tiết mặt hàng trong giỏ)
-- ----------------------------------------------------------
CREATE TABLE [CartItem] (
    [cartItemId] INT IDENTITY(1,1) PRIMARY KEY,
    [cartId] INT NOT NULL,
    [bookId] INT NOT NULL,
    [quantity] INT NOT NULL DEFAULT 1 CHECK ([quantity] > 0),
    [addedAt] DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT [FK_CartItem_Cart] FOREIGN KEY ([cartId]) 
        REFERENCES [Cart]([cartId]) ON DELETE CASCADE,
    CONSTRAINT [FK_CartItem_Book] FOREIGN KEY ([bookId]) 
        REFERENCES [Book]([bookId]) ON DELETE CASCADE,
    CONSTRAINT [UQ_Cart_Book] UNIQUE ([cartId], [bookId])
);
GO

-- ----------------------------------------------------------
-- BẢNG 8: [Order] (Đơn hàng khi chuyển đổi từ Cart)
-- ----------------------------------------------------------
CREATE TABLE [Order] (
    [orderId] INT IDENTITY(1,1) PRIMARY KEY,
    [userId] INT NOT NULL,
    [totalAmount] DECIMAL(18, 2) NOT NULL CHECK ([totalAmount] >= 0),
    [orderStatus] NVARCHAR(50) NOT NULL DEFAULT N'Pending', -- Pending, Shipping, Completed, Cancelled
    [shippingAddress] NVARCHAR(255) NOT NULL,
    [recipientPhone] VARCHAR(20) NOT NULL,
    [paymentMethod] NVARCHAR(50) NOT NULL DEFAULT N'COD',
    [paymentStatus] NVARCHAR(50) NOT NULL DEFAULT N'Unpaid',
    [createdAt] DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT [FK_Order_User] FOREIGN KEY ([userId]) 
        REFERENCES [User]([userId]) ON DELETE NO ACTION
);
GO

-- ----------------------------------------------------------
-- BẢNG 9: [OrderItem] (Chi tiết từng cuốn sách trong đơn)
-- Lưu giá snapshot lúc mua để không bị ảnh hưởng khi giá sách đổi
-- ----------------------------------------------------------
CREATE TABLE [OrderItem] (
    [orderItemId] INT IDENTITY(1,1) PRIMARY KEY,
    [orderId] INT NOT NULL,
    [bookId] INT NOT NULL,
    [quantity] INT NOT NULL CHECK ([quantity] > 0),
    [unitPrice] DECIMAL(18, 2) NOT NULL CHECK ([unitPrice] >= 0),
    CONSTRAINT [FK_OrderItem_Order] FOREIGN KEY ([orderId]) 
        REFERENCES [Order]([orderId]) ON DELETE CASCADE,
    CONSTRAINT [FK_OrderItem_Book] FOREIGN KEY ([bookId]) 
        REFERENCES [Book]([bookId]) ON DELETE NO ACTION
);
GO

-- ==========================================================
-- TẠO CHỈ MỤC (INDEXES) TỐI ƯU HÓA TRUY VẤN
-- ==========================================================
CREATE INDEX [IX_Book_CategoryId] ON [Book]([categoryId]);
CREATE INDEX [IX_BookImages_BookId] ON [BookImages]([bookId]);
CREATE INDEX [IX_CartItem_CartId] ON [CartItem]([cartId]);
CREATE INDEX [IX_Order_UserId] ON [Order]([userId]);
CREATE INDEX [IX_OrderItem_OrderId] ON [OrderItem]([orderId]);
GO

-- ==========================================================
-- DỮ LIỆU KHỞI TẠO CƠ BẢN (MOCK DATA)
-- ==========================================================
INSERT INTO [Role] ([roleName]) VALUES (N'Customer'), (N'Admin');

INSERT INTO [Category] ([categoryName], [description]) 
VALUES 
    (N'Công nghệ thông tin', N'Sách lập trình, cơ sở dữ liệu, mạng máy tính'),
    (N'Kinh tế - Khởi nghiệp', N'Tài chính, quản trị kinh doanh, marketing');

INSERT INTO [User] ([roleId], [fullName], [email], [passwordHash], [phoneNumber], [address])
VALUES 
    (2, N'Quản trị viên', 'admin@bookstore.com', 'hashed_pwd_admin_123', '0901234567', N'Hà Nội'),
    (1, N'Nguyễn Văn A', 'nguyenvana@gmail.com', 'hashed_pwd_user_456', '0987654321', N'Đà Nẵng');

INSERT INTO [Book] ([categoryId], [title], [author], [publisher], [publicationYear], [isbn], [price], [stockQuantity], [description])
VALUES 
    (1, N'Clean Code: A Handbook of Agile Software Craftsmanship', N'Robert C. Martin', N'Prentice Hall', 2008, '9780132350884', 450000.00, 20, N'Sách hướng dẫn viết mã sạch cho lập trình viên'),
    (2, N'Đắc Nhân Tâm', N'Dale Carnegie', N'NXB Tổng Hợp TPHCM', 2020, '9786045892305', 98000.00, 50, N'Nghệ thuật giao tiếp và thu phục lòng người');

-- Demo chèn dữ liệu byte ảnh (chuỗi hex 0x... đại diện cho byte stream)
INSERT INTO [BookImages] ([bookId], [imageData], [mimeType], [isCover])
VALUES 
    (1, 0xFFD8FFE000104A464946, 'image/jpeg', 1),
    (2, 0x89504E470D0A1A0A, 'image/png', 1);

-- Khởi tạo giỏ hàng cho User 2 (Nguyễn Văn A) và đưa sách Clean Code vào giỏ
INSERT INTO [Cart] ([userId]) VALUES (2);
INSERT INTO [CartItem] ([cartId], [bookId], [quantity]) VALUES (1, 1, 2);
GO