"use strict"

const BOOK_API = "/api/books";
const CATEGORY_API = "/api/categories";
const AUTHOR_API = "/api/authors";

const form = document.querySelector("#book-form");
const bookIdInput = document.querySelector("#book-id");
const titleInput =
    document.querySelector(
        "#book-title"
    );

const isbnInput =
    document.querySelector(
        "#book-isbn"
    );

const priceInput =
    document.querySelector(
        "#book-price"
    );

const stockInput =
    document.querySelector(
        "#book-stock"
    );

const categoryInput =
    document.querySelector(
        "#book-category"
    );

const authorInput =
    document.querySelector(
        "#book-author"
    );

const activeInput =
    document.querySelector(
        "#book-active"
    );

const tableBody =
    document.querySelector(
        "#book-table-body"
    );

const saveButton =
    document.querySelector(
        "#save-button"
    );

const cancelButton =
    document.querySelector(
        "#cancel-button"
    );

const refreshButton =
    document.querySelector(
        "#refresh-button"
    );

const message =
    document.querySelector(
        "#message"
    );

async function loadCategories() {
    console.log('api');
    const response = await fetch(CATEGORY_API);

    if (!response.ok) {
        throw new Error(
            "Categories could not be loaded"
        );
    }

    const categories = await response.json();
    categoryInput.innerHTML =
        `
        <option value="">
            Select Category
        </option>
        `;
    for (let category of categories) {
        const option = document.createElement(
            "option"
        );
        option.value = category.id;
        option.textContent = category.name;
        categoryInput.appendChild(option);
    }
}

async function loadAuthors() {
    console.log('author api');
    const response = await fetch(AUTHOR_API);

    if (!response.ok) {
        throw new Error(
            "Authors could not be loaded"
        );
    }

    const authors = await response.json();
    authorInput.innerHTML =
        `
        <option value="">
            Select Author
        </option>
        `;
    for (let author of authors) {
        const option = document.createElement(
            "option"
        );
        option.value = author.id;
        option.textContent = `${author.firstName} ${author.lastName}`;
        authorInput.appendChild(option);
    }
}

async function loadBooks() {
    console.log('books api');
    try {
        const response = await fetch(BOOK_API);

        if (!response.ok) {
            throw new Error(
                "Books could not be loaded"
            );
        }

        const books = await response.json();

        renderBooks(books);

    } catch (error) {
        console.error(error);
        showMessage("Books could not be loaded",
            true)
    }
}
function renderBooks(books) {
    tableBody.innerHTML = "";
    if (books.length === 0) {
        const row =
            document.createElement("tr");

        const cell =
            document.createElement("td");

        cell.colSpan = 9;

        cell.textContent =
            "No books found.";

        row.appendChild(cell);

        tableBody.appendChild(row);

        return;
    }

    for (let book of books) {
        const row = document.createElement('tr');
        addCell(row, book.id);
        addCell(row, book.title);
        addCell(row, book.isbn);
        addCell(row, book.price);
        addCell(row, book.stockQuantity);
        addCell(row, book.categoryName);
        addCell(row, `${book.authorFirstName} ${book.authorLastName}`);
        addCell(row, book.active ? "Yes" : "No");

        const actionCell = document.createElement("td");
        actionCell.className = "action-buttons";
        const editButton = document.createElement("button");
        editButton.type = "button";
        editButton.textContent = "Edit";
        editButton.className = "edit-button";
        editButton.addEventListener("click", () => startEdit(book));
        const deleteButton = document.createElement("button");
        deleteButton.type = "button";
        deleteButton.textContent = "Delete";
        deleteButton.className = "delete-button";
        deleteButton.addEventListener(
            "click",
            () => deleteBook(book.id)
        );
        actionCell.append(
            editButton,
            deleteButton
        );

        row.appendChild(
            actionCell
        );

        tableBody.appendChild(
            row
        );
    }
}

function addCell(row, value) {
    const cell = document.createElement('td');
    cell.textContent = value;
    row.appendChild(cell);
}

async function handleSubmit(event) {
    event.preventDefault();
    const id = bookIdInput.value;
    const book = {
        title: titleInput.value.trim(),
        isbn: isbnInput.value.trim(),
        price: Number(priceInput.value),
        stockQuantity: Number(stockInput.value),
        active: activeInput.checked,
        categoryId: Number(categoryInput.value),
        authorId: Number(authorInput.value)
    }

    const isEditing = id !== '';
    const url =
        isEditing
            ? `${BOOK_API}/${id}`
            : BOOK_API;

    const method =
        isEditing
            ? "PUT"
            : "POST";
    try {
        const response = await fetch(url, {
            method: method,
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(book)
        });

        if (!response.ok) {
            throw new Error(
                "Book request failed"
            );
        }

        showMessage(
            isEditing
                ? "Book updated successfully"
                : "Book created successfully"
        );

        resetForm();
        await loadBooks();
    } catch (error) {
        console.error(error);
        showMessage("Book could not be saved",
            true)
    }
}

function startEdit(book) {
    bookIdInput.value = book.id;
    titleInput.value = book.title;
    isbnInput.value = book.isbn;
    priceInput.value = book.price;
    stockInput.value = book.stockQuantity;
    categoryInput.value = book.categoryId;
    authorInput.value = book.authorId;
    activeInput.checked = book.active;
    saveButton.textContent =
        "Update Book";

    cancelButton.hidden =
        false;

    window.scrollTo({
        top: 0,
        behavior: "smooth"
    });
}

async function deleteBook(id) {
    const confirmed =
        confirm(
            "Are you sure you want to delete this book?"
        );

    if (!confirmed) {
        return;
    }
    try {
        const response =
            await fetch(
                `${BOOK_API}/${id}`,
                {
                    method: "DELETE"
                }
            );
        if (!response.ok) {
            throw new Error(
                "Delete failed"
            );
        }
        showMessage(
            "Book deleted successfully"
        );
        resetForm();
        await loadBooks();
    } catch (error) {
        console.error(error);
        showMessage(
            "Book could not be deleted",
            true
        );
    }
}

function resetForm() {
    form.reset();
    bookIdInput.value = "";
    activeInput.checked = true;
    saveButton.textContent = "Save button";
    cancelButton.hidden = true;
}

function showMessage(text, isError = false) {
    message.textContent = text;
    message.hidden = false;
    if (isError) {
        message.style.background =
            "#fee2e2";
        message.style.color =
            "#991b1b";
    } else {
        message.style.background =
            "#dcfce7";
        message.style.color =
            "#166534";
    }
    setTimeout(() => {
        message.hidden = true;
    }, 3000);
}

form.addEventListener('submit', handleSubmit);

cancelButton.addEventListener('click', resetForm);

refreshButton.addEventListener('click', loadBooks);

async function initializePage() {
    try {
        await Promise.all([
            loadCategories(),
            loadAuthors()
        ])
        await loadBooks();

    } catch (error) {
        console.error(error);
        showMessage(
            "Page could not be initialized",
            true
        );
    }
}

initializePage();
