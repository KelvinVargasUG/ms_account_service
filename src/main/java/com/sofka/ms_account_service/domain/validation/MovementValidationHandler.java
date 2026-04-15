package com.sofka.ms_account_service.domain.validation;

public abstract class MovementValidationHandler {

    private MovementValidationHandler next;

    public void setNext(MovementValidationHandler nextHandler) {
        this.next = nextHandler;
    }

    public void handle(MovementValidationContext context) {
        doHandle(context);
        if (next != null) {
            next.handle(context);
        }
    }

    protected abstract void doHandle(MovementValidationContext context);
}
