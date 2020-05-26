package com.ucas.cloudenterprise.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * 统计代码行数demo author:lizhi
 */

public class StatisticCodeLines {

    public static int normalLines = 0; // 有效程序行数
    public static int whiteLines = 0; // 空白行数
    public static int commentLines = 0; // 注释行数
    public static String Content ="" ; // 注释行数



    /**
     * 获取单个文件的MD5值
     * @param file 文件
     * @param radix  位 16 32 64
     *
     * @return
     */


    static boolean read_able = true;
    static boolean  copy_able = false;
    static boolean   md5_able = true;
    static boolean    ok =false;

    static int len=0;

    public static void getFileMD5s(File file,int radix) {
        try {
            MessageDigest  digest;
            digest = MessageDigest.getInstance("MD5");
            FileInputStream in  = new FileInputStream(file);
            byte buffer[] = new byte[20 * 1024 * 1024];
            Thread read = new Thread(new Runnable() {

                @Override
                public void run() {
                    long  md5_start_time=System.currentTimeMillis();
                    System.out.println( "md5 开始"+md5_start_time);
                    Log.e("ok","md5 开始"+md5_start_time);
                    while (true){
                        try {
//
                            if(read_able){
                                if ((len = in.read(buffer, 0, 20* 1024 * 1024)) != -1) {
//                                    Log.e("ok","buffer is "+buffer.toString());
                                    read_able =false;
                                    copy_able=true;
//
                                }else{
                                    if(md5_able){
                                        long   md5_end_time=System.currentTimeMillis();
                                        Log.e("ok","md5 结束"+md5_end_time);
//                                        System.out.println("md5 结束"+md5_end_time);
                                        Log.e("ok","md5 耗时"+(md5_end_time-md5_start_time)/1000.0);
                                        BigInteger bigInt = new BigInteger(1, digest.digest());
                                        Log.e("ok","md5 结果"+bigInt.toString(16));
//                                        System.out.println("md5 结果"+bigInt.toString(radix));
                                        ok =true;
                                        break;
                                    }

                                }




                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }}
            );

            Thread md5 = new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("start md5");
                    byte copyed_buff[]=null;
                    while (true){

                        if(copy_able&&md5_able&&!read_able){

                            md5_able=false;
                            if(copyed_buff==null){
                                copyed_buff= buffer.clone();
//                                Log.e("ok"," clone copyed_buff is "+copyed_buff.equals(buffer));
                                read_able =true;
                                digest.update(copyed_buff, 0, len);
//                                Log.e("ok"," read_able =true copyed_buff is "+copyed_buff.equals(buffer));
//                                Log.e("ok","digest after copyed_buff is "+copyed_buff.toString());
                                copyed_buff=null;
                                md5_able = true;
                            }

                        }

                        if (ok){

                            break;
                        }


                    }

                }
            }
            );
            read.start();
            md5.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getFileMD5s(File file) {
        if (!file.isFile()) {
            return  null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        long md5_start_time;
        byte buffer[] = new byte[4*1024*1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");

            in = new FileInputStream(file);

            md5_start_time = System.currentTimeMillis();
            Log.e("ok","md5 开始"+md5_start_time);

            while ((len = in.read(buffer,
                    0, 4*1024*1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        long   md5_end_time=System.currentTimeMillis();
        Log.e("ok","md5 结束"+md5_end_time);
//                                System.out.println("md5 结束"+md5_end_time);
        Log.e("ok","md5 耗时"+(md5_end_time-md5_start_time)/1000.0);
        BigInteger bigInt = new BigInteger(1, digest.digest());
        String md5 =bigInt.toString(16);
        Log.e("ok","md5 结果"+md5);
       return md5;

        }


    public static void main(String[] args) throws IOException {


//           getFileMD5s(new File("/Users/simple/Downloads/年兽大作战BD1280高清国语中英双字.MP4"),16);
            int[] a = new int[]{1,2,3,2};
        System.out.println(a);
        int[] b=a.clone();
        System.out.println(b);
        a[0]=10;
        System.out.println(b[0]);
        System.out.println(a[0]);



//        String root =System.getProperty("user.dir");



//        D:\CloudentErprise\app\src\main\java
//        File file = new File(root+"\\app\\src\\main\\java");
//        if (file.exists()) {
//            statistic(file);
//        }
//        System.out.println("总有效代码行数: " + normalLines);
//        System.out.println("总空白行数：" + whiteLines);
//        System.out.println("总注释行数：" + commentLines);
//        System.out.println("总行数：" + (normalLines + whiteLines + commentLines));
//        System.out.println("内容：" + (Content));
    }

    private static void statistic(File file) throws IOException {

        if (file.isDirectory()) {

         File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
        statistic(files[i]);
        }
        }
        if (file.isFile()) {
        // 统计扩展名为java的文件
        if (file.getName().matches(".*\\.kt")) {
        parse(file);
        }
        }

        }

public static void parse(File file) {
        BufferedReader br = null;
        // 判断此行是否为注释行
        boolean comment = false;
        int temp_whiteLines = 0;// 空白行数
        int temp_commentLines = 0;// 注释行数
        int temp_normalLines = 0; // 有效程序行数

        try {
        br = new BufferedReader(new FileReader(file));
        String line = "";
        while ((line = br.readLine()) != null) {

            line = line.trim();

            if (line.matches("^[\\s&&[^\\n]]*$")) {
        // 空行
        whiteLines++;
        temp_whiteLines++;
        } else if (line.startsWith("/*") && line.endsWith("*/")) {
        // 判断此行为"/*xxx*/"的注释行
        commentLines++;
        temp_commentLines++;
        } else if (line.startsWith("/*") && !line.endsWith("*/")) {
        // 判断此行为"/*"开头的注释行
        commentLines++;
        temp_commentLines++;
        comment = true;
        } else if (comment == true && !line.endsWith("*/")) {
        // 为多行注释中的一行（不是开头和结尾）
        commentLines++;
        temp_commentLines++;
        } else if (comment == true && line.endsWith("*/")) {
        // 为多行注释的结束行
        commentLines++;
        temp_commentLines++;
        comment = false;
        } else if (line.startsWith("//")) {
        // 单行注释行
        commentLines++;
        temp_commentLines++;
        } else {
        // 正常代码行
        normalLines++;
        temp_normalLines++;
                Content =Content+ normalLines+"."+line+"\n";
        }
        }

        System.out.println("有效行数" + temp_normalLines + " ,空白行数" + temp_whiteLines + " ,注释行数" + temp_commentLines
        + " ,总行数" + (temp_normalLines + temp_whiteLines + temp_commentLines) + "     " + file.getName());

        } catch (FileNotFoundException e) {
        e.printStackTrace();
        } catch (IOException e) {
        e.printStackTrace();
        } finally {
        if (br != null) {
        try {
        br.close();
        br = null;
        } catch (IOException e) {
        e.printStackTrace();
        }
        }
        }
        }


        }


