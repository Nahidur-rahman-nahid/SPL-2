package com.TimeWise.utils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageBody {
    private String recipient;
    private String messageSubject;
    private String messageDescription;
    private String messageRecipientEntityType; // user/ team / TimeWise
}
