/**
 * Credential Manager
 */

(function() { // avoid variables ending up in the global scope

	document.getElementById("loginButton").addEventListener('click', (e) => {
		var form = e.target.closest("form");
		document.getElementById("genericMessage").textContent = "";
		document.getElementById("errorMessage").textContent = "";
		if (form.checkValidity()) {
			makeCall("POST", 'CheckLogin', e.target.closest("form"),
				function(x) {
					if (x.readyState == XMLHttpRequest.DONE) {
						var message = x.responseText;
						switch (x.status) {
							case 200:
								sessionStorage.setItem('username', message);
								window.location.href = "home.html";
								break;
							case 400: // bad request
								document.getElementById("errorMessage").textContent = message;
								break;
							case 401: // unauthorized
								document.getElementById("errorMessage").textContent = message;
								break;
							case 500: // server error
								document.getElementById("errorMessage").textContent = message;
								break;
						}
					}
				}
			);
		} else {
			form.reportValidity();
		}
	});

	document.getElementById("signUpButton").addEventListener('click', (e) => {
		var form = e.target.closest("form");
		document.getElementById("genericMessage").textContent = "";
		document.getElementById("errorMessage").textContent = "";
		if (form.checkValidity()) {
			makeCall("POST", 'RegisterUser', e.target.closest("form"),
				function(x) {
					if (x.readyState == XMLHttpRequest.DONE) {
						var message = x.responseText;
						switch (x.status) {
							case 200:
								document.getElementById("genericMessage").textContent = message;
								form.reset()
								break;
							case 400: // bad request
								document.getElementById("errorMessage").textContent = message;
								break;
							case 401: // unauthorized
								document.getElementById("errorMessage").textContent = message;
								break;
							case 500: // server error
								document.getElementById("errorMessage").textContent = message;
								break;
						}
					}
				}
			);
		} else {
			form.reportValidity();
		}
	});

})();