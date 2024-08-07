{ // Avoid variables ending up in the global scope

	// Page components
	let createdGroups, invitedGroups, groupDetails, createForm, userList,
		pageOrchestrator = new PageOrchestrator(); // Main controller

	window.addEventListener("load", () => {
		if (sessionStorage.getItem("username") == null) {
			window.location.href = "index.html";
		} else {
			pageOrchestrator.start(); // Initialize the components
			pageOrchestrator.refresh(); // Display initial content
		}
	}, false);


	// Welcome message with username
	function PersonalMessage(_username, messagecontainer) {
		this.username = _username;
		this.show = function() {
			messagecontainer.textContent = this.username;
		}
	}

	// List of user created groups
	function CreatedGroups(_alert, _listcontainer, _message) {
		this.alert = _alert;
		this.listcontainer = _listcontainer;
		this.message = _message;

		this.reset = function() {
			this.listcontainer.style.visibility = "hidden";
		}

		this.show = function(next) {
			var self = this;
			makeCall("GET", 'GetGroupsData', null,
				function(x) {
					if (x.readyState == XMLHttpRequest.DONE) {
						var message = x.responseText;
						switch (x.status) {
							case 200: // OK
								var groupsToShow = JSON.parse(x.responseText)[0];
								if (groupsToShow.length == 0) {
									self.message.textContent = "No created groups yet!";
									return;
								}
								self.update(groupsToShow);
								if (next) next();
								break;
							case 400: // Bad request
								self.alert.textContent = message;
								break;
							case 401: // Unauthorized
								window.location.href = x.getResponseHeader("Location");
								window.sessionStorage.removeItem('username');
								break;
							default: // Server error
								self.alert.textContent = message;
								break;
						}
					}
				}
			);
		};

		this.update = function(arrayGroups) {
			var li, anchor;
			this.listcontainer.innerHTML = "";
			this.message.innerHTML = "";
			var self = this;
			arrayGroups.forEach(function(group) {
				li = document.createElement("li");
				anchor = document.createElement("a");
				li.appendChild(anchor);
				anchor.setAttribute('groupid', group.id);
				anchor.textContent = group.title;
				anchor.addEventListener("click", (e) => {
					groupDetails.show(e.target.getAttribute("groupid"), true);
				}, false);
				anchor.href = "#";
				self.listcontainer.appendChild(li);
			});
			this.listcontainer.style.visibility = "visible";

		}

		this.autoclick = function(groupId) {
			var e = new Event("click");
			var selector = "a[groupid='" + groupId + "']";
			var anchorToClick =
				(groupId) ? document.querySelector(selector) : this.listcontainer.querySelectorAll("a")[0];
			if (anchorToClick) anchorToClick.dispatchEvent(e);
		}
	}

	// List of user invited groups
	function InvitedGroups(_alert, _listcontainer, _message) {
		this.alert = _alert;
		this.listcontainer = _listcontainer;
		this.message = _message;

		this.reset = function() {
			this.listcontainer.style.visibility = "hidden";
		}

		this.show = function() {
			var self = this;
			makeCall("GET", 'GetGroupsData', null,
				function(x) {
					if (x.readyState == XMLHttpRequest.DONE) {
						var message = x.responseText;
						switch (x.status) {
							case 200: // OK
								var groupsToShow = JSON.parse(x.responseText)[1];
								if (groupsToShow.length == 0) {
									self.message.textContent = "No invited groups yet!";
									return;
								}
								self.update(groupsToShow);
								break;
							case 400: // Bad request
								self.alert.textContent = message;
								break;
							case 401: // Unauthorized
								window.location.href = x.getResponseHeader("Location");
								window.sessionStorage.removeItem('username');
								break;
							default: // Server error
								self.alert.textContent = message;
								break;
						}
					}
				}
			);
		};

		this.update = function(arrayGroups) {
			var li, anchor;
			this.listcontainer.innerHTML = "";
			this.message.innerHTML = "";
			var self = this;
			arrayGroups.forEach(function(group) {
				li = document.createElement("li");
				anchor = document.createElement("a");
				li.appendChild(anchor);
				anchor.setAttribute('groupid', group.id);
				anchor.textContent = group.title;
				anchor.addEventListener("click", (e) => {
					groupDetails.show(e.target.getAttribute("groupid"), false);
				}, false);
				anchor.href = "#";
				self.listcontainer.appendChild(li);
			});
			this.listcontainer.style.visibility = "visible";
		}
	}

	// Section for group details
	function GroupDetails(options) {
		this.alert = options['alert'];
		this.detailcontainer = options['detailcontainer'];
		this.detailheader = options['detailheader'];
		this.bin = options['bin'];
		this.title = options['title'];
		this.creator = options['creator'];
		this.date = options['date'];
		this.duration = options['duration'];
		this.minEntrants = options['minEntrants'];
		this.maxEntrants = options['maxEntrants'];
		this.entrants = options['entrants'];

		this.show = function(_groupid, _isCreator) {
			var self = this;
			makeCall("GET", 'GetGroupDetailsData?groupid=' + _groupid, null,
				function(x) {
					if (x.readyState == XMLHttpRequest.DONE) {
						var message = x.responseText;
						switch (x.status) {
							case 200: // OK
								var group = JSON.parse(x.responseText)[0];
								var creator = JSON.parse(x.responseText)[1];
								var entrants = JSON.parse(x.responseText)[2];
								self.update(group, creator, entrants, _isCreator);
								self.detailheader.style.visibility = "visible";
								self.detailcontainer.style.visibility = "visible";
								break;
							case 400: // Bad request
								self.alert.textContent = message;
								break;
							case 401: // Unauthorized
								window.location.href = x.getResponseHeader("Location");
								window.sessionStorage.removeItem('username');
								break;
							default: // Server error
								self.alert.textContent = message;
								break;
						}
					}
				}
			);

			// Rimuovi gli event listener se già presenti
			self.bin.removeEventListener('dragover', self.handleDragOver);
			self.bin.removeEventListener('dragleave', self.handleDragLeave);
			self.bin.removeEventListener('drop', self.handleDrop);

			if (_isCreator) {
				// Definisci le funzioni all'interno del blocco if
				const handleDragOver = (e) => {
					e.preventDefault();
					// Cambia l'immagine del cestino
					self.bin.src = 'css/openedBin.png';
				};

				const handleDragLeave = () => {
					// Ripristina l'immagine del cestino
					self.bin.src = 'css/closedBin.png';
				};

				const handleDrop = (e) => {
					e.preventDefault();
					var userId = e.dataTransfer.getData("userId");
					makeCall("GET", "DeleteEntrant?groupId=" + _groupid + "&userId=" + userId, null,
						function(x) {
							if (x.readyState == XMLHttpRequest.DONE) {
								var message = x.responseText;
								switch (x.status) {
									case 200: // OK
										pageOrchestrator.refresh(x.responseText);
										self.alert.textContent = 'Participant removed successfully';
										break;
									case 400: // Bad request
										self.alert.textContent = 'Removing the participant would violate the minimum entrants constraint';
										break;
									case 401: // Unauthorized
										window.location.href = x.getResponseHeader("Location");
										window.sessionStorage.removeItem('username');
										break;
									default: // Server error
										self.alert.textContent = message;
								}
							}
						}
					);
					self.bin.src = 'css/closedBin.png';
				};
				
				// Aggiungi gli event listener
				self.bin.addEventListener('dragover', handleDragOver);
				self.bin.addEventListener('dragleave', handleDragLeave);
				self.bin.addEventListener('drop', handleDrop);

				// Salva i riferimenti delle funzioni sugli oggetti per poterle rimuovere in futuro
				self.handleDragOver = handleDragOver;
				self.handleDragLeave = handleDragLeave;
				self.handleDrop = handleDrop;

				self.bin.style.visibility = "visible";
			} else {
				self.bin.style.visibility = "hidden";
			}
		};

		this.reset = function() {
			this.detailcontainer.style.visibility = "hidden";
			this.detailheader.style.visibility = "hidden";
			this.bin.style.visibility = "hidden";
		}

		this.update = function(g, c, e, isCreator) {
			this.title.textContent = g.title;
			this.creator.textContent = c.name + ' ' + c.surname;
			this.date.textContent = g.creationDate;
			this.duration.textContent = g.duration;
			this.minEntrants.textContent = g.minEntrants;
			this.maxEntrants.textContent = g.maxEntrants;
			this.entrants.innerHTML = "";
			var self = this;
			e.forEach(function(entrant) {
				li = document.createElement("li");
				li.textContent = entrant.name + ' ' + entrant.surname;
				if (isCreator) {
					li.setAttribute('value', entrant.id);
					li.setAttribute('draggable', true);
					li.addEventListener('dragstart', (e) => {
						e.dataTransfer.setData("userId", e.target.value);
					}, false);
				}
				self.entrants.appendChild(li);
			});
		}
	}

	// Form for group creation
	function CreateForm(_button, _alert) {
		this.button = _button;
		this.alert = _alert;

		this.reset = function() {
			this.button.addEventListener('click', (e) => {
				var form = e.target.closest("form");
				if (form.checkValidity()) {
					var self = this;
					makeCall("POST", 'GetUserListData', e.target.closest("form"),
						function(x) {
							if (x.readyState == XMLHttpRequest.DONE) {
								var message = x.responseText;
								switch (x.status) {
									case 200: // OK
										var users = JSON.parse(x.responseText);
										userList.update(users);
										userList.show();
										break;
									case 400: // Bad request
										self.alert.textContent = message;
										break;
									case 401: // Unauthorized
										window.location.href = x.getResponseHeader("Location");
										window.sessionStorage.removeItem('username');
										break;
									default: // Server error
										self.alert.textContent = message;
								}
							}
						}, false
					);
				} else {
					form.reportValidity();
				}
			});
		}
	}

	// Modal window to invite entrants
	function UserList(options) {
		this.modal = options['modal'];
		this.overlay = options['overlay'];
		this.closeButton = options['closeButton'];
		this.userListContainer = options['userListContainer'];
		this.alert = options['alert'];
		this.button = options['button'];

		this.show = function() {
			sessionStorage.setItem("invitation_attempts", 0);
			this.modal.classList.add('active');
			this.overlay.classList.add('active');
		};

		this.reset = function() {
			this.button.addEventListener('click', (e) => {
				e.preventDefault();
				var selectedUsersNumber = getSelectedUsersNumber();

				var form = document.getElementById("id_invited");
				var formData = new FormData(form);
				var minEntrantsElement = document.getElementById('minEntrants');
				var maxEntrantsElement = document.getElementById('maxEntrants');

				var minEntrants = parseInt(minEntrantsElement.value, 10);
				var maxEntrants = parseInt(maxEntrantsElement.value, 10);
				var checkedUserIds = [];

				var self = this;

				var attempts = parseInt(sessionStorage.getItem("invitation_attempts")) + 1;
				console.log("Incrementing attempts to", attempts);
				sessionStorage.setItem("invitation_attempts", attempts);
				console.log("Current attempts after increment:", sessionStorage.getItem("invitation_attempts"));

				document.querySelectorAll('input[name="checkedUserIds"]:checked').forEach((checkbox) => {
					checkedUserIds.push(checkbox.value);
				});

				if (selectedUsersNumber < minEntrants) {
					let neededParticipants = minEntrants - selectedUsersNumber;
					showModalError("You must invite at least " + neededParticipants + " more participants. Attempts n." + attempts);
				} else if (selectedUsersNumber > maxEntrants) {
					let usersToRemove = selectedUsersNumber - maxEntrants;
					showModalError("Too many users selected. Please, deselect at least " + usersToRemove + ". Attempts n." + attempts);
				} else {

					var invitedUserIds = checkedUserIds.map(id => parseInt(id, 10));

					console.log("Invited User IDs:", invitedUserIds);
					makeCall("POST", 'CheckInvited', form,
						function(x) {
							if (x.readyState == XMLHttpRequest.DONE) {
								var message = x.responseText;
								switch (x.status) {
									case 200: // OK
										closeModalWindow();
										reset_modalWindow();
										pageOrchestrator.refresh(x.responseText);
										self.alert.textContent = "Group created successfully";
										break;
									case 400: // Bad request
										self.alert.textContent = "Request error";
										break;
									case 401: // Unauthorized
										window.location.href = x.getResponseHeader("Location");
										window.sessionStorage.removeItem('username');
										break;
									default: // Server error
										self.alert.textContent = message;
								}
							}
						}
					);
					return;
				}

				if (attempts > 2) {
					self.alert.textContent = "Error: too many attempts to create a group with a wrong number of users.";
					reset_modalWindow();
					closeModalWindow();
				}
			});

			this.closeButton.addEventListener('click', () => {
				sessionStorage.setItem("invitation_attempts", 0);
				reset_modalWindow();
				closeModalWindow();
			});
		}

		this.update = function(users) {
			var self = this;
			this.userListContainer.innerHTML = "";
			users.forEach(function(user) {
				var tr = document.createElement("tr");
				var check = document.createElement("td");
				var name = document.createElement("td");

				var input = document.createElement("input");
				input.setAttribute("type", "checkbox");
				input.setAttribute("value", user.id.toString());
				input.setAttribute("name", "checkedUserIds");
				input.classList.add("form-check-input");

				check.appendChild(input);
				name.textContent = user.name + ' ' + user.surname;

				tr.appendChild(check);
				tr.appendChild(name);

				self.userListContainer.appendChild(tr);
			});
		};
	}

	// Main controller of the page
	function PageOrchestrator() {
		var alertContainer = document.getElementById("id_alert");

		this.start = function() {
			var personalMessage = new PersonalMessage(sessionStorage.getItem('username'),
				document.getElementById("id_username"));
			personalMessage.show();

			createdGroups = new CreatedGroups(
				alertContainer,
				document.getElementById("id_createdgroups"), //list to fill
				document.getElementById("createdMessage")) 

			invitedGroups = new InvitedGroups(
				alertContainer,
				document.getElementById("id_invitedgroups"),
				document.getElementById("invitedMessage"))

			groupDetails = new GroupDetails({ // Many parameters, wrap them in an object
				alert: alertContainer,
				detailcontainer: document.getElementById("id_details"),
				detailheader: document.getElementById("details_header"),
				bin: document.getElementById("id_bin"),
				title: document.getElementById("id_title"),
				creator: document.getElementById("id_creator"),
				date: document.getElementById("id_creationdate"),
				duration: document.getElementById("id_duration"),
				minEntrants: document.getElementById("id_minEntrants"),
				maxEntrants: document.getElementById("id_maxEntrants"),
				entrants: document.getElementById("id_entrants")
			});

			createForm = new CreateForm(
				document.getElementById("id_creategroupbutton"),
				alertContainer
			)

			userList = new UserList({
				modal: document.getElementById("id_modalWindow"),
				overlay: document.getElementById("overlay"),
				closeButton: document.getElementById("id_modalclosebutton"),
				userListContainer: document.getElementById("userList"),
				alert: document.getElementById("id_alert"),
				button: document.getElementById("id_checkinvitedbutton")
			});

			document.querySelector("a[href='Logout']").addEventListener('click', () => {
				window.sessionStorage.removeItem('username');
			})

			createForm.reset();
			userList.reset();
		}

		this.refresh = function(currentGroup) { // CurrentGroup initially null at start
			alertContainer.textContent = "";
						
			createdGroups.reset();
			invitedGroups.reset();
			groupDetails.reset();
			document.getElementById("id_creategroupform").reset();

			createdGroups.show(function() {
				createdGroups.autoclick(currentGroup);
			});
			invitedGroups.show();
		};
	}
};