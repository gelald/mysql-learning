package com.github.gelald.mpcommon.context;

/**
 * 存储当前登录人的id
 *
 * @author WuYingBin
 * date: 2023/3/16
 */
public class CurrentUserContext {
    private static final ThreadLocal<Long> currentUserId = new ThreadLocal<>();

    public static void setId(Long userId) {
        currentUserId.set(userId);
    }

    public static Long getId() {
        return currentUserId.get();
    }
}
