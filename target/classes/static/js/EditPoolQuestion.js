var url = new URL(window.location.href);
var QTypeID = url.searchParams.get("QTypeID");
var CourseID = url.searchParams.get("CourseID");
var id = url.searchParams.get("id");
// send data over ajax to put into database
function editPoolQuestion(){
    // var QTypeID = url.searchParams.get("QTypeID");
    // var CourseID = url.searchParams.get("CourseID");
    // var id = url.searchParams.get("id");
    var data;
    switch (QTypeID) {
        case "M":
            data = {
                "QTypeID":QTypeID,
                "CourseID":CourseID,
                "id":id,
                "Difficulty":$('input[name=difficulty]:checked').val(),
                "Score":$("#score").val(),
                "Question":CKEDITOR.instances['question'].getData(),
                "ChoiceA":CKEDITOR.instances['choiceA'].getData(),
                "ChoiceB":CKEDITOR.instances['choiceB'].getData(),
                "ChoiceC":CKEDITOR.instances['choiceC'].getData(),
                "ChoiceD":CKEDITOR.instances['choiceD'].getData(),
                "Answer":$('input[name=answer]:checked').val()
            };
            break;
        case "T":
            data = {
                "QTypeID":QTypeID,
                "CourseID":CourseID,
                "id":id,
                "Difficulty":$('input[name=difficulty]:checked').val(),
                "Score":$("#score").val(),
                "Question":CKEDITOR.instances['question'].getData(),
                "Answer":$('input[name=answer]:checked').val()
            };
            break;
        case "R":
            data = {
                "QTypeID":QTypeID,
                "CourseID":CourseID,
                "id":id,
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
                "Answer4":$("#answer4").is(':checked')
            };
            break;
        case "F":
            data = {
                "QTypeID":QTypeID,
                "CourseID":CourseID,
                "id":id,
                "Difficulty":$('input[name=difficulty]:checked').val(),
                "Score":$("#score").val(),
                "Question":CKEDITOR.instances['question'].getData(),
                "Answer1":$("#answer1").val(),
                "Answer2":$("#answer2").val(),
                "Answer3":$("#answer3").val(),
                "Answer4":$("#answer4").val()
            };
            break;
        case "N":
            data = {
                "QTypeID":QTypeID,
                "CourseID":CourseID,
                "id":id,
                "Difficulty":$('input[name=difficulty]:checked').val(),
                "Score":$("#score").val(),
                "Question":CKEDITOR.instances['question'].getData(),
                "Answer":$("#number").val()
            };
            break;
        default:
            break;
    }
    $.ajax({
        url: '/editPoolQuestion',
        type: "POST",
        data: data,
        async: false,
        success: function(data){
            window.location.href = data;
        }
    })
}
function loadPoolQuestion(QTypeID, CourseID, id){
    $.ajax({
        url: '/loadPoolQuestion',
        type: "GET",
        data: {
            "QTypeID":QTypeID,
            "CourseID":CourseID,
            "id":id},
        success: function(data){
            var qInfo = eval(data);
            loadCommon(qInfo[0]);
            switch (QTypeID) {
                case "M":
                    loadMCQ(qInfo[0]);
                    break;
                case "T":
                    loadTFQ(qInfo[0]);
                    break;
                case "R":
                    loadMRQ(qInfo[0]);
                    break;
                case "F":
                    loadFBQ(qInfo[0]);
                    break;
                case "N":
                    loadNQ(qInfo[0]);
                    break;
            }
        }
    });
}
function confirmReturnDialog(){
    $( "#confirmReturn" ).dialog({
        autoOpen: false,
        resizable: false,
        height: "auto",
        width: 400,
        modal: true,
        show: {
            effect: "fade",
            duration: 500
        },
        hide: {
            effect: "fade",
            duration: 500
        },
        buttons: {
            "Return": function() {
                window.location.href="MyQuestionPoolGrid.html?CourseID="+CourseID+"&QTypeID="+QTypeID;
            },
            Cancel: function() {
                $( this ).dialog( "close" );
            }
        }
    });
}
function goBack() {
    $( "#confirmReturn" ).dialog( "open" );
}