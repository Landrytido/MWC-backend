package com.mywebcompanion.backendspring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.mywebcompanion.backendspring.service.ExampleService;
import com.mywebcompanion.backendspring.model.ExampleModel; // Assurez-vous d'avoir un modèle approprié

import java.util.List;

@RestController
public class ExampleController {

    @Autowired
    private ExampleService exampleService;

    @GetMapping("/items")
    public List<ExampleModel> getAllItems() {
        return exampleService.getAllItems();
    }

    @PostMapping("/items")
    public ExampleModel createItem(@RequestBody ExampleModel item) {
        return exampleService.createItem(item);
    }
}