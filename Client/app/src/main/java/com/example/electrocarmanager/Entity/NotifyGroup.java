package com.example.electrocarmanager.Entity;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotifyGroup {
    public String date;
    public List<Notify> notifications;

    public NotifyGroup()
    {
        notifications=new ArrayList<>();
    }
}
