package easv.dk.belsign.be;

import java.sql.Timestamp;

public class    Notifications {
    private int NotificationID;
    private String OrderID; // This will hold the Order Number (e.g. "45-00000-000000")
    private String FromUserID; // This will hold the name of the sender
    private String ToUserID; // This will hold the name of the receiver
    private String Message;
    private boolean IsRead;
    private Timestamp CreatedAt;

    public Notifications(int notificationID, String orderID, String fromUserID, String toUserID, String message, boolean isRead, Timestamp createdAt) {
        NotificationID = notificationID;
        OrderID = orderID;
        FromUserID = fromUserID;
        ToUserID = toUserID;
        Message = message;
        IsRead = isRead;
        CreatedAt = createdAt;
    }

    public int getNotificationID() {
        return NotificationID;
    }

    public void setNotificationID(int notificationID) {
        NotificationID = notificationID;
    }

    public String getOrderID() {
        return OrderID;
    }

    public void setOrderID(String orderID) {
        OrderID = orderID;
    }

    public String getFromUserID() {
        return FromUserID;
    }

    public void setFromUserID(String fromUserID) {
        FromUserID = fromUserID;
    }

    public String getToUserID() {
        return ToUserID;
    }

    public void setToUserID(String toUserID) {
        ToUserID = toUserID;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public boolean isRead() {
        return IsRead;
    }

    public void setRead(boolean read) {
        IsRead = read;
    }

    public Timestamp getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        CreatedAt = createdAt;
    }
}
