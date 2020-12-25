$(document).ready(function(){
    $("#instructorName").text(getCookie("name"));
    fillExamFields();
    loadButtons();
    loadQuestion(1);
    $("#answersGrid").jqGrid({
        guiStyle: "Bootstrap4",
        styleUI: "Bootstrap4",
        url: "/loadAnswers?QOrder="+1,
        mtype: "GET",
        datatype: "json",
        page: 1,
        colModel: [
            { label: 'StudentNum', name: 'StudentNum', width: 117, key: true },
            { label: 'Answer1', name: 'Answer1', width: 200 },
            { label: 'Answer2', name: 'Answer2', width: 200 },
            { label: 'Answer3', name: 'Answer3', width: 200 },
            { label: 'Answer4', name: 'Answer4', width: 200 },
            { label: 'Score', name: 'Score', width: 80 }
        ],
        viewrecords: true,
        loadonce: false,
        autowidth: true,
        height: $("#answersDiv").height() - 50,
        shrinkToFit: false,
        hidegrid: false,
        rowNum: 'ALL',
        multiselect: true,
        pager: "#poolPager",
        caption: false
    });
    $("#answersGrid").jqGrid('bindKeys');

    // pager settings
    $("#answersGrid").navGrid("#answersPager",
        {edit: false, add: false, del: false, search: true, refresh: true, view: false, align: "left"},
        {}, // options for the Edit Dialog
        {}, // options for the Add Dialog
        {}, // delete options
        {multipleSearch: true});
});
$( function() {
    $( "#confirmFinish" ).dialog({
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
            "Finish": finishGrading,
            Cancel: function() {
                $( this ).dialog( "close" );
            }
        }
    });
} );
$(window).resize(function(){
    var gridWidth = $("#answersDiv").outerWidth(true);
    var gridHeight = $("#answersDiv").outerHeight(true) - 60;
    $("#answersGrid").setGridWidth(gridWidth).setGridHeight(gridHeight);
})
function fillExamFields(){
    var url = new URL(window.location.href);
    $("#examName").val(url.searchParams.get("ExamName"));
    $("#examType").val(url.searchParams.get("ExamType"));
    $("#course").val(url.searchParams.get("Course"));
}
function loadButtons(){
    $.ajax({
        url: '/loadButtons',
        type: "GET",
        success: function(data){
            var number = parseInt(data);
            setCookie("TotalQuestions",number);
            for ( var i = 1; i <= number; i++ ){
                var btn = $("<button class='btn btn-info rounded-circle m-1 qBtn' onclick='loadQuestion(" + i + ")'>" + i + "</button>");
                $("#buttonsDiv").append(btn);
            }
        }
    })
}
function loadQuestion(QOrder) {
    $("#questionDiv").empty();
    setCookie("QOrder",QOrder);
    $.ajax({
        url: '/loadQuestionToGrade',
        type: "GET",
        data: {"QOrder":QOrder},
        success: function(data){
            var question = eval(data);
            var q = $("<h5>Question: " + QOrder +"</h5>");
            var a1 = $("<span class='mr-5'></span>");
            $(a1).text("Answer1: " + question[0].Answer1);
            var a2 = $("<span class='mr-5'></span>");
            $(a2).text("Answer2: " + question[0].Answer2);
            var a3 = $("<span class='mr-5'></span>");
            $(a3).text("Answer3: " + question[0].Answer3);
            var a4 = $("<span class='mr-5'></span>");
            $(a4).text("Answer4: " + question[0].Answer4);
            $("#questionDiv").append(q).append(question[0].Question).append(a1).append(a2).append(a3).append(a4);
            $("#answersGrid").jqGrid('setGridParam',{datatype:'json', url:'/loadAnswers?QOrder='+QOrder}).trigger('reloadGrid');
        }
    })
}
function gradeQuestion(correct) {
    var grid = $("#answersGrid");
    var rowKey = grid.getGridParam("selrow");
    if (rowKey) {
        var selectedIDs = grid.getGridParam("selarrrow");
        var selectedStudents = "(";
        for (var i = 0; i < selectedIDs.length; i++) {
            selectedStudents += selectedIDs[i] + ",";
        }
        var selected = selectedStudents.substr(0, selectedStudents.length - 1);
        selected += ")";
        $.ajax({
            url: '/gradeQuestion',
            type: "POST",
            data: {"StudentNums":selected, "correct":correct},
            success: function(){
                var QOrder = getCookie("QOrder");
                $("#answersGrid").jqGrid('setGridParam',{datatype:'json', url:'/loadAnswers?QOrder='+QOrder}).trigger('reloadGrid');
            }
        })
    }
}
function previousQ() {
    var QOrder = parseInt(getCookie("QOrder"));
    if ( QOrder == 1 ) {
        alert("This is the first question");
    }
    else {
        loadQuestion(QOrder-1)
    }
}
function nextQ() {
    var QOrder = parseInt(getCookie("QOrder"));
    var totalQ = getCookie("TotalQuestions");
    if ( QOrder == totalQ ) {
        alert("This is the last question");
    }
    else {
        loadQuestion(QOrder+1)
    }
}
function confirmFinish() {
    $( "#confirmFinish" ).dialog( "open" );
}
function finishGrading() {
    $.ajax({
        url: '/finishGrading',
        type: "POST",
        success: function(data){
            window.location.href = data;
        }
    })
}