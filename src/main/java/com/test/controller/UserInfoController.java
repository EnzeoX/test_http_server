package com.test.controller;

import com.test.annotations.server.*;
import com.test.annotations.di.Component;
import com.test.annotations.di.Inject;
import com.test.handler.UserInfoHandler;
import com.test.models.user_info.dto.SetUserInfoDto;
import com.test.models.user_info.dto.UserInfoDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

@Slf4j
@Component
@RequestMapping(path = "/api/v1/info")
public class UserInfoController {

    @Inject
    private UserInfoHandler userInfoHandler;

    @GetMapping(path = "/clear-data")
    public String clearData() {
        userInfoHandler.clearData();
        return "Data deleted";
    }

    @GetMapping(path = "/userinfo/{userId}")
    public Collection<UserInfoDto> getByUserId(@PathVariable(value = "userId") Long userId) {
        return userInfoHandler.getByUserId(userId);
    }

    @GetMapping(path = "/levelinfo/{levelId}")
    public Collection<UserInfoDto> getByLevelId(@PathVariable(value = "levelId") Integer levelId) {
        return userInfoHandler.getByLevelId(levelId);
    }

    @PutMapping(path = "/setinfo")
    public String setUserInfo(@RequestData SetUserInfoDto dto) {
        userInfoHandler.setUserInfo(dto);
        return "Data set: [" + dto.toString() + "]";
    }

    @PutMapping(path = "/setinfo/collection")
    public String setUserInfoCollection(@RequestData Collection<SetUserInfoDto> dto) {
        userInfoHandler.setUserInfoMultiple(dto);
        return "Data set, total size: [" + dto.size() + "]";
    }
}
