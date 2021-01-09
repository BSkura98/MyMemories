package com.bartlomiejskura.mymemories.repository;

import com.bartlomiejskura.mymemories.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TagRepository extends JpaRepository<Tag, Long> {
    @Query("SELECT t FROM Tag t WHERE t.name = ?1 AND t.user.ID = ?2")
    Tag findByNameAndUserId(String name, Long userId);
}
