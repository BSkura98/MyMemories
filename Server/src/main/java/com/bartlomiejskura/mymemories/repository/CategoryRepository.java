package com.bartlomiejskura.mymemories.repository;

import com.bartlomiejskura.mymemories.model.Category;
import com.bartlomiejskura.mymemories.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT t FROM Category t WHERE t.name = ?1 AND t.user.ID = ?2")
    Category findByNameAndUserId(String name, Long userId);

    List<Category> findAllByUser(User user);
}
