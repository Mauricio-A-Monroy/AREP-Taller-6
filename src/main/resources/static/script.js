const apiUrl = "/property";

// Detectar en qué página estamos
const isFormPage = window.location.pathname.includes("form.html");

if (isFormPage) {
    document.getElementById("propertyForm").addEventListener("submit", saveProperty);
    loadPropertyFromQuery();
} else {
    document.addEventListener("DOMContentLoaded", fetchProperties);
}

// Obtener todas las propiedades y mostrarlas en la tabla
function fetchProperties() {
    fetch(apiUrl)
        .then(response => response.json())
        .then(properties => {
            const propertyList = document.getElementById("propertyList");
            propertyList.innerHTML = "";
            properties.forEach(property => {
                const row = document.createElement("tr");
                row.innerHTML = `
                    <td>${property.id}</td>
                    <td>${property.address}</td>
                    <td>$${property.price}</td>
                    <td>${property.size} m²</td>
                    <td>${property.description}</td>
                    <td class="actions">
                        <button class="edit" onclick="redirectToEdit(${property.id})">Editar</button>
                        <button class="delete" onclick="deleteProperty(${property.id})">Eliminar</button>
                    </td>
                `;
                propertyList.appendChild(row);
            });
        })
        .catch(error => console.error("Error al obtener propiedades:", error));
}

// Guardar o actualizar propiedad
function saveProperty(e) {
    e.preventDefault();

    const propertyId = document.getElementById("propertyId").value;
    const propertyData = {
        address: document.getElementById("address").value,
        price: parseFloat(document.getElementById("price").value),
        size: parseFloat(document.getElementById("size").value),
        description: document.getElementById("description").value,
    };

    const method = propertyId ? "PUT" : "POST";
    const url = propertyId ? `${apiUrl}/${propertyId}` : apiUrl;

    fetch(url, {
        method: method,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(propertyData),
    })
    .then(() => window.location.href = "index.html")
    .catch(error => console.error("Error al guardar la propiedad:", error));
}

// Redirigir a la página de edición con datos
function redirectToEdit(id) {
    window.location.href = `form.html?propertyId=${id}`;
}

// Cargar propiedad en el formulario si estamos editando
function loadPropertyFromQuery() {
    const params = new URLSearchParams(window.location.search);
    const propertyId = params.get("propertyId");

    if (propertyId) {
        document.getElementById("formTitle").innerText = "Editar Propiedad";

        fetch(`${apiUrl}/${propertyId}`)
            .then(response => response.json())
            .then(property => {
                document.getElementById("propertyId").value = property.id;
                document.getElementById("address").value = property.address;
                document.getElementById("price").value = property.price;
                document.getElementById("size").value = property.size;
                document.getElementById("description").value = property.description;
            })
            .catch(error => console.error("Error al cargar propiedad:", error));
    }
}

// Eliminar una propiedad
function deleteProperty(id) {
    if (confirm("¿Seguro que deseas eliminar esta propiedad?")) {
        fetch(`${apiUrl}/${id}`, { method: "DELETE" })
            .then(() => fetchProperties())
            .catch(error => console.error("Error al eliminar la propiedad:", error));
    }
}
