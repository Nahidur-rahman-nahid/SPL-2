package com.bsse1401_bsse1429.TimeWise.utils;

import lombok.Getter;
import lombok.Setter;

public class NotificationRequestBody {
    @Getter
    @Setter
    public static class SendNotification{
        private String entityName;
        private String notificationSubject;
        private String recipient;
        private String messageContent;
    }
}
