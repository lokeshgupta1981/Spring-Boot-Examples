package com.howtodoinjava.quartz.library;

import java.time.LocalDate;
import java.util.List;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class LibraryRepository {

  private final JdbcClient jdbc;

  public LibraryRepository(JdbcClient jdbc) {
    this.jdbc = jdbc;
  }

  public List<Loan> openLoansDueOn(LocalDate dueDate) {
    return jdbc.sql("select * from loan where returned_on is null and due_date = ? order by id")
        .param(dueDate)
        .query(Loan.class)
        .list();
  }

  public List<Loan> overdueLoans(LocalDate today) {
    return jdbc.sql("select * from loan where returned_on is null and due_date < ? order by id")
        .param(today)
        .query(Loan.class)
        .list();
  }

  public Loan findLoan(long id) {
    return jdbc.sql("select * from loan where id = ?").param(id).query(Loan.class).single();
  }

  public void saveFine(Fine fine, LocalDate calculatedOn) {
    jdbc.sql("merge into fine (loan_id, days_late, amount, calculated_on) key (loan_id) values (?, ?, ?, ?)")
        .params(fine.loanId(), fine.daysLate(), fine.amount(), calculatedOn)
        .update();
  }

  public List<Fine> finesCalculatedOn(LocalDate day) {
    return jdbc.sql("""
            select f.loan_id, l.member, l.book, f.days_late, f.amount
            from fine f join loan l on l.id = f.loan_id
            where f.calculated_on = ? order by f.loan_id""")
        .param(day)
        .query(Fine.class)
        .list();
  }

  public void saveMessage(SentMessage message) {
    jdbc.sql("insert into sent_message (loan_id, kind, recipient, text) values (?, ?, ?, ?)")
        .params(message.loanId(), message.kind(), message.recipient(), message.text())
        .update();
  }

  public List<SentMessage> sentMessages() {
    return jdbc.sql("select loan_id, kind, recipient, text from sent_message order by id")
        .query(SentMessage.class)
        .list();
  }

  public void deleteSentMessagesAndFines() {
    jdbc.sql("delete from sent_message").update();
    jdbc.sql("delete from fine").update();
  }
}
