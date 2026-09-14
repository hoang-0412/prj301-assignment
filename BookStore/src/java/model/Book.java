package model;

import java.sql.Date;

public class Book {

    int bookId, categoryId;
    String title, author, publisher;
    int publicationYear;
    String isbn;
    double price;
    int stockQuantity;
    String description;
    Date createdAt;

    public Book() {
    }

    public Book(int bookId, int categoryId, String title, String author, String publisher, int publicationYear, String isbn, double price, int stockQuantity, String description, Date createdAt) {
        this.bookId = bookId;
        this.categoryId = categoryId;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.publicationYear = publicationYear;
        this.isbn = isbn;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.description = description;
        this.createdAt = createdAt;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Book{" + "bookId=" + bookId + ", categoryId=" + categoryId + ", title=" + title + ", author=" + author + ", publisher=" + publisher + ", publicationYear=" + publicationYear + ", isbn=" + isbn + ", price=" + price + ", stockQuantity=" + stockQuantity + ", description=" + description + ", createdAt=" + createdAt + '}';
    }

}
