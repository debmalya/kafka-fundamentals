ksql  http://ksqldb-server:8088

SET 'auto.offset.reset'='earliest';


1. In KSQL
 
CREATE STREAM PurchaseStream (id INT KEY, product VARCHAR, left_ts VARCHAR) WITH (KAFKA_TOPIC='PurchaseTopic', VALUE_FORMAT='JSON', TIMESTAMP='left_ts', TIMESTAMP_FORMAT='yyyy-MM-dd''T''HH:mm:ssX', PARTITIONS=4);
 
CREATE STREAM PaymentStream (ID INT KEY, purchaseId INT, status VARCHAR, right_ts VARCHAR) WITH (KAFKA_TOPIC='PaymentTopic', VALUE_FORMAT='JSON', TIMESTAMP='right_ts', TIMESTAMP_FORMAT='yyyy-MM-dd''T''HH:mm:ssX', PARTITIONS=4);
 
 2. Insert values
 
INSERT INTO PurchaseStream (id, product, left_ts) VALUES (1,  'kettle', '2022-01-29T06:01:18Z');
INSERT INTO PurchaseStream (id, product, left_ts) VALUES (2,  'grill' , '2022-01-29T17:02:20Z');
INSERT INTO PurchaseStream (id, product, left_ts) VALUES (3,  'toaster', '2022-01-29T13:44:10Z');
INSERT INTO PurchaseStream (id, product, left_ts) VALUES (4,  'hair dryer', '2022-01-29T11:58:25Z');
 

3. Insert values

INSERT INTO PaymentStream (id, purchaseId, status, right_ts) VALUES (101, 1, 'OK', '2022-01-29T06:11:18Z');
INSERT INTO PaymentStream (id, purchaseId, status, right_ts) VALUES (103, 3, 'OK', '2022-01-29T13:54:10Z');
INSERT INTO PaymentStream (id, purchaseId, status, right_ts) VALUES (104, 4, 'OK', '2022-01-29T12:08:25Z');

INSERT INTO PaymentStream (id, purchaseId, status, right_ts) VALUES (102, 2, 'OK', '2022-01-20T06:11:18Z');

INSERT INTO PaymentStream (id, purchaseId, status, right_ts) VALUES (102, 2, 'OK', '2022-01-24T06:11:18Z');


4.
SELECT l.id AS purchaseId, l.product, r.status FROM PurchaseStream l INNER JOIN PaymentStream r WITHIN 7 DAYS ON l.id = r.purchaseId EMIT CHANGES;


