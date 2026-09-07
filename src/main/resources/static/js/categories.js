"use strict";

const API_URL = '/api/categories';

const form = document.querySelector('#category-form');
const categoryIdInput = document.querySelector('#category-id');
const categoryNameInput = document.querySelector('#category-name');
const tableBody = document.querySelector('#category-table-body');
const saveButton = document.querySelector('#submit-button');
const cancelButton = document.querySelector('#cancel-button');
const refreshButton = document.querySelector('#refresh-button');
const message = document.querySelector('#message');

const csrfToken = document.querySelector('meta[name="_csrf"]').content;
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

async function loadCategories(){
    try {
        const response = await fetch(API_URL);
        if (!response.ok) {
            throw new Error("Categories could not be loaded");
        }
        const data = await response.json();

        renderCategories(data);

    } catch (error) {
        console.error(error);
        showMessage("Categories could not be loaded");
    }
}

function renderCategories(categories) {
    tableBody.innerHTML = "";
    if (categories.length === 0) {
        const row = document.createElement('tr');
        const cell = document.createElement('td');
        cell.colSpan = 3;
        cell.textContent = "Categories not found";

        row.appendChild(cell);
        tableBody.appendChild(row);
        return;
    }

    for (let category of categories) {
        const row = document.createElement('tr');
        const idCell = document.createElement('td');
        idCell.textContent = category.id;

        const nameCell = document.createElement('td');
        nameCell.textContent = category.name;

        const actionCell = document.createElement('td');
        const editButton = document.createElement('button');
        editButton.type = 'button';
        editButton.textContent = 'Edit';
        editButton.className = 'button';
        editButton.classList.add('edit-button');

        editButton.addEventListener('click', ()=> {
            startEdit(category);
        });

        const deleteButton = document.createElement('button');
        deleteButton.type = 'button';
        deleteButton.textContent = 'Delete';
        deleteButton.className = 'button';
        deleteButton.classList.add('delete-button');

        deleteButton.addEventListener('click', ()=> {
            deleteCategory(category.id);
        });

        actionCell.appendChild(editButton);
        actionCell.appendChild(deleteButton);

        row.appendChild(idCell);
        row.appendChild(nameCell);
        row.appendChild(actionCell);

        tableBody.appendChild(row);
    }
}

async function deleteCategory(id) {
    
    const confirmed = confirm("Are you sure, you want to delete the category");
    if(!confirmed) {
        return;
    }

    try {
		const response = await fetch(`${API_URL}/${id}`, {
		    method: "DELETE",
				headers: {
					[csrfHeader]: csrfToken
				}
		    });
        
        if (!response.ok) {
            throw new Error("Delete failed");
        }

        showMessage("Category deleted successfully");
        resetForm();
        await loadCategories();

    } catch (error) {
        console.error(error);
        showMessage("Category could not be deleted");
    }
}

form.addEventListener("submit", handleSubmit);
cancelButton.addEventListener('click', resetForm);
refreshButton.addEventListener('click', loadCategories);

async function handleSubmit(e) {
    e.preventDefault();
    const id = categoryIdInput.value;
    const category = {
        name: categoryNameInput.value.trim()
    };

    if(category.name === "") {
        showMessage("Category name is required");
        return;
    }

    const isEditing = id !== "";
    const url = isEditing ? `${API_URL}/${id}` : API_URL;

    const method = isEditing ? 'PUT' : 'POST';

    try {
        const response = await fetch(url, {
            method: method,
            headers: {
                "Content-Type": "application/json",
				[csrfHeader]: csrfToken
            },

            body: JSON.stringify(category)
        });

        if (!response.ok) {
            throw new Error("Request failed");
        }

        if (isEditing) {
            showMessage("Category Updated successfully");
        } else {
            showMessage("Category Created successfully");
        }

        resetForm();
        await loadCategories();
        
    } catch (error) {
        console.error(error);
        showMessage("Request Failed");
    }
}

function resetForm() {
    form.reset();
    categoryIdInput.value = "";
    saveButton.textContent = "Add Category";
    cancelButton.hidden = true;
}

function startEdit(category) {
    categoryIdInput.value = category.id;
    categoryNameInput.value = category.name;
    saveButton.textContent = "Update Category";
    cancelButton.hidden = false;
}

function showMessage(text) {
    message.textContent = text;
    message.hidden = false;

    setTimeout(()=> {
        message.hidden = true;
    }, 3000);
}

loadCategories();
