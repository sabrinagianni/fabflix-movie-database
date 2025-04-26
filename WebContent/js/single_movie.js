/**
 * Retrieve parameter from request URL, matching by parameter name.
 * @param target String
 * @returns {*}
 */
function getParameterByName(target) {
    let url = window.location.href;
    target = target.replace(/[\[\]]/g, "\\$&");

    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

function createBackButton() {
    $.get("api/get-session-params", function(data) {
        const url = new URLSearchParams();
        if (data.genre) url.append("genre", data.genre);
        if (data.title) url.append("title", data.title);
        if (data.sort) url.append("sort", data.sort);
        if (data.limit) url.append("limit", data.limit);
        if (data.page) url.append("page", data.page);

        const linkHTML = `<a href="movielist.html?${url.toString()}" style="font-weight: bold; font-size: 18px; color: #6684b5">← Back to Movie List</a>`;
        document.getElementById("back-link").innerHTML = linkHTML;
    });
}

/**
 * Handles the data returned by the API and populates it into HTML elements.
 * @param resultData jsonObject
 */
function handleResult(resultData) {
    console.log(resultData);  // Log the result to see its structure

    if (resultData && Array.isArray(resultData) && resultData.length > 0) {
        const movie = resultData[0];

        // Set title + year
        let titleDiv = document.getElementById("movie-info");
        let titleEl = document.createElement("h1");
        titleEl.className = "movie-title";
        titleEl.textContent = movie.title;

        let yearEl = document.createElement("span");
        yearEl.className = "movie-year";
        yearEl.textContent = `(${movie.year})`;


        // Append the year span *inside* the h1 element
        titleEl.appendChild(yearEl);
        // Append the h1 to the container div
        titleDiv.appendChild(titleEl);

        // Build table row
        let row = document.createElement("tr");

        // Director
        let directorCell = document.createElement("td");
        directorCell.textContent = movie.director;
        row.appendChild(directorCell);

        // Genres
        let genresCell = document.createElement("td");
        let genresSorted = [...movie.genres].sort();
        let genresHTML = genresSorted.map(g =>
            `<a href="movielist.html?genre=${encodeURIComponent(g)}">${g}</a>`
        ).join(", ");
        genresCell.innerHTML = genresHTML;
        row.appendChild(genresCell);

        // Stars (hyperlinked)
        let starsCell = document.createElement("td");
        let sortedStars = movie.stars.sort((a, b) => {
            if (b.count === a.count) return a.name.localeCompare(b.name);
            return b.count - a.count;
        });
        sortedStars.forEach((star, index) => {
            const link = document.createElement("a");
            link.href = `single-star.html?id=${star.id}`;
            link.textContent = star.name;
            starsCell.appendChild(link);
            if (index < sortedStars.length - 1) {
                starsCell.appendChild(document.createTextNode(", "));
            }
        });
        row.appendChild(starsCell);

        // Rating
        let ratingCell = document.createElement("td");
        ratingCell.textContent = movie.rating || "N/A";
        row.appendChild(ratingCell);

        document.getElementById("movie-body").appendChild(row);
    } else {
        console.error("Invalid or empty resultData:", resultData);
    }
}

/**
 * Once the .js is loaded, following scripts will be executed.
 */
$(document).ready(() => {
    createBackButton();
    let movieId = getParameterByName('id');
    $.ajax({
        dataType: "json",
        method: "GET",
        url: "api/singlemovie?id=" + movieId,
        success: handleResult
    });
});

// // Get movie ID from the URL
// let movieId = getParameterByName('id');
//
// // Make the HTTP GET request and handle the result
// jQuery.ajax({
//     dataType: "json",  // Setting return data type
//     method: "GET",     // Setting request method
//     url: "api/singlemovie?id=" + movieId, // Request URL, mapped by the backend
//     success: (resultData) => handleResult(resultData) // Callback to handle data
// });
