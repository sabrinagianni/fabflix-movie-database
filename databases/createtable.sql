CREATE DATABASE moviedb;

USE moviedb;

CREATE TABLE movies (
    id VARCHAR(10) PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    year INTEGER NOT NULL,
    director VARCHAR(100) NOT NULL
);

-- Create stars table
CREATE TABLE stars (
   id VARCHAR(10) PRIMARY KEY,
   name VARCHAR(100) NOT NULL,
   birthYear INTEGER
);

-- Create stars_in_movies table (many-to-many relation between stars and movies)
CREATE TABLE stars_in_movies (
     starId VARCHAR(10) NOT NULL,
     movieId VARCHAR(10) NOT NULL,
     PRIMARY KEY (starId, movieId),
     FOREIGN KEY (starId) REFERENCES stars(id),
     FOREIGN KEY (movieId) REFERENCES movies(id)
);

-- Create genres table
CREATE TABLE genres (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(32) NOT NULL
);

-- Create genres_in_movies table (many-to-many relation between genres and movies)
CREATE TABLE genres_in_movies (
      genreId INTEGER NOT NULL,
      movieId VARCHAR(10) NOT NULL,
      PRIMARY KEY (genreId, movieId),
      FOREIGN KEY (genreId) REFERENCES genres(id),
      FOREIGN KEY (movieId) REFERENCES movies(id)
);

-- Create creditcards table
CREATE TABLE creditcards (
     id VARCHAR(20) PRIMARY KEY,
     firstName VARCHAR(50) NOT NULL,
     lastName VARCHAR(50) NOT NULL,
     expiration DATE NOT NULL
);

-- Create customers table
CREATE TABLE customers (
       id INTEGER AUTO_INCREMENT PRIMARY KEY,
       firstName VARCHAR(50) NOT NULL,
       lastName VARCHAR(50) NOT NULL,
       ccId VARCHAR(20) NOT NULL,
       address VARCHAR(200) NOT NULL,
       email VARCHAR(50) NOT NULL,
       password VARCHAR(20) NOT NULL,
       FOREIGN KEY (ccId) REFERENCES creditcards(id)
);

-- Create sales table
CREATE TABLE sales (
       id INTEGER AUTO_INCREMENT PRIMARY KEY,
       customerId INTEGER NOT NULL,
       movieId VARCHAR(10) NOT NULL,
       saleDate DATE NOT NULL,
       FOREIGN KEY (customerId) REFERENCES customers(id),
       FOREIGN KEY (movieId) REFERENCES movies(id)
);

-- Create ratings table
CREATE TABLE ratings (
     movieId VARCHAR(10) NOT NULL,
     rating FLOAT NOT NULL,
     numVotes INTEGER NOT NULL,
     PRIMARY KEY (movieId),
     FOREIGN KEY (movieId) REFERENCES movies(id)
);