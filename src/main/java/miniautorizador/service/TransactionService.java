package miniautorizador.service;

import miniautorizador.dto.NewTransactionDTO;
import miniautorizador.dto.TransactionDTO;
import miniautorizador.entity.CardEntity;
import miniautorizador.entity.TransactionEntity;
import miniautorizador.exeption.BusinessException;
import miniautorizador.repository.CardRepository;
import miniautorizador.repository.TransactionRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static miniautorizador.enums.CardStatus.ATIVO;
import static miniautorizador.util.ConstantUtils.*;

public class TransactionService {

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    private CardRepository cardRepository;

    private ModelMapper mapper = new ModelMapper();

    public String save(NewTransactionDTO newTransactionDTO) {
        Optional<CardEntity> cardEntity = cardRepository.findCardByNumberCard(newTransactionDTO.getCardNumber());
        while (!cardEntity.isEmpty()) {
            while (cardEntity.get().getCardStatus().equals(ATIVO)) {
                while (cardEntity.get().getPassword().equals(newTransactionDTO.getPassword())) {
                    while (cardEntity.get().getAmount().compareTo(newTransactionDTO.getValue()) >= 0) {
                        TransactionEntity transactionEntity = mapper.map(newTransactionDTO, TransactionEntity.class);
                        transactionEntity.setCardEntity(cardEntity.get());
                        transactionRepository.save(transactionEntity);
                        return "OK";
                    }
                    throw new BusinessException(INSUFFICIENT_BALANCE);
                }
                throw new BusinessException(INVALID_PASSWORD);
            }
            throw new BusinessException(INATIVE_CARD);
        }
        throw new BusinessException(INVALID_NUMBER_CARD);
    }

    public TransactionDTO findById(Long id ) {
        TransactionEntity transactionEntity = transactionRepository.findById( id ).orElse( new TransactionEntity() );
        if ( transactionEntity.getId() == null ) {
            throw new BusinessException(TRANSACTION_NOT_FOUND);
        }
        return mapper.map(transactionEntity, TransactionDTO.class);
    }

    public List<TransactionDTO> findAll() throws Exception {
        List<TransactionEntity> transacoes = transactionRepository.findAll();
        while ( transacoes.isEmpty() ) {
            throw new Exception(TRANSACTION_NOT_FOUND);
        }
        return transacoes.stream().map(entity -> mapper.map(entity, TransactionDTO.class)).collect(Collectors.toList());
    }


    public String deleteById(Long id ) {
        Optional<TransactionEntity> transacaoEntity = transactionRepository.findById(id);
        if ( transacaoEntity.isPresent() ) {
            Optional<CardEntity> cardEntity = cardRepository.findCardByNumberCard(transacaoEntity.get().getCardEntity().getCardNumber());
            transactionRepository.deleteById(id);
            return "Transação excluída com sucesso.";
        }
        throw new BusinessException(TRANSACTION_NOT_FOUND);
    }

}
