package com.example.loginregister.chatBox.Model;

public class GroupChat {
//    messageInfoMap.put("name", currentUserName);
//    messageInfoMap.put("message", message);
//    messageInfoMap.put("date", currentDate);
//    messageInfoMap.put("time", currentTime);
//    messageInfoMap.put("senderId",senderId);

    private String name;
    private String message;
    private String date;
    private String time;
    private String senderId;


    public GroupChat(String name, String message, String date, String time, String senderId) {
       this.name = name;
       this.message = message;
       this.date = date;
       this.time = time;
       this.senderId = senderId;
    }

    public GroupChat() {
    }

    public String getName() {
        return name;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getMessage() {
        return message;
    }

    public String getDate(){return date;}

    public String getTime(){return time;}

}
