package com.wedding.invitation.controller;

import com.wedding.invitation.model.Guest;
import com.wedding.invitation.model.GuestRequest;
import com.wedding.invitation.repository.GuestRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/guests")
public class GuestController {

    private final GuestRepository guestRepository;

    public GuestController(GuestRepository guestRepository) {
        this.guestRepository = guestRepository;
    }

    @PostMapping
    public ResponseEntity<Guest> createGuest(@Valid @RequestBody GuestRequest guestRequest) {
        Guest guest = new Guest();
        guest.setName(guestRequest.getName());
        guest.setAttending(guestRequest.getAttending());
        guest.setFoodPreference(guestRequest.getFoodPreference());
        guest.setComment(guestRequest.getComment());

        Guest savedGuest = guestRepository.save(guest);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedGuest);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}
