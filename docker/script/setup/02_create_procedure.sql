CONN scott/tiger@xe

CREATE OR REPLACE PACKAGE calc AS
  PROCEDURE helloworld;
  PROCEDURE add3(num_in IN NUMBER, num_out OUT NUMBER);
END calc;
/

CREATE OR REPLACE PACKAGE BODY calc AS
  PROCEDURE helloworld IS
  BEGIN
    DBMS_OUTPUT.PUT_LINE('hello world');
  END;

  PROCEDURE add3(num_in IN NUMBER, num_out OUT NUMBER) IS
    num3 INT := 3;
  BEGIN
    num_out := num3 + num_in;
  END;
END calc;
/
