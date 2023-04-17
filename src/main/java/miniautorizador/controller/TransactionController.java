package miniautorizador.controller;

import jakarta.validation.Valid;
import miniautorizador.dto.TransactionDTO;
import miniautorizador.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/transaction", produces = MediaType.APPLICATION_JSON_VALUE)
public class TransactionController {

    @Autowired
    private TransactionService transactionService;


    @GetMapping( value = "/{id}" )
    public ResponseEntity<TransactionDTO> getTransactionById(@PathVariable Long id) {
        return new ResponseEntity< >(transactionService.findById(id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List< TransactionDTO >> listTransactions() throws Exception {
        return new ResponseEntity< >( transactionService.findAll(), HttpStatus.OK );
    }

    @PostMapping
    public ResponseEntity< String > createTransaction(@Valid @RequestBody CriaTransacaoModel criaTransacaoModel) {
        return new ResponseEntity< >(transacaoService.save(criaTransacaoModel), HttpStatus.CREATED );
    }

    @DeleteMapping( value = "/{id}" )
    public ResponseEntity< String > deleteTransaction( @PathVariable( "id" ) Long id ) {
        return new ResponseEntity< >( transacaoService.deleteById( id ), HttpStatus.OK );
    }

}
