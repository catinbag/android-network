package org.demo.network.core.dto;

/** JSONPlaceholder post. Used for reading and for the request body of POST and PUT. */
public class PostDto {
    public int userId;
    public int id;
    public String title;
    public String body;

    public PostDto() {
    }

    public PostDto(int userId, String title, String body) {
        this.userId = userId;
        this.title = title;
        this.body = body;
    }
}
