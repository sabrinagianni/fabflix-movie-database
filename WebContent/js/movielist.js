/**
 * Handles the data returned by the MovieListServlet API
 * Populates the movie table using the JSON result
 * @param resultData jsonArray
 */
function handleMovieResult(resultData) {
    console.log("handleMovieResult: populating movie table from resultData");

    let movieTableBodyElement = jQuery("#movie_table_body");

    for (let i = 0; i < Math.min(20, resultData.length); i++) {
        let movie = resultData[i];
        let rowHTML = "<tr>";

        rowHTML += "<td><a href='single_movie.html?id=" + movie["id"] + "'>" + movie["title"] + "</a></td>";
        rowHTML += "<td>" + movie["year"] + "</td>";
        rowHTML += "<td>" + movie["director"] + "</td>";

        let genresHTML = "";
        if (movie["genres"] && movie["genres"].length > 0) {
            let shownGenres = movie["genres"].slice(0, 3);
            genresHTML = shownGenres.join(", ");
        }
        rowHTML += "<td>" + genresHTML + "</td>";

        let starsHTML = "";
        if (movie["stars"] && movie["stars"].length > 0) {
            let shownStars = movie["stars"].slice(0, 3);
            starsHTML = shownStars.map(star =>
                "<a href='single-star.html?id=" + star["id"] + "'>" + star["name"] + "</a>"
            ).join(", ");
        }
        rowHTML += "<td>" + starsHTML + "</td>";

        rowHTML += "<td>" + movie["rating"] + "</td>";

        rowHTML += "</tr>";
        movieTableBodyElement.append(rowHTML);
    }
}

const urlparam = window.location.search;
const fullURL = "api/movielist" + urlparam
jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: fullURL,
    success: handleMovieResult
});
