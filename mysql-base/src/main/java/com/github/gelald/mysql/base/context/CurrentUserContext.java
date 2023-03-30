package com.github.gelald.mysql.base.context;

/**
 * 存储当前登录人的id
 *
 * @author WuYingBin
 * date: 2023/3/16
 */
public class CurrentUserContext {
    private static final ThreadLocal<Long> currentUserId = new ThreadLocal<>();

    public static Long getId() {
        return currentUserId.get();
    }

    public static void setId(Long userId) {
        currentUserId.set(userId);
    }
}
