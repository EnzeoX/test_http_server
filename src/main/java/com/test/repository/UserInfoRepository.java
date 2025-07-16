package com.test.repository;

import com.test.models.user_info.UserInfo;

import java.util.Collection;

public interface UserInfoRepository {

    Collection<UserInfo> getByUserId(Long userId);
    Collection<UserInfo> getByLevelId(Integer levelId);
    Collection<UserInfo> getByResult(Integer result);
    void save(UserInfo ui);
    void saveAll(Collection<UserInfo> userInfoCollection);
}
