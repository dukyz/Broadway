CREATE KEYSPACE proxy WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1 };


CREATE TABLE proxy.archive (
    partition_id int,
    order_id int,
    extra map<text, text>,
    proxy text,
    PRIMARY KEY (partition_id, order_id)
)
;


CREATE TABLE proxy.pool (
    partition_id int,
    ts bigint,
    order_id int,
    proxy text,
    PRIMARY KEY (partition_id,ts)
)
WITH gc_grace_seconds = 3600
AND default_time_to_live = 600
;