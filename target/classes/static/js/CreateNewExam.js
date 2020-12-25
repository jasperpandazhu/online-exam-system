var StaffNum;
$(document).ready(function () {
    $("#instructorName").text(getCookie("name"));
    getExamTypes();
    getCourses();
});

function getExamTypes() {
    var option = "";
    $.ajax({
        url: '/getExamType',
        type: "GET",
        success: function (data) {
            var types = eval(data);
            var now = new Date();
            for (var i = 0; i < types.length; i++) {
                if (now < Date.parse(types[i].EndDate)) {
                    option = $("<option value='" + types[i].ExamID + "'>" + types[i].ExamID + "</option>");
                    $("#examType").append(option);
                }
            }
        }
    })
}
function getCourses() {
    var option;
    $.ajax({
        url: '/getInstructorCourses',
        type: "GET",
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
// datepicker settings
$( function() {
    $("#startTime").datetimepicker({
        dateFormat: 'yy-mm-dd',
        timeFormat: 'HH:mm:ss',
        changeYear: true,
        changeMonth: true,
        showOtherMonths: true,
        selectOtherMonths: true,
        hourGrid: 4,
        minuteGrid: 10,
        secondGrid: 10
    });

    $("#endTime").datetimepicker({
        dateFormat: 'yy-mm-dd',
        timeFormat: 'HH:mm:ss',
        changeYear: true,
        changeMonth: true,
        showOtherMonths: true,
        selectOtherMonths: true,
        hourGrid: 4,
        minuteGrid: 10,
        secondGrid: 10
    });
});

function createNewExam() {
    if( $("#examName").val() == "" ){
        alert("Please name the exam.")
    }
    else if( $("#startTime").val() == "" ){
        alert("Please choose a start time.")
    }
    else if( $("#endTime").val() == "" ){
        alert("Please choose an end time.")
    }
    else {
        $.ajax({
            url: '/createNewExam',
            type: "POST",
            data: {
                "oper":"add",
                "ExamName":$("#examName").val(),
                "ExamType":$("#examType").val(),
                "Course":$("#course").val(),
                "StaffNum":StaffNum,
                "StartTime":$("#startTime").val(),
                "EndTime":$("#endTime").val(),
                "QTypeOrder":"",
                "Submitted":"N",
                "Graded":"0"
            },
            success: function(data){
                window.location.href = data;
            }
        });
    }
}