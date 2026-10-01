package co.edu.udistrital.mdp.pets.dto;

import java.util.Date;
import lombok.Data;

@Data
public class NotificationDTO {
    private Long id;
    private String message;
    private Date date;
    private String channel;
    private ShelterDTO shelter;
}
