{ // avoid variables ending up in the global scope

	  // page components
	  let createdGroups, invitedGroups, groupDetails, creationForm,
	    pageOrchestrator = new PageOrchestrator(); // main controller

	  window.addEventListener("load", () => {
	    if (sessionStorage.getItem("username") == null) {
	      window.location.href = "index.html";
	    } else {
	      pageOrchestrator.start(); // initialize the components
	      pageOrchestrator.refresh();
	    } // display initial content
	  }, false);


	  // Constructors of view components

	  function PersonalMessage(_username, messagecontainer) {
	    this.username = _username;
	    this.show = function() {
	      messagecontainer.textContent = this.username;
	    }
	  }
	  
	  function CreatedGroups(_alert, _listcontainer) {
		this.alert = _alert;
	    this.listcontainer = _listcontainer;

	    this.reset = function() {
	      this.listcontainer.style.visibility = "hidden";
	    }
	    
	    this.show = function(next) {
	      var self = this;
	      makeCall("GET", "GetGroupsData", null,
	        function(req) {
	          if (req.readyState == 4) {
	            var message = req.responseText;
	            if (req.status == 200) {
	              var groupsToShow = JSON.parse(req.responseText)[0];
	              if (groupsToShow.length == 0) {
	                alert("No created groups yet!");
	                return;
	              }
	              self.update(groupsToShow); // self visible by closure
	              if (next) next(); // show the default element of the list if present
	          } else if (req.status == 403) {
                  window.location.href = req.getResponseHeader("Location");
                  window.sessionStorage.removeItem('username');
                  }
                  else {
	            self.alert.textContent = message;
	          }}
	        }
	      );
	  	};

	    this.update = function(arrayGroups) {
	      var li, anchor;
	      this.listcontainer.innerHTML = "";
	      var self = this;
	      arrayGroups.forEach(function(group) {
	        li = document.createElement("li");
	        anchor = document.createElement("a");
	        li.appendChild(anchor);
	        anchor.setAttribute('groupid', group.id);
	        anchor.textContent = group.title;
	        anchor.addEventListener("click", (e) => {
	          groupDetails.show(e.target.getAttribute("groupid"));
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
	        (groupId) ? document.querySelector(selector) : this.listcontainerbody.querySelectorAll("a")[0];
	      if (anchorToClick) anchorToClick.dispatchEvent(e);
	    }
	  }
	  
	  function InvitedGroups(_alert, _listcontainer) {
		this.alert = _alert;
	    this.listcontainer = _listcontainer;

	    this.reset = function() {
	      this.listcontainer.style.visibility = "hidden";
	    }
	    
	    this.show = function(next) {
	      var self = this;
	      makeCall("GET", "GetGroupsData", null,
	        function(req) {
	          if (req.readyState == 4) {
	            var message = req.responseText;
	            if (req.status == 200) {
	              var groupsToShow = JSON.parse(req.responseText)[1];
	              if (groupsToShow.length == 0) {
	                alert("No invited groups yet!");
	                return;
	              }
	              self.update(groupsToShow); // self visible by closure
	              if (next) next(); // show the default element of the list if present
	          } else if (req.status == 403) {
                  window.location.href = req.getResponseHeader("Location");
                  window.sessionStorage.removeItem('username');
                  }
                  else {
	            self.alert.textContent = message;
	          }}
	        }
	      );
	  	};

	    this.update = function(arrayGroups) {
	      var li, anchor;
	      this.listcontainer.innerHTML = "";
	      var self = this;
	      arrayGroups.forEach(function(group) {
	        li = document.createElement("li");
	        anchor = document.createElement("a");
	        li.appendChild(anchor);
	        anchor.setAttribute('groupid', group.id);
	        anchor.textContent = group.title;
	        anchor.addEventListener("click", (e) => {
	          groupDetails.show(e.target.getAttribute("groupid"));
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
	        (groupId) ? document.querySelector(selector) : this.listcontainerbody.querySelectorAll("a")[0];
	      if (anchorToClick) anchorToClick.dispatchEvent(e);
	    }
	  }
	  
	  function GroupDetails(options) {
	    this.alert = options['alert'];
	    this.detailcontainer = options['detailcontainer'];
		this.title = options['title'];
		this.creator = options['creator'];
	    this.date = options['date'];
	    this.duration = options['duration'];
	    this.minEntrants = options['minEntrants'];
	    this.maxEntrants = options['maxEntrants'];
	    this.entrants = options['entrants'];

	    this.show = function(groupid) {
	      var self = this;
	      makeCall("GET", "GetGroupDetailsData?groupid=" + groupid, null,
	        function(req) {
	          if (req.readyState == 4) {
	            var message = req.responseText;
	            if (req.status == 200) {
	              var group = JSON.parse(req.responseText)[0];
	              var creator = JSON.parse(req.responseText)[1];
	              var entrants = JSON.parse(req.responseText)[2];
	              self.update(group, creator, entrants);
	              self.detailcontainer.style.visibility = "visible";
	            } else if (req.status == 403) {
                  window.location.href = req.getResponseHeader("Location");
                  window.sessionStorage.removeItem('username');
                  }
                  else {
	              self.alert.textContent = message;
	            }
	          }
	        }
	      );
	    };

	    this.reset = function() {
	      this.detailcontainer.style.visibility = "hidden";
	    }

	    this.update = function(g, c, e) {
			this.title.textContent = g.title;
			this.creator.textContent = c.name + ' ' + c.surname;
			this.date.textContent = g.creationDate;
			this.duration.textContent = g.duration;
	    	this.minEntrants.textContent = g.minEntrants;
	    	this.maxEntrants.textContent = g.maxEntrants;
	    	var self = this;
	    	this.entrants.innerHTML = "";
	    	e.forEach(function(entrant) {
		        li = document.createElement("li");
		        li.textContent = entrant.name + ' ' + entrant.surname;
		        self.entrants.appendChild(li);
		    });
	    }
	  }

	  function PageOrchestrator() {
	    var alertContainer = document.getElementById("id_alert");
	    
	    this.start = function() {
	      personalMessage = new PersonalMessage(sessionStorage.getItem('username'),
	        document.getElementById("id_username"));
	      personalMessage.show();

	      createdGroups = new CreatedGroups(
	        alertContainer,
	        document.getElementById("id_createdgroups"))
	        
	      invitedGroups = new InvitedGroups(
	        alertContainer,
	        document.getElementById("id_invitedgroups"))
	        
	      groupDetails = new GroupDetails({ // many parameters, wrap them in an object
	        alert: alertContainer,
	        detailcontainer: document.getElementById("id_details"),
	        title: document.getElementById("id_title"),
	        creator: document.getElementById("id_creator"),
	        date: document.getElementById("id_creationdate"),
	        duration: document.getElementById("id_duration"),
	        minEntrants: document.getElementById("id_minEntrants"),
	        maxEntrants: document.getElementById("id_maxEntrants"),
	        entrants: document.getElementById("id_entrants")
	      });

	      document.querySelector("a[href='Logout']").addEventListener('click', () => {
	        window.sessionStorage.removeItem('username');
	      })
      	}

	    this.refresh = function(currentMission) { // currentMission initially null at start
	      alertContainer.textContent = "";        // not null after creation of status change
	      createdGroups.reset();
	      invitedGroups.reset();
	      
	      createdGroups.show();
	      invitedGroups.show();
	    };
    }
};