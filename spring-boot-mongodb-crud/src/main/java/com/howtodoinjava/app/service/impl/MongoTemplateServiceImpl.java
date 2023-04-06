package com.howtodoinjava.app.service.impl;

import com.howtodoinjava.app.model.Item;
import com.howtodoinjava.app.service.MongoTemplateService;
import com.mongodb.client.result.DeleteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MongoTemplateServiceImpl implements MongoTemplateService {

  @Autowired
  private MongoTemplate mongoTemplate;

  @Override
  public Item add(Item item) {
    return mongoTemplate.save(item);
  }

  @Override
  public Item update(Item item) {
    return mongoTemplate.save(item);
  }

  @Override
  public boolean delete(String id) {
    Query query = new Query();
    query.addCriteria(Criteria.where("id").is(id));
    DeleteResult result = mongoTemplate.remove(query, Item.class);
    return result.wasAcknowledged();
  }

  @Override
  public boolean delete(Item item) {
    DeleteResult result = mongoTemplate.remove(item);
    return result.wasAcknowledged();
  }

  @Override
  public List<Item> getAll() {
    return mongoTemplate.findAll(Item.class);
  }

  @Override
  public List<Item> getAll(Integer page, Integer size) {
    Sort sort = Sort.by("category").ascending();
    Pageable pageable = PageRequest.of(page - 1, size, sort);
    Query query = new Query().with(pageable);

    Page<Item> pagedResult = PageableExecutionUtils.getPage(
            mongoTemplate.find(query, Item.class),
            pageable,
            () -> mongoTemplate.count(query, Item.class));

    return pagedResult.getContent();

  }

  @Override
  public Item getById(String id) {
    return mongoTemplate.findById(id, Item.class);
  }

  @Override
  public List<Item> search(String name, Integer minQuantity,
                           Integer maxQuantity, String category) {

    Query query = new Query();
    List<Criteria> criterias = new ArrayList<>();

    if (name != null && !name.isEmpty()) {
      criterias.add(Criteria.where("name").is(name));
    }

    if (minQuantity != null && minQuantity >= 0) {
      criterias.add(Criteria.where("quantity").gte(minQuantity));
    }

    if (maxQuantity != null && maxQuantity >= 0) {
      criterias.add(Criteria.where("quantity").lte(maxQuantity));
    }

    if (category != null && !category.isEmpty()) {
      criterias.add(Criteria.where("category").is(category));
    }

    if (!criterias.isEmpty()) {
      Criteria criteria = new Criteria()
          .andOperator(criterias.toArray(new Criteria[criterias.size()]));
      query.addCriteria(criteria);
    }

    List<Item> items = mongoTemplate.find(query, Item.class);

    return items;
  }
}
