package com.project.bloggiator.controller;

import com.project.bloggiator.entity.BlogEntity;
import com.project.bloggiator.entity.User;
import com.project.bloggiator.service.BloggiatorService;
import com.project.bloggiator.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/bloggiator")
public class BlogController {

    @Autowired
    private BloggiatorService bloggiatorService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getAllBlogsByUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userService.findByUserName(userName);
        List<BlogEntity> allBlog = user.getBlogEntries();
        if (allBlog != null && !allBlog.isEmpty()) {
            return new ResponseEntity<>(allBlog, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    }

    @PostMapping
    public ResponseEntity<BlogEntity> createBlogs(@RequestBody BlogEntity myEntry) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            bloggiatorService.saveEntry(myEntry, userName);
            return new ResponseEntity<>(myEntry, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("id/{ID}")
    public ResponseEntity<BlogEntity> blogById(@PathVariable ObjectId ID) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userService.findByUserName(userName);
        List<BlogEntity> collect = user.getBlogEntries().stream().filter(x -> x.getId().equals(ID)).collect(Collectors.toList());
        if (!collect.isEmpty()) {
            Optional<BlogEntity> blogEntity = bloggiatorService.blogById(ID);
            if (blogEntity.isPresent()) {
                return new ResponseEntity<>(blogEntity.get(), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("id/{ID}")
    public ResponseEntity<?> deleteById(@PathVariable ObjectId ID) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        boolean removed = bloggiatorService.deleteBlogById(ID, userName);
        if (removed) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("id/{ID}")
    public ResponseEntity<?> updateBlogById(@PathVariable ObjectId ID, @RequestBody BlogEntity newEntry) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userService.findByUserName(userName);
        List<BlogEntity> collect = user.getBlogEntries().stream().filter(x -> x.getId().equals(ID)).collect(Collectors.toList());
        if (!collect.isEmpty()) {
            Optional<BlogEntity> blogEntity = bloggiatorService.blogById(ID);
            if (blogEntity.isPresent()) {
                BlogEntity oldEntry=blogEntity.get();
                oldEntry.setTitle(newEntry.getTitle() != null && !newEntry.getTitle().isEmpty() ? newEntry.getTitle() : oldEntry.getTitle());
                oldEntry.setContent(newEntry.getContent() != null && !newEntry.getContent().isEmpty() ? newEntry.getContent() : oldEntry.getContent());
                bloggiatorService.saveEntry(oldEntry);
                return new ResponseEntity<>(oldEntry, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
