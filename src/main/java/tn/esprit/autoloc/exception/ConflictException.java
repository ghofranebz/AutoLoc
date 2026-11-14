package tn.esprit.autoloc.exception;

/** Conflit avec l'état existant (doublon, chevauchement...) -> HTTP 409. */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
