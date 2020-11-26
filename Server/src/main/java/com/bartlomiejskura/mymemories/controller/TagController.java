package com.bartlomiejskura.mymemories.controller;

import com.bartlomiejskura.mymemories.exception.EntityNotFoundException;
import com.bartlomiejskura.mymemories.model.Tag;
import com.bartlomiejskura.mymemories.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tag")
public class TagController {
    @Autowired
    private TagService tagService;

    @GetMapping("/getAll")
    public List<Tag> getAll(){
        return tagService.getAll();
    }

    @PostMapping
    public Tag addTag(@RequestBody Tag tag){
        return tagService.addTag(tag);
    }

    @GetMapping
    public Tag getTag(@RequestParam(name="tagId")Long tagId){
        try{
            return tagService.getTag(tagId);
        }catch (EntityNotFoundException e){
            return null;
        }
    }

    @PutMapping
    public Tag editTag(@RequestBody Tag tag){
        return tagService.editTag(tag);
    }

    @DeleteMapping
    public void deleteTag(@RequestParam(name="tagId")Long tagId){
        tagService.deleteTag(tagId);
    }
}
