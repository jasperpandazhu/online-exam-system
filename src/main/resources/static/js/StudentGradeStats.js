var url = new URL(window.location.href);
var ExamID = url.searchParams.get("ExamID");
var qTypeOrder, fullScore, count;
$(document).ready(function(){
    $.ajax({
        url: '/getScoreAndOrder',
        type:"GET",
        data: {"ExamID":ExamID},
        async: false,
        success: function(data) {
            var info = eval(data);
            qTypeOrder = info[0].QTypeOrder;
            fullScore = info[0].Score;
        }
    });
    $.ajax({
        url: '/getExamStats',
        type: "GET",
        data: {"ExamID":ExamID},
        async: false,
        success: function(data){
            var stats = eval(data);
            $("#count").text(stats[0].count);
            count = stats[0].count;
            $("#mean").text(stats[0].mean);
            $("#std").text(stats[0].std);
            $("#max").text(stats[0].max);
            $("#min").text(stats[0].min);
        }
    });
    $.ajax({
        url: '/getScoreRangeCount',
        type: "GET",
        data: {"ExamID":ExamID, "Score":fullScore},
        success: function(data){
            var num = eval(data);
            $("#range1").text(num[0].num);
            $("#range2").text(num[1].num);
            $("#range3").text(num[2].num);
            $("#range4").text(num[3].num);
            $("#range5").text(num[4].num);
            $("#range6").text(num[5].num);
            $("#failed").text(num[5].num);
            $("#passed").text($("#count").text()-num[5].num);
        }
    });
    for (var i = 0; i < qTypeOrder.length; i++){
        var QTypeID = qTypeOrder.substr(i,1);
        $.ajax({
            url: '/getQCorrectRate',
            type: "GET",
            data: {"ExamID":ExamID, "QTypeID":QTypeID, "Count":count},
            async: false,
            success: function(data){
                var rate = eval(data);
                var div1 = $("<div class='d-inline px-5 mx-5 my-3' style='width: 40%'></div>");
                var heading;
                switch (QTypeID) {
                    case "M":
                        heading = $("<h5 class='text-center'>Multiple Choice:</h5>");
                        break;
                    case "T":
                        heading = $("<h5 class='text-center'>True or False:</h5>");
                        break;
                    case "R":
                        heading = $("<h5 class='text-center'>Multiple Response:</h5>");
                        break;
                    case "F":
                        heading = $("<h5 class='text-center'>Fill in the Blank:</h5>");
                        break;
                    case "N":
                        heading = $("<h5 class='text-center'>Number:</h5>");
                        break;
                }
                var div2 = $("<div class='w-100 d-flex justify-content-between'></div>");
                var qNumTitle = $("<span>#</span>");
                var rateTitle = $("<span>Correct Rate</span>");
                $(div2).append(qNumTitle).append(rateTitle);
                $(div1).append(heading).append(div2);
                for (var i = 0; i < rate.length; i++){
                    var div3 = $("<div class='w-100 d-flex justify-content-between'></div>");
                    var qNum = $("<span></span>").text(i+1);
                    var correctRate = $("<span></span>").text(rate[i].qCorrectRate + "%");
                    $(div3).append(qNum).append(correctRate);
                    $(div1).append(div3);
                }
                $("#qCorrectRate").append(div1);
            }
        })
    }
});