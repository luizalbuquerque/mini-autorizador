package miniautorizador.controller;

import jakarta.validation.Valid;
import miniautorizador.dto.CardDTO;
import miniautorizador.dto.NewCardDTO;
import miniautorizador.dto.NewTransactionDTO;
import miniautorizador.entity.CardEntity;
import miniautorizador.repository.CardRepository;
import miniautorizador.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping(path = "/card", produces = MediaType.APPLICATION_JSON_VALUE)
public class CardController {

    @Autowired
    private CardService cardService;

    // CREATE CARD
    @PostMapping
    public ResponseEntity<CardDTO> createCard(@Valid @RequestBody NewCardDTO newCardDTO) {
        return new ResponseEntity< >(cardService.save(newCardDTO), HttpStatus.CREATED );
    }

    // Get Amount by NumberCard
    @GetMapping
    public ResponseEntity<BigDecimal> getAmountCardByNumber(@RequestBody NewCardDTO newCardDTO) {
        return new ResponseEntity< >(cardService.findCardByNumberCard(newCardDTO.getNumberCard()), HttpStatus.OK);
    }

    // FOUND CARD BY ID
    @GetMapping(value = "/{id}")
    public ResponseEntity<Object> findById(@PathVariable Long id) {
        return ResponseEntity.ok(cardService.findById(id));
    }



}
