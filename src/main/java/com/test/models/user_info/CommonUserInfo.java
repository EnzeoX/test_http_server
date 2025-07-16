package com.test.models.user_info;

public interface CommonUserInfo {

    Long getUserId();
    Long getLevelId();
    Integer getResult();

    void setUserId(Long userId);
    void setLevelId(Long levelId);
    void setResult(Integer result);
}
