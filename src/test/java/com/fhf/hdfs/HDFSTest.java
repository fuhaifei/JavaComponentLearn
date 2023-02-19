package com.fhf.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

public class HDFSTest {
    private static final Logger logger = Logger.getLogger(Test.class);
    @Test
    public void testMkdirs() throws IOException, URISyntaxException, InterruptedException {

        // 1 获取文件系统
        Configuration configuration = new Configuration();
        // FileSystem fs = FileSystem.get(new URI("hdfs://hadoop102:8020"), configuration);
        FileSystem fs = FileSystem.get(new URI("hdfs://192.168.190.101/:8020"), configuration, "fhf");

        // 2 创建目录
        fs.mkdirs(new Path("/testClientDir"));

        // 3 关闭资源
        fs.close();
    }

    @Test
    public void testOperateFile() throws URISyntaxException, IOException, InterruptedException {
        Configuration configuration = new Configuration();
        FileSystem fs = FileSystem.get(new URI("hdfs://192.168.190.101/:8020"), configuration, "fhf");
        logger.info(fs.getHomeDirectory());
        //1.上传文件
        //fs.copyFromLocalFile(new Path("D:\\CodeProject\\HDFSClientTest\\src\\main\\resources\\test.txt"), new Path("/testClientDir/test.txt"));
        //2.修改文件名称
        //fs.rename(new Path("/testClientDir/test.txt"), new Path("/testClientDir/test2.txt"));
        //3.删除文件
        //fs.delete(new Path("/testClientDir"), true);
        //4.获取文件的详细信息
        RemoteIterator<LocatedFileStatus> listFiles = fs.listFiles(new Path("/"), true);

        while (listFiles.hasNext()) {
            LocatedFileStatus fileStatus = listFiles.next();

            System.out.println("========" + fileStatus.getPath() + "=========");
            System.out.println(fileStatus.getPermission());
            System.out.println(fileStatus.getOwner());
            System.out.println(fileStatus.getGroup());
            System.out.println(fileStatus.getLen());
            System.out.println(fileStatus.getModificationTime());
            System.out.println(fileStatus.getReplication());
            System.out.println(fileStatus.getBlockSize());
            System.out.println(fileStatus.getPath().getName());

            // 获取块信息
            BlockLocation[] blockLocations = fileStatus.getBlockLocations();
            System.out.println(Arrays.toString(blockLocations));
        }
        // 3 关闭资源
        fs.close();

    }
}
