/**
 * Group Creation Manager
 */

(function() { // Avoid variables ending in the global scope

    // Event listener per il bottone di invito
    document.getElementById("id_checkinvitedbutton").addEventListener("click", (e) => {
        e.preventDefault();
        var selectedUsersNumber = getSelectedUsersNumber(); // funziona

        var form = document.getElementById("id_invited");
        var formData = new FormData(form); //aggiunto solo per debuggare
        var minEntrantsElement = document.getElementById('minEntrants');
        var maxEntrantsElement = document.getElementById('maxEntrants');

      
        var minEntrants = parseInt(minEntrantsElement.value, 10);
        var maxEntrants = parseInt(maxEntrantsElement.value, 10);
        var checkedUserIds = [];
        
  		document.querySelectorAll('input[name="checkedUserIds"]:checked').forEach((checkbox) => {
        checkedUserIds.push(checkbox.value);
  		 });

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
                for (var pair of formData.entries()) {
       			 console.log(pair[0] + ': ' + pair[1]); //aggiunto solo per debuggare
    			}
    			
    		 var invitedUserIds = checkedUserIds.map(id => parseInt(id, 10));
        	
      		  console.log("Invited User IDs:", invitedUserIds); //solo debug
                makeCall("POST", "CheckInvited", form, function(req) {
                    if (req.readyState == 4) {
                        if (req.status == 200) {
							
							closeModalWindow();
							reset_groupInfo();
							reset_modalWindow();
							// DA AGGIUNGERE funzione per ricaricare i gruppi con autoclick
							
						
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
                return;
            }
            
            if (getAttempts >= 2) {
				showNewGroupError("Error: too many attempts to create a group with a wrong number of users.");
				reset_groupInfo();
				reset_modalWindow();
				closeModalWindow();
				//funzione per la refresh dei group 
				
			}  
    });
    
    
    function closeModalWindow() {
		modal.classList.remove('active');
  		overlay.classList.remove('active');
	}
    
    function reset_groupInfo() {
		sessionStorage.removeItem("invitation_attempts");
		sessionStorage.removeAttribute("tempGroup"); //non sono sicura
		
	}
	function reset_modalWindow() {
		document.getElementById("modalAlertMsg").style.display = "none";
		document.getElementById("userList").innerHTML="";

	}

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
        let checkboxes = document.querySelectorAll("#userList .form-check-input");
        let number = 0;

        checkboxes.forEach((checkbox) => {
            if (checkbox.checked) {
                number++;
            }
        });

        return number;
    }
    
    function showNewGroupError(msg) {
		 document.getElementById("createNewGroupError").style.display = "block";
         document.getElementById("createNewGroupError").textContent = msg;
		
	}
	//aggiungere la funzione connessa al bottone di chiusura che svuota tutto
})();
