package com.woting.crawler.core.etl1.control;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.woting.crawler.core.etl1.Etl1Process;
import com.woting.crawler.core.etl1.model.Etl1Config;

public class Etl1Running extends Thread {
    protected Logger logger = LoggerFactory.getLogger(Etl1Running.class);

    protected Etl1Config config;//etl处理配置

    protected BlockingQueue<Map<String, Object>> queue;//处理队列
    protected Etl1Process process;

    public Etl1Running(Etl1Config eConfig) {
        try {
            config=eConfig;
            queue=new LinkedBlockingQueue<Map<String, Object>>(config.getQueueSize());
            logger.info("[{}]第一次Etl过程，初始化缓存队列，设置最大容量为<{}>", config.getEtl1Name(), config.getQueueSize());
            process=(Etl1Process)Class.forName(config.getClassName()).newInstance();
            process.init();

            //启动加载数据线程：生产者
            (new LoadData("["+config.getEtl1Name()+"]第一次Etl过程数据加载线程")).start();
            //启动单条数据的处理程序：消费者
            for (int i=0; i<config.getThreadNum(); i++) {
                (new DealOneData("["+config.getEtl1Name()+"]第一次Etl过程第<"+i+">个数据处理线程")).start();
            }
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载数据：生产者
     */
    class LoadData extends Thread {
        protected LoadData(String name) {
            super.setName(name);
        }
        public void run() {
            logger.info("{}启动...", this.getName());
            int i=0;
            while (true) {
                List<Map<String, Object>> orgiDatas=process.getOrigDataList(config.getQueueSize(), i);
                if (orgiDatas.isEmpty()) {
                    i=0;
                    continue;
                }
                logger.info("加载第<{}>页原始数据，本页数据<{}>条", i, orgiDatas.size());
                for (Map<String, Object> oneData: orgiDatas) {
                    try {
                        while (!queue.offer(oneData, 20, TimeUnit.MILLISECONDS));//一直要把这个数据加入队列，否则一直等待
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                i=(i+1)%2;
            }
        }
    }

    class DealOneData extends Thread {
        protected DealOneData(String name) {
            super.setName(name);
        }
        public void run() {
            logger.info("{}启动...", this.getName());
            while (true) {
                Map<String, Object> data=queue.poll();
                if (data!=null) {
                    process.dealOneData(data);
                    logger.info("{}处理数据::{}", this.getName(), data.get("assetType")+":"+data.get("assetName")+":"+data.get("seqName"));
                }
            }
        }
    }
}