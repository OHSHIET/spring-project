import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import {tap} from "rxjs";
import {User} from "./models/user";
import {Post} from "./models/post";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './app.component.html',
})
export class AppComponent {
  username = '';
  newPost: string = '';
  title: string = '';
  posts: any = [];
  loggedInUser!: User | null;
  editingPost: Post | null = null;
  editingContent = '';
  editingTitle = '';
  newComments: { [postId: number]: string } = {};
  comments: { [postId: number]: any[] } = {};

  editingCommentId: number | null = null;
  editingContentComment: string = '';

  constructor(private http: HttpClient) {
  }

  getLoggedInUser(): User | null {
    if (typeof window !== 'undefined') {
      const userJson = localStorage.getItem('user');
      return userJson ? JSON.parse(userJson) : null;
    }
    return null;
  }


  ngOnInit() {
    const user = this.getLoggedInUser();
    if (user) {
      this.loggedInUser = user;
      this.loadPosts();
    }
  }

  login() {
    this.http.post<User>('http://localhost:8081/api/auth/login', {username: this.username}, {
      withCredentials: true
    }).pipe(
      tap(user => {
        console.log('User from response:', user);
        localStorage.setItem('user', JSON.stringify(user));
        this.loggedInUser = user;
      })
    ).subscribe({
      next: (response) => {
        this.loadPosts();
      },
      error: (err) => {
        console.error('Login error:', err);
      }
    });
  }

  logout(event: Event): void {
    event.preventDefault();
    localStorage.removeItem('user');
    this.loggedInUser = null;
  }

  createPost(): void {
    if (!this.newPost.trim()) return;

    this.http.post('http://localhost:8081/api/posts/new', {
      username: this.loggedInUser?.username,
      title: this.title.trim(),
      content: this.newPost.trim()
    }, {withCredentials: true}).subscribe(() => {
      this.newPost = '';
      this.title = '';
      this.loadPosts();
    });
  }

  loadPosts() {
    this.http.get<any>('http://localhost:8081/api/posts').subscribe((posts: any[]) => {
      this.posts = posts;
      console.log(posts);
      for (let post of posts) {
        this.loadComments(post.id);
      }
    });
  }

  startEdit(post: Post) {
    console.log(post.id);
    this.editingPost = post;
    this.editingTitle = post.title;
    this.editingContent = post.content;
  }

  cancelEdit() {
    this.editingPost = null;
    this.editingTitle = '';
    this.editingContent = '';
  }

  submitEdit(postId: number) {
    this.http.put(`http://localhost:8081/api/posts/${postId}`, {
      content: this.editingContent,
      title: this.editingTitle,
      userId: this.loggedInUser?.id
    }, {withCredentials: true}).subscribe(updatedPost => {
      this.loadPosts();
      this.cancelEdit();
    });
  }

  deletePost(postId: number) {
    const user = this.loggedInUser;

    this.http.delete(`http://localhost:8081/api/posts/${postId}`, {
      headers: {
        'X-User-Id': String(user?.id)
      },
      withCredentials: true
    }).subscribe(() => {
      this.loadPosts();
    }, error => {
      console.error('Delete failed', error);
    });
  }

  addComment(postId: number) {
    const user = this.loggedInUser;
    const content = this.newComments[postId];

    if (!content) return;

    this.http.post(`http://localhost:8081/api/comments/${postId}`, { content }, {
      headers: {
        'X-User-Id': String(user?.id)
      },
      withCredentials: true
    }).subscribe(() => {
      this.newComments[postId] = '';
      this.loadPosts();
    });
  }

  loadComments(postId: number) {
    this.http.get(`http://localhost:8081/api/comments/${postId}`).subscribe((res: any) => {
      console.log("123", res);
      this.comments[postId] = res;
    });
  }

  editComment(comment: any) {
    this.editingCommentId = comment.id;
    this.editingContentComment = comment.content;
  }

  saveComment(comment: any) {
    this.http.put(`http://localhost:8081/api/comments/${comment.id}`, { content: this.editingContentComment }, {
      headers: {
        'X-User-Id': String(this.loggedInUser?.id)
      }
    }).subscribe(() => {
      this.editingCommentId = null;
      this.loadComments(comment.post.id);
    });
  }

  deleteComment(comment: any) {
    this.http.delete(`http://localhost:8081/api/comments/${comment.id}`, {
      headers: {
        'X-User-Id': String(this.loggedInUser?.id)
      }
    }).subscribe(() => {
      this.loadComments(comment.post.id);
    });
  }

  cancelEditComment() {
    this.editingCommentId = null;
    this.editingContentComment = '';
  }

}
