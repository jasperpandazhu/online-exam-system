var ExamID;
var QTypeOrder;
$(document).ready(function () {
    $("#studentName").text(getCookie("name"));
    var url = new URL(window.location.href);
    $.ajax({
        url: '/getCurrentExam',
        type:"GET",
        data: {"Course": url.searchParams.get("Course")},
        success: function(data){
            if ( data == "" ){
                $("#noExam").removeClass("text-hide");
            }
            else {
                var info = eval(data);
                ExamID = info[0].ExamID;
                QTypeOrder = info[0].QTypeOrder;
                $("#examInfo").removeClass("d-none").addClass("d-flex");
                $("#examName").val(info[0].ExamName);
                $("#examType").val(info[0].ExamType);
                $("#course").val(info[0].Course);
                $("#startTime").val(info[0].StartTime.substr(0,19));
                $("#endTime").val(info[0].EndTime.substr(0,19));
                $("#score").val(info[0].Score);
                for ( var i = 0; i < QTypeOrder.length; i++ ){
                    var label1 = $("<label class='control-label mr-2 col-5 p-0 justify-content-start' for='MRQNumber'>Multiple Response Questions:</label>");
                    var questions = $("<input class='form-control mr-3 col-2' id='MRQNumber' type='text' style='width: 50px' readonly>");
                    var label2 = $("<label class='control-label mr-2 col-2 p-0' for='MRQTotal'>Total Marks:</label>");
                    var score = $("<input class='form-control col-2' id='MRQTotal' type='text' style='width: 50px' readonly>");
                    switch (QTypeOrder.substr(i,1)) {
                        case "M":
                            label1.attr('for','numberMCQ').text("Multiple Choice Questions:");
                            questions.attr('id','numberMCQ');
                            label2.attr('for','totalMCQ').text("Total Marks:");
                            score.attr('id','totalMCQ');
                            break;
                        case "T":
                            label1.attr('for','numberTFQ').text("True or False Questions:");
                            questions.attr('id','numberTFQ');
                            label2.attr('for','totalTFQ').text("Total Marks:");
                            score.attr('id','totalTFQ');
                            break;
                        case "R":
                            label1.attr('for','numberMRQ').text("Multiple Response Questions:");
                            questions.attr('id','numberMRQ');
                            label2.attr('for','totalMRQ').text("Total Marks:");
                            score.attr('id','totalMRQ');
                            break;
                        case "F":
                            label1.attr('for','numberFBQ').text("Fill in the Blank Questions:");
                            questions.attr('id','numberFBQ');
                            label2.attr('for','totalFBQ').text("Total Marks:");
                            score.attr('id','totalFBQ');
                            break;
                        case "N":
                            label1.attr('for','numberNQ').text("Number Questions:");
                            questions.attr('id','numberNQ');
                            label2.attr('for','totalNQ').text("Total Marks:");
                            score.attr('id','totalNQ');
                            break;
                    }
                    $.ajax({
                        url: '/getQuestionTypeInfo',
                        type: "GET",
                        data: {"QTypeID":QTypeOrder.substr(i,1)},
                        async: false,
                        success: function(data){
                            var qTypeInfo = eval(data);
                            var number = qTypeInfo[0].Number;
                            var total = qTypeInfo[0].Total;
                            questions.val(number);
                            score.val(total);
                        }
                    });
                    var div = $("<div class='form-group form-inline w-100'></div>");
                    $(div).append(label1).append(questions).append(label2).append(score);
                    $("#divRight").append(div);
                }
            }
        }
    });
});
function startExam(){
    $.ajax({
        url: '/createAnswers',
        type: "POST",
        success: function (data) {
            window.location.href=data;
        }
    })
}