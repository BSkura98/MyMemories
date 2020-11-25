package com.bartlomiejskura.mymemories.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long ID;

    @ManyToMany
    private List<Memory> sharedMemories;
}
