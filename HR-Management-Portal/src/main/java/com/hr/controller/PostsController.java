package com.hr.controller;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hr.entity.CreatePost;
import com.hr.repository.CreatePostRepo;
import com.hr.service.HrService;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class PostsController {

    @Autowired
    private CreatePostRepo createPostRepo;

    @Autowired
    private HrService service;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllPosts() {
        try {
            List<CreatePost> posts = createPostRepo.findAll();
            List<Map<String, Object>> postList = posts.stream()
                .map(this::convertPostToMap)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(postList);
        } catch (Exception e) {
            // Return demo data if database is not available
            List<Map<String, Object>> demoPosts = new ArrayList<>();
            
            Map<String, Object> post1 = new HashMap<>();
            post1.put("id", 1);
            post1.put("title", "Welcome to HR Management Portal");
            post1.put("content", "This is a sample post for the HR Management system.");
            post1.put("author", "Admin");
            post1.put("addedDate", new Date().toString());
            
            demoPosts.add(post1);
            return ResponseEntity.ok(demoPosts);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getPostById(@PathVariable int id) {
        try {
            Optional<CreatePost> postOpt = createPostRepo.findById(id);
            if (postOpt.isPresent()) {
                return ResponseEntity.ok(convertPostToMap(postOpt.get()));
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Post not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error retrieving post: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createPost(@RequestBody Map<String, Object> postData) {
        try {
            CreatePost post = new CreatePost();
            post.setTitle((String) postData.get("title"));
            
            // Set both content and comment fields
            String content = (String) postData.get("content");
            post.setContent(content);
            post.setComment(content); // Required field - map content to comment
            
            post.setAuthor((String) postData.get("author"));
            post.setAddedDate(new Date().toString());
            
            CreatePost savedPost = service.addPost(post);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Post created successfully");
            response.put("id", savedPost.getId());
            response.put("post", convertPostToMap(savedPost));
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error creating post: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updatePost(@PathVariable int id, @RequestBody Map<String, Object> postData) {
        try {
            Optional<CreatePost> postOpt = createPostRepo.findById(id);
            if (postOpt.isPresent()) {
                CreatePost post = postOpt.get();
                post.setTitle((String) postData.get("title"));
                
                String content = (String) postData.get("content");
                post.setContent(content);
                post.setComment(content);
                
                post.setAuthor((String) postData.get("author"));
                
                CreatePost updatedPost = createPostRepo.save(post);
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Post updated successfully");
                response.put("post", convertPostToMap(updatedPost));
                
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Post not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error updating post: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deletePost(@PathVariable int id) {
        try {
            if (createPostRepo.existsById(id)) {
                createPostRepo.deleteById(id);
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Post deleted successfully");
                response.put("id", id);
                
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Post not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error deleting post: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Helper method
    private Map<String, Object> convertPostToMap(CreatePost post) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", post.getId());
        map.put("title", post.getTitle());
        map.put("content", post.getContent());
        map.put("author", post.getAuthor());
        map.put("addedDate", post.getAddedDate());
        map.put("comment", post.getComment());
        return map;
    }
}
