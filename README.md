# 2025-spring-cs-122b

Sabrina Pukarta : reCAPTCHA, HTTPS, preparedstatement, encrypted password, Parser

Julia Tjia : preparedstatement, encrypted password servlet backend, dashboard with stored procedure, Parser

We used PreparedStatement on our Servlets to prevent SQL injection. 
The files :
BrowseGenreServlet
EmployeeLoginServlet
EncryptEmployeePassword
InsertStartServlet
LoginServlet
MovieListServlet
PaymentServlet
SingleMovieServlet
SingleStarServlet
UpdateSecurePassword
VerifyPassword

Parsing Time Optimization Strategies

To significantly improve performance compared to a naive row-by-row insertion approach, we implemented the following two optimizations:

Batching Inserts:
Instead of executing one SQL INSERT at a time, we use addBatch() to group many insertions together and execute them as a batch. We commit batches every 500-1000 entries. This reduces database overhead and improves throughput.

Multithreading:
We run movie, actor, and cast insertions in parallel using Java’s ExecutorService with 3 threads. This allows us to fully utilize CPU resources and reduces total parsing time, especially given that these insertions operate on independent data.

Additionally, we enabled rewriteBatchedStatements=true in our JDBC URL to allow MySQL to optimize the batched statements further at the driver level.

Inconsistent Data Report

During parsing, malformed or incomplete data is not inserted into the database. Instead, we log inconsistencies to the console for transparency.

Examples of skipped data include:

Movies missing title, year, or director

Actors missing names

Cast entries with unknown movie ID or actor name

All such entries are printed to the console during parsing.


=== SUMMARY (More Details in the actual outputted file when Parser.java is ran) ===
Inserted Movies: 12099
Inserted Genres: 125
Inserted Stars: 6863
Inserted Genres_in_Movies: 9850
Inserted Stars_in_Movies: 30373


DEMO VIDEO LINK: 
For some reason, both my partner and my instance randomly stopped working when we were trying to film the demo video...
We tried things like restarting, redoing the ssh, but nothing seemed to work, and we were not sure what to do since this isn't
something we can control as we found out others were going through a similar situation.

AWS INSTANCE IP: 18.217.138.184