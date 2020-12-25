$(document).ready(function () {
    $("#studentName").text(getCookie("name"));
    $.ajax({
        url: '/getStudentCourses',
        type:"GET",
        success: function(data){
            var courses = eval(data);
            var option;
            var currentCourses = courses[0].Courses.split(",");
            for ( var i = 0; i < currentCourses.length; i++){
                option = $("<option value='" + currentCourses[i] + "'>" + currentCourses[i] + "</option>");
                $("#currentCourse").append(option);
            }
        }
    });
    var currYear = new Date().getFullYear();
    $("#year").val(currYear);
});
$(document).on("click", ".sides", function(){
    $(this).addClass("sidesFlip")
})
    .on("mouseleave", ".sides", function(){
        $(this).removeClass("sidesFlip")
    });