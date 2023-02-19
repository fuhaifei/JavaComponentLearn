package com.fhf.zookeeper.distributelock;

import com.fhf.mapreduce.serialize.CountFlowDriver;
import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class DistributeLockTest {
    private static class DistributeLock{

        private static  String zkAddress = "192.168.190.101:2181,192.168.190.102:2181,192.168.190.103:2183";
        private static int sessionTimeout = 200000;

        private String lockPath;
        private ZooKeeper zkClient;
        private boolean isLocked;
        private String curNodePath;
        DistributeLock(String lockPath) throws IOException, InterruptedException {
            CountDownLatch connectLatch = new CountDownLatch(1);
            zkClient = new ZooKeeper(zkAddress, sessionTimeout, event -> {
                connectLatch.countDown();
            });
            this.lockPath = lockPath;
            this.isLocked = false;
            connectLatch.await();
        }

        public void lock() throws InterruptedException, KeeperException {
            curNodePath = zkClient.create(lockPath + "/sublock",("data:"+ lockPath).getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
//            System.out.println(curNodePath);
            //监控并等待
            List<String> children = zkClient.getChildren(lockPath, false);
            if(children.size() != 1){
                Collections.sort(children);
                //二分查找
                int left = 0, right = children.size() - 1;
                while(left <= right){
                    int middle = (left + right) / 2;
                    if(children.get(middle).compareTo(curNodePath) >= 0){
                        right = middle - 1;
                    }else{
                        left = middle + 1;
                    }
                }
                if(right != -1){
                    //监听前一个结点是否存在
                    CountDownLatch curLatch = new CountDownLatch(1);
                    if(zkClient.exists(children.get(right), event -> curLatch.countDown()) == null){
                        return;
                    }
                    curLatch.await();
                }
            }
            this.isLocked = true;
        }
        public void unlock() throws InterruptedException, KeeperException {
            if(isLocked){
                zkClient.delete(curNodePath,-1);
                isLocked = false;
            }else{
                System.out.println("not lock");
            }

        }
    }

    private static class Customer implements Runnable{
        DistributeLock lock;
        int customerId;
        CountDownLatch c;

        int[] pizzaNum;

        Customer(int customerId, DistributeLock lock, CountDownLatch c, int[] pizza){
            this.customerId = customerId;
            this.lock = lock;
            this.c = c;
            this.pizzaNum = pizza;
        }

        @Override
        public void run() {
            int eatNum = new Random().nextInt(10);
            int totalEatPizza =0;
            for(int j = 0;j < eatNum;j++){
                try {
                   lock.lock();
                    if(pizzaNum[0] > 0){
                        pizzaNum[0]--;
                        totalEatPizza++;
                        //System.out.printf("customer:%d eat one pizza\n", customerId);
                    }
                    lock.unlock();
                    Thread.sleep(new Random().nextInt(3) * 1000);
                } catch (InterruptedException | KeeperException e) {
                    System.out.println(e);
                    throw new RuntimeException(e);
                }
            }

            System.out.printf("customer:%d eat %d pizza, exit\n", customerId, totalEatPizza);
            c.countDown();
        }

    }

    private static class Producer implements Runnable{
        DistributeLock lock;
        int producerId;
        CountDownLatch c;

        int[] pizzaNum;

        Producer(int producerId, DistributeLock lock, CountDownLatch c, int[] pizza){
            this.producerId = producerId;
            this.lock = lock;
            this.c = c;
            this.pizzaNum = pizza;
        }

        @Override
        public void run() {
            int produceNum = new Random().nextInt(10);
            int totalMakePizza =0;
            for(int j = 0;j < produceNum;j++){
                try {
                    lock.lock();
                    pizzaNum[0]++;
                    totalMakePizza++;
                    lock.unlock();
                    Thread.sleep(new Random().nextInt(3) * 1000);
                    //System.out.printf("producer:%d make a pizza\n", producerId);
                } catch (InterruptedException | KeeperException e) {
                    throw new RuntimeException(e);
                }
            }

            System.out.printf("producer:%d make %d pizza exit\n", producerId, totalMakePizza);
            c.countDown();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        String lockPath = "/locks/testApp";
        final int[] pizzaNum = {0};
        System.out.println("pizza start to make pizza");
        CountDownLatch c = new CountDownLatch(6);
        for(int i = 0;i < 3;i++){
            new Thread(new Producer(i, new DistributeLock(lockPath), c, pizzaNum)).start();
        }

        for(int i = 0;i < 3;i++){
            new Thread(new Customer(i, new DistributeLock(lockPath), c, pizzaNum)).start();
        }
        c.await();
        System.out.println("pizza shop is closed");
    }
}
