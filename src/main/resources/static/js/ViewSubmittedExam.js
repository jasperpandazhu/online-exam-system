$(document).ready(function(){
    $("#instructorName").text(getCookie("name"));
    fillExamFields();
    var QTypeOrder = getCookie("QTypeOrder");
    for ( var i = 0; i < QTypeOrder.length; i++ ){
        var QTypeID = QTypeOrder.substr(i,1);
        $.ajax({
            url: '/viewQuestions',
            type: "GET",
            data: {"QTypeID": QTypeID},
            async: false,
            success: function (data) {
                displayQuestions(QTypeID, data);
            }
        });
    }
    $("#score").text(getCookie("Score"));
    // convert images to base64 once they have been loaded
    imgToBase64();
});
function fillExamFields(){
    var url = new URL(window.location.href);
    $("#examName").val(url.searchParams.get("ExamName"));
    $("#examType").val(url.searchParams.get("ExamType"));
    $("#course").val(url.searchParams.get("Course"));
    $("#startTime").val(url.searchParams.get("StartTime").substr(0, 19));
    $("#endTime").val(url.searchParams.get("EndTime").substr(0,19));
    $("#examTitle").text(url.searchParams.get("ExamName")+" ( ___ / " + getCookie("Score") + " )");
}
jQuery(document).ready(function($) {
    $("button.word-export").click(function(event) {
        $("#displayDiv").wordExport();
    });
});
// convert images to base 64 first
function imgToBase64(){
    $('img').each(function(){
        var src = $(this).attr('src');
        var img = $(this);
        if (src.includes("latex.codecogs.com")) {
            toDataURL(
                'https://cors-anywhere.herokuapp.com/' + src,
                function (dataUrl) {
                    img.attr('src', dataUrl);
                });
        }
    });
}
function toDataURL(src, callback, outputFormat) {
    var img = new Image();
    img.crossOrigin = 'Anonymous';
    img.onload = function() {
        var canvas = document.createElement('CANVAS');
        var ctx = canvas.getContext('2d');
        var dataURL;
        canvas.height = this.naturalHeight;
        canvas.width = this.naturalWidth;
        ctx.drawImage(this, 0, 0);
        dataURL = canvas.toDataURL(outputFormat);
        callback(dataURL);
    };
    img.src = src;
    if (img.complete || img.complete === undefined) {
        img.src = "data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///ywAAAAAAQABAAACAUwAOw==";
        img.src = src;
    }
}
function displayQuestions(QTypeID, data){
    var questions = eval(data);
    switch (QTypeID) {
        case "M":
            var MCQ = $("<div class='p-4'></div>");
            var title = $("<h3></h3>");
            $(MCQ).append(title);
            var totalQTypeScore = 0;
            for ( var i = 0; i < questions.length; i++ ){
                var questionBlock = $("<div class='mt-4'></div>");
                var question = $("<h4 class='d-inline-block mb-3'>Question " + (i+1) + " (" + questions[i].Score + " points):</h4>");
                totalQTypeScore += parseInt(questions[i].Score);
                var choiceA = $("<h5><strong>A:</strong></h5>");
                var choiceB = $("<h5><strong>B:</strong></h5>");
                var choiceC = $("<h5><strong>C:</strong></h5>");
                var choiceD = $("<h5><strong>D:</strong></h5>");
                $(questionBlock)
                    .append(question)
                    .append(questions[i].Question)
                    .append(choiceA)
                    .append(questions[i].ChoiceA)
                    .append(choiceB)
                    .append(questions[i].ChoiceB)
                    .append(choiceC)
                    .append(questions[i].ChoiceC)
                    .append(choiceD)
                    .append(questions[i].ChoiceD);
                $(MCQ).append(questionBlock);
            }
            // var qTypeScore = $("<h3>/<sub>" + totalQTypeScore +  "</sub></h3>");
            $(title).text("___ / " + totalQTypeScore + " Multiple Choice Questions (circle the best answer):");
            $("#displayDiv").append(MCQ);
            break;
        case "T":
            var TFQ = $("<div class='p-4'></div>");
            var title = $("<h3></h3>");
            $(TFQ).append(title);
            var totalQTypeScore = 0;
            for ( var i = 0; i < questions.length; i++ ){
                var questionBlock = $("<div class='mt-4'></div>");
                var question = $("<h4 class='d-inline-block mb-3'>Question " + (i+1) + " (" + questions[i].Score + " points):</h4>");
                totalQTypeScore += parseInt(questions[i].Score);
                // var answerTrue = $("<span class='mr-5'><strong>True</strong></span>");
                // var answerFalse = $("<span class='ml-5'><strong>False</strong></span>");
                var answer = $("<span class='mr-2'>Answer: <strong>T / F</strong></span>");
                $(questionBlock)
                    .append(question)
                    .append(questions[i].Question)
                    .append(answer);
                    // .append(answerTrue).append(answerFalse);
                $(TFQ).append(questionBlock);
            }
            $(title).text("___ / " + totalQTypeScore + " True or False Questions (circle T or F):");
            $("#displayDiv").append(TFQ);
            break;
        case "R":
            var MRQ = $("<div class='p-4'></div>");
            var title = $("<h3></h3>");
            $(MRQ).append(title);
            var totalQTypeScore = 0;
            for ( var i = 0; i < questions.length; i++ ){
                var questionBlock = $("<div class='mt-4'></div>");
                var question = $("<h4 class='d-inline-block mb-3'>Question " + (i+1) + " (" + questions[i].Score + " points):</h4>");
                totalQTypeScore += parseInt(questions[i].Score);
                var choiceA = $("<h5><strong>A:</strong></h5>");
                var choiceB = $("<h5><strong>B:</strong></h5>");
                var choiceC = $("<h5><strong>C:</strong></h5>");
                var choiceD = $("<h5><strong>D:</strong></h5>");
                $(questionBlock)
                    .append(question)
                    .append(questions[i].Question)
                    .append(choiceA)
                    .append(questions[i].ChoiceA)
                    .append(choiceB)
                    .append(questions[i].ChoiceB)
                    .append(choiceC)
                    .append(questions[i].ChoiceC)
                    .append(choiceD)
                    .append(questions[i].ChoiceD);
                $(MRQ).append(questionBlock);
            }
            $(title).text("___ / " + totalQTypeScore + " Multiple Response Questions (circle all that apply):");
            $("#displayDiv").append(MRQ);
            break;
        case "F":
            var FBQ = $("<div class='p-4'></div>");
            var title = $("<h3></h3>");
            $(FBQ).append(title);
            var totalQTypeScore = 0;
            for ( var i = 0; i < questions.length; i++ ){
                var questionBlock = $("<div class='mt-4'></div>");
                var question = $("<h4 class='d-inline-block mb-3'>Question " + (i+1) + " (" + questions[i].Score + " points):</h4>");
                totalQTypeScore += parseInt(questions[i].Score);
                $(questionBlock)
                    .append(question)
                    .append(questions[i].Question);
                $(FBQ).append(questionBlock);
            }
            $(title).text("___ / " + totalQTypeScore + " Fill in the Blank Questions:");
            $("#displayDiv").append(FBQ);
            break;
        case "N":
            var NQ = $("<div class='p-4'></div>");
            var title = $("<h3></h3>");
            $(NQ).append(title);
            var totalQTypeScore = 0;
            for ( var i = 0; i < questions.length; i++ ){
                var questionBlock = $("<div class='mt-4'></div>");
                var question = $("<h4 class='d-inline-block mb-3'>Question " + (i+1) + " (" + questions[i].Score + " points):</h4>");
                totalQTypeScore += parseInt(questions[i].Score);
                var answer = $("<span class='mr-2'>Answer:</span>");
                $(questionBlock)
                    .append(question)
                    .append(questions[i].Question)
                    .append(answer);
                $(NQ).append(questionBlock);
            }
            $(title).text("___ / " + totalQTypeScore + " Number Questions:");
            $("#displayDiv").append(NQ);
            break;
        default:
            break;
    }
}
