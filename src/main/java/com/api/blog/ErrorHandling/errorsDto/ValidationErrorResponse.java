package com.api.blog.ErrorHandling.errorsDto;

import java.util.Map;

public record ValidationErrorResponse(
        Map<String, String> fieldErrors

) {
}
