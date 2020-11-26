package com.bartlomiejskura.mymemories.repository;

import com.bartlomiejskura.mymemories.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
}
