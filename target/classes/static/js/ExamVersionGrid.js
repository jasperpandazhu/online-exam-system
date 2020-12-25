$( function() {
    $( "#accordion" ).accordion({
        collapsible: true,
        heightStyle: "fill"
    });
} );

var userLevelRules;
$(document).ready(function () {
    // get user level
    var userLevel = getCookie("userLevel");
    switch (userLevel) {
        case "1":
            userLevelRules = { edit: true, add: true, del: true, search: true, refresh: true, view: false, align: "left" };
            break;
        case "2":
            userLevelRules = { edit: true, add: true, del: true, search: true, refresh: true, view: false, align: "left" };
            break;
        default:
            userLevelRules = { edit: false, add: false, del: false, search: true, refresh: true, view: false, align: "left" };
            break;
    }

    $("#courseGrid").jqGrid({
        url: '/getCourse',
        editurl: '/editCourse',
        datatype: "json",
        colModel: [
            {
                label: 'Course ID',
                name: 'CourseID',
                width: 60,
                key: true,
                editable: true
            },
            {
                label: 'Course Name',
                name: 'CourseName',
                width: 200,
                editable: true
            },
            {
                label: 'Instructor',
                name: 'Instructor',
                width: 120,
                editable: true,
                edittype: "select",
                editoptions: {
                    value: getInstructors
                }
            }
        ],
        viewrecords: true, // show the current page, data range and total records on the toolbar
        width: $("#courseGridDiv").width(),
        height: $("#courseGridDiv").height() - 85,
        shrinkToFit: true, // need to tobe set for frozen columns to take effect
        rowNum: 10,
        // rowList: [10,20,30,40],
        rownumbers: false,
        loadonce: true,
        hidegrid: false,
        pager: "#coursePager",
        emptyrecords: "Nothing to display",
        caption: false // set caption to any string you wish and it will appear on top of the grid
    });

    $("#courseGrid").jqGrid('bindKeys');
    $("#courseGrid").jqGrid("setFrozenColumns");

    // pager settings
    $("#courseGrid").navGrid("#coursePager", userLevelRules,
        // { edit: true, add: true, del: true, search: true, refresh: true, view: false, align: "left" },
        {
            editCaption: "The Edit Dialog",
            recreateForm: true,
            checkOnUpdate : true,
            checkOnSubmit : true,
            onclickSubmit : parseInstructor,
            afterSubmit: function()
            {
                $("#courseGrid").jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
                return [true,"",''];
            },
            closeAfterEdit: true,
            errorTextFormat: function (data) {
                return 'Error: ' + data.responseText;
            }
        }, // options for the Edit Dialog
        {
            onclickSubmit : parseInstructor,
            afterSubmit:function()
            {
                $("#courseGrid").jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
                return [true,"",''];
            },
            closeAfterAdd: true,
            recreateForm: true,
            errorTextFormat: function (data) {
                return 'Error: ' + data.responseText;
            }
        }, // options for the Add Dialog
        {
            afterSubmit:function()
            {
                $("#courseGrid").jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
                return [true,"",''];
            },
            errorTextFormat: function (data) {
                return 'Error: ' + data.responseText;
            }
        }, // delete options
        { multipleSearch: true });



    $("#examTypeGrid").jqGrid({
        url: '/getExamType',
        editurl: '/editExamType',
        datatype: "json",
        colModel: [
            {
                label: 'Exam ID',
                name: 'ExamID',
                width: 60,
                key: true,
                editable: true
            },
            {
                label: 'Exam Name',
                name: 'ExamName',
                width: 150,
                editable: true
            },
            {
                label: 'Start Date',
                name: 'StartDate',
                width: 200,
                formatter: 'date',
                formatoptions: {
                    newformat: 'Y-m-d'
                },
                editable: true,
                editoptions: {
                    dataInit: function (element) {
                        $(element).datepicker({
                            autoclose: true,
                            dateFormat: 'yy-mm-dd',
                            orientation: 'bottom',
                            showOtherMonths: true,
                            selectOtherMonths: true,
                            showAnim: "slideDown",
                            changeMonth: true,
                            changeYear: true,
                            showWeek: true,
                            firstDay: 1
                        })
                    }
                },
                sorttype: 'number'
            },
            {
                label: 'End Date',
                name: 'EndDate',
                width: 200,
                formatter: 'date',
                formatoptions: {
                    newformat: 'Y-m-d'
                },
                editable: true,
                editoptions: {
                    dataInit: function (element) {
                        $(element).datepicker({
                            autoclose: true,
                            dateFormat: 'yy-mm-dd',
                            orientation: 'bottom',
                            showOtherMonths: true,
                            selectOtherMonths: true,
                            showAnim: "slideDown",
                            changeMonth: true,
                            changeYear: true,
                            showWeek: true,
                            firstDay: 1
                        })
                    }
                },
                sorttype: 'number'
            }
        ],
        viewrecords: true, // show the current page, data range and total records on the toolbar
        width: $("#examTypeGridDiv").width(),
        height: $("#examTypeGridDiv").height() - 85,
        shrinkToFit: true, // need to tobe set for frozen columns to take effect
        rowNum: 10,
        // rowList: [10,20,30,40],
        rownumbers: false,
        loadonce: true,
        hidegrid: false,
        pager: "#examTypePager",
        emptyrecords: "Nothing to display",
        caption: false // set caption to any string you wish and it will appear on top of the grid
    });

    $("#examTypeGrid").jqGrid('bindKeys');

    // pager settings
    $("#examTypeGrid").navGrid("#examTypePager", userLevelRules,
        // { edit: true, add: true, del: true, search: true, refresh: true, view: false, align: "left" },
        {
            editCaption: "The Edit Dialog",
            recreateForm: true,
            checkOnUpdate : true,
            checkOnSubmit : true,
            afterSubmit:function()
            {
                $("#examTypeGrid").jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
                return [true,"",''];
            },
            closeAfterEdit: true,
            errorTextFormat: function (data) {
                return 'Error: ' + data.responseText;
            }
        }, // options for the Edit Dialog
        {
            afterSubmit:function()
            {
                $("#examTypeGrid").jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
                return [true,"",''];
            },
            closeAfterAdd: true,
            recreateForm: true,
            errorTextFormat: function (data) {
                return 'Error: ' + data.responseText;
            }
        }, // options for the Add Dialog
        {
            afterSubmit:function()
            {
                $("#examTypeGrid").jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
                return [true,"",''];
            },
            errorTextFormat: function (data) {
                return 'Error: ' + data.responseText;
            }
        }, // delete options
        { multipleSearch: true });



    $("#questionTypeGrid").jqGrid({
        url: '/getQuestionType',
        editurl: '/editQuestionType',
        datatype: "json",
        colModel: [
            {
                label: 'Question Type ID',
                name: 'QTypeID',
                width: 70,
                key: true,
                editable: true
            },
            {
                label: 'Question Type',
                name: 'QType',
                width: 150,
                editable: true
            }
        ],
        viewrecords: true, // show the current page, data range and total records on the toolbar
        width: $("#questionTypeGridDiv").width(),
        height: $("#questionTypeGridDiv").height() - 85,
        shrinkToFit: true, // need to tobe set for frozen columns to take effect
        rowNum: 10,
        // rowList: [10,20,30,40],
        rownumbers: false,
        loadonce: true,
        hidegrid: false,
        pager: "#questionTypePager",
        emptyrecords: "Nothing to display",
        caption: false // set caption to any string you wish and it will appear on top of the grid
    });

    $("#questionTypeGrid").jqGrid('bindKeys');

    // pager settings
    $("#questionTypeGrid").navGrid("#questionTypePager", userLevelRules,
        // { edit: true, add: true, del: true, search: true, refresh: true, view: false, align: "left" },
        {
            editCaption: "The Edit Dialog",
            recreateForm: true,
            checkOnUpdate : true,
            checkOnSubmit : true,
            afterSubmit:function()
            {
                $("#questionTypeGrid").jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
                return [true,"",''];
            },
            closeAfterEdit: true,
            errorTextFormat: function (data) {
                return 'Error: ' + data.responseText;
            }
        }, // options for the Edit Dialog
        {
            afterSubmit:function()
            {
                $("#questionTypeGrid").jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
                return [true,"",''];
            },
            closeAfterAdd: true,
            recreateForm: true,
            errorTextFormat: function (data) {
                return 'Error: ' + data.responseText;
            }
        }, // options for the Add Dialog
        {
            afterSubmit:function()
            {
                $("#questionTypeGrid").jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
                return [true,"",''];
            },
            errorTextFormat: function (data) {
                return 'Error: ' + data.responseText;
            }
        }, // delete options
        { multipleSearch: true });
});

$(window).resize(function(){
    $( "#accordion" ).accordion( "refresh" );
    var GridWidth = $("#courseGridDiv").width();
    var GridHeight = $("#courseGridDiv").height() - 85;
    $("#courseGrid").setGridWidth(GridWidth).setGridHeight(GridHeight);
    $("#examTypeGrid").setGridWidth(GridWidth).setGridHeight(GridHeight);
    $("#questionTypeGrid").setGridWidth(GridWidth).setGridHeight(GridHeight);
});

function getInstructors() {
    var options = "";
    $.ajax({
        url: '/getInstructor',
        type: "GET",
        async: false,
        success: function(data){
            var instructors = eval(data);
            options += ":;";
            for (var i = 0; i < instructors.length; i++){
                var instructor = instructors[i].FirstName + " " + instructors[i].LastName + " " + instructors[i].StaffNum;
                options += instructor + ":" + instructor + ";";
            }
        }
    });
    return options.substr(0, options.length-1);
}

function parseInstructor( params, postData ){
    var instructor = postData.Instructor.split(" ");
    return {"Instructor":instructor[2]};
}