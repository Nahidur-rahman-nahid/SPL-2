package com.bsse1401_bsse1429.TimeWise.utils;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class MessageBody {
    private String recipient;
    private String messageSubject;
    private String messageDescription;
    private String messageRecipientEntityType; // user/ team / TimeWise
}
