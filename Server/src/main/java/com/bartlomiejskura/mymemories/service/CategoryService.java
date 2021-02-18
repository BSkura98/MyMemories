package com.bartlomiejskura.mymemories.service;

import com.bartlomiejskura.mymemories.exception.EntityNotFoundException;
import com.bartlomiejskura.mymemories.model.Category;
import com.bartlomiejskura.mymemories.model.User;
import com.bartlomiejskura.mymemories.repository.CategoryRepository;
import com.bartlomiejskura.mymemories.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Category> getAll(){
        return categoryRepository.findAll();
    }

    public List<Category> getAllForUser(Long userId) {
        User user = this.userRepository.findById(userId).orElseThrow();
        return this.categoryRepository.findAllByUser(user);
    }

    public Category addCategory(Category category){
        if(category.getUser()==null){
            return null;
        }
        try{
            Category t = categoryRepository.findByNameAndUserId(category.getName(), category.getUser().getID());
            if(t != null){
                return t;
            }
        }catch (NullPointerException ignored){ }
        return categoryRepository.save(category);
    }

    public Category getCategory(Long categoryId) throws EntityNotFoundException {
        return categoryRepository.findById(categoryId).orElseThrow(EntityNotFoundException::new);
    }

    public Category editCategory(Category category){
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long categoryId){
        categoryRepository.deleteById(categoryId);
    }
}
