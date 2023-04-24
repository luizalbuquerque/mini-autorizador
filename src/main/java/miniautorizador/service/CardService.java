package miniautorizador.service;

import miniautorizador.dto.CardDTO;
import miniautorizador.dto.NewCardDTO;
import miniautorizador.entity.CardEntity;
import miniautorizador.enums.CardStatus;
import miniautorizador.exception.BusinessException;
import miniautorizador.repository.CardRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static miniautorizador.util.ConstantUtils.CARD_NOT_FOUND;

@Service
public class CardService {

    private final CardRepository cardRepository;

    private ModelMapper mapper = new ModelMapper();

    public CardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public CardDTO save(NewCardDTO newCardDTO){

        while ( isCardExist(newCardDTO) ) {
            throw new BusinessException("CARD_EXISTS");
        }

        CardEntity cardEntity = new CardEntity();

        try {
            cardEntity.setNumberCard(newCardDTO.getNumberCard());
            cardEntity.setPassword(newCardDTO.getPassword());
            cardEntity.setAmount(BigDecimal.valueOf( 500,00));
            cardEntity.setCardStatus(CardStatus.ATIVO);
            cardEntity = cardRepository.save(cardEntity);
            return mapper.map(cardEntity, CardDTO.class);
        } catch (Exception e) {
            throw new BusinessException("ERROR_CREATING");
        }
    }

    public BigDecimal findCardByNumberCard(String numberCard){
        CardEntity cardEntity = cardRepository.findCardByNumberCard(numberCard).orElse( null );
        if ( cardEntity == null ) {
            throw new BusinessException("INVALID_NUMBER_CARD");
        }
        return cardEntity.getAmount();
    }

    public boolean isCardExist(NewCardDTO newCardDTO) {
        return cardRepository.findCardByNumberCard( newCardDTO.getNumberCard() ).isPresent();
    }

    public CardEntity findById(Long id) {
        return cardRepository.findById(id)
                .orElseThrow(() -> new BusinessException(CARD_NOT_FOUND));
    }
}
