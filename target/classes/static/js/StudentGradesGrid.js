var url = new URL(window.location.href);
var ExamID = parseInt(url.searchParams.get("ExamID"));
$(document).ready(function () {
    var colModel = [{ label: 'Student Number', name: 'StudentNum', width: 100, editable: false, key: true }];
    $.ajax({
        url :'/getQSumScore',
        type: "GET",
        data: {"ExamID": ExamID},
        async:false,
        success: function(data){
            var info = eval(data);
            var fullScore = 0;
            for ( var i = 0; i < info.length; i++ ){
                switch (info[i].QTypeID) {
                    case "M":
                        var MLabel = "MCQ Score (" + info[i].QSumScore + ")";
                        fullScore += info[i].QSumScore;
                        colModel.push({ label: MLabel, name: 'MScore', width: 50, editable: true, key: false });
                        break;
                    case "T":
                        var TLabel = "TFQ Score (" + info[i].QSumScore + ")";
                        fullScore += info[i].QSumScore;
                        colModel.push({ label: TLabel, name: 'TScore', width: 50, editable: true, key: false });
                        break;
                    case "R":
                        var RLabel = "MRQ Score (" + info[i].QSumScore + ")";
                        fullScore += info[i].QSumScore;
                        colModel.push({ label: RLabel, name: 'RScore', width: 50, editable: true, key: false });
                        break;
                    case "F":
                        var FLabel = "FBQ Score (" + info[i].QSumScore + ")";
                        fullScore += info[i].QSumScore;
                        colModel.push({ label: FLabel, name: 'FScore', width: 50, editable: true, key: false });
                        break;
                    case "N":
                        var NLabel = "NQ Score (" + info[i].QSumScore + ")";
                        fullScore += info[i].QSumScore;
                        colModel.push({ label: NLabel, name: 'NScore', width: 50, editable: true, key: false });
                        break;
                }
            }
            var Label = "Full Exam Score (" + fullScore + ")";
            colModel.push({ label: Label, name: 'Score', width: 80, editable: true, key: false });
        }
    });
    $("#stuGradesGrid").jqGrid({
        url: "/getStudentGrades?ExamID="+ExamID,
        editurl: '/editStudentGrades?ExamID='+ExamID,
        mtype: "GET",
        datatype: "json",
        page: 1,
        colModel: colModel,
        viewrecords: true,
        loadonce: false,
        autowidth: true,
        height: $("#stuGradesGridDiv").height() - 120,
        shrinkToFit: true,
        hidegrid: false,
        rowNum: 20,
        rowList:['ALL',20,50,80],
        pager: "#stuGradesPager",
        caption: "Student Grades"
    });
    $("#stuGradesGrid").jqGrid('bindKeys');
    // pager settings
    $("#stuGradesGrid").navGrid("#stuGradesPager",
        {edit: true, add: false, del: false, search: true, refresh: true, view: false, align: "left"},
        {
            editCaption: "The Edit Dialog",
            recreateForm: true,
            checkOnUpdate : true,
            checkOnSubmit : true,
            afterSubmit:function()
            {
                $("#stuGradesGrid").jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
                return [true,"",''];
            },
            closeAfterEdit: true,
            errorTextFormat: function (data) {
                return 'Error: ' + data.responseText;
            }
        }, // options for the Edit Dialog
        {}, // options for the Add Dialog
        {}, // delete options
        {multipleSearch: true});
});

$(window).resize(function(){
    var gridWidth = $("#stuGradesGridDiv").outerWidth(true);
    var gridHeight = $("#stuGradesGridDiv").outerHeight(true) - 120;
    $("#stuGradesGrid").setGridWidth(gridWidth).setGridHeight(gridHeight);
});