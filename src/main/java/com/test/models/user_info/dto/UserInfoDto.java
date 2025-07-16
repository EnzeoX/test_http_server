package com.test.models.user_info.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDto {

    private Long userId;
    private Integer levelId;
    private Integer result;
}
