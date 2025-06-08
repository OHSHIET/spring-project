package com.shareable.demo.controller;

import com.shareable.demo.model.User;
import com.shareable.demo.repository.PostRepository;
import com.shareable.demo.model.Post;
import com.shareable.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class PostController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/new")
    public ResponseEntity<?> createPost(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String content = body.get("content");
        String title = body.get("title");

        if (username == null || content == null || content.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("username/content required");
        }

        User user = userRepository.findByUsername(username)
                .orElseGet(() -> userRepository.save(new User(username)));

        Post post = new Post(content, user, title);
        postRepository.save(post);

        return ResponseEntity.ok(post);
    }

    @GetMapping
    public List<Post> getAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Long userId = Long.valueOf(body.get("userId"));
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body("user not found");
        }

        return postRepository.findById(id).map(post -> {
            if (!post.getUser().getId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            post.setTitle(body.get("title"));
            post.setContent(body.get("content"));
            post.setEdited(true);
            postRepository.save(post);
            return ResponseEntity.ok(post);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id, @RequestHeader("X-User-Id") Long userId) {
        return postRepository.findById(id).map(post -> {
            if (!post.getUser().getId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            postRepository.delete(post);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
