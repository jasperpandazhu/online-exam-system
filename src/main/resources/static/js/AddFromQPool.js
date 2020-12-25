// column model for different question types
var colModel;
// string of the selected question IDs
var selected;
var url = new URL(window.location.href);
var QTypeID = url.searchParams.get("QTypeID");
$(document).ready(function () {
    switch (QTypeID) {
        case 'M':
            colModel = [
                { label: 'id', name: 'id', width: 50, key: true },
                { label: 'Difficulty', name: 'Difficulty', width: 70 },
                { label: 'Score', name: 'Score', width: 60 },
                { label: 'Question', name: 'Question', width: 400 },
                { label: 'ChoiceA', name: 'ChoiceA', width: 250 },
                { label: 'ChoiceB', name: 'ChoiceB', width: 250 },
                { label: 'ChoiceC', name: 'ChoiceC', width: 250 },
                { label: 'ChoiceD', name: 'ChoiceD', width: 250 },
                { label: 'Answer', name: 'Answer', width: 70 }
            ];
            break;
        case 'T':
            colModel = [
                { label: 'id', name: 'id', width: 50, key: true },
                { label: 'Difficulty', name: 'Difficulty', width: 70 },
                { label: 'Score', name: 'Score', width: 60 },
                { label: 'Question', name: 'Question', width: 780 },
                { label: 'Answer', name: 'Answer', width: 70 }
            ];
            break;
        case 'R':
            colModel = [
                { label: 'id', name: 'id', width: 50, key: true },
                { label: 'Difficulty', name: 'Difficulty', width: 70 },
                { label: 'Score', name: 'Score', width: 60 },
                { label: 'Question', name: 'Question', width: 400 },
                { label: 'ChoiceA', name: 'ChoiceA', width: 250 },
                { label: 'ChoiceB', name: 'ChoiceB', width: 250 },
                { label: 'ChoiceC', name: 'ChoiceC', width: 250 },
                { label: 'ChoiceD', name: 'ChoiceD', width: 250 },
                { label: 'Answer1', name: 'Answer1', width: 70 },
                { label: 'Answer2', name: 'Answer2', width: 70 },
                { label: 'Answer3', name: 'Answer3', width: 70 },
                { label: 'Answer4', name: 'Answer4', width: 70 }
            ];
            break;
        case 'F':
            colModel = [
                { label: 'id', name: 'id', width: 50, key: true },
                { label: 'Difficulty', name: 'Difficulty', width: 70 },
                { label: 'Score', name: 'Score', width: 60 },
                { label: 'Question', name: 'Question', width: 600 },
                { label: 'Answer1', name: 'Answer1', width: 250 },
                { label: 'Answer2', name: 'Answer2', width: 250 },
                { label: 'Answer3', name: 'Answer3', width: 250 },
                { label: 'Answer4', name: 'Answer4', width: 250 }
            ];
            break;
        case 'N':
            colModel = [
                { label: 'id', name: 'id', width: 50, key: true },
                { label: 'Difficulty', name: 'Difficulty', width: 70 },
                { label: 'Score', name: 'Score', width: 60 },
                { label: 'Question', name: 'Question', width: 700 },
                { label: 'Answer', name: 'Answer', width: 150 }
            ];
            break;
    }
    $("#poolGrid").jqGrid({
        url: "/getPoolQuestions?QTypeID="+QTypeID,
        editurl: "/addPoolQuestions",
        mtype: "GET",
        datatype: "json",
        page: 1,
        colModel: colModel,
        viewrecords: true,
        loadonce: false,
        autowidth: true,
        height: $("#poolGridDiv").height() - 120,
        shrinkToFit: false,
        hidegrid: false,
        rowNum: 30,
        rowList:['ALL',30,50,100],
        multiselect: true,
        pager: "#poolPager",
        caption: "My Question Pool",
        gridComplete: function () {
            $("#poolGrid").jqGrid('hideCol', 'cb');
        }
    });
    $("#poolGrid").jqGrid('bindKeys');
    // pager settings
    $("#poolGrid").navGrid("#poolPager",
        {edit: false, add: false, del: false, search: true, refresh: true, view: false, align: "left"},
        {}, // options for the Edit Dialog
        {}, // options for the Add Dialog
        {}, // delete options
        {multipleSearch: true}
    ).navButtonAdd('#poolPager',{
        caption:"",
        buttonicon:"glyphicon-plus",
        onClickButton: getSelectedRows,
        position: "last",
        title:"Add to Exam",
        cursor: "pointer"
    }).navButtonAdd('#poolPager',{
        caption:"",
        buttonicon:"glyphicon-arrow-left",
        onClickButton: function(){
            $("#confirmReturn").dialog( "open" );
        },
        position: "last",
        title:"Add to Exam",
        cursor: "pointer"
    });
});
function getSelectedRows() {
    var grid = $("#poolGrid");
    var rowKey = grid.getGridParam("selrow");

    if (!rowKey)
        alert("No rows are selected");
    else {
        var selectedIDs = grid.getGridParam("selarrrow");
        var selectedQuestions = "";
        for (var i = 0; i < selectedIDs.length; i++) {
            selectedQuestions += selectedIDs[i] + ",";
        }
        selected = selectedQuestions.substr(0,selectedQuestions.length-1);
        $("#confirmation").text("These questions will be added to the exam in the order you have selected: " + selected);
        $( "#confirmAdd" ).dialog( "open" );
    }
}
$( function() {
    $( "#confirmAdd" ).dialog({
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
            "Add": function() {
                $.ajax({
                    url: '/addPoolQuestions',
                    type: "POST",
                    data: {
                        "QTypeID":QTypeID,
                        "selected":selected
                    },
                    success: function(data){
                        window.location.href = data;
                    }
                })
            },
            Cancel: function() {
                $( this ).dialog( "close" );
            }
        }
    });
} );
$( function() {
    $( "#confirmReturn" ).dialog({
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
            "Return": function() {
                window.location.href = "/ExamLayout.html";
            },
            Cancel: function() {
                $( this ).dialog( "close" );
            }
        }
    });
} );
$(window).resize(function(){
    var gridWidth = $("#poolGridDiv").outerWidth(true);
    var gridHeight = $("#poolGridDiv").outerHeight(true) - 120;
    $("#poolGrid").setGridWidth(gridWidth).setGridHeight(gridHeight);
})