package com.shareable.demo.controller;

import com.shareable.demo.model.Comment;
import com.shareable.demo.model.Post;
import com.shareable.demo.model.User;
import com.shareable.demo.repository.CommentRepository;
import com.shareable.demo.repository.PostRepository;
import com.shareable.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class CommentController {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @PostMapping("/{postId}")
    public ResponseEntity<?> addComment(@PathVariable Long postId,
                                        @RequestBody Map<String, String> body,
                                        @RequestHeader("X-User-Id") Long userId) {

        System.out.println(userId);

        String content = body.get("content");
        Optional<Post> post = postRepository.findById(postId);
        Optional<User> user = userRepository.findById(userId);

        System.out.println(user);
        System.out.println(content);

        if (post.isPresent() && user.isPresent()) {
            Comment comment = new Comment();
            comment.setContent(content);
            comment.setUser(user.get());
            comment.setPost(post.get());

            commentRepository.save(comment);
            return ResponseEntity.ok(comment);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("invalid post or user");
    }

    @GetMapping("/{postId}")
    public List<Comment> getCommentsByPost(@PathVariable Long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtDesc(postId);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable Long commentId,
                                           @RequestBody Map<String, String> body,
                                           @RequestHeader("X-User-Id") Long userId) {
        return commentRepository.findById(commentId).map(comment -> {
            if (!comment.getUser().getId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            comment.setContent(body.get("content"));
            comment.setEdited(true);
            return ResponseEntity.ok(commentRepository.save(comment));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId,
                                           @RequestHeader("X-User-Id") Long userId) {
        return commentRepository.findById(commentId).map(comment -> {
            if (!comment.getUser().getId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            commentRepository.delete(comment);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }
}

