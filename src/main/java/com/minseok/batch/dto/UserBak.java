package com.minseok.batch.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * package      : com.minseok.batch.dto
 * class        : UserBak
 * author       : blenderkims
 * date         : 2023/04/19
 * description  :
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class UserBak {
    private String id;
    private String email;
    private String password;
    private String name;
    private String nickname;
    private String mobile;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    @Builder
    public UserBak(String id, String email, String password, String name, String nickname, String mobile, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.mobile = mobile;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }
    public static UserBak of(User user) {
        return UserBak.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .name(user.getName())
                .nickname(user.getNickname())
                .mobile(user.getMobile())
                .createdAt(user.getCreatedAt())
                .modifiedAt(user.getModifiedAt())
                .build();
    }
}
