document.getElementById("search-and-browse").addEventListener("submit", function(event) {
    event.preventDefault();
    const params = new URLSearchParams(new FormData(this)).toString();
    window.location.href = `movielist.html?${params}`;
});

function browseGenres() {
    fetch("api/browse-genres")
        .then(res => res.json())
        .then(data => {
            const container = document.getElementById("browse-by-genre");
            container.innerHTML = "<h3>Browse by Genre:</h3>";

            data.forEach(genre => {
                const a = document.createElement("a");
                a.href = `movielist.html?genre=${encodeURIComponent(genre.name)}`;
                a.textContent = genre.name;
                container.appendChild(a);
                container.append(" ");
            });
        });
}

    function browseTitle() {
        const letters = ["*", ..."0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"];
        const container = document.getElementById("browse-by-title");
        container.innerHTML = "<h3>Browse by Title:</h3>";

        letters.forEach(word => {
            const a = document.createElement("a");
            a.href = `movielist.html?title=${word}`;
            a.textContent = word;
            container.appendChild(a);
            container.append(" ");
        });
    }

    function logout() {
        fetch("api/logout").then(() => window.location.href = "login.html");
    }

browseGenres();
browseTitle();