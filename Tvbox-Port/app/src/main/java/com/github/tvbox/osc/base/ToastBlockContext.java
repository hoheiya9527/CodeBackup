package com.github.tvbox.osc.base;

import android.content.Context;
import android.content.ContextWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.github.tvbox.osc.util.LOG;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;

/**
 * Toast拦截Context - 通过代理WindowManager拦截Toast
 * 适配所有Android版本，不使用系统隐藏API
 * <p>
 * 使用示例：
 * <pre>
 * // 1. 在App.getSystemService()中自动拦截，无需手动调用
 *
 * // 2. 动态管理屏蔽关键词
 * ToastBlockContext.addBlockedKeyword("新关键词");        // 添加
 * ToastBlockContext.removeBlockedKeyword("广告");        // 移除
 * ToastBlockContext.getBlockedKeywords();               // 获取列表
 * ToastBlockContext.clearBlockedKeywords();             // 清空
 *
 * // 3. 批量设置
 * Set<String> keywords = new HashSet<>();
 * keywords.add("关键词1");
 * keywords.add("关键词2");
 * ToastBlockContext.setBlockedKeywords(keywords);
 * </pre>
 * <p>
 * 默认屏蔽的关键词：
 * - 广告、请购买、VIP、升级、会员、付费、解锁、试用、公众号、关注
 */
public class ToastBlockContext extends ContextWrapper {
    private static final String TAG = "ToastBlockContext";
    private static Set<String> blockedKeywords = new HashSet<>();
    private static volatile ToastBlockContext sApplicationContext;

    static {
        // 默认屏蔽关键词
        blockedKeywords.add("广告");
        blockedKeywords.add("请购买");
        blockedKeywords.add("VIP");
        blockedKeywords.add("升级");
        blockedKeywords.add("会员");
        blockedKeywords.add("付费");
        blockedKeywords.add("解锁");
        blockedKeywords.add("试用");
        blockedKeywords.add("公众号");
        blockedKeywords.add("关注");
        blockedKeywords.add("太硬");
    }

    public ToastBlockContext(Context base) {
        super(base);
    }

    /**
     * 获取单例的ApplicationContext包装器
     * 避免每次都创建新对象导致Context相等性判断失败
     */
    public static ToastBlockContext wrapApplicationContext(Context appContext) {
        if (sApplicationContext == null) {
            synchronized (ToastBlockContext.class) {
                if (sApplicationContext == null) {
                    sApplicationContext = new ToastBlockContext(appContext);
                }
            }
        }
        return sApplicationContext;
    }

    @Override
    public Context getApplicationContext() {
        // 返回单例的包装Context，保持对象一致性
        return wrapApplicationContext(super.getApplicationContext());
    }

    @Override
    public Object getSystemService(String name) {
        Object service = super.getSystemService(name);

        // 拦截WindowManager
        if (Context.WINDOW_SERVICE.equals(name) && service instanceof WindowManager) {
            return createWindowManagerProxy((WindowManager) service);
        }

        return service;
    }

    /**
     * 创建WindowManager代理（公开方法供App类调用）
     */
    public static WindowManager createWindowManagerProxy(final WindowManager original) {
        return (WindowManager) Proxy.newProxyInstance(
                original.getClass().getClassLoader(),
                new Class<?>[]{WindowManager.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        // 拦截addView方法
                        if ("addView".equals(method.getName()) && args != null && args.length >= 2) {
                            View view = (View) args[0];

                            // 提取Toast文本内容
                            String toastText = extractToastText(view);

                            if (shouldBlock(toastText)) {
                                LOG.i(TAG, "Toast已屏蔽: " + toastText);
                                // 不调用原方法，直接返回（屏蔽Toast）
                                return null;
                            }
                        }

                        // 其他方法正常调用
                        return method.invoke(original, args);
                    }
                }
        );
    }

    /**
     * 提取Toast的文本内容
     */
    private static String extractToastText(View view) {
        if (view == null) return "";

        StringBuilder sb = new StringBuilder();
        extractTextRecursive(view, sb);
        return sb.toString();
    }

    /**
     * 递归提取View树中的所有文本
     */
    private static void extractTextRecursive(View view, StringBuilder sb) {
        if (view instanceof TextView) {
            CharSequence text = ((TextView) view).getText();
            if (text != null && text.length() > 0) {
                if (sb.length() > 0) sb.append(" ");
                sb.append(text);
            }
        }

        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                extractTextRecursive(group.getChildAt(i), sb);
            }
        }
    }

    /**
     * 判断是否应该屏蔽
     */
    private static boolean shouldBlock(String text) {
        LOG.d("TEST", "屏蔽TOAST shouldBlock text: " + text);
        if (text == null || text.isEmpty()) {
            return false;
        }

        for (String keyword : blockedKeywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    // ========== 公开API ==========

    /**
     * 添加屏蔽关键词
     */
    public static void addBlockedKeyword(String keyword) {
        if (keyword != null && !keyword.isEmpty()) {
            blockedKeywords.add(keyword);
        }
    }

    /**
     * 移除屏蔽关键词
     */
    public static void removeBlockedKeyword(String keyword) {
        blockedKeywords.remove(keyword);
    }

    /**
     * 批量设置屏蔽关键词
     */
    public static void setBlockedKeywords(Set<String> keywords) {
        blockedKeywords.clear();
        if (keywords != null) {
            blockedKeywords.addAll(keywords);
        }
    }

    /**
     * 获取所有屏蔽关键词
     */
    public static Set<String> getBlockedKeywords() {
        return new HashSet<>(blockedKeywords);
    }

    /**
     * 清空所有屏蔽关键词
     */
    public static void clearBlockedKeywords() {
        blockedKeywords.clear();
    }
}

