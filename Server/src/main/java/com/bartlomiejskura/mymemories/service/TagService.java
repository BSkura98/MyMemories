package com.bartlomiejskura.mymemories.service;

import com.bartlomiejskura.mymemories.exception.EntityNotFoundException;
import com.bartlomiejskura.mymemories.model.Tag;
import com.bartlomiejskura.mymemories.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {
    @Autowired
    private TagRepository tagRepository;

    public List<Tag> getAll(){
        return tagRepository.findAll();
    }

    public Tag addTag(Tag tag){
        if(tag.getUser()==null){
            return null;
        }
        try{
            Tag t = tagRepository.findByNameAndUserId(tag.getName(), tag.getUser().getID());
            if(t != null){
                return t;
            }
        }catch (NullPointerException ignored){ }
        return tagRepository.save(tag);
    }

    public Tag getTag(Long tagId) throws EntityNotFoundException {
        return tagRepository.findById(tagId).orElseThrow(EntityNotFoundException::new);
    }

    public Tag editTag(Tag tag){
        return tagRepository.save(tag);
    }

    public void deleteTag(Long tagId){
        tagRepository.deleteById(tagId);
    }
}
