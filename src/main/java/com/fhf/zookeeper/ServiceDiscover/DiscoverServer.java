package com.fhf.zookeeper.ServiceDiscover;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.Random;

public class DiscoverServer {
    private static String zkAddress = "192.168.190.101:2181,192.168.190.102:2181,192.168.190.103:2183";
    private static int sessionTimeout = 2000;

    private  ZooKeeper zkClient;
    private static String discoverNode = "/discover";

    private String serverName;

    public DiscoverServer(String serverName){
        this.serverName = serverName;
    }

    public void doSomething() throws InterruptedException {
        System.out.println(serverName + ":do something");

        Thread.sleep(new Random().nextInt(5) * 1000);
    }

    public void register() throws InterruptedException, KeeperException, IOException {

        zkClient = new ZooKeeper(zkAddress, sessionTimeout, event -> {});
        String s = zkClient.create(discoverNode + "/" + serverName, serverName.getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        System.out.printf("%s register success %s\n" ,serverName, s);
    }

    public void logout() throws InterruptedException {
        zkClient.close();
        System.out.printf("%s out success\n" ,serverName);
    }

    private static class RunServer implements Runnable{
        DiscoverServer discoverServer;
        public RunServer(DiscoverServer discoverServer){
            this.discoverServer = discoverServer;
        }

        @Override
        public void run() {
            try {
                discoverServer.register();
                //周期性调用dosomething
                int times = new Random().nextInt(20);
                for (int i = 0; i < times; i++) {
                    discoverServer.doSomething();
                }
                discoverServer.logout();
            } catch (InterruptedException | IOException | KeeperException e) {
                throw new RuntimeException(e);
            }

        }
    }
    public static void main(String[] args) throws InterruptedException {
        for(int i = 0;i < 4;i++){
            Thread curThread = new Thread(new RunServer(new DiscoverServer(String.format("server:%d", i))));
            curThread.start();
            Thread.sleep(new Random().nextInt(5) * 1000);
        }

        Thread.sleep(1000 * 1000);
    }
}
