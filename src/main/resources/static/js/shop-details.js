const csrfToken = document.querySelector('meta[name="_csrf"]').content;
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;
const button = document.querySelector("#add-to-cart-button");

if(button) {
	button.addEventListener('click', async () => {
		const bookId = Number(button.dataset.bookId);
		const response = await fetch("/api/cart/items", {
			method: "POST",
						headers: {
							"Content-Type": "application/json",
							[csrfHeader]: csrfToken
						},

						body: JSON.stringify({
							bookId: bookId,
							quantity: 1
						})

		});
		
		if(response.status === 401 || response.status === 403) {
			window.location.href = "/login";
			return;
		}
		
		if(!response.ok) {
			alert("Coulfd not add to cart");
			return;
		}
		
		window.location.href = "/customer/cart";
	});
}