package miniautorizador.service;

import miniautorizador.dto.TransactionDTO;
import miniautorizador.entity.TransactionEntity;
import miniautorizador.exeption.BusinessException;
import miniautorizador.repository.TransactionRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

import static miniautorizador.util.ConstantUtils.CARD_NOT_FOUND;
import static miniautorizador.util.ConstantUtils.TRANSACTION_NOT_FOUND;

public class TransactionService {

    @Autowired
    TransactionRepository transactionRepository;

    private ModelMapper mapper = new ModelMapper();

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


}
