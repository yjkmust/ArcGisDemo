package com.yjkmust.arcgisdemo.Utils;

import android.content.Context;
import android.util.DisplayMetrics;

import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.MapGeometry;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * Created by Shyam on 2016/8/23.
 */
public final class Utility {

    /**
     * 混淆加密md5
     * @param str
     * @return
     */
    public static String scrambleMD5(String str){
        String s = onceMD5(str);
        String tempS = "";
        for (int i = s.length() - 1; i >= 0; i--){
            tempS += s.toCharArray()[i];
        }
        return onceMD5(tempS);
    }

    /**
     * MD5加密字符串
     * */
    public static String onceMD5(String password) {
        MessageDigest md;
        try {
            // 生成一个MD5加密计算摘要
            md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(password.getBytes());
            // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            String pwd = new BigInteger(1, md.digest()).toString(16);
            System.err.println(pwd);
            return pwd.toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return password;
    }
    // 复制文件夹
    public static void copyDirectiory(String sourceDir, String targetDir) throws IOException {
        // 新建目标目录
        (new File(targetDir)).mkdirs();
        // 获取源文件夹当前下的文件或目录
        File[] file = (new File(sourceDir)).listFiles();
        if(file == null){
            return;
        }
        for (int i = 0; i < file.length; i++) {
            if (file[i].isFile()) {
                // 源文件
                File sourceFile = file[i];
                // 目标文件
                File targetFile = new File(new File(targetDir).getAbsolutePath() + File.separator + file[i].getName());
                copyFile(sourceFile, targetFile);
            }
            if (file[i].isDirectory()) {
                if (file[i].getName().equals("Tiledlayers")) {// 不备份切片
                    continue;
                }
                // 准备复制的源文件夹
                String dir1 = sourceDir + "/" + file[i].getName();
                // 准备复制的目标文件夹
                String dir2 = targetDir + "/" + file[i].getName();
                copyDirectiory(dir1, dir2);
            }
        }
    }
    /**
     * 拷贝文件
     *
     * @param sourceFile
     * @param destFile
     * @throws IOException
     */
    public static void copyFile(File sourceFile, File destFile) throws IOException {
        InputStream inputStream;
        OutputStream outputStream;
        inputStream = new FileInputStream(sourceFile);
        outputStream = new FileOutputStream(destFile);
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }

    /**
     * 删除文件夹和文件夹里面的文件
     * @param path
     */
    public static void deleteDir(String path) {
        File dir = new File(path);
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;

        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDir(file.getAbsolutePath()); // 递规的方式删除文件夹
        }
        dir.delete();// 删除目录本身
    }




    /**
     * dip转米
     *
     * @param dp
     * @param scale
     * @param ctx
     * @return
     */
    public static double dpi2Map(float dp, double scale, Context ctx) {
        double meter = dip2m(dp, ctx);
        return meter * scale;
    }

    /**
     * dip转换为米
     *
     * @param dipValue
     * @param ctx
     * @return
     */
    public static double dip2m(float dipValue, Context ctx) {
        DisplayMetrics dm = ctx.getResources()
                .getDisplayMetrics();
        float scale = dm.density;
        int px = (int) (dipValue * scale + 0.5f);
        double in = (double) px / (double) dm.densityDpi;
        return (in * 25.4) / 1000.0;
    }

    /**
     * byte[4] to int
     * @param bytes
     * @return
     */
    public static int bytes2Int(byte[] bytes){
        int addr = bytes[0] & 0xFF;
        addr |= ((bytes[1] << 8) & 0xFF00);
        addr |= ((bytes[2] << 16) & 0xFF0000);
        addr |= ((bytes[3] << 24) & 0xFF000000);
        return addr;
    }

    /**
     * byte[8] to long
     * @param bytes
     * @return
     */
    public static long bytes2Long(byte[] bytes){
        if (bytes == null || bytes.length != 8){
            return 0;
        }
        long  values = 0;
        for (int i = 0; i < 8; ++i) {
            values <<= 8;
            values |= (bytes[i] & 0xff);
        }
        return values;
//        ByteBuffer buffer = ByteBuffer.allocate(8);
//        buffer.put(bytes, 0, bytes.length);
//        buffer.flip();
//        return buffer.getLong();

//        long addr = bytes[0] & 0xFF;
//        addr |= ((bytes[1] << 8) & 0xFF);
//        addr |= ((bytes[2] << 16) & 0xFF);
//        addr |= ((bytes[3] << 24) & 0xFF);
//        addr |= ((bytes[4] << 32) & 0xFF);
//        addr |= ((bytes[5] << 40) & 0xFF);
//        addr |= ((bytes[6] << 48) & 0xFF);
//        addr |= ((bytes[7] << 56) & 0xFF);
//        return addr;
    }

    /**
     * int to byte[4]
     * @param i
     * @return
     */
    public static byte[] int2Bytes(int i){
        byte[] abyte0 = new byte[4];
        abyte0[0] = (byte) (0xFF & i);
        abyte0[1] = (byte) ((0xFF00 & i) >> 8);
        abyte0[2] = (byte) ((0xFF0000 & i) >> 16);
        abyte0[3] = (byte) ((0xFF000000 & i) >> 24);
        return abyte0;
    }

    /**
     * long to byte[8]
     * @param i
     * @return
     */
    public static byte[] long2Bytes(long i){
        byte[] byteNum = new byte[8];
        for (int ix = 0; ix < 8; ++ix){
            int offset = 64 - (ix + 1) * 8;
            byteNum[ix] = (byte)((i >> offset) & 0xff);
        }
        return byteNum;

//        byte[] abyte0 = new byte[8];
//        abyte0[0] = (byte) (0xFFL & i);
//        abyte0[1] = (byte) ((0xFF00L & i) >> 8);
//        abyte0[2] = (byte) ((0xFF0000L & i) >> 16);
//        abyte0[3] = (byte) ((0xFF000000L & i) >> 24);
//        abyte0[4] = (byte) ((0xFF00000000L & i) >> 32);
//        abyte0[5] = (byte) ((0xFF0000000000L & i) >> 40);
//        abyte0[6] = (byte) ((0xFF000000000000L & i) >> 48);
//        abyte0[7] = (byte) ((0xFF00000000000000L & i) >> 56);
//        return abyte0;
    }

    public static String toLengthString(double len){
//        if (len < 1000) {
//            if (len < 10) {
//            } else {
//                return String.format("%.5f米", len);
//            }
//        } else {
//            return String.format("%.5f公里", len / 1000.0);
//        }
        return String.format("%.5f公里", len * 111);
    }

    public static String toAreaString(double area){
//        if (area < 1000000) {
//            if (area < 10) {
//            } else {
//                return String.format("%.5f平方米", area);
//            }
//        } else {
//            return String.format("%.6f平方公里", area / 1000000.0);
//        }
        return String.format("%.5f平方公里", area * 111 * 111);
    }

    public static Geometry json2Geometry(String json){
        JsonFactory jsonFactory = new JsonFactory();
        JsonParser jsonParser = null;
        try {
            jsonParser = jsonFactory.createJsonParser(json);
        } catch (IOException e) {
            return null;
        }
        MapGeometry mapGeometry = GeometryEngine.jsonToGeometry(jsonParser);
        return mapGeometry.getGeometry();
    }

    /**
     * 递归删除文件和文件夹
     *
     * @param file 要删除的根目录
     */
    public static void recursionDeleteFile(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                file.delete();
                return;
            }
            for (File f : childFile) {
                recursionDeleteFile(f);
            }
            file.delete();
        }
    }

}
