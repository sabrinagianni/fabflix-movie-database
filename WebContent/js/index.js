/**
 * Handles the data returned by the API, reads the jsonObject, and populates the data into HTML elements
 * @param resultData jsonObject
 */
function handleTopMoviesResult(resultData) {
    console.log("handleTopMoviesResult: populating top movies table from resultData");

    // Populate the top movies table
    // Find the empty table body by id "top_movies_table_body"
    let topMoviesTableBodyElement = jQuery("#top_movies_table_body");

    // Iterate through resultData, up to 20 entries (since it's the top 20)
    for (let i = 0; i < Math.min(20, resultData.length); i++) {

        // Concatenate the HTML tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<td>" + resultData[i]["title"] + "</td>";  // Movie title
        rowHTML += "<td>" + resultData[i]["year"] + "</td>";    // Movie year
        rowHTML += "<td>" + resultData[i]["rating"] + "</td>";  // Movie rating
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        topMoviesTableBodyElement.append(rowHTML);
    }
}

/**
 * Once this .js is loaded, the following scripts will be executed by the browser
 */

// Makes the HTTP GET request and registers the success callback function handleTopMoviesResult
jQuery.ajax({
    dataType: "json", // Setting the return data type
    method: "GET", // Setting the request method
    url: "api/movies/top", // Setting request URL, which should be mapped to a backend servlet (e.g., MoviesServlet)
    success: (resultData) => handleTopMoviesResult(resultData) // Setting callback function to handle data returned by the backend
});
