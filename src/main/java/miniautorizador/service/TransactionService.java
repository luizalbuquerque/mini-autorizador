package miniautorizador.service;

import miniautorizador.dto.NewTransactionDTO;
import miniautorizador.dto.TransactionDTO;
import miniautorizador.entity.CardEntity;
import miniautorizador.entity.TransactionEntity;
import miniautorizador.enums.CardStatus;
import miniautorizador.enums.TypeTransaction;
import miniautorizador.exeption.BusinessException;
import miniautorizador.repository.CardRepository;
import miniautorizador.repository.TransactionRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static miniautorizador.util.ConstantUtils.*;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    @Autowired
    private CardRepository cardRepository;

    private ModelMapper mapper = new ModelMapper();

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public String save(NewTransactionDTO newTransactionDTO) {

        List<TransactionEntity> transactionList = new ArrayList<>();
        try {
            CardEntity card = cardRepository.findCardByNumberCard(newTransactionDTO.getCardNumber())
                    .orElseThrow(() -> new BusinessException("INVALID_NUMBER_CARD"));

            if (card.getCardStatus() != CardStatus.ATIVO) {
                throw new BusinessException("INATIVE_CARD");
            }

            if (!card.getPassword().equals(newTransactionDTO.getPassword())) {
                throw new BusinessException("INVALID_PASSWORD");
            }

            if (card.getAmount().compareTo(newTransactionDTO.getValue()) < 0) {
                throw new BusinessException("INSUFFICIENT_BALANCE");
            }

            updateBalance(Optional.of(card), newTransactionDTO.getValue(), "debito");

            TransactionEntity transactionEntity = new TransactionEntity();
            transactionEntity.setValue(newTransactionDTO.getValue());
            transactionEntity.setUpdatedAt(Date.from(Instant.now()));
            transactionEntity.setCardEntity(card);
            transactionRepository.save(transactionEntity);
            return "Transação bem sucedida!";

        } catch (Exception ex) {
            throw new BusinessException("UNEXPECTED_ERROR");
        }
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


    public CardEntity recharge(NewTransactionDTO newTransactionDTO) {

        // Instancing list of transactions
        List<TransactionEntity> transactionList = new ArrayList<>();

        try {
            Optional<CardEntity> existentCard = cardRepository.findCardByNumberCard(newTransactionDTO.getCardNumber());

            // Updating value from client card like alelo, sodexo or other..
            CardEntity updatedCard = existentCard.get();
            updatedCard.setAmount(updatedCard.getAmount().add(newTransactionDTO.getValue()));
            updatedCard.setUpdatedAt(Date.from(Instant.now()));

            TransactionEntity transactionEntity = new TransactionEntity();
            transactionEntity.setValue(newTransactionDTO.getValue());
            transactionEntity.setTypeTransaction(TypeTransaction.RECHARGE);

            // Adding to list of transactions
            transactionList.add(transactionEntity);

            // Save transaction
            TransactionEntity transactionCreated = transactionRepository.save(transactionEntity);

            updatedCard.setTransactionEntity(transactionList);
            CardEntity accountsaved = cardRepository.save(updatedCard);
            return ResponseEntity.ok().body(accountsaved).getBody();

        } catch (Exception e) {
            throw new BusinessException("Card not exist");
        }

    }
}
