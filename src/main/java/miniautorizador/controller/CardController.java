package miniautorizador.controller;

import jakarta.validation.Valid;
import miniautorizador.dto.CardDTO;
import miniautorizador.dto.NewCardDTO;
import miniautorizador.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping( "/cards" )
public class CardController {

    @Autowired
    private CardService cardService;

    @PostMapping
    public ResponseEntity<CardDTO> createCard(@Valid @RequestBody NewCardDTO newCardDTO) throws Exception {
        return new ResponseEntity< >(cardService.save(newCardDTO), HttpStatus.CREATED );
    }

    @GetMapping( value = "/{cardNumber}" )
    public ResponseEntity<BigDecimal> getCardByNumber(@PathVariable String cardNumber) throws Exception {
        return new ResponseEntity< >(cardService.findCardByNumberCard(cardNumber), HttpStatus.OK);
    }
}
