
package com.api.conectaComunidade.exception;

public record FieldValidationError(
        String field,
        String message
) {
}