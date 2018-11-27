# Dropwizard application example 
---
* Build : mvn install. 
* Run : java -jar target/dw-ms-app-1.0-SNAPSHOT.jar server application.yml 



### Authentication 
* Request URL  : localhost:8080/people 
* Request Type :  POST 

#### Request Header

* Authorization : Basic YWRtaW46cGFzc3dvcmQ= 
* Content-Type  : application/json 
* Cache-Control : no-cache 
* Accept        : */* 

#### Request Body

```json
{"fullName": "Rashad Javadov"  ,"jobTitle":"Engineer"}  
```

#### Health Check
* Request URL  : http://localhost:8081/healthcheck
* Request Type : GET 

