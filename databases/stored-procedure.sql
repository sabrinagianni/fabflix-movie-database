DELIMITER $$

CREATE PROCEDURE add_movie(
    IN movieTitle VARCHAR(100),
    IN movieYear INT,
    IN movieDirector VARCHAR(100),
    IN starName VARCHAR(100),
    IN starBirthYear INT,
    IN genreName VARCHAR(32)
)
BEGIN
    DECLARE movieId VARCHAR(10);
    DECLARE starId VARCHAR(10);
    DECLARE genreId INT;

    -- Generate new movie ID
SELECT CONCAT('tt', LPAD(COUNT(*) + 1, 7, '0')) INTO movieId FROM movies;

-- Insert movie if it doesn’t exist
IF NOT EXISTS (SELECT * FROM movies WHERE title = movieTitle AND year = movieYear AND director = movieDirector) THEN
        INSERT INTO movies(id, title, year, director) VALUES (movieId, movieTitle, movieYear, movieDirector);
END IF;

    -- Check for existing star or insert new
SELECT id INTO starId FROM stars WHERE name = starName LIMIT 1;
IF starId IS NULL THEN
SELECT CONCAT('nm', LPAD(COUNT(*) + 1, 7, '0')) INTO starId FROM stars;
INSERT INTO stars(id, name, birthYear) VALUES (starId, starName, starBirthYear);
END IF;

    -- Check for existing genre or insert new
SELECT id INTO genreId FROM genres WHERE name = genreName LIMIT 1;
IF genreId IS NULL THEN
        INSERT INTO genres(name) VALUES (genreName);
        SET genreId = LAST_INSERT_ID();
END IF;

    -- Insert into relationships
    INSERT IGNORE INTO stars_in_movies(starId, movieId) VALUES (starId, movieId);
    INSERT IGNORE INTO genres_in_movies(genreId, movieId) VALUES (genreId, movieId);
END$$

DELIMITER ;
