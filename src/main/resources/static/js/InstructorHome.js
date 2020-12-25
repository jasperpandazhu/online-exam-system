var submitted = 0;
var toGrade = 0;
$(document).ready(function () {
    $("#instructorName").text(getCookie("name"));
    $.ajax({
        url: '/getInstructorExams',
        type:"GET",
        success: function(data){
            var exams = eval(data);
            var option;
            var now = new Date();
            for ( var i = 0; i < exams.length; i++){
                if ( now > Date.parse(exams[i].EndTime) && exams[i].Submitted == 'Y' && exams[i].Graded == 'N' ){
                    option = $("<option value='" + exams[i].ExamName + "'>" + exams[i].ExamName + "</option>");
                    $("#gradeExamSelect").append(option);
                    toGrade += 1;
                }
                else if ( now < Date.parse(exams[i].EndTime) && exams[i].Submitted == 'Y'  ){
                    option = $("<option value='" + exams[i].ExamName + "'>" + exams[i].ExamName + "</option>");
                    $("#viewExamSelect").append(option);
                    submitted += 1;
                }
                else if ( now < Date.parse(exams[i].EndTime) && exams[i].Submitted == 'N' ){
                    option = $("<option value='" + exams[i].ExamName + "'>" + exams[i].ExamName + "</option>");
                    $("#createExamSelect").append(option);
                }
            }
        }
    });
    $.ajax({
        url: '/getInstructorCourses',
        type: "GET",
        async:false,
        success: function(data){
            var courses = eval(data);
            for (var i = 0; i < courses.length; i++){
                var option = $("<option value='" + courses[i].CourseID + "'>" + courses[i].CourseID + "</option>");
                $("#viewGradesSelect").append(option);
            }
            var currYear = new Date().getFullYear();
            $("#year").val(currYear);
        }
    });
});
$(document).on("click", ".sides", function(){
    $(this).addClass("sidesFlip")
})
    .on("mouseleave", ".sides", function(){
        $(this).removeClass("sidesFlip")
    });
function checkGradeSelection() {
    if (toGrade == 0) {
        alert("There are no exams to grade.");
        $("#gradeForm").attr("action", "#");
    }
}
function checkSubmittedSelection() {
    if (submitted == 0){
        alert("There are no submitted exams.");
        $("#submittedForm").attr("action","#");
    }
}