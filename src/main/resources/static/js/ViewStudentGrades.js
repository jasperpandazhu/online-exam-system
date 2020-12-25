$(document).ready(function () {
    $("#instructorName").text(getCookie("name"));
    getPastExams();
    getStudentGrades();
});
function getPastExams() {
    var url = new URL(window.location.href);
    var CourseID = url.searchParams.get("CourseID");
    var Year = url.searchParams.get("Year");
    $.ajax({
        url: '/getPastExams',
        type: "GET",
        data:{"CourseID":CourseID, "Year":Year+"%"},
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
function getStudentGrades(){
    $("#iframe").attr('src','StudentGradesGrid.html?ExamID='+$("#examName").val());
}
function getStudentStats(){
    $("#iframe").attr('src','StudentGradesStats.html?ExamID='+$("#examName").val());
}