package model;

import java.sql.Date;
import java.util.Base64;

public class BookImages {

    private int imageId;
    private int bookId;
    private byte[] imageData;
    private String mimeType;
    private int isCover;
    private Date createdAt;

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

    public BookImages(int bookId, byte[] imageData, String mimeType, int isCover) {
        this.bookId = bookId;
        this.imageData = imageData;
        this.mimeType = mimeType;
        this.isCover = isCover;
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

    /**
     * Chuyển mảng byte ảnh sang chuỗi Base64 để hiển thị trực tiếp trên HTML/JSP
     */
    public String getBase64Image() {
        if (imageData != null && imageData.length > 0) {
            return Base64.getEncoder().encodeToString(imageData);
        }
        return "";
    }

    /**
     * Trả về URI Data hoàn chỉnh cho thẻ <img>: data:<mimeType>;base64,<base64Data>
     */
    public String getBase64Src() {
        if (imageData != null && imageData.length > 0) {
            String type = (mimeType != null && !mimeType.trim().isEmpty()) ? mimeType : "image/jpeg";
            return "data:" + type + ";base64," + getBase64Image();
        }
        return "";
    }

    @Override
    public String toString() {
        return "BookImages{" 
                + "imageId=" + imageId 
                + ", bookId=" + bookId 
                + ", imageDataSize=" + (imageData != null ? (imageData.length + " bytes") : "null") 
                + ", mimeType=" + mimeType 
                + ", isCover=" + isCover 
                + ", createdAt=" + createdAt 
                + '}';
    }
}
