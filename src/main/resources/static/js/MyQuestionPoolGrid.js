// column model for different question types
var colModel;
// string of the selected question IDs
var url = new URL(window.location.href);
var QTypeID = url.searchParams.get("QTypeID");
var CourseID = url.searchParams.get("CourseID");
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
                { label: 'Question', name: 'Question', width: 800 },
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
        url: "/getMyPoolQuestions?QTypeID="+QTypeID+"&CourseID="+CourseID,
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
        pager: "#poolPager",
        caption: "My Question Pool"
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
        onClickButton: function(){
            switch (QTypeID) {
                case "M":
                    window.location.href = "/EditPoolMCQ.html?QTypeID="+QTypeID+"&CourseID="+CourseID+"&id=0";
                    break;
                case "T":
                    window.location.href = "/EditPoolTFQ.html?QTypeID="+QTypeID+"&CourseID="+CourseID+"&id=0";
                    break;
                case "R":
                    window.location.href = "/EditPoolMRQ.html?QTypeID="+QTypeID+"&CourseID="+CourseID+"&id=0";
                    break;
                case "F":
                    window.location.href = "/EditPoolFBQ.html?QTypeID="+QTypeID+"&CourseID="+CourseID+"&id=0";
                    break;
                case "N":
                    window.location.href = "/EditPoolNQ.html?QTypeID="+QTypeID+"&CourseID="+CourseID+"&id=0";
                    break;
            }

        },
        position: "last",
        title:"Add",
        cursor: "pointer"
    }).navButtonAdd('#poolPager',{
        caption:"",
        buttonicon:"glyphicon-edit",
        onClickButton: function(){
            var id = $("#poolGrid").getGridParam("selrow");
            if (!id){
                alert("Please select a question");
            }
            else {
                switch (QTypeID) {
                    case "M":
                        window.location.href = "/EditPoolMCQ.html?QTypeID="+QTypeID+"&CourseID="+CourseID+"&id="+id;
                        break;
                    case "T":
                        window.location.href = "/EditPoolTFQ.html?QTypeID="+QTypeID+"&CourseID="+CourseID+"&id="+id;
                        break;
                    case "R":
                        window.location.href = "/EditPoolMRQ.html?QTypeID="+QTypeID+"&CourseID="+CourseID+"&id="+id;
                        break;
                    case "F":
                        window.location.href = "/EditPoolFBQ.html?QTypeID="+QTypeID+"&CourseID="+CourseID+"&id="+id;
                        break;
                    case "N":
                        window.location.href = "/EditPoolNQ.html?QTypeID="+QTypeID+"&CourseID="+CourseID+"&id="+id;
                        break;
                }
            }
        },
        position: "last",
        title:"Edit",
        cursor: "pointer"
    }).navButtonAdd('#poolPager',{
        caption:"",
        buttonicon:"glyphicon-trash",
        onClickButton: function(){
            var id = $("#poolGrid").getGridParam("selrow");
            if (!id){
                alert("Please select a question");
            }
            else {
                $.ajax({
                    url: '/deletePoolQuestion',
                    type: "POST",
                    data: {"QTypeID":QTypeID, "id":id},
                    success: function(){
                        location.reload();
                    }
                })
            }
        },
        position: "last",
        title:"Edit",
        cursor: "pointer"
    });
});

$(window).resize(function(){
    var gridWidth = $("#poolGridDiv").outerWidth(true);
    var gridHeight = $("#poolGridDiv").outerHeight(true) - 120;
    $("#poolGrid").setGridWidth(gridWidth).setGridHeight(gridHeight);
})