ksql  http://ksqldb-server:8088

SET 'auto.offset.reset'='earliest';

CREATE STREAM CheckingResult (line VARCHAR) WITH (KAFKA_TOPIC='OutputUpperTextLineProcessor', VALUE_FORMAT='KAFKA');

SELECT * FROM CheckingResult EMIT CHANGES;

CREATE STREAM OutputLowerTextLineProcessor2 WITH (KAFKA_TOPIC='OutputLowerTextLineProcessor',  VALUE_FORMAT='KAFKA') AS SELECT Lcase(line) FROM  CheckingResult;

SELECT * FROM OutputLowerTextLineProcessor2 EMIT CHANGES;


private long paymentId;
   private long purchaseId;
   private java.lang.String product;


CREATE STREAM PurchaseStream (id INT KEY, product VARCHAR, left_ts VARCHAR) WITH (KAFKA_TOPIC='PurchaseTopic', VALUE_FORMAT='JSON', TIMESTAMP='left_ts', TIMESTAMP_FORMAT='yyyy-MM-dd''T''HH:mm:ssX', PARTITIONS=4);

CREATE STREAM PayedPurchases2 (id INT KEY, purchaseId INT KEY, paymentId INT, product VARCHAR) WITH (KAFKA_TOPIC='PayedPurchases', VALUE_FORMAT='AVRO');

CREATE STREAM PayedPurchasesCheckingResult (res VARCHAR) WITH (KAFKA_TOPIC='PayedPurchases', VALUE_FORMAT='KAFKA');

SELECT * FROM PayedPurchasesCheckingResult EMIT CHANGES;