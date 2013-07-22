package com.myandroid.calendar.alerts;

import com.myandroid.calendar.alerts.AlertService.NotificationWrapper;

public interface NotificationMgr {
    public void cancel(int id);
    public void cancel(String tag, int id);
    public void cancelAll();
    public void notify(int id, NotificationWrapper notification);
    public void notify(String tag, int id, NotificationWrapper notification);
}
