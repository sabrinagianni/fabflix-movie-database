// Insert Star
$("#insert-star-form").on("submit", function (e) {
    e.preventDefault();
    $.post("api/insert-star", $(this).serialize(), function (response) {
        const color = response.status === "success" ? "rgba(42, 177, 105, 0.71)" : "#FF6F61";
        $("#star-result").css("color", color).text(response.message);
    });
});

// Show Metadata
$("#load-metadata").on("click", function () {
    $.get("api/metadata", function (response) {
        $("#metadata-display").text(response.metadata);
    });
});

// Add Movie
$("#add-movie-form").on("submit", function (e) {
    e.preventDefault();
    $.post("api/add-movie", $(this).serialize(), function (response) {
        const color = response.status === "success" ? "rgba(42, 177, 105, 0.71)" : "#FF6F61";
        $("#movie-result").css("color", color).text(response.message);
    });
});
