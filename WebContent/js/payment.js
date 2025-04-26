let paymentForm = $("#payment-form");

function handlePaymentResult(resultData) {
    console.log("handle payment response");

    console.log(resultData);

    if (resultData["status"] === "success") {
        window.location.replace("confirmation.html");
    } else {
        $("#payment-error").text(resultData["message"]);
    }
}

function submitPaymentForm(event) {
    event.preventDefault();

    $.ajax("api/payment", {
        method: "POST",
        data: paymentForm.serialize(),
        dataType: "json",
        success: handlePaymentResult
    });
}

paymentForm.submit(submitPaymentForm);
