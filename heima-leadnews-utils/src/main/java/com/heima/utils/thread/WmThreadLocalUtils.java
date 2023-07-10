package com.heima.utils.thread;

import com.heima.model.wemedia.pojos.WmUser;

/**
 * Description: Todo
 * Class Name: WmThreadLocalUtil
 * Date: 2023/7/10 11:32
 *
 * @author Hao
 * @version 1.1
 */
public class WmThreadLocalUtils {

    private final static ThreadLocal<WmUser> WM_USER_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 添加用户，存入线程中
     *
     * @param wmUser
     */
    public static void setUser(WmUser wmUser) {
        WM_USER_THREAD_LOCAL.set(wmUser);
    }

    /**
     * 获取用户
     */
    public static WmUser getUser() {
        return WM_USER_THREAD_LOCAL.get();
    }

    /**
     * 清理用户
     */
    public static void clear() {
        WM_USER_THREAD_LOCAL.remove();
    }
}
