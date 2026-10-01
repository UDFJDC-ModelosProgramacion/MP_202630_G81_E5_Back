package co.edu.udistrital.mdp.pets.dto;

import java.util.Date;

import lombok.Data;

@Data
public class LifeEventDTO {

    private Long id;
    private String type;
    private String description;
    private Date date;

}