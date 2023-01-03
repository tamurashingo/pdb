CREATE USER scott IDENTIFIED BY "tiger"
    DEFAULT TABLESPACE users
    TEMPORARY TABLESPACE temp;
GRANT DBA TO scott;
GRANT UNLIMITED TABLESPACE TO scott;
GRANT DEBUG CONNECT SESSION TO scott;
GRANT DEBUG ANY PROCEDURE TO scott;
