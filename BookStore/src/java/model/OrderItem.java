package model;

public class OrderItem {

    int orderItemId, orderId, bookId, quantity;
    double unitPrice;

    public OrderItem() {
    }

    public OrderItem(int orderItemId, int orderId, int bookId, int quantity, double unitPrice) {
        this.orderItemId = orderItemId;
        this.orderId = orderId;
        this.bookId = bookId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public OrderItem(int orderId, int bookId, int quantity, double unitPrice) {
        this.orderId = orderId;
        this.bookId = bookId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public int getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(int orderItemId) {
        this.orderItemId = orderItemId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    private String bookTitle;

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getFormattedUnitPrice() {
        return String.format("%,.0f đ", unitPrice).replace(',', '.');
    }

    public String getFormattedSubtotal() {
        return String.format("%,.0f đ", unitPrice * quantity).replace(',', '.');
    }

    @Override
    public String toString() {
        return "OrderItem{" + "orderItemId=" + orderItemId + ", orderId=" + orderId + ", bookId=" + bookId + ", quantity=" + quantity + ", unitPrice=" + unitPrice + '}';
    }

}
