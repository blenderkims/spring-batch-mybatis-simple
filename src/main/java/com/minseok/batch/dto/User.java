package com.minseok.batch.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * package      : com.minseok.batch.dto
 * class        : User
 * author       : blenderkims
 * date         : 2023/04/19
 * description  :
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class User {
    private String id;
    private String email;
    private String password;
    private String name;
    private String nickname;
    private String mobile;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    @Builder
    public User(String id, String email, String password, String name, String nickname, String mobile) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.mobile = mobile;
    }
}
