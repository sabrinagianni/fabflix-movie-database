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

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.util.*;

public class Parser extends DefaultHandler {
    // Temporary data holders
    private String tempVal;

    // Data structures to store the parsed data
    private List<Movie> movies = new ArrayList<>();
    private List<Actor> actors = new ArrayList<>();
    private List<Cast> casts = new ArrayList<>();

    private Movie tempMovie;
    private Actor tempActor;
    private Cast tempCast;

    private String currentFile = "";

    public void runExample() {
        parseFile("mains243.xml");
        parseFile("actors63.xml");
        parseFile("casts124.xml");

        // Optionally, print the data to check
        printData();
    }

    private void parseFile(String fileName) {
        currentFile = fileName;
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(new File(fileName), this);
            System.out.println("Parsed file: " + fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // When element starts
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        tempVal = "";
        if (currentFile.contains("mains") && qName.equalsIgnoreCase("film")) {
            tempMovie = new Movie();
            tempMovie.id = attributes.getValue("fid");
        } else if (currentFile.contains("actors") && qName.equalsIgnoreCase("actor")) {
            tempActor = new Actor();
        } else if (currentFile.contains("casts") && qName.equalsIgnoreCase("m")) {
            tempCast = new Cast();
            tempCast.movieId = attributes.getValue("fid");
        }
    }

    // Read text content
    public void characters(char[] ch, int start, int length) {
        tempVal = new String(ch, start, length).trim();
    }

    // When element ends
    public void endElement(String uri, String localName, String qName) {
        if (currentFile.contains("mains")) {
            if (qName.equalsIgnoreCase("film")) {
                movies.add(tempMovie);
            } else if (qName.equalsIgnoreCase("t")) {
                tempMovie.title = tempVal;
            } else if (qName.equalsIgnoreCase("year")) {
                tempMovie.year = tempVal;
            } else if (qName.equalsIgnoreCase("dirn")) {
                tempMovie.director = tempVal;
            }
        } else if (currentFile.contains("actors")) {
            if (qName.equalsIgnoreCase("actor")) {
                actors.add(tempActor);
            } else if (qName.equalsIgnoreCase("stagename")) {
                tempActor.name = tempVal;
            } else if (qName.equalsIgnoreCase("dob")) {
                tempActor.dob = tempVal;
            }
        } else if (currentFile.contains("casts")) {
            if (qName.equalsIgnoreCase("m")) {
                casts.add(tempCast);
            } else if (qName.equalsIgnoreCase("a")) {
                tempCast.actorName = tempVal;
            } else if (qName.equalsIgnoreCase("role")) {
                tempCast.role = tempVal;
            }
        }
    }

    // Print data for verification
    private void printData() {
        System.out.println("\nMovies Parsed: " + movies.size());
        for (Movie m : movies) System.out.println(m);

        System.out.println("\nActors Parsed: " + actors.size());
        for (Actor a : actors) System.out.println(a);

        System.out.println("\nCasts Parsed: " + casts.size());
        for (Cast c : casts) System.out.println(c);
    }

    // Movie class
    static class Movie {
        String id, title, year, director;

        public String toString() {
            return String.format("[Movie] %s (%s), Dir: %s, ID: %s", title, year, director, id);
        }
    }

    // Actor class
    static class Actor {
        String name, dob;

        public String toString() {
            return String.format("[Actor] %s (DOB: %s)", name, dob);
        }
    }

    // Cast class
    static class Cast {
        String movieId, actorName, role;

        public String toString() {
            return String.format("[Cast] MovieID: %s, Actor: %s, Role: %s", movieId, actorName, role);
        }
    }

    // Main
    public static void main(String[] args) {
        new Parser().runExample();
    }
}

