let suggestionCache = {};  // Cache to store previously fetched results

function handleLookup(query, doneCallback) {
    console.log("autocomplete initiated");

    if (suggestionCache[query]) {
        console.log("using cached results for: " + query);
        doneCallback({ suggestions: suggestionCache[query] });
        return;
    }

    console.log("sending AJAX request to backend Java Servlet");

    $.ajax({
        method: "GET",
        url: "api/autocomplete?query=" + encodeURIComponent(query),
        success: function(data) {
            handleLookupAjaxSuccess(data, query, doneCallback);
        },
        error: function(errorData) {
            console.log("lookup ajax error");
            console.log(errorData);
        }
    });
}

function handleLookupAjaxSuccess(data, query, doneCallback) {
    console.log("lookup ajax successful");

    const tokens = query.trim().toLowerCase().split(/\s+/);

    let suggestions = data.map(entry => {
        let originalTitle = entry.value;
        let highlighted = originalTitle;

        tokens.forEach(token => {
            const regex = new RegExp(`(${token})`, "gi");
            highlighted = highlighted.replace(regex, "<mark>$1</mark>");
        });

        return {
            value: originalTitle,
            data: entry.data,
            highlighted: highlighted
        };
    });

    suggestionCache[query] = suggestions;
    console.log("suggestions used:", suggestions);

    doneCallback({ suggestions: suggestions });
}

function handleSelectSuggestion(suggestion) {
    const movieId = suggestion.data.movieId;
    console.log(`you selected "${suggestion.value}" (ID: ${movieId})`);
    window.location.href = `single_movie.html?id=${movieId}`;
}

$("#autocomplete").autocomplete({
    lookup: function (query, doneCallback) {
        if (query.length >= 3) {
            handleLookup(query, doneCallback);
        }
    },
    onSelect: handleSelectSuggestion,
    deferRequestBy: 300,
    minChars: 3,
    formatResult: function(suggestion, currentValue) {
        return suggestion.highlighted;
    }
});

$("#autocomplete").keypress(function(event) {
    if (event.keyCode === 13) {
        const selectedSuggestion = $(".autocomplete-selected").text();
        const currentQuery = $("#autocomplete").val();
        console.log("enter pressed, query: " + currentQuery);

        if (!selectedSuggestion || selectedSuggestion !== currentQuery) {
            window.location.href = `movielist.html?query=${encodeURIComponent(currentQuery)}`;
        }
    }
});
