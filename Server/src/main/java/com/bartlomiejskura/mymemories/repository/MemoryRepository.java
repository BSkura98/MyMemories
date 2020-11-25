package com.bartlomiejskura.mymemories.repository;

import com.bartlomiejskura.mymemories.model.Memory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemoryRepository extends JpaRepository<Memory, Long> {
}
