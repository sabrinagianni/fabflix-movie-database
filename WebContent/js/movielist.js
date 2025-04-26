/**
 * Handles the data returned by the MovieListServlet API
 * Populates the movie table using the JSON result
 * @param resultData jsonArray
 */
let currentPage = 1;

function getParams() {
    const urlParams = new URLSearchParams(window.location.search);
    return {
        genre: urlParams.get("genre") || "",
        title: urlParams.get("title") || "",
        sort: $("#sort").val(),
        limit: $("#numpage").val(),
        page: currentPage
    };
}

function fetchMovies() {
    const params = getParams();
    const queryString = new URLSearchParams(params).toString();
    $.ajax({
        dataType: "json",
        method: "GET",
        url: `api/movielist?${queryString}`,
        success: handleMovieResult
    });
}

function handleMovieResult(resultData) {
    console.log("handleMovieResult: populating movie table from resultData");

    let movieTableBodyElement = jQuery("#movie_table_body");
    movieTableBodyElement.empty(); // clear table before repopulating

    resultData.forEach(movie => {
        let rowHTML = "<tr>";

        rowHTML += `<td><a href='single_movie.html?id=${movie["id"]}'>${movie["title"]}</a></td>`;
        rowHTML += `<td>${movie["year"]}</td>`;
        rowHTML += `<td>${movie["director"]}</td>`;

        let genresHTML = movie["genres"].slice(0, 3).sort().map(g =>
            `<a href="movielist.html?genre=${encodeURIComponent(g)}">${g}</a>`).join(", ");
        rowHTML += `<td>${genresHTML}</td>`;

        let starsHTML = movie["stars"].slice(0, 3).map(star =>
            `<a href="single-star.html?id=${star["id"]}">${star["name"]}</a>`).join(", ");
        rowHTML += `<td>${starsHTML}</td>`;

        rowHTML += `<td>${movie["rating"]}</td>`;
        rowHTML += "</tr>";

        movieTableBodyElement.append(rowHTML);
    });
    $("#page_number").text(`Page ${currentPage}`);
}

$("#sort, #numpage").on("change", () => {
    currentPage = 1;
    fetchMovies();
});

$("#prev").on("click", () => {
    if (currentPage > 1) {
        currentPage--;
        fetchMovies();
    }
});

$("#next").on("click", () => {
    currentPage++;
    fetchMovies();
});

$(document).ready(() => {
    fetchMovies();
});
