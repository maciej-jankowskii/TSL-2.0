package com.tsl.exceptions.exceptionHandler;

import com.tsl.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler({NullEntityException.class, CarrierNotFoundException.class, CustomerNotFoundException.class,
            AddressNotFoundException.class, CargoNotFoundException.class, ContactPersonNotFoundException.class,
            ForwarderNotFoundException.class, ForwardingOrderNotFoundException.class, GoodsNotFoundException.class,
            WarehouseNotFoundException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleEntityNotFoundExceptions(RuntimeException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler({CargoAlreadyAssignedException.class, InsufficientWarehouseSpaceException.class,
            InvoiceAlreadyPaidException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleErrorsRelatedToConflicts(RuntimeException ex){
        return ex.getMessage();
    }

    @ExceptionHandler({CurrencyMismatchException.class, IncompatibleGoodsTypeException.class,
            NoGoodsSelectedException.class, NonUniqueLabelsException.class, WrongLoadigDateException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleErrorRelatedToInvalidRequest(RuntimeException ex){
        return ex.getMessage();
    }
}
