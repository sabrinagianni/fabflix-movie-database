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

[Inconsistency] Skipping movie: [Movie] Dough and Dynamite (1914), Dir: null, ID: ttnull
[Inconsistency] Skipping movie: [Movie] Fatty and Mabel at the San Diego Exposition (1915), Dir: null, ID: ttnull
[Inconsistency] Skipping movie: [Movie] Fatty and Mabel's Simple Life (1915), Dir: null, ID: ttnull
[Inconsistency] Skipping movie: [Movie] Crime School (1938), Dir: , ID: ttnull
[Inconsistency] Skipping movie: [Movie] El grand cavalcodos (19yy), Dir: Bunuel, ID: ttnull
[Inconsistency] Skipping movie: [Movie] Desire Me (1947), Dir: , ID: ttnull
[Inconsistency] Skipping movie: [Movie] A Girl with Ideas (1937), Dir: , ID: ttnull
[Inconsistency] Skipping movie: [Movie] Four Girls in White (1939), Dir: , ID: ttnull
[Inconsistency] Skipping movie: [Movie] Whistling in the Dark (1941), Dir: , ID: ttnull
[Inconsistency] Skipping movie: [Movie] Song of the Open Road (1944), Dir: , ID: ttnull
[Inconsistency] Skipping movie: [Movie] Secret Agents (1966), Dir: , ID: ttnull
[Inconsistency] Skipping movie: [Movie] I Believe in You (1952), Dir: , ID: ttnull
[Inconsistency] Skipping movie: [Movie]  (1947), Dir: Dassin, ID: ttnull
[Inconsistency] Skipping movie: [Movie] Catherine of Russia (19yy), Dir: W.Staudte, ID: ttnull
[Inconsistency] Skipping movie: [Movie]  (1949), Dir: W.Jackson, ID: ttnull
[Inconsistency] Skipping movie: [Movie] Morgan Stewart's Coming Home (199x), Dir: Alan~Smithee, ID: ttnull
[Inconsistency] Skipping movie: [Movie] Raging Angels (199x), Dir: Alan~Smithee, ID: ttnull
[Inconsistency] Skipping movie: [Movie]  (1999), Dir: Pollock, ID: ttnull
[Inconsistency] Skipping movie: [Movie]  (1954), Dir: G.Reinhardt, ID: ttnull
[Inconsistency] Skipping movie: [Movie]  (1954), Dir: G.Reinhardt, ID: ttnull
[Inconsistency] Skipping movie: [Movie] Stop Train 349 (199x), Dir: Haedrich, ID: ttnull
[Inconsistency] Skipping movie: [Movie] The Human Duplicators (1965), Dir: , ID: ttnull
[Inconsistency] Skipping movie: [Movie] Karate Kid (199x), Dir: J.G.Avildsen, ID: ttnull
[Inconsistency] Skipping movie: [Movie] The Eternal City (196x), Dir: Scorsese, ID: ttnull
[Inconsistency] Skipping movie: [Movie] Buena Vista Social Club (19yy), Dir: Ullrich Felsberg, ID: ttnull
[Inconsistency] Skipping movie: [Movie] Mulholland Drive (19yy), Dir: D.Lynch, ID: ttnull
[Inconsistency] Skipping movie: [Movie]  (1984), Dir: M.Lester, ID: ttnull
[Inconsistency] Skipping movie: [Movie] Sarabande (19yy), Dir: Egoyan, ID: ttnull
[Inconsistency] Skipping movie: [Movie]  (1988), Dir: Silberling, ID: ttnull
[Inconsistency] Skipping movie: [Movie] The Idiots (198), Dir: vonTrier, ID: ttnull
[Inconsistency] Skipping movie: [Movie] Fairy Tale: A True Story (199x), Dir: Sturridge, ID: ttnull
[Inconsistency] Skipping movie: [Movie] Batman, Mask of The Phantasm (1993), Dir: null, ID: ttnull
[Inconsistency] Skipping movie: [Movie] Je suis le seignur du chateau (196), Dir: Wargnier, ID: ttnull
[Inconsistency] Skipping movie: [Movie]  (199x), Dir: , ID: ttnull
[Inconsistency] Skipping movie: [Movie] Jerry and Tom (19yy), Dir: diCillo, ID: ttnull
[Inconsistency] Skipping movie: [Movie] The Shanghai Triad (19yy), Dir: UnYear95, ID: ttnull
[Inconsistency] Skipping movie: [Movie] Taste of Cherry (19yy), Dir: Kiarostami, ID: ttnull
[Inconsistency] Skipping movie: [Movie] All is Routine (19yy), Dir: M.Judge, ID: ttnull
[Inconsistency] Skipping movie: [Movie] Knockout (2000), Dir: null, ID: ttnull
[Inconsistency] Skipping movie: [Movie] Crime + Punsihment in Suburbia (2000), Dir: null, ID: ttnull
Parsed file: mains243.xml
Parsed file: actors63.xml
Skipping bad cast: [Cast] MovieID: ttClC18, Actor: , Role: lover
Skipping bad cast: [Cast] MovieID: ttDMl10, Actor: , Role: Michel's wife
Skipping bad cast: [Cast] MovieID: ttH65, Actor: , Role: missing person
Parsed file: casts124.xml


DEMO VIDEO LINK: https://youtu.be/9HoAo8cbJSs

AWS INSTANCE IP: 3.141.47.152