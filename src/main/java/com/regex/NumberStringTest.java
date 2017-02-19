package com.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * Created by daiwei on 2017/1/14.
 */
public class NumberStringTest {
    /**
     * 正则表达式获取字符串中数字字符串和字母字符串的个数,以及统计字符个数
     作者： 霜花似雪
     链接：http://www.imooc.com/article/15827
     来源：慕课网
     本文原创发布于慕课网 ，转载请注明出处，谢谢合作！
     * @param args
     */
    public static void main(String[] args) {
        String u = "wedbnwy32iy232344236tegvewjhn23843872738349348wngeijhwj84378438974324g2nf642";
        //正则表达式的编译表示形式
        Pattern p = Pattern.compile("\\d{2,}");
        //创建匹配给定输入与此模式的匹配器
        Matcher m = p.matcher(u);
        int i = 0;
        while (m.find()) {//如果匹配成功
            //返回所匹配的输入子序列
            System.out.println(m.group());
            i++;
        }
        System.out.println("共有" + i + "个数字字符串");
        Pattern p1 = Pattern.compile("\\D{2,}");
        Matcher m1 = p1.matcher(u);
        int j = 0;
        while (m1.find()) {
            System.out.println(m1.group());
            j++;
        }
        System.out.println("共有" + j + "个字母字符串");
        Pattern p2 = Pattern.compile("\\w");
        Matcher m2 = p2.matcher(u);
        int k = 0;
        while (m2.find()) {
            System.out.print(m2.group());
            k++;
        }
        System.out.println("\n共有" + k + "个字符");
    }
}

