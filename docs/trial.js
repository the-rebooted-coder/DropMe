var rootref = firebase.database().ref().child("users");

rootref.on("child_added", snap =>{
    firebase.auth().onAuthStateChanged(user => {
        if (user) {
            var image = snap.child(user.uid).val();
            $("#gallery_div").append("<img src=" + image + "></img>");
        }
    })
    

    
});