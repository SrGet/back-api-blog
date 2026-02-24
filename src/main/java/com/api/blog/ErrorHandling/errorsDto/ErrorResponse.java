package com.api.blog.ErrorHandling.errorsDto;

import java.util.Map;

public record ErrorResponse(Map<String,String> error) {
}
