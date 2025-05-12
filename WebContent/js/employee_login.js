let employee_login_form = $("#employee-login-form");

// function handleEmployeeLoginResult(resultDataString) {
//     let resultDataJson = JSON.parse(resultDataString);
//
//     console.log("handle employee login response");
//     console.log(resultDataJson);
//     console.log(resultDataJson["status"]);
//
//     if (resultDataJson["status"] === "success") {
//         window.location.replace("_dashboard_main.html");
//     } else {
//         console.log("show error message");
//         console.log(resultDataJson["message"]);
//         $("#login_error_message").text(resultDataJson["message"]);
//     }
// }

function handleEmployeeLoginResult(resultDataJson) {
    console.log("handle employee login response");
    console.log(resultDataJson);
    console.log(resultDataJson["status"]);

    if (resultDataJson["status"] === "success") {
        window.location.replace("_dashboard");
    } else {
        console.log("show error message");
        console.log(resultDataJson["message"]);
        $("#login_error_message").text(resultDataJson["message"]);
    }
}

function submitEmployeeLoginForm(event) {
    event.preventDefault();

    $.ajax("api/employee-login", {
        method: "POST",
        data: employee_login_form.serialize(),
        success: handleEmployeeLoginResult
    });
}

employee_login_form.submit(submitEmployeeLoginForm);
