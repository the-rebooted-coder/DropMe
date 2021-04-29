function googleSignIn() {
    // Google provider object is created here.
    const googleAuth = new firebase.auth.GoogleAuthProvider();
    
    // using the object we will authenticate the user.
    firebase.auth().signInWithPopup(googleAuth);

    if(user) {
        //After successful login, user will be redirected to home.html
        window.location = 'home.html'; 
    }
}