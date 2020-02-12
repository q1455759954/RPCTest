package log;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;


public class PictureServiceImpl  {


    public static void main(String[] args){
        PictureServiceImpl pictureService = new PictureServiceImpl();
        pictureService.createFile("C:\\Users\\Administrator\\Desktop\\6ade4348ly8fpcrds6cy3j20v90vq75s.jpg","/picture/avatar/");
    }


    /**
     * 拷贝一个本地文件到hadoop里面
     * @param localFile 本地文件和路径名
     * @param hadoopFile hadoop文件和路径名
     * @return
     */
    public  boolean createFile(String localFile,String hadoopFile){
        try {
            Configuration conf=new Configuration();
            FileSystem src= FileSystem.getLocal(conf);
            FileSystem dst= FileSystem.get(new URI("hdfs://master:9000"),conf,"root");
            Path srcpath = new Path(localFile);
            Path dstpath = new Path(hadoopFile);
            System.out.println(srcpath.toString());
            System.out.println(dstpath.toString());
            FileUtil.copy(src, srcpath, dst, dstpath,false,conf);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }


    /**将一个流作为输入，生成一个hadoop里面的文件
     * @param inStream 输入流
     * @param hadoopFile hadoop路径及文件名字
     * @return
     */
    public boolean createFileByInputStream(InputStream inStream,String hadoopFile){
        try {
            Configuration conf=new Configuration();
            FileSystem dst= FileSystem.get(new URI("hdfs://master:9000"),conf,"root");
            Path dstpath = new Path(hadoopFile);
            FSDataOutputStream oStream=dst.create(dstpath);
            byte[] buffer = new byte[400];
            int length = 0;
            while((length = inStream.read(buffer))>0){
                oStream.write(buffer,0,length);
            }
            oStream.flush();
            oStream.close();
            inStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    /**
     * 删除hadoop里面的一个文件
     * @param hadoopFile
     * @return
     */
    public  boolean deleteFile(String hadoopFile){
        try {
            Configuration conf=new Configuration();
            FileSystem dst= FileSystem.get(new URI("hdfs://master:9000"),conf,"root");
            FileUtil.fullyDelete(dst,new Path(hadoopFile));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
    /**
     * 从hadoop中读取一个文件流
     * @param hadoopFile
     * @return
     */
    public FSDataInputStream getInputStream(String hadoopFile){
        FSDataInputStream iStream=null;
        try {
            Configuration conf=new Configuration();
            FileSystem dst= FileSystem.get(new URI("hdfs://master:9000"),conf,"root");
            Path p=new Path(hadoopFile);
            iStream=dst.open(p);
        } catch (Exception e) {
            try {
                Configuration conf=new Configuration();
                FileSystem dst= FileSystem.get(new URI("hdfs://master:9000"),conf,"root");
                Path p=new Path(hadoopFile.replaceAll("photos","avatar"));
                iStream=dst.open(p);
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            } catch (URISyntaxException e1) {
                e1.printStackTrace();
            }
//            e.printStackTrace();
        }
        return iStream;
    }

}

