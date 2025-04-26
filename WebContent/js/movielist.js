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

function saveSessionParams() {
    const params = getParams();
    return $.ajax({
        type: "POST",
        url: "api/save-session-params",
        data: params
    });
}

function saveCurrentListParams() {
    const params = getParams();
    $.ajax({
        method: "POST",
        url: "api/save-session-params",
        data: params
    });
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

function addToCart(movieId, title) {
    $.ajax({
        method: "POST",
        url: "api/cart",
        data: {
            movieId: movieId,
            title: title,
            price: 19.99
        },
        success: function(response) {
            showMessage("Added to cart!");
        },
        error: function() {
            showMessage("Failed to add to cart.");
        }
    });
}

function showMessage(message, isError = false) {
    const messageDiv = document.getElementById("cart-message");
    messageDiv.textContent = message;
    messageDiv.style.color = isError ? "red" : "green";

    setTimeout(() => {
        messageDiv.textContent = "";
    }, 2000);
}


function handleMovieResult(resultData) {
    console.log("handleMovieResult: populating movie table from resultData");

    let movieTableBodyElement = jQuery("#movie_table_body");
    movieTableBodyElement.empty(); // clear table before repopulating

    resultData.forEach(movie => {
        let rowHTML = "<tr>";

        // rowHTML += `<td><a href="single_movie.html?id=${movie["id"]}" onclick="saveSessionParams()">${movie["title"]}</a></td>`;
        rowHTML += `<td><a href="#" class="movie-link" data-id="${movie["id"]}">${movie["title"]}</a></td>`;
        rowHTML += `<td>${movie["year"]}</td>`;
        rowHTML += `<td>${movie["director"]}</td>`;

        let genresHTML = movie["genres"].slice(0, 3).sort().map(g =>
            `<a href="movielist.html?genre=${encodeURIComponent(g)}">${g}</a>`).join(", ");
        rowHTML += `<td>${genresHTML}</td>`;

        let starsHTML = movie["stars"].slice(0, 3).map(star =>
            // `<a href="single-star.html?id=${star["id"]}">${star["name"]}</a>`).join(", ");
            `<a href="#" class="star-link" data-id="${star["id"]}">${star["name"]}</a>`).join(", ");
        rowHTML += `<td>${starsHTML}</td>`;

        rowHTML += `<td>${movie["rating"]}</td>`;
        rowHTML += `<td><button onclick="addToCart('${movie["id"]}', '${movie["title"]}')">Add to Shopping Cart</button></td>`;
        rowHTML += "</tr>";

        movieTableBodyElement.append(rowHTML);
    });

    $(".movie-link").on("click", function (e) {
        e.preventDefault();
        const movieId = $(this).data("id");
        saveSessionParams().then(() => {
            window.location.href = `single_movie.html?id=${movieId}`;
        });
    });

    $(".star-link").on("click", function (e) {
        e.preventDefault();
        const starId = $(this).data("id");
        saveSessionParams().then(() => {
            window.location.href = `single-star.html?id=${starId}`;
        });
    });

    // $(".movie-link").click(function (e) {
    //     e.preventDefault();
    //     const movieId = $(this).data("id");
    //     saveCurrentListParams();
    //     window.location.href = `single_movie.html?id=${movieId}`;
    // });
    //
    // $(".star-link").click(function (e) {
    //     e.preventDefault();
    //     const starId = $(this).data("id");
    //     saveCurrentListParams();
    //     window.location.href = `single-star.html?id=${starId}`;
    // });

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
