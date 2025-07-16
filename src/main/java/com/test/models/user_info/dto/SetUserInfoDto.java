package com.test.models.user_info.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SetUserInfoDto {

    private Long userId;
    private Integer levelId;
    private Integer result;
}
