/**
 * AJAX call management
 */
function makeCall(method, url, formElement, cback, reset = true) {
	var req = new XMLHttpRequest(); // visible by closure
	req.onreadystatechange = function() {
		cback(req)
	}; // closure
	req.open(method, url);
	if (formElement == null) {
		req.send();
	} else {
		req.send(new FormData(formElement));
	}
	if (formElement !== null && reset === true) {
		formElement.reset();
	}
}

function closeModalWindow() {
	document.getElementById("id_modalWindow").classList.remove('active');
	document.getElementById("overlay").classList.remove('active');
	document.getElementById("modalAlertMsg").style.display = "none";
	document.getElementById("userList").innerHTML = "";
}

function reset_modalWindow() {
	document.getElementById("modalAlertMsg").style.display = "none";
	document.getElementById("userList").innerHTML = "";

}

// Funzione per mostrare un errore nel modal
function showModalError(msg) {
	document.getElementById("modalAlertMsg").textContent = msg;
	document.getElementById("modalAlertMsg").style.display = "block";
}

// Funzione per ottenere il numero di utenti selezionati
function getSelectedUsersNumber() {
	let checkboxes = document.querySelectorAll("#userList .form-check-input");
	let number = 0;

	checkboxes.forEach((checkbox) => {
		if (checkbox.checked) {
			number++;
		}
	});

	return number;
}