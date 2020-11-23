package com.ucas.cloudenterprise.utils;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/*
 *作者：啊钰
 *邮箱：1819747216@qq.com
 * 上传
 */
public class Utls {
     //123
    String strasada="123";

    public static boolean copyFile(String oldPath$Name, String newPath$Name) {
        try {
            File oldFile = new File(oldPath$Name);
            if (!oldFile.exists()) {
                Log.e("--Method--", "copyFile:  oldFile not exist.");
                return false;
            } else if (!oldFile.isFile()) {
                Log.e("--Method--", "copyFile:  oldFile not file.");
                return false;
            } else if (!oldFile.canRead()) {
                Log.e("--Method--", "copyFile:  oldFile cannot read.");
                return false;
            }

            /* 如果不需要打log，可以使用下面的语句
            if (!oldFile.exists() || !oldFile.isFile() || !oldFile.canRead()) {
                return false;
            }
            */

            File newFile=new File(newPath$Name);
            if(!newFile.exists()){
                newFile.createNewFile();
            }
            FileInputStream fileInputStream = new FileInputStream(oldPath$Name);
            FileOutputStream fileOutputStream = new FileOutputStream(newFile);
            byte[] buffer = new byte[1024*10];
            int byteRead;
            while (-1 != (byteRead = fileInputStream.read(buffer))) {
                fileOutputStream.write(buffer, 0, byteRead);
            }
            fileInputStream.close();
            fileOutputStream.flush();
            fileOutputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean copyFolder(String oldPath, String newPath) {
        try {
            File newFile = new File(newPath);
            if (!newFile.exists()) {
                if (!newFile.mkdirs()) {
                    Log.e("--Method--", "copyFolder: cannot create directory.");
                    return false;
                }
            }
            File oldFile = new File(oldPath);
            String[] files = oldFile.list();
            File temp;
            for (String file : files) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file);
                } else {
                    temp = new File(oldPath + File.separator + file);
                }

                if (temp.isDirectory()) {   //如果是子文件夹
                    copyFolder(oldPath + "/" + file, newPath + "/" + file);
                } else if (!temp.exists()) {
                    Log.e("--Method--", "copyFolder:  oldFile not exist.");
                    return false;
                } else if (!temp.isFile()) {
                    Log.e("--Method--", "copyFolder:  oldFile not file.");
                    return false;
                } else if (!temp.canRead()) {
                    Log.e("--Method--", "copyFolder:  oldFile cannot read.");
                    return false;
                } else {
                    FileInputStream fileInputStream = new FileInputStream(temp);
                    FileOutputStream fileOutputStream = new FileOutputStream(newPath + "/" + temp.getName());
                    byte[] buffer = new byte[1024];
                    int byteRead;
                    while ((byteRead = fileInputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, byteRead);
                    }
                    fileInputStream.close();
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }

                /* 如果不需要打log，可以使用下面的语句
                if (temp.isDirectory()) {   //如果是子文件夹
                    copyFolder(oldPath + "/" + file, newPath + "/" + file);
                } else if (temp.exists() && temp.isFile() && temp.canRead()) {
                    FileInputStream fileInputStream = new FileInputStream(temp);
                    FileOutputStream fileOutputStream = new FileOutputStream(newPath + "/" + temp.getName());
                    byte[] buffer = new byte[1024];
                    int byteRead;
                    while ((byteRead = fileInputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, byteRead);
                    }
                    fileInputStream.close();
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
                 */
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
