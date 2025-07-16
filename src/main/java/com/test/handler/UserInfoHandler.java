package com.test.handler;

import com.test.annotations.di.Component;
import com.test.annotations.di.Inject;
import com.test.models.user_info.dto.SetUserInfoDto;
import com.test.models.user_info.dto.UserInfoDto;
import com.test.repository.impl.UserInfoRepository;
import com.test.util.mappers.UserInfoDataMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

@Slf4j
@Component
public class UserInfoHandler {

    @Inject
    private UserInfoRepository userInfoRepository;

    @Inject
    private UserInfoDataMapper userInfoDataMapper;

    public Collection<UserInfoDto> getByUserId(Long userId) {
        return userInfoDataMapper.toDtoCollection(userInfoRepository.getByUserId(userId));
    }

    public Collection<UserInfoDto> getByLevelId(Integer levelId) {
        return userInfoDataMapper.toDtoCollection(userInfoRepository.getByLevelId(levelId));
    }

    public void setUserInfo(SetUserInfoDto dto) {
        userInfoRepository.save(userInfoDataMapper.toEntity(dto));
    }

    public void setUserInfoMultiple(Collection<SetUserInfoDto> dtoCollection) {
        userInfoRepository.saveAll(userInfoDataMapper.toEntityCollection(dtoCollection));
    }

    public void clearData() {
        userInfoRepository.clearAll();
    }
}
