package co.edu.udistrital.mdp.pets.entities;

import java.util.Date;
import jakarta.persistence.Entity;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
public class NotificationEntity extends BaseEntity {

    private String message;

    @Temporal(TemporalType.DATE)
    private Date date;

    private String channel; // "EMAIL", "SMS" o "PUSH"

}