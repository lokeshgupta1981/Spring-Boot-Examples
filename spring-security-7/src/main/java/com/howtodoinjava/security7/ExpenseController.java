package com.howtodoinjava.security7;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExpenseController {

  public record Expense(String name, int amount) {
  }

  private final Map<String, Integer> expenses = new ConcurrentHashMap<>(Map.of("taxi", 25, "lunch", 12));

  @GetMapping("/public/currencies")
  public List<String> currencies() {
    return List.of("USD", "EUR");
  }

  @GetMapping("/expenses")
  public Map<String, Integer> expenses() {
    return expenses;
  }

  @PostMapping("/expenses")
  @ResponseStatus(HttpStatus.CREATED)
  public Expense add(@RequestBody Expense expense) {
    expenses.put(expense.name(), expense.amount());
    return expense;
  }

  @DeleteMapping("/expenses/{name}")
  public Map<String, Integer> delete(@PathVariable String name) {
    expenses.remove(name);
    return expenses;
  }

  @GetMapping("/reports/monthly")
  public Map<String, Integer> monthlyReport() {
    int total = expenses.values().stream().mapToInt(Integer::intValue).sum();
    return Map.of("total", total);
  }

  @GetMapping("/audit/log")
  public List<String> auditLog() {
    return List.of("anna deleted taxi");
  }
}
