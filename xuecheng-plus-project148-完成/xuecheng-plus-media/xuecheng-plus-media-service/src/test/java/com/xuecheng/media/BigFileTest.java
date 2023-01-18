package com.xuecheng.media;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

/**
 * @author Mr.M
 * @version 1.0
 * @description 大文件分块、合并
 * @date 2022/10/14 10:48
 */
public class BigFileTest {

    @Test
    public void testChunk() throws IOException {
        //源文件
        File sourceFile = new File("D:\\develop\\bigfile_test\\nacos.mp4");

        //分块文件存储路径
        File chunkFolderPath = new File("D:\\develop\\bigfile_test\\chunk\\");
        if(!chunkFolderPath.exists()){
            chunkFolderPath.mkdirs();
        }

        //分块的大小
        int chunkSize = 1024 * 1024 * 1;

        //分块数量
        long chunkNum = (long) Math.ceil(sourceFile.length() * 1.0 / chunkSize);

        //思路，使用流对象读取源文件，向分块文件写数据，达到分块大小不再写
        RandomAccessFile raf_read = new RandomAccessFile(sourceFile, "r");
        //缓冲区
        byte[] b = new byte[1024];
        for (long i = 0; i < chunkNum; i++) {

            File file = new File("D:\\develop\\bigfile_test\\chunk\\" + i);
            //如果分块文件存在，则删除
            if(file.exists()){
                file.delete();
            }
            boolean newFile = file.createNewFile();
            if(newFile){
                //向分块文件写数据流对象
                RandomAccessFile raf_write = new RandomAccessFile(file, "rw");
                int len=-1;
                while ((len=raf_read.read(b))!=-1){
                    //向文件中写数据
                    raf_write.write(b,0,len);
                    //达到分块大小不再写了
                    if(file.length()>=chunkSize){
                        break;
                    }
                }
                raf_write.close();

            }

        }
        raf_read.close();

    }
    //测试合并
    @Test
    public void testMerge() throws IOException {
        //源文件
        File sourceFile = new File("D:\\develop\\bigfile_test\\nacos.mp4");

        //分块文件存储路径
        File chunkFolderPath = new File("D:\\develop\\bigfile_test\\chunk\\");
        if(!chunkFolderPath.exists()){
            chunkFolderPath.mkdirs();
        }
        //合并后的文件
        File mergeFile = new File("D:\\develop\\bigfile_test\\nacos_01.mp4");
        boolean newFile1 = mergeFile.createNewFile();

        //思路，使用流对象读取分块文件，按顺序将分块文件依次向合并文件写数据
        //获取分块文件列表,按文件名升序排序
        File[] chunkFiles = chunkFolderPath.listFiles();
        List<File> chunkFileList = Arrays.asList(chunkFiles);
        //按文件名升序排序
        Collections.sort(chunkFileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return Integer.parseInt(o1.getName()) - Integer.parseInt(o2.getName()) ;
            }
        });
        //创建合并文件的流对象
        RandomAccessFile raf_write = new RandomAccessFile(mergeFile, "rw");
        byte[] b = new byte[1024];
        for (File file : chunkFileList) {
            //读取分块文件的流对象
            RandomAccessFile raf_read = new RandomAccessFile(file, "r");
            int len=-1;
            while ((len=raf_read.read(b))!=-1){
                //向合并文件写数据
                raf_write.write(b,0,len);
            }
        }

        //校验合并后的文件是否正确
        FileInputStream sourceFileStream = new FileInputStream(sourceFile);
        FileInputStream mergeFileStream = new FileInputStream(mergeFile);
        String sourceMd5Hex = DigestUtils.md5Hex(sourceFileStream);
        String mergeMd5Hex = DigestUtils.md5Hex(mergeFileStream);
        if(sourceMd5Hex.equals(mergeMd5Hex)){
            System.out.println("合并成功");
        }


    }
}
