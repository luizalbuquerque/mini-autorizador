package miniautorizador.service;

import miniautorizador.dto.CardDTO;
import miniautorizador.dto.NewCardDTO;
import miniautorizador.entity.CardEntity;
import miniautorizador.enums.CardStatus;
import miniautorizador.exeption.BusinessException;
import miniautorizador.repository.CardRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static miniautorizador.util.ConstantUtils.CARD_NOT_FOUND;

@Service
public class CardService {

    private CardRepository cardRepository;

    private ModelMapper mapper = new ModelMapper();

    public CardDTO save(NewCardDTO newCardDTO) throws Exception {
        CardEntity cartaoEntity = mapper.map(newCardDTO, CardEntity.class);
        if ( isCardExist(cartaoEntity) ) {
            throw new Exception("CARD_EXISTS");
        }
        try {
            cartaoEntity.setAmount(BigDecimal.valueOf( 500,00));
            cartaoEntity.setCardStatus(CardStatus.ATIVO);
            cartaoEntity = cardRepository.save(cartaoEntity);
            return mapper.map(cartaoEntity, CardDTO.class);
        } catch (Exception e) {
            throw new Exception("ERROR_CREATING");
        }
    }

    public BigDecimal findCardByNumberCard(String cardNumber) throws Exception {
        CardEntity cardEntity = cardRepository.findCardByNumberCard(cardNumber).orElse( null );
        if ( cardEntity == null ) {
            throw new Exception("INVALID_NUMBER_CARD");
        }
        return cardEntity.getAmount();
    }

    public boolean isCardExist( CardEntity cardEntity) {
        return cardRepository.findCardByNumberCard( cardEntity.getNumberCard() ).isPresent();
    }

    public CardEntity findById(Long id) {
        return cardRepository.findById(id)
                .orElseThrow(() -> new BusinessException(CARD_NOT_FOUND));
    }
}
