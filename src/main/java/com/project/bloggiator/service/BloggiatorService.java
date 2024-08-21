package com.project.bloggiator.service;

import com.project.bloggiator.entity.BlogEntity;
import com.project.bloggiator.entity.User;
import com.project.bloggiator.repo.BloggiatorRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class BloggiatorService {

    private static final Logger log = LoggerFactory.getLogger(BloggiatorService.class);
    @Autowired
    private BloggiatorRepository bloggiatorRepository;

    @Autowired
    UserService userService;

    @Transactional
    public void saveEntry(BlogEntity blogEntry, String userName) {
        try {
            User user = userService.findByUserName(userName);
            blogEntry.setDate(LocalDateTime.now());
            BlogEntity save = bloggiatorRepository.save(blogEntry);
            user.getBlogEntries().add(save);
            userService.saveUser(user);
        }
        catch (Exception e){

            throw new RuntimeException("An error occured while saving the " +
                    "entry!",e);
        }
    }
    public void saveEntry(BlogEntity blogEntry) {
            bloggiatorRepository.save(blogEntry);
    }

    public Optional<BlogEntity> blogById(ObjectId Id) {
        return bloggiatorRepository.findById(Id);
    }

    @Transactional
    public boolean deleteBlogById(ObjectId Id, String userName) {
        boolean removed=false;
        try
        {
            User user = userService.findByUserName(userName);
            removed = user.getBlogEntries().removeIf(x -> x.getId().equals(Id));
            if (removed) {
                userService.saveUser(user);
                bloggiatorRepository.deleteById(Id);
            }
        }
        catch (Exception e){
            log.error("Error ",e);
            throw new RuntimeException("An error occured while deleting the " +
                    "entry");
        }
        return removed;
    }

//    public List<BlogEntity> findByUserName(String userName){
//
//    }
}
