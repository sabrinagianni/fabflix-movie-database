/**
 * This JavaScript follows frontend and backend separation.
 * It populates the Single Star Page with data retrieved from the backend.
 */

/**
 * Retrieve parameter from request URL, matching by parameter name.
 * @param target String
 * @returns {*}
 */
function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    target = target.replace(/[\[\]]/g, "\\$&");

    // Use regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
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

    // Check if resultData is an array
    if (resultData && Array.isArray(resultData) && resultData.length > 0) {
        const star = resultData[0];

        let starNameElement = jQuery("#star_name");
        starNameElement.text(star["star_name"]);  // Name in big bold font
        starNameElement.append(`<span>(${star["star_dob"]})</span>`);

        console.log("Populating movie table from resultData");

        let movieTableBodyElement = jQuery("#movie_table_body");
        const movies = star["movies"];
        for (let i = 0; i < Math.min(10, movies.length); i++) {
            const movie = movies[i];
            let rowHTML = "";
            rowHTML += "<tr>";
            rowHTML += `<td><a href="single_movie.html?id=${movie["movie_id"]}">${movie["movie_title"]}</a></td>`;
            rowHTML += "<td>" + movie["movie_year"] + "</td>";
            rowHTML += "<td>" + movie["movie_director"] + "</td>";
            rowHTML += "</tr>";

            movieTableBodyElement.append(rowHTML);
        }
    } else {
        console.error("Invalid or empty resultData:", resultData);
    }
}

/**
 * Once the .js is loaded, following scripts will be executed.
 */
$(document).ready(() => {
    createBackButton();
    let starId = getParameterByName('id');
    $.ajax({
        dataType: "json",
        method: "GET",
        url: "api/single-star?id=" + starId,
        success: handleResult
    });
});

// // Get star ID from the URL
// let starId = getParameterByName('id');
//
// // Make the HTTP GET request and handle the result
// jQuery.ajax({
//     dataType: "json",  // Setting return data type
//     method: "GET",     // Setting request method
//     url: "api/single-star?id=" + starId, // Request URL, mapped by the backend
//     success: (resultData) => handleResult(resultData) // Callback to handle data
// });
