package co.edu.udistrital.mdp.pets.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.udistrital.mdp.pets.entities.EmailNotificationStrategyEntity;

@Repository
public interface EmailNotificationStrategyRepository extends JpaRepository<EmailNotificationStrategyEntity, Long> {

}