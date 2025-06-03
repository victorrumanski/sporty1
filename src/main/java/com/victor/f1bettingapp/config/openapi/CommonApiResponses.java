package com.victor.f1bettingapp.config.openapi;

import com.victor.f1bettingapp.exception.GlobalExceptionHandler;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses(value = {
    @ApiResponse(
        responseCode = "400",
        description = "Invalid input data",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)
        )
    ),
    @ApiResponse(
        responseCode = "404",
        description = "Resource not found",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)
        )
    ),
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)
        )
    )
})
public @interface CommonApiResponses {
} 