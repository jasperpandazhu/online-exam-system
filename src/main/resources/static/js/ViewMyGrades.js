$(document).ready(function () {
    $("#studentName").text(getCookie("name"));
    getMyExams();
    getMyGrades();
});
function getMyExams() {
    var url = new URL(window.location.href);
    var Year = url.searchParams.get("Year");
    $.ajax({
        url: '/getMyExams',
        type: "GET",
        data: {"Year":Year+"%"},
        async:false,
        success: function(data){
            var exams = eval(data);
            for (var i = 0; i < exams.length; i++){
                var option = $("<option value='" + exams[i].ExamID + "'>" + exams[i].ExamName + "</option>");
                $("#examName").append(option);
            }
        }
    });
}
function getMyGrades() {
    $.ajax({
        url: '/getExamInformation',
        type: "GET",
        data: {"ExamID":$("#examName").val()},
        success: function(data){
            var examInfo = eval(data);
            $("#name").val(examInfo[0].ExamName);
            $("#course").val(examInfo[0].Course);
            $("#examType").val(examInfo[0].ExamType);
            $("#fullTotalScore").val(examInfo[0].Score)
        }
    });
    $("#MScoreRow").addClass("d-none");
    $("#TScoreRow").addClass("d-none");
    $("#RScoreRow").addClass("d-none");
    $("#FScoreRow").addClass("d-none");
    $("#NScoreRow").addClass("d-none");
    $("#fullMScore").val("0");
    $("#fullTScore").val("0");
    $("#fullRScore").val("0");
    $("#fullFScore").val("0");
    $("#fullNScore").val("0");
    $.ajax({
        url: '/getQTypeFullMarks',
        type: "GET",
        data: {"ExamID":$("#examName").val()},
        success: function(data){
            var examInfo = eval(data);
            for( var i = 0; i < examInfo.length; i++ ){
                if ( examInfo[i].QSumScore != 0 ){
                    switch (examInfo[i].QTypeID) {
                        case "M":
                            $("#fullMScore").val(examInfo[i].QSumScore);
                            $("#MScoreRow").removeClass("d-none");
                            break;
                        case "T":
                            $("#fullTScore").val(examInfo[i].QSumScore);
                            $("#TScoreRow").removeClass("d-none");
                            break;
                        case "R":
                            $("#fullRScore").val(examInfo[i].QSumScore);
                            $("#RScoreRow").removeClass("d-none");
                            break;
                        case "F":
                            $("#fullFScore").val(examInfo[i].QSumScore);
                            $("#FScoreRow").removeClass("d-none");
                            break;
                        case "N":
                            $("#fullNScore").val(examInfo[i].QSumScore);
                            $("#NScoreRow").removeClass("d-none");
                            break;
                    }
                }
            }
        }
    });
    $.ajax({
        url: '/getMyMarks',
        type: "GET",
        data: {"ExamID":$("#examName").val()},
        success: function(data){
            var examInfo = eval(data);
            $("#MScore").val(examInfo[0].MScore);
            $("#TScore").val(examInfo[0].TScore);
            $("#RScore").val(examInfo[0].RScore);
            $("#FScore").val(examInfo[0].FScore);
            $("#NScore").val(examInfo[0].NScore);
            $("#totalScore").val(examInfo[0].Score);
        }
    })
}