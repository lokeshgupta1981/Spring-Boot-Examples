package com.howtodoinjava.validation;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

/**
 * A yoga class. The id rules differ between create and update (validation groups).
 */
public record YogaClassRequest(
    @Null(groups = OnCreate.class) @NotNull(groups = OnUpdate.class) Long id,
    @NotBlank String title,
    @NotNull @Min(5) @Max(30) Integer capacity) {
}
