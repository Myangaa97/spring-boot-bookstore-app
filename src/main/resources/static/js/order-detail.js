"use strict";

const page = document.querySelector("#order-page");

const cancelButton = document.querySelector("#cancel-order-button");
const csrfToken = document.querySelector('meta[name="_csrf"]').content;
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;
const payButton = document.querySelector("#pay-button");

if (cancelButton) {
    cancelButton.addEventListener('click', cancelOrder); //callback
}

if (payButton) {
    payButton.addEventListener('click', startStripePayment); //callback
}

async function startStripePayment() {
    const orderId = page.dataset.orderId;
    payButton.disabled = true;
    console.log(csrfHeader);
    console.log(csrfToken);

    try {
        const response = await fetch(`/api/customer/orders/${orderId}/payment`, {
            method: "POST",
            headers: {
                [csrfHeader]: csrfToken
            }
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || "Payment could not be started");
        }

        const data = await response.json();
        window.location.href = data.checkoutUrl;

    } catch (error) {
        console.error(error);
        alert(error.message);
        payButton.disabled = false;
    }
}

async function cancelOrder() {
    const confirmed = confirm("Are you sure, you want to calcel order");

    if (!confirmed) {
        return;
    }

    const orderId = page.dataset.orderId;

    try {
        const response = await fetch(`/api/customer/orders/${orderId}/cancel`,
            {
                method: "PUT",
                headers: { [csrfHeader]: csrfToken }
            }
        );
        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || "Order could not been canceled.")
        }

        const order = await response.json();
        document.querySelector("#order-status").textContent = order.status;
        cancelButton.remove();

    } catch (error) {
        console.error(error);
        alert(error.message);
    }
}