function loginInstructor() {
    $.ajax({
        url: '/loginInstructor',
        type: "POST",
        data: {"username": $("#usernameInstructor").val(), "password": $("#passwordInstructor").val()},
        success: function(data){
            if( data == "error" ){
                $("#errorMsg1").removeClass("d-none");
            }
            else{
                $("#errorMsg1").addClass("d-none");
                window.location.href = data;
            }
        }
    })
}

function loginAdmin() {
    $.ajax({
        url: '/loginAdmin',
        type: "POST",
        data: {"username": $("#usernameAdmin").val(), "password": $("#passwordAdmin").val()},
        success: function(data){
            if( data == "error" ){
                $("#errorMsg2").removeClass("d-none");
            }
            else{
                $("#errorMsg2").addClass("d-none");
                window.location.href = data;
            }
        }
    })
}

function loginStudent() {
    $.ajax({
        url: '/loginStudent',
        type: "POST",
        data: {"username": $("#usernameStudent").val(), "password": $("#passwordStudent").val()},
        success: function(data){
            if( data == "error" ){
                $("#errorMsg").removeClass("d-none");
            }
            else{
                $("#errorMsg").addClass("d-none");
                window.location.href = data;
            }
        }
    })
}