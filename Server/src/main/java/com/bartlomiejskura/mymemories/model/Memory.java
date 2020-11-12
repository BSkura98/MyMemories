package com.bartlomiejskura.mymemories.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Memory {
    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String description;

    public Memory(){}

    public Memory(String description){
        this.description = description;
    }

    public Long getId(){
        return id;
    }

    public String getDescription(){
        return description;
    }
}
