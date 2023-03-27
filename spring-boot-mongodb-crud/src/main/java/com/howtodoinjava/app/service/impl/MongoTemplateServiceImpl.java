package com.howtodoinjava.app.service.impl;

import com.howtodoinjava.app.model.GroceryItem;
import com.howtodoinjava.app.service.MongoTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public GroceryItem addGrocery(GroceryItem groceryItem) {
        return mongoTemplate.save(groceryItem);
    }

    @Override
    public GroceryItem updateGrocery(GroceryItem groceryItem) {
        //GroceryItem fetchedItem = mongoTemplate.findById(groceryItem.getId(), GroceryItem.class);
        //fetchedItem.setName(groceryItem.getName());
        //fetchedItem.setQuantity(groceryItem.getQuantity());
        //fetchedItem.setCategory(groceryItem.getCategory());
        //mongoTemplate.save(fetchedItem);
        return mongoTemplate.save(groceryItem);
    }

    @Override
    public String deleteGrocery(Integer id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(id));
        mongoTemplate.remove(query, GroceryItem.class);
        return "Success";
    }

    @Override
    public List<GroceryItem> getAllGroceries() {
        return mongoTemplate.findAll(GroceryItem.class);
    }

    @Override
    public GroceryItem getGroceryById(Integer id) {
        return mongoTemplate.findById(id, GroceryItem.class);
    }

    @Override
    public List<GroceryItem> searchGroceries(String name, Integer minQuantity, Integer maxQuantity,
                                             String category) {

        Query query = new Query();
        List<Criteria> criterias = new ArrayList<>();

        if(name !=null && !name.isEmpty()) {
            criterias.add(Criteria.where("name").is(name));
        }

        if(minQuantity !=null && maxQuantity !=null) {
            criterias.add(Criteria.where("quantity").gte(minQuantity).lte(maxQuantity));
        }

        if(category !=null && !category.isEmpty()) {
            criterias.add(Criteria.where("category").is(category));
        }

        if(!criterias.isEmpty()) {
            Criteria criteria = new Criteria().andOperator(criterias.toArray(new Criteria[criterias.size()]));

            query.addCriteria(criteria);

        }

        List<GroceryItem> groceryItems = mongoTemplate.find(query, GroceryItem.class);

        return groceryItems;
    }
}
