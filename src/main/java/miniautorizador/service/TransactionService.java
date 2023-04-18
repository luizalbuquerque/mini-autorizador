package miniautorizador.service;

import miniautorizador.dto.NewTransactionDTO;
import miniautorizador.dto.TransactionDTO;
import miniautorizador.entity.CardEntity;
import miniautorizador.entity.TransactionEntity;
import miniautorizador.enums.CardStatus;
import miniautorizador.exeption.BusinessException;
import miniautorizador.repository.CardRepository;
import miniautorizador.repository.TransactionRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static miniautorizador.util.ConstantUtils.*;

@Service
public class TransactionService {

    TransactionRepository transactionRepository;

    @Autowired
    private CardRepository cardRepository;

    private ModelMapper mapper = new ModelMapper();

    public String save(NewTransactionDTO newTransactionDTO) {

        // Take card per numberCard
        Optional<CardEntity> card = cardRepository.findCardByNumberCard(newTransactionDTO.getCardNumber());

        while (!card.isEmpty()) {
            while (card.get().getCardStatus().equals(CardStatus.ATIVO)) {
                while (card.get().getPassword().equals(newTransactionDTO.getPassword())) {
                    while (card.get().getAmount().compareTo(newTransactionDTO.getValue()) >= 0) {
                        updateBalance(card, newTransactionDTO.getValue(), "debito");
                        TransactionEntity transactionEntity = mapper.map(newTransactionDTO, TransactionEntity.class);
                        transactionEntity.setCardEntity(card.get());
                        transactionRepository.save(transactionEntity);
                        return "OK";
                    }
                    throw new BusinessException("INSUFFICIENT_BALANCE");
                }
                throw new BusinessException("INVALID_PASSWORD");
            }
            throw new BusinessException("INATIVE_CARD");
        }
        throw new BusinessException("INVALID_NUMBER_CARD");
    }

    public CardEntity updateBalance (Optional<CardEntity> card, BigDecimal valueTransaction, String typeTransaction) {

        Optional<CardEntity> cardEntity =  cardRepository.findById(card.get().getId());

        BigDecimal newAmount = (typeTransaction.equals("debito")) ? cardEntity.get().getAmount().subtract(valueTransaction) : cardEntity.get().getAmount().add(valueTransaction);

        cardEntity.get().setAmount(newAmount);
        return cardRepository.save(cardEntity.get());
    }

    public TransactionDTO findById(Long id ) {
        TransactionEntity transactionEntity = transactionRepository.findById( id ).orElse( new TransactionEntity() );
        if ( transactionEntity.getId() == null ) {
            throw new BusinessException(TRANSACTION_NOT_FOUND);
        }
        return mapper.map(transactionEntity, TransactionDTO.class);
    }

    public List<TransactionDTO> findAll() {
        List<TransactionEntity> transactionEntityList = transactionRepository.findAll();
        while ( transactionEntityList.isEmpty() ) {
            throw new BusinessException(TRANSACTION_NOT_FOUND);
        }
        return transactionEntityList.stream().map(entity -> mapper.map(entity, TransactionDTO.class)).collect(Collectors.toList());
    }


    public String deleteById(Long id ) {
        Optional<TransactionEntity> transacaoEntity = transactionRepository.findById(id);
        if ( transacaoEntity.isPresent() ) {
            Optional<CardEntity> cardEntity = cardRepository.findCardByNumberCard(transacaoEntity.get().getCardEntity().getNumberCard());
            transactionRepository.deleteById(id);
            return "Transação excluída com sucesso.";
        }
        throw new BusinessException(TRANSACTION_NOT_FOUND);
    }



}
