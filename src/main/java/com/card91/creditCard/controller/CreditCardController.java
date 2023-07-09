package com.card91.creditCard.controller;

import com.card91.creditCard.model.Card;
import com.card91.creditCard.service.CardService;
import com.card91.creditCard.service.Views;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/cardProject")
public class CreditCardController {

    @Autowired
    private CardService cardService;

    @PostMapping("/card")
    public ResponseEntity<?> addCard(@Valid @RequestBody Card card) {
        Card savedCard = null;
        String duplicate = cardService.checkForDuplicate(card);
        if(duplicate.isEmpty()) {
            savedCard = cardService.saveCard(card);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(duplicate + " is already used");
        }
        return savedCard != null? ResponseEntity.status(HttpStatus.CREATED).body("card created") :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body("card not created");
    }

    @GetMapping("/card")
    @JsonView(Views.MyResponseViews.class)
    public ResponseEntity<?> getAllCards() {
        List cards = cardService.getAllCards();
        return !(cards.isEmpty()) ? ResponseEntity.ok(cards): ResponseEntity.status(HttpStatus.NOT_FOUND).body("no data found");
    }

    @GetMapping("/card/{refId}")
    @JsonView(Views.MyResponseViews.class)
    public ResponseEntity<?> getCard(@PathVariable String refId) {
        List cards = cardService.getAllCards(refId);
        return !(cards.isEmpty()) ? ResponseEntity.ok(cards): ResponseEntity.status(HttpStatus.NOT_FOUND).body("ref Id not found");
    }

    @PostMapping("/card/print")
    public ResponseEntity<List<Card>> getAllPrintedCards() throws IOException {
        cardService.getAllCardsPrint();
        return null;
    }
}
