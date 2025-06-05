let login_form = $("#login_form");

function handleLoginResult(resultDataJson) {
    console.log("handle login response");
    console.log(resultDataJson);
    console.log(resultDataJson["status"]);

    if (resultDataJson["status"] === "success") {
        window.location.replace("SearchandBrowse.html");
    } else {
        console.log("show error message");
        console.log(resultDataJson["message"]);
        $("#login_error_message").text(resultDataJson["message"]);
    }
}

function submitLoginForm(event) {
    event.preventDefault();

    $.ajax("api/login", {
        method: "POST",
        data: login_form.serialize(),
        dataType: "json",
        success: handleLoginResult
    });
}

login_form.submit(submitLoginForm);
