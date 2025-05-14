//import org.xml.sax.*;
//import org.xml.sax.helpers.DefaultHandler;
//import javax.xml.parsers.SAXParser;
//import javax.xml.parsers.SAXParserFactory;
//import java.io.File;
//import java.sql.*;
//import java.util.*;
//
//public class Parser {
//    private static Connection conn;
//
//    public static void main(String[] args) throws Exception {
//        String loginUser = "mytestuser";
//        String loginPasswd = "My6$Password";
//        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
//
//        Class.forName("com.mysql.cj.jdbc.Driver");
//        conn = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
//
//        parseMains();
//        parseActors();
//        parseCasts();
//
//        conn.close();
//        System.out.println("Parsing and insertion completed.");
//    }
//
//    private static void parseMains() throws Exception {
//        SAXParserFactory factory = SAXParserFactory.newInstance();
//        SAXParser parser = factory.newSAXParser();
//
//        parser.parse(new File("mains243.xml"), new DefaultHandler() {
//            String currentElement = "";
//            String id = "", title = "", year = "", director = "";
//            Set<String> genres = new HashSet<>();
//
//            public void startElement(String uri, String localName, String qName, Attributes attributes) {
//                currentElement = qName;
//                if (qName.equals("film")) {
//                    id = "tt" + attributes.getValue("fid");
//                    genres.clear();
//                }
//            }
//
//            public void characters(char[] ch, int start, int length) {
//                String data = new String(ch, start, length).trim();
//                if (currentElement.equals("t")) title += data;
//                else if (currentElement.equals("year")) year += data;
//                else if (currentElement.equals("dirn")) director += data;
//                else if (currentElement.equals("cat")) genres.add(data);
//            }
//
//            public void endElement(String uri, String localName, String qName) throws SAXException {
//                if (qName.equals("film")) {
//                    try {
//                        PreparedStatement psMovie = conn.prepareStatement(
//                                "INSERT IGNORE INTO movies(id, title, year, director) VALUES (?, ?, ?, ?)");
//                        psMovie.setString(1, id);
//                        psMovie.setString(2, title);
//                        psMovie.setInt(3, Integer.parseInt(year));
//                        psMovie.setString(4, director);
//                        psMovie.executeUpdate();
//                        psMovie.close();
//
//                        for (String genre : genres) {
//                            PreparedStatement psGenre = conn.prepareStatement(
//                                    "INSERT IGNORE INTO genres(name) VALUES (?)");
//                            psGenre.setString(1, genre);
//                            psGenre.executeUpdate();
//                            psGenre.close();
//
//                            PreparedStatement psGenreInMovie = conn.prepareStatement(
//                                    "INSERT IGNORE INTO genres_in_movies(genreId, movieId) SELECT id, ? FROM genres WHERE name = ?");
//                            psGenreInMovie.setString(1, id);
//                            psGenreInMovie.setString(2, genre);
//                            psGenreInMovie.executeUpdate();
//                            psGenreInMovie.close();
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    title = "";
//                    year = "";
//                    director = "";
//                }
//            }
//        });
//        System.out.println("Parsed mains243.xml");
//    }
//
//    private static void parseActors() throws Exception {
//        SAXParserFactory factory = SAXParserFactory.newInstance();
//        SAXParser parser = factory.newSAXParser();
//
//        parser.parse(new File("actors63.xml"), new DefaultHandler() {
//            String currentElement = "";
//            String name = "", birthYear = "";
//            int starCount = 1;
//
//            public void startElement(String uri, String localName, String qName, Attributes attributes) {
//                currentElement = qName;
//            }
//
//            public void characters(char[] ch, int start, int length) {
//                String data = new String(ch, start, length).trim();
//                if (currentElement.equals("stagename")) name += data;
//                else if (currentElement.equals("dob")) birthYear += data;
//            }
//
//            public void endElement(String uri, String localName, String qName) throws SAXException {
//                if (qName.equals("actor")) {
//                    try {
//                        String id = String.format("nm%07d", starCount++);
//                        PreparedStatement ps = conn.prepareStatement("INSERT IGNORE INTO stars(id, name, birthYear) VALUES (?, ?, ?)");
//                        ps.setString(1, id);
//                        ps.setString(2, name);
//                        if (birthYear.isEmpty()) ps.setNull(3, Types.INTEGER);
//                        else ps.setInt(3, Integer.parseInt(birthYear));
//                        ps.executeUpdate();
//                        ps.close();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    name = "";
//                    birthYear = "";
//                }
//            }
//        });
//        System.out.println("Parsed actors63.xml");
//    }
//
//    private static void parseCasts() throws Exception {
//        SAXParserFactory factory = SAXParserFactory.newInstance();
//        SAXParser parser = factory.newSAXParser();
//
//        parser.parse(new File("casts124.xml"), new DefaultHandler() {
//            String currentElement = "";
//            String movieId = "", starName = "";
//
//            public void startElement(String uri, String localName, String qName, Attributes attributes) {
//                currentElement = qName;
//                if (qName.equals("m")) movieId = "tt" + attributes.getValue("fid");
//            }
//
//            public void characters(char[] ch, int start, int length) {
//                String data = new String(ch, start, length).trim();
//                if (currentElement.equals("a")) starName += data;
//            }
//
//            public void endElement(String uri, String localName, String qName) throws SAXException {
//                if (qName.equals("m")) {
//                    try {
//                        PreparedStatement ps = conn.prepareStatement(
//                                "INSERT IGNORE INTO stars_in_movies(starId, movieId) SELECT id, ? FROM stars WHERE name = ?");
//                        ps.setString(1, movieId);
//                        ps.setString(2, starName);
//                        ps.executeUpdate();
//                        ps.close();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    starName = "";
//                }
//            }
//        });
//        System.out.println("Parsed casts124.xml");
//    }
//}

//import org.xml.sax.Attributes;
//import org.xml.sax.SAXException;
//import org.xml.sax.helpers.DefaultHandler;
//
//import javax.xml.parsers.SAXParser;
//import javax.xml.parsers.SAXParserFactory;
//import java.io.File;
//import java.util.*;
//import java.sql.*;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//public class Parser extends DefaultHandler {
//    // Temporary data holders
//    private String tempVal;
//
//    // Data structures to store the parsed data
//    private List<Movie> movies = new ArrayList<>();
//    private List<Actor> actors = new ArrayList<>();
//    private List<Cast> casts = new ArrayList<>();
//
//    private Movie tempMovie;
//    private Actor tempActor;
//    private Cast tempCast;
//
//    private String currentFile = "";
//
//    // Toggle this to test casts in smaller batch (-1 to disable?)
//    private static final int CASTS_TEST_LIMIT = -1;
//
//    public void runExample() {
//        parseFile("mains243.xml");
//        parseFile("actors63.xml");
//        parseFile("casts124.xml");
//
//        // Insert parsed data using multithreading
//        try {
//            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb?rewriteBatchedStatements=true", "mytestuser", "My6$Password");
//            ExecutorService executor = Executors.newFixedThreadPool(3);
//
//            executor.execute(() -> insertMovies(conn));
//            executor.execute(() -> insertActors(conn));
//            executor.execute(() -> insertCasts(conn));
//
//            executor.shutdown();
//            while (!executor.isTerminated()) {}
//
//            conn.close();
//            System.out.println("DONE.");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        // Optionally, print the data to check
//        printData();
//    }
//
//    private void parseFile(String fileName) {
//        currentFile = fileName;
//        try {
//            SAXParserFactory factory = SAXParserFactory.newInstance();
//            SAXParser saxParser = factory.newSAXParser();
//
//            File file = new File("src/" + fileName);
//            saxParser.parse(file, this);
//            System.out.println("Parsed file: " + fileName);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    // When element starts
//    public void startElement(String uri, String localName, String qName, Attributes attributes) {
//        tempVal = "";
//        if (currentFile.contains("mains") && qName.equalsIgnoreCase("film")) {
//            tempMovie = new Movie("tt" + attributes.getValue("fid"));
//            tempMovie.genres = new HashSet<>();
//        } else if (currentFile.contains("actors") && qName.equalsIgnoreCase("actor")) {
//            tempActor = new Actor();
//        } else if (currentFile.contains("casts") && qName.equalsIgnoreCase("m")) {
//            tempCast = new Cast();
////            String fid = attributes.getValue("fid");
////            if (fid != null) {
////                tempCast.movieId = "tt" + fid;
////            } else {
////                tempCast.movieId = null; // allow it to be tracked
////            }
//        }
//    }
//
//    // Read text content
//    public void characters(char[] ch, int start, int length) {
//        tempVal += new String(ch, start, length).trim();
//    }
//
//    // When element ends
//    public void endElement(String uri, String localName, String qName) {
//        if (currentFile.contains("mains")) {
////            if (qName.equalsIgnoreCase("film")) {
////                movies.add(tempMovie);
////            } else if (qName.equalsIgnoreCase("t")) {
////                tempMovie.title = tempVal;
////            } else if (qName.equalsIgnoreCase("year")) {
////                tempMovie.year = tempVal;
////            } else if (qName.equalsIgnoreCase("dirn")) {
////                tempMovie.director = tempVal;
////            }
//            switch (qName) {
//                case "film":
//                    if (tempMovie.isValid()) {
//                        movies.add(tempMovie);
//                    } else {
//                        System.out.println("[Inconsistency] Skipping movie: " + tempMovie);
//                    }
//                    break;
//                case "t":
//                    tempMovie.title = tempVal;
//                    break;
//                case "year":
//                    tempMovie.year = tempVal;
//                    break;
//                case "dirn":
//                    tempMovie.director = tempVal;
//                    break;
//                case "cat":
//                    tempMovie.genres.add(tempVal);
//                    break;
//            }
//        } else if (currentFile.contains("actors")) {
////            if (qName.equalsIgnoreCase("actor")) {
////                actors.add(tempActor);
////            } else if (qName.equalsIgnoreCase("stagename")) {
////                tempActor.name = tempVal;
////            } else if (qName.equalsIgnoreCase("dob")) {
////                tempActor.dob = tempVal;
////            }
//            switch (qName) {
//                case "actor":
//                    if (tempActor.name != null && !tempActor.name.isEmpty()) {
//                        actors.add(tempActor);
//                    } else {
//                        System.out.println("[Inconsistency] Skipping actor: " + tempActor);
//                    }
//                    break;
//                case "stagename":
//                    tempActor.name = tempVal;
//                    break;
//                case "dob":
//                    tempActor.dob = tempVal;
//                    break;
//            }
//        } else if (currentFile.contains("casts")) {
////            if (qName.equalsIgnoreCase("m")) {
////                casts.add(tempCast);
////            } else if (qName.equalsIgnoreCase("a")) {
////                tempCast.actorName = tempVal;
////            } else if (qName.equalsIgnoreCase("role")) {
////                tempCast.role = tempVal;
////            }
//            switch (qName) {
//                case "f":
//                    tempCast.movieId = "tt" + tempVal;
//                    break;
//                case "a":
//                    tempCast.actorName = tempVal;
//                    break;
//                case "r":
//                    tempCast.role = tempVal;
//                    break;
//                case "m":
//                    // only add if both fields exist
//                    if (tempCast.movieId != null && tempCast.actorName != null && !tempCast.actorName.isEmpty()) {
//                        casts.add(tempCast);
//                    } else {
//                        System.out.println("Skipping bad cast: " + tempCast);
//                    }
//                    break;
//            }
//        }
//    }
//
//    private void insertMovies(Connection conn) {
//        System.out.println("Inserting " + movies.size() + " movies...");
//        try {
//            conn.setAutoCommit(false);
//
//            PreparedStatement psMovie = conn.prepareStatement("INSERT IGNORE INTO movies(id, title, year, director) VALUES (?, ?, ?, ?)");
//            PreparedStatement psGenre = conn.prepareStatement("INSERT IGNORE INTO genres(name) VALUES (?)");
//            PreparedStatement psGenreLink = conn.prepareStatement("INSERT IGNORE INTO genres_in_movies(genreId, movieId) SELECT id, ? FROM genres WHERE name = ?");
//
//            int count = 0;
//            for (Movie m : movies) {
//                try {
//                    psMovie.setString(1, m.id);
//                    psMovie.setString(2, m.title);
//                    psMovie.setInt(3, Integer.parseInt(m.year));
//                    psMovie.setString(4, m.director);
//                    psMovie.addBatch();
//
//                    for (String genre : m.genres) {
//                        psGenre.setString(1, genre);
//                        psGenre.addBatch();
//
//                        psGenreLink.setString(1, m.id);
//                        psGenreLink.setString(2, genre);
//                        psGenreLink.addBatch();
//                    }
//
//                    if (++count % 1000 == 0) {
//                        psMovie.executeBatch();
//                        psGenre.executeBatch();
//                        psGenreLink.executeBatch();
//                    }
//                } catch (Exception e) {
//                    System.out.println("Skipping bad movie: " + m);
//                    e.printStackTrace();
//                }
//            }
//            psMovie.executeBatch();
//            psGenre.executeBatch();
//            psGenreLink.executeBatch();
//            conn.commit();
//
//            psMovie.close();
//            psGenre.close();
//            psGenreLink.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void insertActors(Connection conn) {
//        System.out.println("Inserting " + actors.size() + " actors...");
//        try {
//            conn.setAutoCommit(false);
//            PreparedStatement ps = conn.prepareStatement("INSERT IGNORE INTO stars(id, name, birthYear) VALUES (?, ?, ?)");
//
//            int count = 1;
//            for (Actor a : actors) {
//                try {
//                    String id = String.format("nm%07d", count++);
//                    ps.setString(1, id);
//                    ps.setString(2, a.name);
//                    if (a.dob == null || a.dob.isEmpty() || !a.dob.matches("\\d+")) {
//                        ps.setNull(3, Types.INTEGER);
//                    } else {
//                        ps.setInt(3, Integer.parseInt(a.dob));
//                    }
//                    ps.addBatch();
//
//                    if (count % 500 == 0) ps.executeBatch();
//                } catch (Exception e) {
//                    System.out.println("Skipping bad actor: " + a);
//                }
//            }
//            ps.executeBatch();
//            conn.commit();
//            ps.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void insertCasts(Connection conn) {
//        int castLimit = (CASTS_TEST_LIMIT > 0) ? Math.min(CASTS_TEST_LIMIT, casts.size()) : casts.size();
//        System.out.println("Inserting " + castLimit /*casts.size()*/ + " casts...");
//        try {
//            conn.setAutoCommit(false);
//            PreparedStatement ps = conn.prepareStatement(
//                    "INSERT IGNORE INTO stars_in_movies(starId, movieId) " +
//                            "SELECT id, ? FROM stars WHERE name = ? LIMIT 1");
//
//            for (int i = 0; i < castLimit; ++i) {
//                Cast c = casts.get(i);
//                try {
//                    ps.setString(1, c.movieId);
//                    ps.setString(2, c.actorName);
//                    ps.addBatch();
//
//                    if (i % 500 == 0) ps.executeBatch();
//                } catch (Exception e) {
//                    System.out.println("Skipping bad cast: " + c);
//                }
//            }
////            int count = 0;
////            for (Cast c : casts) {
////                try {
////                    ps.setString(1, c.movieId);
////                    ps.setString(2, c.actorName);
////                    ps.addBatch();
////
////                    if (++count % 500 == 0) ps.executeBatch();
////                } catch (Exception e) {
////                    System.out.println("Skipping bad cast: " + c);
////                }
////            }
//            ps.executeBatch();
//            conn.commit();
//            ps.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    // Print data for verification
//    private void printData() {
//        System.out.println("\nMovies Parsed: " + movies.size());
//        for (Movie m : movies) System.out.println(m);
//
//        System.out.println("\nActors Parsed: " + actors.size());
//        for (Actor a : actors) System.out.println(a);
//
//        System.out.println("\nCasts Parsed: " + casts.size());
//        for (Cast c : casts) System.out.println(c);
//    }
//
//    // Movie class
//    static class Movie {
//        String id, title, year, director;
//        Set<String> genres = new HashSet<>();
//
//        Movie(String id) { this.id = id; }
//        boolean isValid() {
//            return id != null && !id.isEmpty() && title != null && !title.isEmpty()
//                    && year != null && year.matches("\\d{4}")
//                    && director != null && !director.isEmpty();
//        }
//        public String toString() {
//            return String.format("[Movie] %s (%s), Dir: %s, ID: %s", title, year, director, id);
//        }
//    }
//
//    // Actor class
//    static class Actor {
//        String name, dob;
//
//        public String toString() {
//            return String.format("[Actor] %s (DOB: %s)", name, dob);
//        }
//    }
//
//    // Cast class
//    static class Cast {
//        String movieId, actorName, role;
//
//        public String toString() {
//            return String.format("[Cast] MovieID: %s, Actor: %s, Role: %s", movieId, actorName, role);
//        }
//    }
//
//    // Main
//    public static void main(String[] args) {
//        new Parser().runExample();
//    }
//}

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.sql.*;
import java.util.*;

public class Parser extends DefaultHandler {

    private List<Movie> movies = new ArrayList<>();
    private List<Actor> actors = new ArrayList<>();
    private List<Cast> casts = new ArrayList<>();

    private String currentFile = "";
    private String currentElement = "";
    private StringBuilder tempVal = new StringBuilder();

    private Movie tempMovie;
    private Actor tempActor;
    private Cast tempCast;

    private static Connection conn;

    public static void main(String[] args) {
        try {
            Parser parser = new Parser();
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb", "mytestuser", "My6$Password");
            parser.parseAll();
            parser.insertAll();
            conn.close();
            System.out.println("Parsing & insertion complete.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void parseAll() throws Exception {
        parseFile("src/mains243.xml");
        parseFile("src/actors63.xml");
        parseFile("src/casts124.xml");
    }

    private void parseFile(String fileName) {
        currentFile = fileName;
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(new File(fileName), this);
            System.out.println("Finished parsing: " + fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        currentElement = qName;
        tempVal.setLength(0);
        if (currentFile.contains("mains") && qName.equals("film")) {
            tempMovie = new Movie("tt" + attributes.getValue("fid"));
        } else if (currentFile.contains("actors") && qName.equals("actor")) {
            tempActor = new Actor();
        } else if (currentFile.contains("casts") && qName.equals("m")) {
            tempCast = new Cast();
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        tempVal.append(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        String data = tempVal.toString().trim();

        if (currentFile.contains("mains")) {
            switch (qName) {
                case "film":
                    if (tempMovie.isValid()) {
                        movies.add(tempMovie);
                    } else {
                        System.out.println("[Inconsistency] Skipping movie: " + tempMovie);
                    }
                    break;
                case "t": tempMovie.title = data; break;
                case "year": tempMovie.year = data; break;
                case "dirn": tempMovie.director = data; break;
                case "cat": tempMovie.genres.add(data); break;
            }
        } else if (currentFile.contains("actors")) {
            switch (qName) {
                case "actor":
                    if (tempActor.isValid()) {
                        actors.add(tempActor);
                    } else {
                        System.out.println("[Inconsistency] Skipping actor: " + tempActor);
                    }
                    break;
                case "stagename": tempActor.name = data; break;
                case "dob": tempActor.dob = data; break;
            }
        } else if (currentFile.contains("casts")) {
            switch (qName) {
                case "f": tempCast.movieId = "tt" + data; break;
                case "a": tempCast.actorName = data; break;
                case "m":
                    if (tempCast.isValid()) {
                        casts.add(tempCast);
                    } else {
                        System.out.println("[Inconsistency] Skipping cast: " + tempCast);
                    }
                    break;
            }
        }
    }

    public void insertAll() throws SQLException {
        insertMovies();
        insertActors();
        insertCasts();
    }

    private void insertMovies() throws SQLException {
        System.out.println("Inserting " + movies.size() + " movies...");
        PreparedStatement psMovie = conn.prepareStatement("INSERT IGNORE INTO movies(id, title, year, director) VALUES (?, ?, ?, ?)");
        PreparedStatement psGenre = conn.prepareStatement("INSERT IGNORE INTO genres(name) VALUES (?)");
        PreparedStatement psLink = conn.prepareStatement("INSERT IGNORE INTO genres_in_movies(genreId, movieId) SELECT id, ? FROM genres WHERE name = ?");

        conn.setAutoCommit(false);
        for (Movie m : movies) {
            try {
                psMovie.setString(1, m.id);
                psMovie.setString(2, m.title);
                psMovie.setInt(3, Integer.parseInt(m.year));
                psMovie.setString(4, m.director);
                psMovie.addBatch();

                for (String genre : m.genres) {
                    psGenre.setString(1, genre);
                    psGenre.addBatch();

                    psLink.setString(1, m.id);
                    psLink.setString(2, genre);
                    psLink.addBatch();
                }
            } catch (Exception e) {
                System.out.println("[Skip movie] " + m);
            }
        }
        psMovie.executeBatch();
        psGenre.executeBatch();
        psLink.executeBatch();
        conn.commit();

        psMovie.close();
        psGenre.close();
        psLink.close();
    }

    private void insertActors() throws SQLException {
        System.out.println("Inserting " + actors.size() + " actors...");
        PreparedStatement ps = conn.prepareStatement("INSERT IGNORE INTO stars(id, name, birthYear) VALUES (?, ?, ?)");
        int count = 1;
        conn.setAutoCommit(false);
        for (Actor a : actors) {
            try {
                ps.setString(1, String.format("nm%07d", count++));
                ps.setString(2, a.name);
                if (a.dob.matches("\\d{4}")) {
                    ps.setInt(3, Integer.parseInt(a.dob));
                } else {
                    ps.setNull(3, Types.INTEGER);
                }
                ps.addBatch();
            } catch (Exception e) {
                System.out.println("[Skip actor] " + a);
            }
        }
        ps.executeBatch();
        conn.commit();
        ps.close();
    }

    private void insertCasts() throws SQLException {
        System.out.println("Inserting " + casts.size() + " casts...");
        PreparedStatement ps = conn.prepareStatement(
                "INSERT IGNORE INTO stars_in_movies(starId, movieId) SELECT id, ? FROM stars WHERE name = ? LIMIT 1");
        conn.setAutoCommit(false);
        for (Cast c : casts) {
            try {
                ps.setString(1, c.movieId);
                ps.setString(2, c.actorName);
                ps.addBatch();
            } catch (Exception e) {
                System.out.println("[Skip cast] " + c);
            }
        }
        ps.executeBatch();
        conn.commit();
        ps.close();
    }

    // Classes
    static class Movie {
        String id, title, year, director;
        Set<String> genres = new HashSet<>();

        Movie(String id) { this.id = id; }
        boolean isValid() {
            return id != null && !id.isEmpty() && title != null && !title.isEmpty()
                    && year != null && year.matches("\\d{4}")
                    && director != null && !director.isEmpty();
        }
        public String toString() { return String.format("[Movie] %s (%s), Dir: %s, ID: %s", title, year, director, id); }
    }

    static class Actor {
        String name = "", dob = "";
        boolean isValid() { return name != null && !name.isEmpty(); }
        public String toString() { return String.format("[Actor] %s (DOB: %s)", name, dob); }
    }

    static class Cast {
        String movieId = "", actorName = "";
        boolean isValid() { return movieId != null && actorName != null && !actorName.isEmpty(); }
        public String toString() { return String.format("[Cast] MovieID: %s, Actor: %s", movieId, actorName); }
    }
}

