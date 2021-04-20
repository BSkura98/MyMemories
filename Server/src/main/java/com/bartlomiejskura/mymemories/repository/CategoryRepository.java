package com.bartlomiejskura.mymemories.repository;

import com.bartlomiejskura.mymemories.model.Category;
import com.bartlomiejskura.mymemories.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT t FROM Category t WHERE t.name = ?1 AND t.user.ID = ?2")
    Category findByNameAndUserId(String name, Long userId);

    @Query("SELECT t FROM Category t WHERE t.name = ?1 AND t.user.email = ?2")
    Category findByNameAndEmail(String name, String email);

    List<Category> findAllByUser(User user);
}
