var user = firebase.auth().currentUser;

if (user) {
    window.location = 'home.html';
}

else {
    console.log('No User Recorded.')
}