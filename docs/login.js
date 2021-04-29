function googleSignIn() {
    // Google provider object is created here.
    const googleAuth = new firebase.auth.GoogleAuthProvider();
    
    // using the object we will authenticate the user.
    firebase.auth().signInWithPopup(googleAuth);
}