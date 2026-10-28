package com.howtodoinjava.jpatest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
class PurchaseOrderRepositoryTest {
    @Autowired TestEntityManager entityManager;
    @Autowired PurchaseOrderRepository repository;

    @Test
    void findsOrderByItsBusinessNumber() {
        entityManager.persistAndFlush(new PurchaseOrder("PO-101", "one@example.com"));
        entityManager.clear();

        assertThat(repository.findByOrderNumber("PO-101"))
            .get().extracting(PurchaseOrder::getCustomerEmail)
            .isEqualTo("one@example.com");
    }

    @Test
    void filtersOrdersByCustomerEmail() {
        entityManager.persist(new PurchaseOrder("PO-101", "one@example.com"));
        entityManager.persist(new PurchaseOrder("PO-102", "one@example.com"));
        entityManager.persist(new PurchaseOrder("PO-103", "two@example.com"));
        entityManager.flush();
        entityManager.clear();

        assertThat(repository.findByCustomerEmail("one@example.com"))
            .extracting(PurchaseOrder::getOrderNumber)
            .containsExactlyInAnyOrder("PO-101", "PO-102");
    }

    @Test
    void returnsEmptyForUnknownOrderNumber() {
        assertThat(repository.findByOrderNumber("PO-999")).isEmpty();
    }
}
