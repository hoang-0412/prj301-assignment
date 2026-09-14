package model;

import java.sql.Date;

public class BookImages {

    int imageId, bookId;
    byte[] imageData;
    String mimeType;
    int isCover;
    Date createdAt;

    public BookImages() {
    }

    public BookImages(int imageId, int bookId, byte[] imageData, String mimeType, int isCover, Date createdAt) {
        this.imageId = imageId;
        this.bookId = bookId;
        this.imageData = imageData;
        this.mimeType = mimeType;
        this.isCover = isCover;
        this.createdAt = createdAt;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public int getIsCover() {
        return isCover;
    }

    public void setIsCover(int isCover) {
        this.isCover = isCover;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "BookImages{" + "imageId=" + imageId + ", bookId=" + bookId + ", imageData=" + imageData + ", mimeType=" + mimeType + ", isCover=" + isCover + ", createdAt=" + createdAt + '}';
    }

}
