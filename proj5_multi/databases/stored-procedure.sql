DELIMITER $$

DROP PROCEDURE IF EXISTS add_movie$$

CREATE PROCEDURE add_movie(
    IN movieTitle VARCHAR(100),
    IN movieYear INT,
    IN movieDirector VARCHAR(100),
    IN starName VARCHAR(100),
    IN starBirthYear INT,
    IN genreName VARCHAR(32),
    OUT resultMessage VARCHAR(255)
)
block_label: BEGIN
    DECLARE movieId VARCHAR(10);
    DECLARE starId VARCHAR(10);
    DECLARE genreId INT;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        SET resultMessage = 'An error occurred. No changes were made.';
    END;

    -- Check for duplicate movie
    IF EXISTS (
        SELECT * FROM movies
        WHERE title = movieTitle AND year = movieYear AND director = movieDirector
    ) THEN
        SET resultMessage = 'Movie already exists. No changes made.';
        LEAVE block_label;
    END IF;

    -- Generate new movie ID
    SELECT MAX(id) INTO movieId FROM movies;
    IF movieId IS NULL THEN
        SET movieId = 'tt0000001';
    ELSE
        SET movieId = CONCAT('tt', LPAD(CAST(SUBSTRING(movieId, 3) AS UNSIGNED) + 1, 7, '0'));
    END IF;

    -- Insert movie
    INSERT INTO movies(id, title, year, director)
    VALUES (movieId, movieTitle, movieYear, movieDirector);
    INSERT INTO ratings(movieId, rating, numVotes)
    VALUES (movieId, 0.0, 0);

    -- Handle star
    SELECT id INTO starId FROM stars WHERE name = starName LIMIT 1;
    IF starId IS NULL THEN
        SELECT MAX(id) INTO starId FROM stars;
        IF starId IS NULL THEN
            SET starId = 'nm0000001';
        ELSE
            SET starId = CONCAT('nm', LPAD(CAST(SUBSTRING(starId, 3) AS UNSIGNED) + 1, 7, '0'));
        END IF;

        IF starBirthYear IS NULL THEN
            INSERT INTO stars(id, name) VALUES (starId, starName);
        ELSE
            INSERT INTO stars(id, name, birthYear) VALUES (starId, starName, starBirthYear);
        END IF;
    END IF;

    -- Handle genre
    SELECT id INTO genreId FROM genres WHERE name = genreName LIMIT 1;
    IF genreId IS NULL THEN
        INSERT INTO genres(name) VALUES (genreName);
        SET genreId = LAST_INSERT_ID();
    END IF;

    -- Insert relationships
    INSERT IGNORE INTO stars_in_movies VALUES (starId, movieId);
    INSERT IGNORE INTO genres_in_movies VALUES (genreId, movieId);

    -- Final output
    SET resultMessage = CONCAT(
        'Movie added successfully with ID: ', movieId,
        ', Genre ID: ', genreId,
        ', Star ID: ', starId
    );
END;
$$

DELIMITER ;
