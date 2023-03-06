package project.telegram.weatherbot.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class User {

    @Id
    private String id;
    private Role role;
    private Long telegramChatId;
    private Boolean notify;

    public User() {}

}
