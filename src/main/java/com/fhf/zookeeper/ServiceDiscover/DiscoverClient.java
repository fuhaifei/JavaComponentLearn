package com.fhf.zookeeper.ServiceDiscover;

import org.apache.kerby.x509.type.IssuerSerial;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DiscoverClient {

    private static String zkAddress = "192.168.190.101:2181,192.168.190.102:2181,192.168.190.103:2183";
    private static int sessionTimeout = 20000;

    private static String discoverNode = "/discover";

    private static ZooKeeper zkClient;

    private static List<String> serverList = new ArrayList<>();


    public DiscoverClient() throws IOException, InterruptedException, KeeperException {
        zkClient = new ZooKeeper(zkAddress, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                try {
                    updateServerList();
                    doSomething();
                } catch (InterruptedException | KeeperException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        updateServerList();
    }

    private void updateServerList() throws InterruptedException, KeeperException {
        serverList = new ArrayList<>();
        serverList.addAll(zkClient.getChildren(discoverNode, true));
        for(String server:serverList){
            System.out.print(server + " ");
        }
        System.out.println();
    }

    public void doSomething(){
        System.out.println(serverList.size());
    }

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        DiscoverClient curClient = new DiscoverClient();

        for (int i = 0; i < 100; i++) {
            Thread.sleep(3000);
        }
        Thread.sleep(100000);
    }
}
