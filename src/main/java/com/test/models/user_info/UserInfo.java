package com.test.models.user_info;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class UserInfo {

    private Long userId;

    private Integer levelId;

    private Integer result;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserInfo userInfo = (UserInfo) o;
        return Objects.equals(userId, userInfo.userId)
                && Objects.equals(levelId, userInfo.levelId)
                && Objects.equals(result, userInfo.result);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, levelId, result);
    }
}
