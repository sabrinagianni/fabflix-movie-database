$("#employee-login-form").on("submit", function(event) {
    event.preventDefault();

    $.ajax({
        method: "POST",
        url: "api/employee-login",
        data: $(this).serialize(),
        success: function(response) {
            if (response.status === "success") {
                window.location.href = "_dashboard_main.html";
            } else {
                $("#login_error_message").text("Login failed: " + response.message);
            }
        }
    });
});
