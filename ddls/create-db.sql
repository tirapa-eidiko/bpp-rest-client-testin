-- To run this file, issue the following command from the DB2 command line:
-- db2 -svtf create-db.sql

create database app_sor;

connect to app_sor;

CREATE TABLE "SAMPLE"."ACCOUNT"  (
		  "ACCOUNT_ID" INTEGER NOT NULL,
		  "ACCOUNT_NAME" VARCHAR(128) NOT NULL , 
		  "EMAIL" VARCHAR(128) NOT NULL , 
		  "CATEGORY_ID" INTEGER NOT NULL ,
          PRIMARY KEY (ACCOUNT_ID)); 
          
CREATE TABLE "SAMPLE"."ACCOUNT_CATEGORY"  (
		  "CATEGORY_ID" INTEGER NOT NULL, 
		  "CATEGORY_NAME" VARCHAR(128) NOT NULL , 
		  PRIMARY KEY (CATEGORY_ID)); 
		  
CREATE TABLE "SAMPLE"."REQUEST" (
    "REQUEST_ID" INTEGER NOT NULL, 
	"ACCOUNT_ID" INTEGER NOT NULL,
	"DESCRIPTION" VARCHAR(1024),
	"APPROVED" CHAR(1),
	"TYPE_ID" INTEGER NOT NULL,
	"SYSTEM_CODE" CHAR(4),
	"COMMENTS" VARCHAR(1024),
	PRIMARY KEY (REQUEST_ID));
	
CREATE TABLE "SAMPLE"."REQUEST_TYPE"  (
		  "TYPE_ID" INTEGER NOT NULL, 
		  "TYPE_NAME" VARCHAR(128) NOT NULL ,  
		  PRIMARY KEY (TYPE_ID)); 

ALTER TABLE "SAMPLE"."ACCOUNT"
    ADD CONSTRAINT "CATEGORY_ID_FK1" FOREIGN KEY
        ("CATEGORY_ID")
    REFERENCES "SAMPLE"."ACCOUNT_CATEGORY"
        ("CATEGORY_ID")
    ON DELETE RESTRICT
    ON UPDATE NO ACTION;

ALTER TABLE "SAMPLE"."REQUEST"
    ADD CONSTRAINT "TYPE_ID_FK1" FOREIGN KEY
        ("TYPE_ID")
    REFERENCES "SAMPLE"."REQUEST_TYPE"
        ("TYPE_ID")
    ON DELETE RESTRICT
    ON UPDATE NO ACTION;
    
ALTER TABLE "SAMPLE"."REQUEST"
    ADD CONSTRAINT "ACCT_ID_FK2" FOREIGN KEY
        ("ACCOUNT_ID")
    REFERENCES "SAMPLE"."ACCOUNT"
        ("ACCOUNT_ID")
    ON DELETE CASCADE
    ON UPDATE NO ACTION;

disconnect current;


#or



connect to <database>;

CREATE TABLE REQUEST  
( REQUEST_ID number(10) NOT NULL,  
  ACCOUNT_ID number(10),  
  DESCRIPTION varchar2(50) ,  
  TYPE_ID number(10),
  approved varchar2(50) ,  
  system_code varchar2(50) ,  
  comments varchar2(50) 
);  

Insert into REQUEST values(123,123,'description',123,'Y','123','123');


			CREATE TABLE ACCOUNT_CATEGORY  
( CATEGORY_ID number(10) NOT NULL,  
  CATEGORY_NAME varchar2(50) 
  );
  
Insert into ACCOUNT_CATEGORY values(123,'123');
  		CREATE TABLE REQUEST_TYPE  
( TYPE_ID number(10) NOT NULL,  
  TYPE_NAME varchar2(50) 
  );
Insert into REQUEST_TYPE values(123,'123');


CREATE TABLE ACCOUNT  
( ACCOUNT_ID number(10) NOT NULL,  
  ACCOUNT_NAME varchar2(50) ,  
  EMAIL varchar2(50) ,  
  CATEGORY_ID number(10) 
);  


Insert into ACCOUNT values(123,'123','123',123);

Select * from ACCOUNT;
Select * from REQUEST_TYPE;
Select * from ACCOUNT_CATEGORY;
Select * from REQUEST;

disconnect current;


javac -d "C:\MULE\DOWN_STAIRS\BPM\Content\BPM Testing\BPM JUnit DBUnit Sample\classes" -classpath "%CLASSPATH%;C:\MULE\DOWN_STAIRS\BPM\Content\BPM Testing\BPM JUnit DBUnit Sample\classes" "BPDProcessTests.java"

