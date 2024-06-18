package com.yupc.myshortlink.admin.utils;

import java.util.Random;

public final class RandomNumberGenerator {

    private static final int LENGTH = 6;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final Random RANDOM = new Random();

    // 私有构造函数，防止实例化
    private RandomNumberGenerator() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    // 生成包含数字和字母的6位随机字符串的方法
    public static String generateRandomString() {
        StringBuilder sb = new StringBuilder(LENGTH);

        for (int i = 0; i < LENGTH; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(index);
            sb.append(randomChar);
        }

        return sb.toString();
    }
}
