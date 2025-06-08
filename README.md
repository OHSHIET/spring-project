Very simple project built with Spring Boot, Angular, and MySQL.

Users can log in, create posts and comments, edit and delete them, all the data is stored in local database (`database_dump.sql` is provided).

Users log in without any password protection, the authentication data is stored in browser's local storage, and removed after user logs out. This way anyone can log in under any nickname and edit/delete anyone's posts or comments.

Logging in also serves purpose of registration, if user isn't in the database yet.

![Application design](https://i.imgur.com/K8JuIzG.png)