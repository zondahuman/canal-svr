march action

# Install Conf
* canal: canal.deployer-1.0.25.tar.gz
* zookeeper: 3.4.9



# Canal-API :

* INSERT INTO `team`(`team_name`, `create_time`, `update_time`, `version`) VALUES ('abin4', now(),  now(), 0);

* INSERT INTO `business_info`(`user_id`, `longitude`, `latitude`, `business_name`, `business_price`, `province_name`, `city_name`, `district_name`, `category`, `field1`, `field2`, `flag`, `create_time`, `update_time`, `version`) VALUES ('11123', '116.480979', '39.827404', '海底捞', 450.05, '北京', '北京市', '朝阳区', 'COMMON', '二狗', '狗蛋', 'PAYED', now(), now(), 0);



# 同步网络时间
sudo ntpdate -u time.nist.gov


# Canal Install :

* wget https://github.com/alibaba/canal/releases/download/v1.0.25/canal.deployer-1.0.25.tar.gz


# Mysql Conf:
binlog：开通binlog日志，以及canal用户
python
[mysqld]
log-bin=mysql-bin #添加这一行就ok
binlog-format=ROW #选择row模式
server_id=1 #配置mysql replaction需要定义，不能和canal的slaveId重复

# Mysql Configuration :

CREATE USER canal IDENTIFIED BY 'canal';
GRANT SELECT, REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'canal'@'%';
-- GRANT ALL PRIVILEGES ON *.* TO 'canal'@'%' ;
FLUSH PRIVILEGES;

show grants for 'canal'


# debezium binlog subscribe :

* debezium


# Zookeeper Install :

* wget https://archive.apache.org/dist/zookeeper/zookeeper-3.4.6/zookeeper-3.4.6.tar.gz
* wget https://archive.apache.org/dist/zookeeper/zookeeper-3.4.5/zookeeper-3.4.5.tar.gz




# git tag

git tag v1.0.0_canal_springboot2.0.0
git push -u origin v1.0.0_canal_springboot2.0.0


git tag v1.0.0_elasticsearh_springboot2.0.0

git push -u origin v1.0.0_elasticsearh_springboot2.0.0


# Show Binlog

show binary logs;
show binlog events;
show master status;

SHOW BINLOG EVENTS IN 'mysql-bin.000002' FROM 4 LIMIT 2 ;

SHOW BINLOG EVENTS IN 'mysql-bin.000005';


C:\Users\tinkpad>cd D:\Sys\Database\mysql-5.6.26-winx64\bin
C:\Users\tinkpad>d:
D:\Sys\Database\mysql-5.6.26-winx64\bin>mysqlbinlog.exe D:\Sys\Database\mysql-5.6.26-winx64\data\mysql-bin.000001






