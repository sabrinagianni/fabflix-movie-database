import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.sql.*;
import java.util.*;

public class Parser extends DefaultHandler {

    // Models
    static class Movie {
        String id, title, director;
        int year;
    }

    static class Genre {
        int id;
        String name;
    }

    static class Star {
        String id, name;
        Integer birthYear;
    }

    static class GenreInMovie {
        String movieId;
        int genreId;
    }

    static class StarInMovie {
        String movieId, starId;
    }

    // Data structures
    List<Movie> movieList = new ArrayList<>();
    Map<String, Integer> genreMap = new HashMap<>();
    List<GenreInMovie> genreInMovieList = new ArrayList<>();
    List<Star> starList = new ArrayList<>();
    List<StarInMovie> starInMovieList = new ArrayList<>();
    Set<String> inconsistencyLog = new HashSet<>();

    // Temp variables for parsing
    private String tempVal;
    private Movie tempMovie;
    private Genre tempGenre;
    private Star tempStar;
    private GenreInMovie tempGenreInMovie;
    private StarInMovie tempStarInMovie;

    private String currentDirectorName = null;

    private String currentFile = "";

    //Counters for report
    private int insertedMovies = 0, insertedGenres = 0, insertedStars = 0, insertedSIM = 0, insertedGIM = 0;
    private int skippedMovies = 0, skippedStars = 0;

    public static void main(String[] args) throws Exception {
        Parser handler = new Parser();
        handler.parseFile("stanford-movies/mains243.xml");
        handler.parseFile("stanford-movies/actors63.xml");
        handler.parseFile("stanford-movies/casts124.xml");
        handler.insertIntoDatabase();
    }

    public void parseFile(String fileName) {
        try {
            currentFile = fileName;
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            parser.parse(new File(fileName), this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void characters(char[] ch, int start, int length) {
        tempVal = new String(ch, start, length).trim();
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        tempVal = "";

        if (currentFile.contains("mains") && qName.equals("film")) {
            tempMovie = new Movie();
        } else if (currentFile.contains("actors") && qName.equals("actor")) {
            tempStar = new Star();
        } else if (currentFile.contains("casts") && qName.equals("m")) {
            tempStarInMovie = new StarInMovie();
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (currentFile.contains("mains")) {
            switch (qName) {
                case "dirname":
                    currentDirectorName = tempVal;
                    break;
                case "fid":
                    tempMovie.id = tempVal;
                    break;
                case "t":
                    tempMovie.title = tempVal;
                    break;
                case "year":
                    try {
                        tempMovie.year = Integer.parseInt(tempVal);
                    } catch (Exception e) {
                        tempMovie.year = 0;
                        inconsistencyLog.add("Invalid year: " + tempVal);
                    }
                    break;
                case "cat":
                    if (!genreMap.containsKey(tempVal))
                        genreMap.put(tempVal, genreMap.size() + 1);
                    genreInMovieList.add(new GenreInMovie() {{
                        movieId = tempMovie.id;
                        genreId = genreMap.get(tempVal);
                    }});
                    break;
                case "film":
                    tempMovie.director = currentDirectorName;
                    if (tempMovie.title != null && tempMovie.year != 0 && tempMovie.director != null)
                        movieList.add(tempMovie);
                    else
                        inconsistencyLog.add("Incomplete movie object: " + (tempMovie != null ? tempMovie.id : "null"));
                    break;
            }
        } else if (currentFile.contains("actors")) {
            switch (qName) {
                case "stagename": tempStar.name = tempVal; break;
                case "dob":
                    try { tempStar.birthYear = Integer.parseInt(tempVal); }
                    catch (Exception e) { tempStar.birthYear = null; }
                    break;
                case "actor":
                    if (tempStar.name != null && !tempStar.name.isEmpty()) {
                        tempStar.id = "nm" + (starList.size() + 1);
                        starList.add(tempStar);
                    }
                    break;
            }
        } else if (currentFile.contains("casts")) {
            switch (qName) {
                case "f": tempStarInMovie.movieId = tempVal; break;
                case "a":
                    for (Star s : starList) {
                        if (s.name.equals(tempVal)) {
                            tempStarInMovie.starId = s.id;
                            break;
                        }
                    }
                    break;
                case "m":
                    if (tempStarInMovie.movieId != null && tempStarInMovie.starId != null)
                        starInMovieList.add(tempStarInMovie);
                    else
                        inconsistencyLog.add("Missing star/movie in cast: " + tempStarInMovie);
                    break;
            }
        }
    }


    public void insertIntoDatabase() {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb?rewriteBatchedStatements=true", "mytestuser", "My6$Password");
            conn.setAutoCommit(false);

            // Example insert for movies
            PreparedStatement movieStmt = conn.prepareStatement("INSERT IGNORE INTO movies (id, title, year, director) VALUES (?, ?, ?, ?)");

            for (Movie m : movieList) {
                movieStmt.setString(1, m.id);
                movieStmt.setString(2, m.title);
                movieStmt.setInt(3, m.year);
                movieStmt.setString(4, m.director);
                movieStmt.addBatch();
            }

            movieStmt.executeBatch();
            movieStmt.close();
            insertedMovies = movieList.size();

            PreparedStatement genreStmt = conn.prepareStatement("INSERT IGNORE INTO genres (id, name) VALUES (?, ?)");
            for (Map.Entry<String, Integer> e : genreMap.entrySet()) {
                genreStmt.setInt(1, e.getValue());
                genreStmt.setString(2, e.getKey());
                genreStmt.addBatch();
            }
            genreStmt.executeBatch();
            genreStmt.close();
            insertedGenres = genreMap.size();

            PreparedStatement gimStmt = conn.prepareStatement("INSERT IGNORE INTO genres_in_movies (genreId, movieId) VALUES (?, ?)");
            for (GenreInMovie gim : genreInMovieList) {
                gimStmt.setInt(1, gim.genreId);
                gimStmt.setString(2, gim.movieId);
                gimStmt.addBatch();
            }
            gimStmt.executeBatch();
            gimStmt.close();
            insertedGIM = genreInMovieList.size();

            PreparedStatement starStmt = conn.prepareStatement("INSERT IGNORE INTO stars (id, name, birthYear) VALUES (?, ?, ?)");
            for (Star s : starList) {
                starStmt.setString(1, s.id);
                starStmt.setString(2, s.name);
                if (s.birthYear != null) starStmt.setInt(3, s.birthYear);
                else starStmt.setNull(3, Types.INTEGER);
                starStmt.addBatch();
            }
            starStmt.executeBatch();
            starStmt.close();
            insertedStars = starList.size();

            PreparedStatement simStmt = conn.prepareStatement("INSERT IGNORE INTO stars_in_movies (starId, movieId) VALUES (?, ?)");
            for (StarInMovie sim : starInMovieList) {
                simStmt.setString(1, sim.starId);
                simStmt.setString(2, sim.movieId);
                simStmt.addBatch();
            }
            simStmt.executeBatch();
            simStmt.close();
            insertedSIM = starInMovieList.size();

            conn.commit();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try (PrintWriter writer = new PrintWriter("logs/inconsistencies.txt")) {
            for (String s : inconsistencyLog) {
                writer.println(s);
            }
            writer.println("\n=== SUMMARY ===");
            writer.println("Inserted Movies: " + insertedMovies);
            writer.println("Inserted Genres: " + insertedGenres);
            writer.println("Inserted Stars: " + insertedStars);
            writer.println("Inserted Genres_in_Movies: " + insertedGIM);
            writer.println("Inserted Stars_in_Movies: " + insertedSIM);
            writer.println("Skipped Movies (incomplete): " + skippedMovies);
            writer.println("Skipped Stars (incomplete): " + skippedStars);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
