package miniautorizador.repository;

import miniautorizador.entity.CardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<CardEntity, Long> {

    Optional<CardEntity> findCardByNumberCard(String cardNumber);

//    List<CardEntity> findAllByOrderByNumberCardAsc();
//
//    List<CardEntity> findAllByStatusOrderByNumberCardAsc(CardStatus status);

}
