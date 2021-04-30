function signOut() {
    firebase.auth().signOut()
    .then(function() {
        console.log('Signout Succesfull')
        window.location = 'index.html';
    }, function(error) {
        console.log('Signout Failed')  
    });
}