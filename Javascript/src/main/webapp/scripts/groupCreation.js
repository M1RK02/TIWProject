/**
 * Group Creation Manager
 */

(function() { // Avoid variables ending in the global scope

    // Event listener per il bottone di invito
    document.getElementById("id_checkinvitedbutton").addEventListener("click", (e) => {
        e.preventDefault();
        var selectedUsersNumber = getSelectedUsersNumber(); // DA CONTROLLARE CHE FUNZIONI

        var form = document.getElementById("id_creategroupform");
        var minEntrantsElement = document.getElementById('minEntrants');
        var maxEntrantsElement = document.getElementById('maxEntrants');

        if (minEntrantsElement && maxEntrantsElement) {
            var minEntrants = parseInt(minEntrantsElement.value, 10);
            var maxEntrants = parseInt(maxEntrantsElement.value, 10);

            if (selectedUsersNumber < minEntrants) {
                incrementAttempts();
                let neededParticipants = minEntrants - selectedUsersNumber;
                showModalError("You must invite at least " + neededParticipants + " more participants. Attempts n." + getAttempts());
            } else if (selectedUsersNumber > maxEntrants) {
                incrementAttempts();
                let usersToRemove = selectedUsersNumber - maxEntrants;
                showModalError("Too many users selected. Please, deselect at least " + usersToRemove + ". Attempts n." + getAttempts());
            } else {
                let alert = document.getElementById("id_alert");
                makeCall("POST", "CheckInvited", e.target.closest("form"), function(req) {
                    if (req.readyState == 4) {
                        if (req.status == 200) {
                            alert.textContent = "Gruppo creato con successo";
                        } else if (req.status == 403) {
                            window.location.href = req.getResponseHeader("Location");
                            window.sessionStorage.removeItem('username');
                        } else if (req.status == 400) {
                            alert.textContent = "Errore nella richiesta";
                        } else {
                            alert.textContent = "Errore";
                        }
                    }
                }, true);
            }
        } else {
            console.error("minEntrantsElement o maxEntrantsElement non trovati.");
        }
    });

    function getAttempts() {
        // lato client ho questo item a parte che mi gestisce il controllo dei tentativi
        return parseInt(sessionStorage.getItem("invitation_attempts"));
    }

    function resetAttempts() {
        sessionStorage.setItem("invitation_attempts", "0");
    }

    function incrementAttempts() {
        let temp = getAttempts();
        temp++;
        console.log("Attempts: " + temp);
        sessionStorage.setItem("invitation_attempts", temp.toString());
    }

    // Funzione per mostrare un errore nel modal
    function showModalError(msg) {
        document.getElementById("modalAlertMsg").textContent = msg;
        document.getElementById("modalAlertMsg").style.display = "block";
    }

    // Funzione per ottenere il numero di utenti selezionati
    function getSelectedUsersNumber() {
        let checkboxes = document.getElementById("id_invited").getElementsByClassName("form-check-input");
        let number = 0;

        for (let i = 0; i < checkboxes.length; i++) {
            if (checkboxes[i].checked) {
                number++;
            }
        }
        return number;
    }
})();
