package com.howtodoinjava.springboothibernate;

import java.math.BigDecimal;

public record NewRepairJob(Long mechanicId, String bikeModel, String problem, BigDecimal cost) {
}
