- # General

    - #### Names: Sabrina Pukarta, Julia Tjia

    - #### Project 5 Video Demo Link: [https://youtu.be/qNNeYg45_pg](https://youtu.be/tXLd-HR3b90)

    - #### Instruction of deployment: AWS IP INSTANCE : 
    - MASTER: 3.17.166.11
    - SLAVE: 18.118.16.20
    - ORIGINAL FABFLIX: 3.129.169.14

    - #### Collaborations and Work Distribution:
    - Sabrina Pukarta: Task 3-4
    - Julia Tjia: Task 1-2

    - #### Note:
    - Root folder is meant for task 3 files
    - Multiservice files can be found under the "proj5_multi" folder


- # Connection Pooling
    - #### Include the filename/path of all code/configuration files in GitHub of using JDBC Connection Pooling.
    - movies.AddMovieServlet
    - movies.AutocompleteServlet
    - movies.BrowseGenreServlet
    - login.EmployeeLoginServlet
    - movies.InsertStarServlet
    - login.LoginServlet
    - movies.MetadataServlet
    - movies.MovieListServlet
    - movies.PaymentServlet
    - movies.SingleMovieServlet
    - movies.SingleStarServlet 
    - web.xml
    - context.xml

        - #### Explain how Connection Pooling is utilized in the Fabflix code.
        - Fabflix uses Tomcat JDBC Connection Pooling to efficiently manage database connections instead of opening and closing a new connection for every query. The connection pool is configured in the context.xml file with attributes like maxTotal, maxIdle, and maxWaitMillis.

        - #### Explain how Connection Pooling works with two backend SQL.
        - Each instance (Master and Slave) runs its own Tomcat and maintains its own connection pool.
          In the Java code, we load two sets of DB properties (master and slave), and route read vs. write operations accordingly using separate pools.
          The read queries use a load-balanced pool that randomly chooses between master and slave. Write queries always go to the master.


- # Master/Slave
    - #### Include the filename/path of all code/configuration files in GitHub of routing queries to Master/Slave SQL.
      - movies.AddMovieServlet
      - movies.AutocompleteServlet
      - movies.BrowseGenreServlet
      - login.EmployeeLoginServlet
      - movies.InsertStarServlet
      - login.LoginServlet
      - movies.MetadataServlet
      - movies.MovieListServlet
      - movies.PaymentServlet
      - movies.SingleMovieServlet
      - movies.SingleStarServlet
      - movies.Parser
      - movies.UpdateSecurePassword
      - movies.VerifyPassword
      - my.cnf for replication

        - #### How read/write requests were routed to Master/Slave SQL?
        - Fabflix separates read and write operations at the Tomcat level. Write requests are handled by the Tomcat connected to the master MySQL. For read operations, we randomly choose between master and slave pool to reduce load on the master.
          We deployed the same .war file to both servers and updated each context.xml to connect to the appropriate database. An Apache load balancer splits traffic between the two Tomcats, making the routing seamless to users.


- # JMeter TS/TJ Time Logs
    - #### Instructions of how to use the `log_processing.*` script to process the JMeter logs.


- # JMeter TS/TJ Time Measurement Report

| **Single-instance Version Test Plan**          | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 2: HTTP/10 threads                        | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 3: HTTPS/10 threads                       | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 4: HTTP/10 threads/No connection pooling  | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |

| **Scaled Version Test Plan**                   | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 2: HTTP/10 threads                        | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 3: HTTP/10 threads/No connection pooling  | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
