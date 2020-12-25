$(document).ready(function(){
    $('[data-toggle="tooltip"]').tooltip();
    $.ajax({
        url: '/getQuestionTypes',
        type:"GET",
        success: function(data){
            var fullScore = 0;
            var databaseTypes = eval(data);
            for (var i = 0; i < databaseTypes.length ; i++) {
                addQuestionDiv(databaseTypes[i].QTypeID, Number(databaseTypes[i].QNum), Number(databaseTypes[i].ScoreSum));
                fullScore += Number(databaseTypes[i].ScoreSum);
            }
            $("#fullScore").text(fullScore);
        }
    });
});
function addNew() {
    var qNum = $("#qNumber").val();
    if ( qNum < 1 ){
        alert("Please enter the number of questions.");
    }
    else if( qTypes.indexOf($("#qType").val()) >= 0 ){
        alert("This question type already exists.");
    }
    else{
        $.ajax({
            url: '/addDeleteQuestionType',
            type: "POST",
            data: {
                "QTypeID":$("#qType").val(),
                "QNum":qNum,
                "oper":"add"
            },
            success: function () {
                location.reload();
            }
        })
    }
}
var qTypes = [];
function addQuestionDiv(qType, qNum, scoreSum){
    qTypes.push(qType);
    var qTypeTitle;
    switch (qType) {
        case 'M':
            qTypeTitle = "Multiple Choice Questions";
            break;
        case 'T':
            qTypeTitle = "True or False Questions";
            break;
        case 'R':
            qTypeTitle = "Multiple Response Questions";
            break;
        case 'F':
            qTypeTitle = "Fill in the Blank Questions";
            break;
        case 'N':
            qTypeTitle = "Number Questions";
            break;
    }
    var div = $("<div id='div" + qType + "' class='m-3 pb-1 questionDiv'></div>");
    var infoDiv = $("<div class='my-1'></div>");
    var type = $("<h5 class='mr-5 d-inline'></h5>").text(qTypeTitle);
    var totalMarks = $("<div class='float-right mr-1'></div>");
    var marks = $("<span class='mr-2'></span>").text("Total Marks:");
    var total = $("<span></span>").text(scoreSum);
    $(totalMarks).append(marks).append(total);
    $(infoDiv).append(type).append(totalMarks);
    var bottomDiv = $("<div class='d-flex'></div>");
    var btnDiv = $("<div class='col-10 p-0'></div>");
    var deleteDiv = $("<div class='d-flex justify-content-end col-2 ml-auto mt-auto p-0 '></div>");
    for ( var i = 1; i <= qNum; i++ ){
        var btn = $("<button class='btn btn-info mr-3 my-1 rounded-circle qButton' id='" + qType + i + "' onclick='getQuestionPage(this.id)'>" + i + "</button>");
        $(btnDiv).append(btn);
    }
    var deleteBtn = $("<button class='btn btn-danger deleteBtn' id='delete" + qType + "' onclick='deleteAll(this.id)'></button>").text("Delete All");
    $(deleteDiv).append(deleteBtn);
    $(bottomDiv).append(btnDiv).append(deleteDiv);
    $(div).append(infoDiv).append(bottomDiv);
    $("#bodyDiv").append(div);
}
var deleteAllID = "";
$( function() {
    $( "#confirmDeleteAll" ).dialog({
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
            "Confirm": function (){
                deleteQuestionType(deleteAllID)
            },
            Cancel: function() {
                $( this ).dialog( "close" );
            }
        }
    });
} );
function deleteAll(id) {
    deleteAllID = id;
    $( "#confirmDeleteAll" ).dialog( "open" );
}
function deleteQuestionType(btnID){
    $.ajax({
        url: '/addDeleteQuestionType',
        type: "POST",
        data: {
            "QTypeID":btnID.substr(btnID.length-1, btnID.length),
            "QNum":0,
            "oper":"delete"
        },
        success: function(){
            location.reload();
        }
    })
}
function getQuestionPage(btnID){
    var qType = btnID.substr(0, 1);
    var qNum = btnID.substr(1, btnID.length);
    $.ajax({
        url: '/getQuestionPage',
        type: "GET",
        data: {"QTypeID":qType, "QNum":qNum},
        success: function (data) {
            window.location.href = data
        }
    });
}
function addFromQPool() {
    if( qTypes.indexOf($("#qType").val()) >= 0 ){
        alert("This question type already exists.");
    }
    else{
        $.ajax({
            url: '/addFromQPool',
            type: "GET",
            data: {"QTypeID": $("#qType").val()},
            success: function(data){
                window.location.href = data;
            }
        })
    }
}
$( function() {
    $( "#confirmSubmit" ).dialog({
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
            "Submit": function() {
                $.ajax({
                    url: '/submitExam',
                    type: "POST",
                    data: {"fullScore":$("#fullScore").text()},
                    success: function(data){
                        if (data == "N/A"){
                            $( "#confirmSubmit" ).dialog( "close" );
                            alert("Exam is currently empty.")
                        }
                        else if (data.substr(0,6) == "Error:"){
                            $( "#confirmSubmit" ).dialog( "close" );
                            alert("Question " + data.substr(6,data.length-1) + " has not been set.")
                        }
                        else {
                            top.window.location.href=data;
                        }
                    }
                })
            },
            Cancel: function() {
                $( this ).dialog( "close" );
            }
        }
    });
} );
function submitExam() {
    $( "#confirmSubmit" ).dialog( "open" );
}