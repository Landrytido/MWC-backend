package com.mywebcompanion.backendspring.service;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ExampleService {

    public List<String> getAllItems() {
        // Logic to retrieve all items
        return List.of("Item1", "Item2", "Item3");
    }

    public String getItemById(Long id) {
        // Logic to retrieve an item by its ID
        return "Item" + id;
    }
}