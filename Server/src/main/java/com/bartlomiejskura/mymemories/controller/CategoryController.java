package com.bartlomiejskura.mymemories.controller;

import com.bartlomiejskura.mymemories.exception.EntityNotFoundException;
import com.bartlomiejskura.mymemories.exception.ForbiddenException;
import com.bartlomiejskura.mymemories.model.Category;
import com.bartlomiejskura.mymemories.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/getAll")
    @PreAuthorize("#email.equals(authentication.name)")
    public List<Category> getAll(@RequestParam(value = "email") String email){
        return categoryService.getAllForUser(email);
    }

    @PostMapping
    @PreAuthorize("#category.user.email.equals(authentication.name)")
    public Category addCategory(@RequestBody Category category){
        return categoryService.addCategory(category);
    }

    @PostMapping("/addCategories")
    public List<Category> addCategories(@RequestBody List<Category> categories){
        categories.removeIf(c -> !c.getUser().getEmail().equals(SecurityContextHolder.getContext().getAuthentication().getName()));

        List<Category> resultCategories = new ArrayList<>();
        for(Category category : categories){
            resultCategories.add(categoryService.addCategory(category));
        }
        return resultCategories;
    }

    @GetMapping
    public Category getCategory(@RequestParam(name="categoryId")Long categoryId){
        try{
            Category category = categoryService.getCategory(categoryId);
            if(!category.getUser().getEmail().equals(SecurityContextHolder.getContext().getAuthentication().getName())){
                throw new ForbiddenException();
            }
            return category;
        }catch (EntityNotFoundException e){
            return null;
        }
    }

    @PutMapping
    @PreAuthorize("#category.user.email.equals(authentication.name)")
    public Category editCategory(@RequestBody Category category){
        return categoryService.editCategory(category);
    }

    @DeleteMapping
    public void deleteCategory(@RequestParam(name="categoryId")Long categoryId){
        try{
            if(!categoryService.getCategory(categoryId).getUser().getEmail().equals(SecurityContextHolder.getContext().getAuthentication().getName())){
                throw new ForbiddenException();
            }
            categoryService.deleteCategory(categoryId);
        }catch (EntityNotFoundException ignored){

        }
    }
}
