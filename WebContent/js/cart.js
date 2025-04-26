function showMyCart() {
    $.ajax({
        method: "GET",
        url: "api/cart",
        success: function(data) {
            const container = $("#cart-container");
            container.empty();

            if (data.length === 0) {
                container.html("<p>Your cart is empty!</p>");
                return;
            }

            let table = $("<table>");
            table.append(`
                <tr>
                    <th>Title</th>
                    <th>Price</th>
                    <th>Quantity</th>
                    <th>Total</th>
                    <th>Actions</th>
                </tr>
            `);

            data.forEach(function(item) {
                let row = $(`
                    <tr>
                        <td>${item.title}</td>
                        <td>$${item.price.toFixed(2)}</td>
                        <td>
                            <input type="number" value="${item.quantity}" min="1" data-movieid="${item.movieId}" class="quantity-input">
                        </td>
                        <td>$${item.total.toFixed(2)}</td>
                        <td>
                            <button class="delete-button" data-movieid="${item.movieId}">Remove</button>
                        </td>
                    </tr>
                `);
                table.append(row);
            });

            container.append(table);

            $(".quantity-input").change(updateQuantity);
            $(".delete-button").click(deleteItem);
        }
    });
}

function updateQuantity() {
    const movieId = $(this).data("movieid");
    const quantity = $(this).val();

    $.ajax({
        method: "PUT",
        url: "api/cart",
        data: {
            movieId: movieId,
            quantity: quantity
        },
        success: function() {
            showMyCart();
        }
    });
}

function deleteItem() {
    const movieId = $(this).data("movieid");

    $.ajax({
        method: "DELETE",
        url: "api/cart",
        data: {
            movieId: movieId
        },
        success: function() {
            showMyCart();
        }
    });
}

function proceedToCheckout() {
    window.location.href = "checkout.html";
}

showMyCart();
