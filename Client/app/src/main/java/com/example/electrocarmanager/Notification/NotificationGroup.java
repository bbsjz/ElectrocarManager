package com.example.electrocarmanager.Notification;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotificationGroup {
    public String date;
    public List<Notification> notifications;

    public NotificationGroup()
    {
        notifications=new ArrayList<>();
    }
}
