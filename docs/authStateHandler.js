firebase.auth().onAuthStateChanged(function(user) {
    if (user) {
        //User Signed In Do Something Like a Redirect
      window.location = "home.html";
    }
    else {
        //not signed in
    }
});