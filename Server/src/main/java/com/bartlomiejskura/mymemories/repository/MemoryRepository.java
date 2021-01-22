package com.bartlomiejskura.mymemories.repository;

import com.bartlomiejskura.mymemories.model.Memory;
import com.bartlomiejskura.mymemories.model.Tag;
import com.bartlomiejskura.mymemories.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemoryRepository extends JpaRepository<Memory, Long> {
    List<Memory> findAllByMemoryOwner(User memoryOwner);
    List<Memory> findAllByTag(Tag tag);
    //List<Memory> findAllByMemoryFriends_Id(Long id);

    @Query("SELECT m FROM Memory m WHERE m.memoryOwner = ?1 AND m.tag = ?2")
    List<Memory> findAllByMemoryOwnerAndTag(User memoryOwner, Tag tag);
}
