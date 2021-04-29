function googleSignIn() {
    // Google provider object is created here.
    const googleAuth = new firebase.auth.GoogleAuthProvider();
    
    // using the object we will authenticate the user.
    firebase.auth().signInWithPopup(googleAuth);
}

firebase.auth().onAuthStateChanged(function(user) {
    if (user) {
        //User Signed In Do Something Like a Redirect
      window.location = "splash.html";
    }
    else {
      //User not Signed Do Nothing
      //Keep this for future condtions
    }
});