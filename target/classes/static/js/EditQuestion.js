// load the settings that all question types have in common
function loadCommon(data) {
    switch (data.Difficulty) {
        case 1:
            $("#easy").prop("checked", true);
            break;
        case 2:
            $("#normal").prop("checked", true);
            break;
        case 3:
            $("#hard").prop("checked", true);
            break;
    }
    $("#score").val(data.Score);
    if (data.QPool == true){
        $("#pool").prop("checked", true);
    }
    else{
        $("#pool").prop("checked", false);
    }
    $("#question").html(data.Question);

}
// load multiple choice specific settings
function loadMCQ(data){
    switch (data.Answer) {
        case "A":
            $("#A").prop("checked", true);
            break;
        case "B":
            $("#B").prop("checked", true);
            break;
        case "C":
            $("#C").prop("checked", true);
            break;
        case "D":
            $("#D").prop("checked", true);
            break;
    }
    $("#choiceA").html(data.ChoiceA);
    $("#choiceB").html(data.ChoiceB);
    $("#choiceC").html(data.ChoiceC);
    $("#choiceD").html(data.ChoiceD);
}
// load true or false specific settings
function loadTFQ(data){
    if (data.Answer == true) {
        $("#T").prop("checked", true);
    }
    if (data.Answer == false) {
        $("#F").prop("checked", true);
    }
}
// load multiple response specific settings
function loadMRQ(data){
    if (data.Answer1 == true) {
        $("#answer1").prop("checked", true);
    }
    if (data.Answer2 == true) {
        $("#answer2").prop("checked", true);
    }
    if (data.Answer3 == true) {
        $("#answer3").prop("checked", true);
    }
    if (data.Answer4 == true) {
        $("#answer4").prop("checked", true);
    }
    $("#choiceA").html(data.ChoiceA);
    $("#choiceB").html(data.ChoiceB);
    $("#choiceC").html(data.ChoiceC);
    $("#choiceD").html(data.ChoiceD);
}
// load fill in the blank specific settings
function loadFBQ(data){
    $("#answer1").val(data.Answer1);
    $("#answer2").val(data.Answer2);
    $("#answer3").val(data.Answer3);
    $("#answer4").val(data.Answer4);
}
// load number type specific settings
function loadNQ(data){
    $("#number").val(data.Answer);
}
// send data over ajax to put into database
function editQuestion(){
    // // convert images to base 64
    // imgToBase64();
    var QTypeID = getCookie("QTypeID");
    var data;
    if( CKEDITOR.instances['question'].getData() !="" && $("#score").val() !="" ){
        switch (QTypeID) {
            case "M":
                if(!($("#A").is(':checked') || $("#B").is(':checked') || $("#C").is(':checked') ||$("#D").is(':checked'))){
                    alert("Answer has not been set. This question will not be saved.");
                    return;
                }
                data = {
                    "Difficulty":$('input[name=difficulty]:checked').val(),
                    "Score":$("#score").val(),
                    "Question":CKEDITOR.instances['question'].getData(),
                    "ChoiceA":CKEDITOR.instances['choiceA'].getData(),
                    "ChoiceB":CKEDITOR.instances['choiceB'].getData(),
                    "ChoiceC":CKEDITOR.instances['choiceC'].getData(),
                    "ChoiceD":CKEDITOR.instances['choiceD'].getData(),
                    "Answer":$('input[name=answer]:checked').val(),
                    "QPool":$("#pool").is(':checked')
                };
                break;
            case "T":
                if(!($("#T").is(':checked') || $("#F").is(':checked'))){
                    alert("Answer has not been set. This question will not be saved.");
                    return;
                }
                data = {
                    "Difficulty":$('input[name=difficulty]:checked').val(),
                    "Score":$("#score").val(),
                    "Question":CKEDITOR.instances['question'].getData(),
                    "Answer":$('input[name=answer]:checked').val(),
                    "QPool":$("#pool").is(':checked')
                };
                break;
            case "R":
                if(!($("#answer1").is(':checked') || $("#answer2").is(':checked') || $("#answer3").is(':checked') ||$("#answer4").is(':checked'))){
                    alert("Answer has not been set. This question will not be saved.");
                    return;
                }
                data = {
                    "Difficulty":$('input[name=difficulty]:checked').val(),
                    "Score":$("#score").val(),
                    "Question":CKEDITOR.instances['question'].getData(),
                    "ChoiceA":CKEDITOR.instances['choiceA'].getData(),
                    "ChoiceB":CKEDITOR.instances['choiceB'].getData(),
                    "ChoiceC":CKEDITOR.instances['choiceC'].getData(),
                    "ChoiceD":CKEDITOR.instances['choiceD'].getData(),
                    "Answer1":$("#answer1").is(':checked'),
                    "Answer2":$("#answer2").is(':checked'),
                    "Answer3":$("#answer3").is(':checked'),
                    "Answer4":$("#answer4").is(':checked'),
                    "QPool":$("#pool").is(':checked')
                };
                break;
            case "F":
                data = {
                    "Difficulty":$('input[name=difficulty]:checked').val(),
                    "Score":$("#score").val(),
                    "Question":CKEDITOR.instances['question'].getData(),
                    "Answer1":$("#answer1").val(),
                    "Answer2":$("#answer2").val(),
                    "Answer3":$("#answer3").val(),
                    "Answer4":$("#answer4").val(),
                    "QPool":$("#pool").is(':checked')
                };
                break;
            case "N":
                if ($("#number").val() == ""){
                    alert("Answer has not been set. This question will not be saved.");
                    return;
                }
                data = {
                    "Difficulty":$('input[name=difficulty]:checked').val(),
                    "Score":$("#score").val(),
                    "Question":CKEDITOR.instances['question'].getData(),
                    "Answer":$("#number").val(),
                    "QPool":$("#pool").is(':checked')
                };
                break;
            default:
                break;
        }
        $.ajax({
            url: '/editQuestion',
            type: "POST",
            data: data,
            async: false,
            success: function(){
                location.reload();
            }
        })

    }
    else {
        alert("Question or score has not been set. This question will not be saved.");
    }
}

function returnToLayout() {
    editQuestion();
    window.location.href = "/ExamLayout.html";
}
function goToPrevious() {
    var QNum = getCookie("QNum");
    if (QNum == 1){
        alert("This is the first question.")
    }
    else {
        editQuestion();
        setCookie("QNum", parseInt(QNum)-1);
        location.reload();
    }
}
function goToNext() {
    var QTypeID = getCookie("QTypeID");
    var totalQNum = getCookie("total" + QTypeID + "Questions");
    var QNum = getCookie("QNum");
    if (QNum == totalQNum){
        alert("This is the last question.")
    }
    else {
        editQuestion();
        setCookie("QNum", parseInt(QNum)+1);
        location.reload();
    }
}
function insertDeleteQuestion(id) {
    if (id == "insert"){
        var data = {"oper":"insert"}
    }
    else {
        var data = {"oper":"delete"}
    }
    $.ajax({
        url: '/insertDeleteQuestion',
        type: "POST",
        data: data,
        success: function(){
            if ( id == "delete" ) {
                var QNum = getCookie("QNum");
                if ( QNum > 1 ) {
                    setCookie("QNum", parseInt(QNum)-1);
                }
                var QTypeID = getCookie("QTypeID");
                var totalQNum = getCookie("total" + QTypeID + "Questions");
                if ( totalQNum == 0 ){
                    window.location.href="/ExamLayout.html";
                }
                else {
                    location.reload();
                }
            }
            else{ //id == "insert"
                var QNum = getCookie("QNum");
                setCookie("QNum", parseInt(QNum)+1);
                location.reload();
            }
        }
    })
}