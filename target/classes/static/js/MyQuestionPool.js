var StaffNum;
$(document).ready(function () {
    $("#instructorName").text(getCookie("name"));
    getCourses();
    getQuestionTypes();
    getMyQuestionPool();
});
function getCourses() {
    var option;
    $.ajax({
        url: '/getInstructorCourses',
        type: "GET",
        async:false,
        success: function(data){
            var courses = eval(data);
            StaffNum = courses[0].Instructor;
            for (var i = 0; i < courses.length; i++){
                option = $("<option value='" + courses[i].CourseID + "'>" + courses[i].CourseID + "</option>");
                $("#course").append(option);
            }
        }
    });
}
function getQuestionTypes() {
    var option = "";
    $.ajax({
        url: '/getQuestionType',
        type: "GET",
        async:false,
        success: function (data) {
            var types = eval(data);
            for (var i = 0; i < types.length; i++) {
                option = $("<option value='" + types[i].QTypeID + "'>" + types[i].QType + "</option>");
                $("#questionType").append(option);
            }
        }
    })
}
function getMyQuestionPool(){
    $("#iframe").attr('src','MyQuestionPoolGrid.html?CourseID='+$("#course").val()+'&QTypeID='+$("#questionType").val());
}