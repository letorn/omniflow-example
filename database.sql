-- DROP DATABASE IF EXISTS OMNIFLOW_EXAMPLE;
-- DROP TABLE COP_WORKFLOW_INSTANCE_ERROR ;
-- DROP TABLE COP_WORKFLOW_INSTANCE;
-- DROP TABLE COP_WAIT;
-- DROP TABLE COP_RESPONSE;
-- DROP TABLE COP_QUEUE;
-- DROP TABLE COP_AUDIT_TRAIL_EVENT;
-- DROP TABLE COP_ADAPTERCALL;
-- DROP TABLE COP_LOCK;
-- DROP TABLE schedulerconfig;


CREATE DATABASE IF NOT EXISTS OMNIFLOW_EXAMPLE;
USE OMNIFLOW_EXAMPLE;


--
-- BUSINESSPROCESS
--
CREATE TABLE COP_WORKFLOW_INSTANCE (
  ID VARCHAR (128) NOT NULL,
  STATE TINYINT NOT NULL,
  PRIORITY TINYINT NOT NULL,
  LAST_MOD_TS TIMESTAMP NOT NULL,
  PPOOL_ID VARCHAR (32) NOT NULL,
  DATA MEDIUMTEXT NULL,
  OBJECT_STATE MEDIUMTEXT NULL,
  CS_WAITMODE TINYINT,
  MIN_NUMB_OF_RESP SMALLINT,
  NUMB_OF_WAITS SMALLINT,
  TIMEOUT TIMESTAMP,
  CREATION_TS TIMESTAMP NOT NULL,
  CLASSNAME VARCHAR (512) NOT NULL,
  PRIMARY KEY (ID)
) ENGINE = INNODB DEFAULT CHARSET = UTF8 ;


--
--
--
CREATE TABLE COP_WORKFLOW_INSTANCE_ERROR (
  WORKFLOW_INSTANCE_ID VARCHAR (128) NOT NULL,
  EXCEPTION TEXT NOT NULL,
  ERROR_TS TIMESTAMP NOT NULL
) ENGINE = INNODB DEFAULT CHARSET = UTF8 ;

CREATE INDEX IDX_COP_WFID_WFID 
ON COP_WORKFLOW_INSTANCE_ERROR (WORKFLOW_INSTANCE_ID) ;


--
-- RESPONSE
--
CREATE TABLE COP_RESPONSE (
  RESPONSE_ID VARCHAR (128) NOT NULL,
  CORRELATION_ID VARCHAR (128) NOT NULL,
  RESPONSE_TS TIMESTAMP NOT NULL,
  RESPONSE MEDIUMTEXT,
  RESPONSE_TIMEOUT TIMESTAMP,
  RESPONSE_META_DATA VARCHAR (4000),
  PRIMARY KEY (RESPONSE_ID)
) ENGINE = INNODB DEFAULT CHARSET = UTF8 ;

CREATE INDEX IDX_COP_RESP_CID 
ON COP_RESPONSE (CORRELATION_ID) ;


--
-- WAIT
--
CREATE TABLE COP_WAIT (
  CORRELATION_ID VARCHAR (128) NOT NULL,
  WORKFLOW_INSTANCE_ID VARCHAR (128) NOT NULL,
  MIN_NUMB_OF_RESP SMALLINT NOT NULL,
  TIMEOUT_TS TIMESTAMP NULL,
  STATE TINYINT NOT NULL,
  PRIORITY TINYINT NOT NULL,
  PPOOL_ID VARCHAR (32) NOT NULL,
  PRIMARY KEY (CORRELATION_ID)
) ENGINE = INNODB DEFAULT CHARSET = UTF8 ;

CREATE INDEX IDX_COP_WAIT_WFI_ID 
ON COP_WAIT (WORKFLOW_INSTANCE_ID) ;


--
-- QUEUE
--
CREATE TABLE COP_QUEUE (
  PPOOL_ID VARCHAR (32) NOT NULL,
  PRIORITY TINYINT NOT NULL,
  LAST_MOD_TS TIMESTAMP NOT NULL,
  WORKFLOW_INSTANCE_ID VARCHAR (128) NOT NULL,
  ENGINE_ID VARCHAR (16) NULL,
  PRIMARY KEY (WORKFLOW_INSTANCE_ID)
) ENGINE = INNODB DEFAULT CHARSET = UTF8 ;


--
--
--
CREATE TABLE COP_AUDIT_TRAIL_EVENT (
  SEQ_ID BIGINT NOT NULL AUTO_INCREMENT,
  OCCURRENCE TIMESTAMP NOT NULL,
  CONVERSATION_ID VARCHAR (64) NOT NULL,
  LOGLEVEL TINYINT NOT NULL,
  CONTEXT VARCHAR (128) NOT NULL,
  INSTANCE_ID VARCHAR (128) NULL,
  CORRELATION_ID VARCHAR (128) NULL,
  TRANSACTION_ID VARCHAR (128) NULL,
  LONG_MESSAGE LONGTEXT NULL,
  MESSAGE_TYPE VARCHAR (256) NULL,
  PRIMARY KEY (SEQ_ID)
) ENGINE = INNODB DEFAULT CHARSET = UTF8 ;


--
--
--
CREATE TABLE COP_ADAPTERCALL (
  WORKFLOWID VARCHAR (128) NOT NULL,
  ENTITYID VARCHAR (128) NOT NULL,
  ADAPTERID VARCHAR (128) NOT NULL,
  PRIORITY BIGINT NOT NULL,
  DEFUNCT CHAR(1) DEFAULT '0' NOT NULL,
  DEQUEUE_TS TIMESTAMP,
  METHODDECLARINGCLASS VARCHAR (1024) NOT NULL,
  METHODNAME VARCHAR (1024) NOT NULL,
  METHODSIGNATURE VARCHAR (2048) NOT NULL,
  ARGS LONGTEXT,
  PRIMARY KEY (ADAPTERID, WORKFLOWID, ENTITYID)
) ENGINE = INNODB DEFAULT CHARSET = UTF8 ;

CREATE INDEX COP_IDX_ADAPTERCALL 
ON COP_ADAPTERCALL (ADAPTERID, PRIORITY) ;


--
-- COP_LOCK
--
CREATE TABLE COP_LOCK (
  LOCK_ID VARCHAR (128) NOT NULL,
  CORRELATION_ID VARCHAR (128) NOT NULL,
  WORKFLOW_INSTANCE_ID VARCHAR (128) NOT NULL,
  INSERT_TS TIMESTAMP NOT NULL,
  REPLY_SENT CHAR(1) NOT NULL,
  PRIMARY KEY (LOCK_ID, WORKFLOW_INSTANCE_ID)
) ENGINE = INNODB DEFAULT CHARSET = UTF8 ;

 
 
--
-- 定时器
--
CREATE TABLE schedulerconfig (
  id bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'AI',
  schedulerName varchar(64) NOT NULL COMMENT 'scheduler名字, 应该在SchedulerDef里面定义',
  triggerType varchar(64) NOT NULL COMMENT '触发类型, 参照TriggerType枚举类型目前支持INTERVAL_REPEAT,TIME_REPEAT,RULE',
  enabled tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用1为启用, 0为不启用, 不影响当前正在运行的scheduler',
  repeatInterval int(10) unsigned DEFAULT NULL COMMENT '下次执行间隔时间INTERVAL_REPEAT类型必填',
  timeUnit varchar(16) DEFAULT NULL COMMENT 'repeatInterval 的时间单位, 目前支持的: MILLISECONDS,SECONDS,MINUTES,HOURS,DAYS, INTERVAL_REPEAT类型必填',
  cronExp varchar(64) DEFAULT NULL COMMENT 'TIME_REPEAT类型必填, 使用的触发cron expression其它类型无视',
  firstRunTime datetime DEFAULT NULL COMMENT '首次触发时间, 如果为空则等待配置的间隔时间',
  totalRunCount int(10) unsigned DEFAULT NULL COMMENT '总执行次数, 如果为空则永远执行下去',
  noCatchUp tinyint(1) unsigned DEFAULT '0' COMMENT '如果设为true则不会在当前时间比下次执行时间晚的情况下立即触发任务. 例子: 上次触发时间是8:30分, 设定是每30分钟触发一次, 然后因为系统维护, 启动的时候当然已经9:10了,如果noCatchUp=true则下次执行时间是9:30, 如果false则马上会执行',
  defaultPoolId varchar(255) DEFAULT NULL COMMENT '默认工作流池ID, 如果为空则进入默认池',
  defaultPriority int(10) unsigned DEFAULT NULL COMMENT '默认执行优先级, 如果为空则设为默认优先级(5), 数字越小优先级越高, 1为最高',
  createdBy bigint(20) NOT NULL,
  createdDate datetime NOT NULL,
  modifiedBy bigint(20) NOT NULL,
  modifiedDate datetime NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY UNI_schedulerconfig_schedulerName (schedulerName)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='所有定时任务都需要在这里配置默认属性';