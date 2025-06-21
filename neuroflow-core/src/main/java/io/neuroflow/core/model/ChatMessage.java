package io.neuroflow.core.model;

public class ChatMessage {
    private String role; // system, user, assistant, function
    private String content;
    private String name; // function name when role is function

    // Getters and Setters
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
