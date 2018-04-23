CREATE KEYSPACE crawler WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1 };

CREATE TABLE crawler.url (
    url text,
    html text,
    extra map<text, text>,
    PRIMARY KEY (url)
)
;


CREATE TABLE crawler.word (
    main_word text,
    extra map<text, text>,
    PRIMARY KEY (main_word)
)
;