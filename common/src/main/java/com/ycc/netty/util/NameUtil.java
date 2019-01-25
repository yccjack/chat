package com.ycc.netty.util;


import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author ycc
 * @version 1.0
 * @date 2019/1/23 14:20
 */
public class NameUtil {
    public static ConcurrentMap<String, String> nameMap = new ConcurrentHashMap<>();
    public static ConcurrentMap<String, String> remoteMap = new ConcurrentHashMap<>();

    private static List<String> SUR_NAME = new ArrayList<>();

    private static void init() {
        File file = new File("surname.json");
        BufferedReader bufferedReader = null;
        StringBuilder finalString = new StringBuilder();
        try {
            FileInputStream reader = new FileInputStream(NameUtil.class.getClassLoader().getResource("surname.json").getFile());
            InputStreamReader inputStreamReader = new InputStreamReader(reader, StandardCharsets.UTF_8);
            bufferedReader = new BufferedReader(inputStreamReader);
            String temp = null;
            while ((temp = bufferedReader.readLine()) != null) {
                finalString.append(temp);
            }
            bufferedReader.close();
            disposeReader(finalString.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static {
        init();
    }

    /**
     * 去除所有空格,搜有["]号,"["号和"]"号
     *
     * @param finalString 需要去除的字符串
     */
    private static void disposeReader(String finalString) {
        String s = StringUtils.deleteWhitespace(finalString).replaceAll("\"", "").replace("[", "").replace("]", "");
        /**
         * Arrays.asList(数组)生成的List无法支持add和remove,生成的是final修饰的 请勿使用;
         */
        //SUR_NAME = Arrays.asList(s.split(","));
        String[] split = s.split(",");
        Collections.addAll(SUR_NAME, split);
    }

    /**
     * 随机获取姓名并删除获取的姓名
     *
     * @return 随机姓名
     */
    public static String getRandomName() {
        Random random = new Random();
        int next = random.nextInt(SUR_NAME.size());
        while (next > SUR_NAME.size()) {
            next = random.nextInt(SUR_NAME.size());
        }
        String s = SUR_NAME.get(next);
        SUR_NAME.remove(next);
        return s;
    }


    public static String getName(String key) {
        String name = nameMap.get(key);
        if (StringUtils.isEmpty(name)) {
            name = getRandomName();
            nameMap.put(key, name);
            remoteMap.put(name, key);
        }
        return name;
    }

    public static void remove(String key) {
        String name = nameMap.get(key);
        if (name == null) {
            return;
        }
        nameMap.remove(key);
        remoteMap.remove(name);
        SUR_NAME.add(name);
    }
}
