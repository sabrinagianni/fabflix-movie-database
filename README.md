# 2025-spring-cs-122b

Sabrina Pukarta : Search and Browse, Shopping Cart

Julia Tjia : Login, Extended Movie List/Single Pages/Jumping Functionality

We used LIKE to help search for movies by title, director, or star name.
m.title LIKE ? lets us find movies where the title contains the search keyword anywhere inside it.
m.director LIKE ? also helps support substring search for a director's name.
s.name LIKE ? helps us find the star with the given search input.
We also used % to help with the substring matching.
So we used LIKE to enable the pattern matching and % for the substring matching itself.
For *, we match it REGEXP non-alphanumeric characters so the search will return everything else but alphanumeric.

if (title != null && !title.isEmpty()) {
if (title.equals("*")) {
filters.add("m.title REGEXP '^[^a-zA-Z0-9]'");
} else {
filters.add("m.title LIKE ?");
params.add("%" + title + "%");
}
}

if (director != null && !director.isEmpty()) {
filters.add("m.director LIKE ?");
params.add("%" + director + "%");
}

if (star != null && !star.isEmpty()) {
filters.add("s.name LIKE ?");
params.add("%" + star + "%");
}

DEMO VIDEO LINK: https://youtu.be/9HoAo8cbJSs

AWS INSTANCE IP: 3.138.118.136