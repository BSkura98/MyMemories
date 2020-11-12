package com.bartlomiejskura.mymemories.repository;

import com.bartlomiejskura.mymemories.model.Memory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemoryRepository extends CrudRepository<Memory, Long> {
}
