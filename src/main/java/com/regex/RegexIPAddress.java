package com.regex;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by daiwei on 2017/1/14.
 */
public class RegexIPAddress {
    public static class IPAdd {
        public boolean isIP(String addr) {
            //判断IP地址的长度 1.1.1.1------255.255.255.255
            if (addr.length() < 7 || addr.length() > 15 || "".equals(addr)) {
                return false;
            }
            // 判断IP格式和范围
            String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
            Pattern pat = Pattern.compile(rexp);
            Matcher mat = pat.matcher(addr);
            boolean ipAddress = mat.find();
            return ipAddress;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入一个IP地址：");
        String ip = scanner.nextLine();
        IPAdd ipAdd = new IPAdd();
        //判断是否是IP地址
        System.out.println(ipAdd.isIP(ip));
    }

    }

