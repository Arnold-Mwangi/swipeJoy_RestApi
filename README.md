# Project: Simple Blog Platform

## Entities and Relationships
- **User**: Represents a user in the system. A user can have multiple posts.
- **Post**: Represents a blog post. A post belongs to a user and can have multiple comments.
- **Comment**: Represents a comment on a blog post. A comment belongs to a post.

## Endpoints

### User Endpoints:
- **GET /users**: Get all users
- **POST /users**: Create a new user
- **GET /users/{id}**: Get a user by ID
- **PUT /users/{id}**: Update a user by ID
- **DELETE /users/{id}**: Delete a user by ID

### Post Endpoints:
- **GET /posts**: Get all posts
- **POST /posts**: Create a new post
- **GET /posts/{id}**: Get a post by ID
- **PUT /posts/{id}**: Update a post by ID
- **DELETE /posts/{id}**: Delete a post by ID
- **GET /posts/user/{userId}**: Get all posts by a specific user

### Comment Endpoints:
- **GET /comments**: Get all comments
- **POST /comments**: Create a new comment
- **GET /comments/{id}**: Get a comment by ID
- **PUT /comments/{id}**: Update a comment by ID
- **DELETE /comments/{id}**: Delete a comment by ID
- **GET /comments/post/{postId}**: Get all comments for a specific post
# swipeJoy_RestApi
