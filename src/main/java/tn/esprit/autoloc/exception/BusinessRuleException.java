package tn.esprit.autoloc.exception;

/** Règle métier violée -> HTTP 400. */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
