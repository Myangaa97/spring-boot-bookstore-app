"use strict"

const CART_API = "/api/cart";

const csrfToken = document.querySelector('meta[name="_csrf"]').content;
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;
const cartItems = document.querySelector("#cart-items");
const cartTotal = document.querySelector("#cart-total");
const checkoutButton = document.querySelector("#checkout-button");

async function errorMessage(response, fallback) {
	try {
		const data = await response.json();
		return data && data.message ? data.message : fallback;
	} catch (e) {
		return `${fallback} (HTTP ${response.status})`;
	}
}

async function checkout() {
	const confirmed = confirm("Place the order");
	if(!confirmed) {
		return;
	}
	
	try {
		const response = await fetch(`/api/checkout`, {
			method: "POST",
			headers: {
				[csrfHeader]: csrfToken
			}
		});
		
		if(!response.ok) {
			throw new Error(await errorMessage(response, "Checkout failed"));
		}
						
		const order = await response.json();
		alert(`Order #${order.orderId} created successfully`);
						
		window.location.href = order && order.orderId ? `/customer/orders/${order.orderId}` : "/customer/orders";
		
	} catch(error) {
		console.error(error);
		alert(error.message);
	}
	
}

checkoutButton.addEventListener('click', checkout);



async function loadCart() {
	const response = await fetch(CART_API);
	if(!response.ok) {
		throw new Error("Cart could not be loaded");
	}
	
	const cart = await response.json();
	renderCart(cart);
}

function renderCart(cart) {
	cartItems.innerHTML = "";
	if(cart.items.length === 0) {
		cartItems.textContent = "Your cart is empty";
		cartTotal.textContent = "0  $";
		return ;
	}
	
	for(const item of cart.items) {
		const row = document.createElement("div");
		row.className = "cart-item";
		
		row.innerHTML = `
			<div>
				<h3>${item.title}</h3>
				<p>${item.unitPrice}</p>
			</div>
			
			<input 
				type = "number"
				min = "1"
				value = "${item.quantity}"
				data-id = "${item.id}"
				class = "quantity-input"
			>
			<div>
				${item.lineTotal} $
			</div>
			
			<button data-id = "${item.id}" class = "remove-button">
				Remove
			</button>
		`;
		cartItems.appendChild(row);
	}
	cartTotal.textContent = `${cart.totalAmount} $`;
	attachCartEvents();
}

function attachCartEvents() {
	document.querySelectorAll(".quantity-input").forEach(input => {
		input.addEventListener('change', async ()=> {
			await updateQuantity(input.dataset.id, input.value);
		});
	});
	
	document.querySelectorAll(".remove-button").forEach(button => {
			button.addEventListener('click', async ()=> {
				await removeItem(button.dataset.id);
			});
		});
}

async function updateQuantity(id, quantity) {
	const response = await fetch(`${CART_API}/items/${id}`,
		{
			method: "PUT",
			headers: {
				"Content-Type": "application/json",
				[csrfHeader]: csrfToken
			},

			body: JSON.stringify({
				quantity: Number(quantity)
			})
			
			}
		);
		
		console.log("UPDATE status:", response.status);
		
	if(!response.ok) {
		throw new Error(await errorMessage(response, "Quantity update failed"));
	}
	const cart = await response.json();
	renderCart(cart);
}

async function removeItem(id) {

	const response = await fetch(`${CART_API}/items/${id}`, {
		method: "DELETE",
		headers: {
			[csrfHeader]: csrfToken
			}
		}
	);
			        
	if(!response.ok) {
		throw new Error(await errorMessage(response, "Item delete failed"));
	}
	const cart = await response.json();
	renderCart(cart);
		
}

loadCart();
