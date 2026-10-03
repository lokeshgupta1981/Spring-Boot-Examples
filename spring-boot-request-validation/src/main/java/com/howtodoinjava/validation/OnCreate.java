package com.howtodoinjava.validation;

import jakarta.validation.groups.Default;

/**
 * Validation group for create requests. Extends Default so that constraints
 * without a group are checked too.
 */
public interface OnCreate extends Default {
}
