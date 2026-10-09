
package com.api.conectaComunidade.exception;
import java.util.List;

public record ApiErrorResponse(
        int status,
        String message,
        List<FieldValidationError> fieldErrors
) {
}