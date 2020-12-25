var url = new URL(window.location.href);
$(document).ready(function(){
    $("#studentName").text(getCookie("name"));
    $("#examName").text(url.searchParams.get("ExamName"));
    $('[data-toggle="tooltip"]').tooltip();
    setCountdown();
    setQTypes();
});
function setCountdown(){
    var endTime;
    $.ajax({
        url: '/getEndTime',
        type: "GET",
        async: false,
        success: function (data) {
            endTime = data.substr(0,19)
        }
    });
    $('#countdown').timeTo(new Date(endTime), function () {
        submitExam();
    });
}
function toggleTime(){
    $("#countdown").toggleClass("d-none");
}
function setQTypes(){
    var qTypeOrder = getCookie("QTypeOrder");
    for ( var i = 0; i < qTypeOrder.length; i++ ){
        var qType = qTypeOrder.substr(i,1);
        var btn = $("<button class='btn btn-info rounded-circle typeBtn' data-toggle='tooltip' data-placement='top' title='' data-original-title='" + getType(qType) + "' id='" + qType + "' onclick='getQuestions(this.id,1)'>" + qTypeOrder.substr(i,1) + "</button>");
        $(qTypes).append(btn);
    }
    getQuestions(qTypeOrder.substr(0,1),1);
}
function getType(id) {
    switch (id) {
        case "M":
            return "Multiple Choice Questions";
        case "T":
            return "True or False Questions";
        case "R":
            return "Multiple Response Questions";
        case "F":
            return "Fill in the Blank Questions";
        case "N":
            return "Number Questions";
    }
}
function switchAnswers(QTypeID){
    switch (QTypeID) {
        case "M":
            $("#MCA").removeClass("d-none");
            $("#TFA").addClass("d-none");
            $("#MRA").addClass("d-none");
            $("#FBA").addClass("d-none");
            $("#NA").addClass("d-none");
            break;
        case "T":
            $("#MCA").addClass("d-none");
            $("#TFA").removeClass("d-none");
            $("#MRA").addClass("d-none");
            $("#FBA").addClass("d-none");
            $("#NA").addClass("d-none");
            break;
        case "R":
            $("#MCA").addClass("d-none");
            $("#TFA").addClass("d-none");
            $("#MRA").removeClass("d-none");
            $("#FBA").addClass("d-none");
            $("#NA").addClass("d-none");
            break;
        case "F":
            $("#MCA").addClass("d-none");
            $("#TFA").addClass("d-none");
            $("#MRA").addClass("d-none");
            $("#FBA").removeClass("d-none");
            $("#NA").addClass("d-none");
            break;
        case "N":
            $("#MCA").addClass("d-none");
            $("#TFA").addClass("d-none");
            $("#MRA").addClass("d-none");
            $("#FBA").addClass("d-none");
            $("#NA").removeClass("d-none");
            break;
    }
}
function getQuestions(id,qNum) {
    switchAnswers(id);
    $.ajax({
        url: '/getQTypeScore',
        type: "GET",
        data: {"QTypeID":id},
        success: function(data){
            $("#typeTitle").text(getType(id));
            $("#score").text("Marks: " + data);
        }
    });
    $.ajax({
        url: '/getQuestionsRandom',
        type: "GET",
        data: {"QTypeID":id},
        success: function(data){
            var info = eval(data);
            $("#type").remove();
            $("#questions").empty();
            for ( var i = 1; i <= info.length; i++ ){
                if ( info[i-1].Answered == 0 ){
                    var btn = $("<button class='btn btn-warning rounded-circle my-1 ml-2 mr-1 qNumBtn' id='" + id + i + "' value='" + id + info[i-1].QOrder + "' onclick='loadQuestion(this.id,this.value)'>" + i + "</button>");
                }
                else {
                    var btn = $("<button class='btn btn-danger rounded-circle my-1 ml-2 mr-1 qNumBtn' id='" + id + i + "' value='" + id + info[i-1].QOrder + "' onclick='loadQuestion(this.id,this.value)'>" + i + "</button>");
                }
                $("#questions").append(btn);
            }
            loadQuestion(id+qNum,id+info[qNum-1].QOrder);
        }
    });
}
function loadQuestion(id,value) {
    $("#viewDiv").empty();
    var QTypeID = id.substr(0,1);
    var QNum = id.substr(1,id.length-1);
    var QOrder = value.substr(1,value.length-1);
    setCookie("QTypeID", QTypeID);
    setCookie("QOrder", QOrder);
    setCookie("QNum",QNum);
    $.ajax({
        url: '/loadExamQuestion',
        type: "GET",
        async:false,
        success: function(data) {
            var questions = eval(data);
            if ( QTypeID == "M" || QTypeID == "R" ) {
                var questionBlock = $("<div class='w-75' id='questionBlock'></div>");
                var question = $("<h4 class='mt-3 mb-2'>Question " + QNum + " (" + questions[0].Score + " marks):</h4>");
                var centerViewDiv = $("<div id='centerViewDiv' class='overflow-auto d-flex flex-column align-items-center justify-content-center' style='height: 85%'></div>")
                var choiceA = $("<h5><strong>A:</strong></h5>");
                var choiceB = $("<h5><strong>B:</strong></h5>");
                var choiceC = $("<h5><strong>C:</strong></h5>");
                var choiceD = $("<h5><strong>D:</strong></h5>");
                $(questionBlock)
                    .append(questions[0].Question)
                    .append(choiceA)
                    .append(questions[0].ChoiceA)
                    .append(choiceB)
                    .append(questions[0].ChoiceB)
                    .append(choiceC)
                    .append(questions[0].ChoiceC)
                    .append(choiceD)
                    .append(questions[0].ChoiceD);
                $(centerViewDiv).append(questionBlock);
                $("#viewDiv").append(question).append(centerViewDiv);
            } else {
                var questionBlock = $("<div id='questionBlock'></div>");
                var question = $("<h4 class='mt-3 mb-2'>Question " + QNum + " (" + questions[0].Score + " marks):</h4>");
                var centerViewDiv = $("<div id='centerViewDiv' class='overflow-auto d-flex flex-column align-items-center justify-content-center' style='height: 85%'></div>");
                $(questionBlock)
                    .append(questions[0].Question);
                $(centerViewDiv).append(questionBlock);
                $("#viewDiv").append(question).append(centerViewDiv);
            }
        }
    });
    loadAnswer(QTypeID,QOrder);
}
function previousQuestion(){
    answerQuestion();
    var QNum = getCookie("QNum");
    var QTypeID = getCookie("QTypeID");
    var QTypeOrder = getCookie("QTypeOrder");
    if ( QNum == 1 ){
        if ( QTypeOrder.substr(0,1) == QTypeID ){
            alert("This is the first question");
        }
        else {
            QTypeID = QTypeOrder.substr(QTypeOrder.indexOf(QTypeID)-1,1);
            $.ajax({
                url: '/getQuestionTypeInfo',
                type: "GET",
                data: {"QTypeID":QTypeID},
                async: false,
                success: function(data){
                    var info = eval(data);
                    var lastQNum = info[0].Number;
                    getQuestions(QTypeID,lastQNum);
                }
            });
        }
    }
    else {
        QNum = parseInt(QNum-1);
        $("#"+QTypeID+QNum).click();
    }
}
function nextQuestion(){
    answerQuestion();
    var QNum = getCookie("QNum");
    var QTypeID = getCookie("QTypeID");
    var QTypeOrder = getCookie("QTypeOrder");
    var lastQNum=1;
    $.ajax({
        url: '/getQuestionTypeInfo',
        type: "GET",
        data: {"QTypeID":QTypeID},
        async: false,
        success: function(data){
            var info = eval(data);
            lastQNum = info[0].Number;
        }
    });
    if ( QNum == lastQNum ){
        if ( QTypeOrder.substr(QTypeOrder.length-1,1) == QTypeID ){
            alert("This is the last question");
        }
        else {
            QTypeID = QTypeOrder.substr(QTypeOrder.indexOf(QTypeID)+1,1);
            getQuestions(QTypeID,1);
        }
    }
    else {
        QNum = parseInt(QNum)+1;
        $("#"+QTypeID+QNum).click();
    }
}
function answerQuestion() {
    if ( $(".check").is(':checked') || $("#stuNumber").val() != "" || $("#stuAnswer1").val() != "" ){
        var QTypeID = getCookie("QTypeID");
        var QOrder = getCookie("QOrder");
        var data;
        switch (QTypeID) {
            case "M":
                data = {
                    "QTypeID":QTypeID,
                    "QOrder":QOrder,
                    "Answer":$('input[name=choice]:checked').val()
                };
                break;
            case "T":
                data = {
                    "QTypeID":QTypeID,
                    "QOrder":QOrder,
                    "Answer":$('input[name=answer]:checked').val()
                };
                break;
            case "R":
                data = {
                    "QTypeID":QTypeID,
                    "QOrder":QOrder,
                    "Answer1":$("#choiceA").is(':checked'),
                    "Answer2":$("#choiceB").is(':checked'),
                    "Answer3":$("#choiceC").is(':checked'),
                    "Answer4":$("#choiceD").is(':checked')
                };
                break;
            case "F":
                data = {
                    "QTypeID":QTypeID,
                    "QOrder":QOrder,
                    "Answer1":$("#stuAnswer1").val(),
                    "Answer2":$("#stuAnswer2").val(),
                    "Answer3":$("#stuAnswer3").val(),
                    "Answer4":$("#stuAnswer4").val()
                };
                break;
            case "N":
                data = {
                    "QTypeID":QTypeID,
                    "QOrder":QOrder,
                    "Answer":$("#stuNumber").val()
                };
                break;
        }
        $.ajax({
            url: '/answerQuestion',
            type: "POST",
            data: data,
            async:false,
            success: function () {
                var QTypeID = getCookie("QTypeID");
                var QNum = getCookie("QNum");
                $("#"+QTypeID+QNum).removeClass("btn-warning").addClass("btn-danger");
                $("#bigDiv").fadeOut(100).fadeIn(100);
            }
        })
    }
}
function loadAnswer(QTypeID,QOrder){
    $(".check").prop("checked", false);
    $(".text").val("");
    $.ajax({
        url: '/loadAnswer',
        type: "GET",
        data: {"QTypeID":QTypeID, "QOrder":QOrder},
        async: false,
        success: function (data) {
            var answer = eval(data);
            if ( answer[0].Answered ){
                switch (QTypeID) {
                    case "M":
                        $("#"+answer[0].Answer).prop("checked", true);
                        break;
                    case "T":
                        $("#"+answer[0].Answer).prop("checked", true);
                        break;
                    case "R":
                        $("#choiceA").prop("checked", answer[0].Answer1);
                        $("#choiceB").prop("checked", answer[0].Answer2);
                        $("#choiceC").prop("checked", answer[0].Answer3);
                        $("#choiceD").prop("checked", answer[0].Answer4);
                        break;
                    case "F":
                        $("#stuAnswer1").val(answer[0].Answer1);
                        $("#stuAnswer2").val(answer[0].Answer2);
                        $("#stuAnswer3").val(answer[0].Answer3);
                        $("#stuAnswer4").val(answer[0].Answer4);
                        break;
                    case "N":
                        $("#stuNumber").val(answer[0].Answer);
                        break;
                }
            }
        }
    })
}
$( function() {
    $( "#confirmSubmit" ).dialog({
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
            "Submit": submitExam,
            Cancel: function() {
                $( this ).dialog( "close" );
            }
        }
    });
} );
function confirmSubmitExam() {
    $( "#confirmSubmit" ).dialog( "open" );
}
function submitExam() {
    answerQuestion();
    $.ajax({
        url: '/studentSubmitExam',
        type: "POST",
        success: function(data){
            deleteCookie("name");
            window.location.href=data;
        }
    })
}