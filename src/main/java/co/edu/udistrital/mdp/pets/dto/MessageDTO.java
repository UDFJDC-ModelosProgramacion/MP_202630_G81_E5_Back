package co.edu.udistrital.mdp.pets.dto;

import java.util.Date;

import lombok.Data;

@Data
public class MessageDTO {

    private Long id;
    private String content;
    private Date sentDate;

} 