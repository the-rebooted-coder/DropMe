function googleSignIn() {
    // Google provider object is created here.
    const googleAuth = new firebase.auth.GoogleAuthProvider();
    
    // using the object we will authenticate the user.
    firebase.auth().signInWithPopup(googleAuth);

    var user = firebase.auth().currentUser;

    if (user) {
        window.location = 'home.html';
    }

    else {
        console.log('No User Recorded.');
    }
}