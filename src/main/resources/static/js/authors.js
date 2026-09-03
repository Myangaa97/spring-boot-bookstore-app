"use strict";

const API_URL = '/api/authors';

const form = document.querySelector('#author-form');
const authorIdInput = document.querySelector('#author-id');
const firstNameInput = document.querySelector('#author-first-name');
const lastNameInput = document.querySelector('#author-last-name');
const bioInput = document.querySelector('#author-bio');
const tableBody = document.querySelector('#author-table-body');
const saveButton = document.querySelector('#save-button');
const cancelButton = document.querySelector('#cancel-button');
const message = document.querySelector('#message');

async function loadAuthors(){
    try {
        const response = await fetch(API_URL);
        if (!response.ok) {
            throw new Error("Authrs could not be loaded");
        }
        const data = await response.json();

        renderAuthors(data);

    } catch (error) {
        console.error(error);
        showMesage("Authors could not be loaded");
    }
}

function renderAuthors(authors) {
    tableBody.innerHTML = "";
    if (authors.length === 0) {
        const row = document.createElement('tr');
        const cell = document.createElement('td');
        cell.colSpan = 5;
        cell.textContent = "Authors not found";

        row.appendChild(cell);
        tableBody.appendChild(row);
        return;
    }

    for (let author of authors) {
        const row = document.createElement('tr');
        const idcell = document.createElement('td');
        idcell.textContent = author.id;

        const firstNameCell = document.createElement('td');
        firstNameCell.textContent = author.firstName;
        const lastNameCell = document.createElement('td');
        lastNameCell.textContent = author.lastName;

        const bioCell = document.createElement('td');
        bioCell.textContent = author.bio ?? "";

        const actionCall = document.createElement('td');
        const editButton = document.createElement('button');
        editButton.type = 'button';
        editButton.textContent = 'Edit';

        editButton.addEventListener('click', ()=> {
            startEdit(author);
        });

        const deleteButton = document.createElement('button');
        deleteButton.type = 'button';
        deleteButton.textContent = 'Delete';

        deleteButton.addEventListener('click', ()=> {
            deleteAuthor(author.id);
        });

        actionCall.appendChild(editButton);
        actionCall.appendChild(deleteButton);

        row.appendChild(idcell);
        row.appendChild(firstNameCell);
        row.appendChild(lastNameCell);
        row.appendChild(bioCell);
        row.appendChild(actionCall);

        tableBody.appendChild(row);
    }
}

async function deleteAuthor(id) {
    
    const confirmed = confirm("Are you sure, you want to delete the author");
    if(!confirmed) {
        return;
    }

    try {
		const response = await fetch(`${API_URL}/${id}`, {
		    method: "DELETE"
		    });
        
        if (!response.ok) {
            throw new Error("Delete failed");
        }

        showMesage("Author deleted successfully");
        resetForm();
        await loadAuthors();

    } catch (error) {
        console.error(error);
        showMesage("Author could not be deleted");
    }
}

form.addEventListener("submit", handleSubmit);
cancelButton.addEventListener('click', resetForm);

async function handleSubmit(e) {
    e.preventDefault();
    const id = authorIdInput.value;
    const author = {
        firstName: firstNameInput.value.trim(),
        lastName: lastNameInput.value.trim(),
        bio: bioInput.value.trim()
    };

    if(author.firstName === "" || author.lastName === "") {
        showMesage("First name and last name required");
        return;
    }

    const isEditing = id !== "";
    const url = isEditing ? `${API_URL}/${id}` : API_URL;

    const method = isEditing ? 'PUT' : 'POST';

    try {
        const response = await fetch(url, {
            method: method,
            headers: {
                "Content-Type": "application/json"
            },

            body: JSON.stringify(author)
        });

        if (!response.ok) {
            throw new Error("Request failed");
        }

        if (isEditing) {
            showMesage("Author Updated seccussfully");
        } else {
            showMesage("Author Created successfully");
        }

        resetForm();
        await loadAuthors();


        
    } catch (error) {
        console.error(error);
        showMesage("Request Failed")
    }
}

function resetForm() {
    form.reset();
    authorIdInput.value = "";
    firstNameInput.value = "";
    lastNameInput.value = "";
    bioInput.value = "";
    saveButton.textContent = "SAve Author";
    cancelButton.hiden = true;
}
function startEdit(author) {
    authorIdInput.value = author.id;
    firstNameInput.value = author.firstName;
    lastNameInput.value = author.lastName;
    bioInput.value = author.bio ?? "";
    saveButton.textContent = "Update button";
    cancelButton.hidden = false;
}

function showMesage(text) {
    message.textContent = text;
    message.hidden = false;

    setTimeout(()=> {
        message.hidden = true;
    }, 3000)
}

loadAuthors();