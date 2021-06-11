package com.bartlomiejskura.mymemories.service;

import com.bartlomiejskura.mymemories.exception.EntityNotFoundException;
import com.bartlomiejskura.mymemories.model.Memory;
import com.bartlomiejskura.mymemories.model.Category;
import com.bartlomiejskura.mymemories.model.User;
import com.bartlomiejskura.mymemories.repository.MemoryRepository;
import com.bartlomiejskura.mymemories.repository.CategoryRepository;
import com.bartlomiejskura.mymemories.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MemoryService {
    @Autowired
    private MemoryRepository memoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Memory> getAll(){
        return memoryRepository.findAll();
    }

    public List<Memory> getAllForUser(String email) {
        User user = this.userRepository.findByEmail(email);
        return this.memoryRepository.findAllByMemoryOwner(user);
    }

    public List<Memory> getNewestForUser(Long userId) {
        User user = this.userRepository.findById(userId).orElseThrow();
        LocalDateTime newestDate = this.memoryRepository.findAllByMemoryOwner(user).stream()
                .map(Memory::getDate)
                .filter(date -> date.getYear() <= LocalDateTime.now().getYear() &&
                        date.getDayOfYear() <= LocalDateTime.now().getDayOfYear())
                .max(LocalDateTime::compareTo)
                .get();
        return this.memoryRepository.findAllByMemoryOwner(user).stream()
                .filter(memory -> memory.getDate().getYear()==newestDate.getYear() &&
                        memory.getDate().getDayOfYear()==newestDate.getDayOfYear())
                .collect(Collectors.toList());
    }

    public List<Memory> getAllForCategory(Long categoryId){
        Category category = this.categoryRepository.findById(categoryId).orElseThrow();
        return this.memoryRepository.findAllByCategory(category);
    }

    public List<Memory> getAllForUserAndCategory(String email, Long categoryId){
        User user = this.userRepository.findByEmail(email);
        Category category = this.categoryRepository.findById(categoryId).orElseThrow();
        return this.memoryRepository.findAllByMemoryOwnerAndCategory(user, category);
    }

    public List<Memory> getAllSharedMemoriesForUser(String email){
        User user = this.userRepository.findByEmail(email);
        return user.getSharedMemories();
    }

    public List<Memory> getAllForUserAndDate(String email, LocalDateTime time){
        User user = this.userRepository.findByEmail(email);

        return this.memoryRepository.findAllByMemoryOwner(user).stream()
                .filter(memory -> memory.getDate().getYear()==time.getYear() &&
                        memory.getDate().getDayOfYear()==time.getDayOfYear())
                .collect(Collectors.toList());
    }

    public List<Memory> getAllSharedMemoriesForUserAndDate(String email, LocalDateTime time){
        User user = this.userRepository.findByEmail(email);

        return user.getSharedMemories().stream()
                .filter(memory -> memory.getDate().getYear()==time.getYear() &&
                        memory.getDate().getDayOfYear()==time.getDayOfYear())
                .collect(Collectors.toList());
    }

    public List<Memory> getMemories(String email, String keyword, Boolean hasImage, LocalDateTime creationDateStart,
                                    LocalDateTime creationDateEnd, LocalDateTime dateStart, LocalDateTime dateEnd,
                                    String memoryPriorities, Boolean publicToFriends, Boolean isSharedMemory,
                                    String categories){
        User user = this.userRepository.findByEmail(email);
        List<Memory> memories;
        if(isSharedMemory!=null){
            if(isSharedMemory){
                memories = new LinkedList<>(user.getSharedMemories());
            }else{
                memories = new LinkedList<>(memoryRepository.findAllByMemoryOwner(user));
            }
        }else{
            memories = new LinkedList<>(memoryRepository.findAllByMemoryOwner(user));
            memories.addAll(user.getSharedMemories());
        }

        List<Category> categoryList=new ArrayList<>();
        if (categories != null) {
            String[] categoryArray = categories.split(" ");
            for(String categoryName:categoryArray){
                categoryList.add(categoryRepository.findByNameAndEmail(categoryName, email));
            }
        }

        List<Integer> memoryPriorityList = new ArrayList<>();
        if(memoryPriorities!=null){
            String[] memoryPriorityArray = memoryPriorities.split(" ");
            for(String memoryPriority:memoryPriorityArray){
                memoryPriorityList.add(Integer.parseInt(memoryPriority));
            }
        }

        return memories.stream()
                .filter(keyword!=null?memory -> memory.getDescription().toLowerCase().contains(keyword.toLowerCase())||
                        memory.getTitle().toLowerCase().contains(keyword.toLowerCase()): memory -> true)
                .filter(hasImage!=null?memory -> (memory.getImageUrl()!=null&&!memory.getImageUrl().isEmpty())==hasImage:memory -> true)
                .filter(creationDateStart!=null?memory -> memory.getModificationDate().isAfter(creationDateStart): memory -> true)
                .filter(creationDateEnd!=null?memory -> memory.getModificationDate().isBefore(creationDateEnd): memory -> true)
                .filter(dateStart!=null?memory -> memory.getDate().isAfter(dateStart):memory -> true)
                .filter(dateEnd!=null?memory -> memory.getDate().isBefore(dateEnd):memory -> true)
                .filter(!memoryPriorityList.isEmpty()?memory -> memoryPriorityList.contains(memory.getPriority()): memory -> true)
                .filter(publicToFriends!=null?memory -> memory.getIsPublicToFriends().equals(publicToFriends): memory -> true)
                .filter(!categoryList.isEmpty()?memory -> !Collections.disjoint(memory.getCategories(), categoryList):memory -> true)
                .collect(Collectors.toList());
    }

    public Memory addMemory(Memory memory){
        List<User> memoryFriends = memory.getMemoryFriends();
        if(memoryFriends!=null){
            for(User user:memoryFriends){
                if(user.getID()==null||user.getEmail()!=null){
                    user.setID(userRepository.findByEmail(user.getEmail()).getID());
                }
            }
            memory.setMemoryFriends(memoryFriends);
        }
        return memoryRepository.save(memory);
    }

    public Memory getMemory(Long memoryId) throws EntityNotFoundException {
        return memoryRepository.findById(memoryId).orElseThrow(EntityNotFoundException::new);
    }

    public Memory editMemory(Memory memory){
        return memoryRepository.save(memory);
    }

    public void deleteMemory(Long memoryId){
        memoryRepository.deleteById(memoryId);
    }

    public List<Memory> search(String keyword, Long userId){
        return memoryRepository.search(keyword).stream()
                .filter(memory -> memory.getMemoryOwner().getID().equals(userId)||
                        memory.getMemoryFriends().stream()
                                .anyMatch(user -> user.getID().equals(userId)))
                .collect(Collectors.toList());
    }

    public List<Memory> getFriendsPublicMemories(String email){
        User user = this.userRepository.findByEmail(email);
        List<User> friends = user.getFriends();
        List<Memory> publicMemories = new ArrayList<>();


        for(User u:friends){
            for(Memory m:memoryRepository.findAllByMemoryOwner(u)){
                if(m.getIsPublicToFriends()){
                    publicMemories.add(m);
                }
            }
        }

        return publicMemories;
    }

    public Memory deleteUserFromMemory(String email, Long memoryId) throws EntityNotFoundException {
        Memory memory = memoryRepository.findById(memoryId).orElseThrow(EntityNotFoundException::new);
        User user = userRepository.findByEmail(email);

        List<User> newTaggedUsers = memory.getMemoryFriends();
        newTaggedUsers.remove(user);
        memory.setMemoryFriends(newTaggedUsers);
        return memoryRepository.save(memory);
    }
}
