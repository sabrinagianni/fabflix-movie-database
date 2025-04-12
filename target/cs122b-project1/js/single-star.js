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
            rowHTML += `<th><a href="single_movie.html?id=${movie["movie_id"]}">${movie["movie_title"]}</a></th>`;
            rowHTML += "<th>" + movie["movie_year"] + "</th>";
            rowHTML += "<th>" + movie["movie_director"] + "</th>";
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

// Get star ID from the URL
let starId = getParameterByName('id');

// Make the HTTP GET request and handle the result
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",     // Setting request method
    url: "api/single-star?id=" + starId, // Request URL, mapped by the backend
    success: (resultData) => handleResult(resultData) // Callback to handle data
});
