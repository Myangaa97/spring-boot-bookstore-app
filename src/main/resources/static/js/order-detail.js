"use strict";

const page = document.querySelector("#order-page");

const cancelButton = document.querySelector("#cancel-order-button");
const csrfToken = document.querySelector('meta[name="_csrf"]').content;
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

if(cancelButton) {
    cancelButton.addEventListener('click', cancelOrder); //callback
}

async function cancelOrder() {
    const confirmed = confirm("Are you sure, you want to calcel order");

    if(!confirmed) {
        return;
    }

    const orderId = page.dataset.orderId;

    try {
        const response = await fetch(`/api/customer/orders/${orderId}/cancel`,
            {
                method: "PUT",
                header: {[csrfHeader]: csrfToken}
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