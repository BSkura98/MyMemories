package com.bartlomiejskura.mymemories.controller;

import com.bartlomiejskura.mymemories.exception.EntityNotFoundException;
import com.bartlomiejskura.mymemories.model.Category;
import com.bartlomiejskura.mymemories.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/getAll")
    public List<Category> getAll(@RequestParam(value = "userId", required = false) Long userId){
        if (userId != null) {
            return categoryService.getAllForUser(userId);
        }
        return categoryService.getAll();
    }

    @PostMapping
    public Category addCategory(@RequestBody Category category){
        return categoryService.addCategory(category);
    }

    @PostMapping("/addCategories")
    public List<Category> addCategories(@RequestBody List<Category> categories){
        List<Category> resultCategories = new ArrayList<>();
        for(Category category : categories){
            resultCategories.add(categoryService.addCategory(category));
        }
        return resultCategories;
    }

    @GetMapping
    public Category getCategory(@RequestParam(name="categoryId")Long categoryId){
        try{
            return categoryService.getCategory(categoryId);
        }catch (EntityNotFoundException e){
            return null;
        }
    }

    @PutMapping
    public Category editCategory(@RequestBody Category category){
        return categoryService.editCategory(category);
    }

    @DeleteMapping
    public void deleteCategory(@RequestParam(name="categoryId")Long categoryId){
        categoryService.deleteCategory(categoryId);
    }
}
