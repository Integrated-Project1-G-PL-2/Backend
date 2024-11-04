package com.itbangmodkradankanbanapi.db1.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "mail")
public class EmailConfig {
    private String feSite;
    private String replyTo;
    private String from;

    public String getSubject(String inviter, String accessRight, String boardName) {
        return inviter + " has invited you to collaborate with " + accessRight + " access right on " + boardName + " board";
    }

    public String getBody(String id) {
        return "Click here to accepted or declined " + this.feSite + "/board/" + id + "/collab/invitations";
    }

    public static EmailConfig getInstance() {
        return new EmailConfig();
    }
}
