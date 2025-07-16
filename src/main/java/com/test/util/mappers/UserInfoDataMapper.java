package com.test.util.mappers;

import com.test.annotations.di.Component;
import com.test.models.user_info.UserInfo;
import com.test.models.user_info.dto.SetUserInfoDto;
import com.test.models.user_info.dto.UserInfoDto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class UserInfoDataMapper {

    public UserInfoDto toDto(UserInfo ui) {
        UserInfoDto dto = new UserInfoDto();
        dto.setUserId(ui.getUserId());
        dto.setLevelId(ui.getLevelId());
        dto.setResult(ui.getResult());
        return dto;
    }

    public Collection<UserInfoDto> toDtoCollection(Collection<UserInfo> collection) {
        if (Objects.isNull(collection) || collection.isEmpty()) return new ArrayList<>();

        return collection.stream().filter(Objects::nonNull)
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public UserInfo toEntity(SetUserInfoDto dto) {
        UserInfo userInfo = new UserInfo();
        if (Objects.nonNull(dto.getUserId()))
            userInfo.setUserId(dto.getUserId());
        if (Objects.nonNull(dto.getLevelId()))
            userInfo.setLevelId(dto.getLevelId());
        if (Objects.nonNull(dto.getResult()))
            userInfo.setResult(dto.getResult());
        return userInfo;
    }

    public Collection<UserInfo> toEntityCollection(Collection<SetUserInfoDto> dtoCollection) {
        if (Objects.isNull(dtoCollection) || dtoCollection.isEmpty()) return new ArrayList<>();
        return dtoCollection.stream().filter(Objects::nonNull)
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}
