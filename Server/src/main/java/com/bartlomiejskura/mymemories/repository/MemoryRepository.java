package com.bartlomiejskura.mymemories.repository;

import com.bartlomiejskura.mymemories.model.Memory;
import com.bartlomiejskura.mymemories.model.Category;
import com.bartlomiejskura.mymemories.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemoryRepository extends JpaRepository<Memory, Long> {
    List<Memory> findAllByMemoryOwner(User memoryOwner);

    @Query("SELECT m FROM Memory m WHERE ?1 member m.categories")
    List<Memory> findAllByCategory(Category category);
    //List<Memory> findAllByMemoryFriends_Id(Long id);

    @Query("SELECT m FROM Memory m WHERE m.memoryOwner = ?1 AND ?2 member m.categories")
    List<Memory> findAllByMemoryOwnerAndCategory(User memoryOwner, Category category);

    @Query("SELECT m FROM Memory m WHERE m.longDescription like %?1% or m.shortDescription like %?1%")
    List<Memory> search(String keyword);
}
