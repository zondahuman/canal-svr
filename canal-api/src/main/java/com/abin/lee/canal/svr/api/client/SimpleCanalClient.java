package com.abin.lee.canal.svr.api.client;

import com.abin.lee.canal.svr.api.enums.SchemaEnums;
import com.abin.lee.canal.svr.api.enums.TableEnums;
import com.abin.lee.canal.svr.api.service.DealWrapperService;
import com.abin.lee.canal.svr.api.service.TradeWrapperService;
import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry.*;
import com.alibaba.otter.canal.protocol.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: tinkpad
 * Date: 16-1-11
 * Time: 下午10:28
 * To change this template use File | Settings | File Templates.
 */
@Slf4j
@Component
public class SimpleCanalClient {

    @Autowired
    DealWrapperService dealWrapperService;
    @Autowired
    TradeWrapperService tradeWrapperService;

    @PostConstruct
    public void connectSingle() {
//        String canalHost = "172.16.2.132";
        String canalHost = "127.0.0.1";
//        String canalHost = "localhost";
        // 创建链接
        CanalConnector connector = CanalConnectors.newSingleConnector(new InetSocketAddress(canalHost,
                11111), "example", "canal", "canal");
        int batchSize = 1000;
        int emptyCount = 0;
        try {
            connector.connect();
            connector.subscribe(SchemaEnums.deal + "." + TableEnums.business_info);
            connector.rollback();
            int totalEmtryCount = 120;
            while (emptyCount < totalEmtryCount) {
                Message message = connector.getWithoutAck(batchSize); // 获取指定数量的数据
                long batchId = message.getId();
                int size = message.getEntries().size();
                if (batchId == -1 || size == 0) {
                    emptyCount++;
                    System.out.println("empty count : " + emptyCount);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                } else {
                    emptyCount = 0;
                    // System.out.printf("message[batchId=%s,size=%s] \n", batchId, size);
                    printEntry(message.getEntries());
                }

                connector.ack(batchId); // 提交确认
                // connector.rollback(batchId); // 处理失败, 回滚数据
            }

            System.out.println("empty too many times, exit");
        } finally {
//            connector.disconnect();
        }
    }

    private void printEntry(List<Entry> entrys) {
        for (Entry entry : entrys) {
            if (entry.getEntryType() == EntryType.TRANSACTIONBEGIN || entry.getEntryType() == EntryType.TRANSACTIONEND) {
                continue;
            }

            RowChange rowChage = null;
            try {
                rowChage = RowChange.parseFrom(entry.getStoreValue());
            } catch (Exception e) {
                throw new RuntimeException("ERROR ## parser of eromanga-event has an error , data:" + entry.toString(),
                        e);
            }

            EventType eventType = rowChage.getEventType();
            System.out.println(String.format("================> binlog[%s:%s] , name[%s,%s] , eventType : %s",
                    entry.getHeader().getLogfileName(), entry.getHeader().getLogfileOffset(),
                    entry.getHeader().getSchemaName(), entry.getHeader().getTableName(),
                    eventType));

            for (RowData rowData : rowChage.getRowDatasList()) {
                if (eventType == EventType.DELETE) {
                    printColumn(rowData.getBeforeColumnsList(), entry);
                } else if (eventType == EventType.INSERT) {
                    printColumn(rowData.getAfterColumnsList(), entry);
                } else {
                    System.out.println("-------> before");
                    printColumn(rowData.getBeforeColumnsList(), entry);
                    System.out.println("-------> after");
                    printColumn(rowData.getAfterColumnsList(), entry);
                }
            }
        }
    }


    private void printColumn(List<Column> columns, Entry entry) {
        System.out.println("entry.getHeader().getTableName()=" + entry.getHeader().getTableName());
        String schemaName = entry.getHeader().getSchemaName();
        SchemaEnums schemaEnums = SchemaEnums.find(schemaName);
        switch (schemaEnums) {
            case deal:
                this.dealWrapperService.deal(columns, entry);
                break;
            case trade:
                this.tradeWrapperService.trade(columns, entry);
                break;
            default:
                for (Column column : columns) {
                    System.out.println(column.getName() + " : " + column.getValue() + "    update=" + column.getUpdated());
                }
                break;
        }
    }


}