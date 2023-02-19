package com.fhf.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

public class ZKClientTest {
    private static String zkAddress = "192.168.190.101:2181,192.168.190.102:2181,192.168.190.103:2183";

    private ZooKeeper zkClient = null;

    @Before
    public void init() throws IOException {
        int sessionTimeout = 20000;
        zkClient = new ZooKeeper(zkAddress, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println(event.getType() + " " + event.getPath() + " " + event.getState());
            }
        });
    }

    @Test
    public void create() throws InterruptedException, KeeperException {
        if(zkClient.exists("/java/testCreate", false) != null){
            zkClient.delete("/java/testCreate", -1);
        }
        System.out.println(zkClient.create("/java/testCreate", "hello".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT));
    }

    @Test
    public void exist() throws InterruptedException, KeeperException {
        Stat exists = zkClient.exists("/java/javaClient", false);
        System.out.println(exists);
        exists = zkClient.exists("/java/hahah", false);
        System.out.println(exists);
    }

    @Test
    public void watchChild() throws InterruptedException, KeeperException {
        List<String> children = zkClient.getChildren("/java", true);

        for(String child:children){
            System.out.println(child);
        }
        //休息十秒钟
        Thread.sleep(10000);
        System.out.println("test exit");
    }

}
