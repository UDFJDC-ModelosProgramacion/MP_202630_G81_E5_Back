package co.edu.udistrital.mdp.pets.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Notification no tiene asociaciones de cardinalidad N: la asociacion con
 * Shelter es de cardinalidad 1 y por eso se define en NotificationDTO.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class NotificationDetailDTO extends NotificationDTO {

}
