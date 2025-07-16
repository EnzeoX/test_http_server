package com.test.repository.impl;

import com.test.annotations.di.Component;
import com.test.models.user_info.UserInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Slf4j
@Component
public class UserInfoRepository {

    private final static int LIST_MAX_SIZE = 20;

    private final List<UserInfo> userInfoCache = new CopyOnWriteArrayList<>();
    private final Map<Integer, UserInfo> userInfoHash = new ConcurrentHashMap<>();

    public Collection<UserInfo> getByUserId(Long userId) {
        if (Objects.isNull(userId)) throw new NullPointerException("UserId is not provided!");
        if (Objects.isNull(userInfoCache) // Just to be sure
                || userInfoCache.isEmpty()) return new ArrayList<>();
        return userInfoCache.stream()
                .filter(data -> Objects.nonNull(data) && data.getUserId().equals(userId))
                .sorted(
                        Comparator.comparing(UserInfo::getResult, Comparator.reverseOrder())
                                .thenComparing(UserInfo::getLevelId, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    public Collection<UserInfo> getByLevelId(Integer levelId) {
        if (Objects.isNull(levelId)) throw new NullPointerException("LevelId is not provided!");
        if (Objects.isNull(userInfoCache) // Just to be sure
                || userInfoCache.isEmpty()) return new ArrayList<>();
        return userInfoCache.stream()
                .filter(data -> Objects.nonNull(data) && data.getLevelId().equals(levelId))
                .sorted(
                        Comparator.comparing(UserInfo::getResult, Comparator.reverseOrder())
                                .thenComparing(UserInfo::getUserId, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    public void save(UserInfo ui) {
        synchronized (userInfoCache) {
            if (Objects.isNull(ui)) return;
            int userInfoHashcode = ui.hashCode();
            if (userInfoHash.containsKey(userInfoHashcode)) { // or just maybe totally skip this, because it's already with the same data (should be)
                UserInfo existing = userInfoHash.get(userInfoHashcode);
                if (ui.getResult() > existing.getResult()) {
                    userInfoCache.remove(existing);
                    userInfoCache.add(ui);
                    userInfoHash.put(userInfoHashcode, ui);
                } else {
                    return;
                }
            } else {
                if (userInfoCache.size() < LIST_MAX_SIZE) {
                    userInfoCache.add(ui);
                    userInfoHash.put(userInfoHashcode, ui);
                } else {
                    UserInfo worst = Collections.min(userInfoCache, Comparator.comparing(UserInfo::getResult));
                    if (ui.getResult() > worst.getResult()) {
                        userInfoCache.remove(worst);
                        userInfoHash.values().remove(worst);
                        userInfoCache.add(ui);
                        userInfoHash.put(userInfoHashcode, ui);
                    } else {
                        return;
                    }
                }
            }
            userInfoCache.sort(
                    Comparator.comparing(UserInfo::getResult).reversed()
                            .thenComparing(UserInfo::getUserId)
                            .thenComparing(UserInfo::getLevelId));
        }
    }

    public void saveAll(Collection<UserInfo> userInfoCollection) {
        synchronized (userInfoCache) {
            if (Objects.isNull(userInfoCollection) || userInfoCollection.isEmpty()) return;
            for (UserInfo ui : userInfoCollection) save(ui);
        }
    }

    public void clearAll() {
        synchronized (userInfoCache) {
            userInfoCache.clear();
        }
    }
}
