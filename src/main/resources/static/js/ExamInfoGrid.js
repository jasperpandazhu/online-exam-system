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
    // set initial filter dates
    var today = new Date();
    var dd = String(today.getDate()).padStart(2, '0');
    var mm = String(today.getMonth() + 1).padStart(2, '0'); //January is 0!
    var yy = today.getFullYear();
    today = yy + '-' + mm + '-' + dd;
    $("#from").val(today);
    var plus1Year = yy + 1;
    var nextYear = plus1Year + '-' + mm + '-' + dd;
    $("#to").val(nextYear);

    $("#examInfoGrid").jqGrid({
        url: '/getExamInfo?From='+$("#from").val()+'&To='+$("#to").val(),
        editurl: '/editExamInfo',
        datatype: "json",
        colModel: [
            {
                label: 'Exam ID',
                name: 'ExamID',
                width: 80,
                key: true,
                editable: false,
                frozen: true,
                sorttype: 'number'
            },
            {
                label: 'Exam Name',
                name: 'ExamName',
                width: 350,
                editable: true,
                edittype: "text"
            },
            {
                label: 'Exam Type',
                name: 'ExamType',
                width: 120,
                editable: true,
                edittype: "select",
                editoptions:{
                    value: getExamTypes
                }
            },
            {
                label: 'Course',
                name: 'Course',
                width: 120,
                editable: true,
                edittype: "select",
                editoptions:{
                    value: getCourses
                }
            },
            {
                label: 'Instructor',
                name: 'Instructor',
                width: 220,
                editable: true,
                edittype: "select",
                editoptions: {
                    value: getInstructors
                }
            },
            {
                label: 'Exam Start Time',
                name: 'StartTime',
                width: 200,
                editable: true,
                formatter: 'date',
                formatoptions: {
                    srcformat: "ISO8601Long",
                    newformat: "ISO8601Long",
                    userLocalTime : true
                },
                editoptions: {
                    dataInit: function (element) {
                        $(element).datetimepicker({
                            dateFormat: 'yy-mm-dd',
                            timeFormat: 'HH:mm:ss',
                            changeYear: true,
                            changeMonth: true,
                            showOtherMonths: true,
                            selectOtherMonths: true,
                            hourGrid: 4,
                            minuteGrid: 10,
                            secondGrid: 10
                        });
                    }
                },
                sorttype: 'number'
            },
            {
                label: 'Exam End Time',
                name: 'EndTime',
                width: 200,
                editable: true,
                formatter: 'date',
                formatoptions: {
                    srcformat: "Y-m-d H:i:s",
                    newformat: "Y-m-d H:i:s",
                    userLocalTime : true
                },
                editoptions: {
                    dataInit: function (element) {
                        $(element).datetimepicker({
                            dateFormat: 'yy-mm-dd',
                            timeFormat: 'HH:mm:ss',
                            changeYear: true,
                            changeMonth: true,
                            showOtherMonths: true,
                            selectOtherMonths: true,
                            hourGrid: 4,
                            minuteGrid: 10,
                            secondGrid: 10
                        });
                    }
                },
                sorttype: 'number'
            },
            {
                label: 'QType Order',
                name: 'QTypeOrder',
                width: 100,
                editable: true
            },
            {
                label: 'Score',
                name: 'Score',
                width: 80,
                editable: true
            },
            {
                label: 'Submitted',
                name: 'Submitted',
                width: 80,
                editable: true,
                edittype: "custom",
                editoptions: {
                    custom_value: getElementValue,
                    custom_element: createSubmitEditElement
                }
            },
            {
                label: 'Graded',
                name: 'Graded',
                width: 80,
                editable: true,
                edittype: "custom",
                editoptions: {
                    custom_value: getElementValue,
                    custom_element: createGradedEditElement
                }
            }
        ],
        viewrecords: true, // show the current page, data range and total records on the toolbar
        width: $("#examInfoGridDiv").width(),
        height: $("#examInfoGridDiv").height() - 125,
        shrinkToFit: false, // need to tobe set for frozen columns to take effect
        rowNum: 10,
        rowList: [10,20,30,40],
        rownumbers: false,
        loadonce: true,
        hidegrid: false,
        pager: "#examInfoPager",
        emptyrecords: "Nothing to display",
        caption: ' '
    });

    $("#examInfoGrid").jqGrid('bindKeys');
    $("#examInfoGrid").jqGrid("setFrozenColumns");

    // pager settings
    $("#examInfoGrid").navGrid("#examInfoPager", userLevelRules,
        // { edit: true, add: true, del: true, search: true, refresh: true, view: false, align: "left" },
        {
            editCaption: "The Edit Dialog",
            recreateForm: true,
            checkOnUpdate : true,
            checkOnSubmit : true,
            onclickSubmit : parseInstructor,
            afterSubmit: function()
            {
                $("#examInfoGrid").jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
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
                $("#examInfoGrid").jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
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
                $("#examInfoGrid").jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
                return [true,"",''];
            },
            errorTextFormat: function (data) {
                return 'Error: ' + data.responseText;
            }
        }, // delete options
        { multipleSearch: true });
});

function getExamTypes() {
    var options = "";
    $.ajax({
        url: '/getExamType',
        type: "GET",
        async: false,
        success: function(data){
            var types = eval(data);
            options += ":;";
            for (var i = 0; i < types.length; i++){
                options += types[i].ExamID + ":" + types[i].ExamID + ";";
            }
        }
    });
    return options.substr(0, options.length-1);
}

function getCourses() {
    var options = "";
    $.ajax({
        url: '/getCourse',
        type: "GET",
        async: false,
        success: function(data){
            var courses = eval(data);
            options += ":;";
            for (var i = 0; i < courses.length; i++){
                options += courses[i].CourseID + ":" + courses[i].CourseID + ";";
            }
        }
    });
    return options.substr(0, options.length-1);
}

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

function createSubmitEditElement(value) {
    var div = $("<div></div>");
    var option1 = $("<span style='margin-right: 40px'></span>");
    var label1 = $("<label for='male'>Yes</label>");
    var radio1 = $("<input>", { type: "radio", value: "Y", name: "submit", id: "yes", style: "width: 20px;", checked: value == "Y"});
    option1.append(radio1).append(label1);
    var option2 = $("<span style='margin-right: 40px'></span>");
    var label2 = $("<label for='female'>No</label>");
    var radio2 = $("<input>", { type: "radio", value: "N", name: "submit", id: "no", style: "width: 20px;", checked: value == "N"});
    option2.append(radio2).append(label2);
    div.append(option1).append(option2);
    return div;
}
function createGradedEditElement(value) {
    var div = $("<div></div>");
    var option1 = $("<span style='margin-right: 40px'></span>");
    var label1 = $("<label for='male'>Yes</label>");
    var radio1 = $("<input>", { type: "radio", value: "Y", name: "graded", style: "width: 20px;", checked: value == "Y"});
    option1.append(radio1).append(label1);
    var option2 = $("<span style='margin-right: 40px'></span>");
    var label2 = $("<label for='female'>No</label>");
    var radio2 = $("<input>", { type: "radio", value: "N", name: "graded", style: "width: 20px;", checked: value == "N"});
    option2.append(radio2).append(label2);
    div.append(option1).append(option2);
    return div;
}
function getElementValue(elem, oper, value) {
    if (oper === "set") {
        var radioButton = $(elem).find("input:radio[value='" + value + "']");
        if (radioButton.length > 0) {
            radioButton.prop("checked", true);
        }
    }
    if (oper === "get") {
        return $(elem).find("input:radio:checked").val();
    }
}

// datepicker settings
$( function() {
    $("#from")
        .datepicker({
            defaultDate: "+1w",
            changeMonth: true,
            changeYear: true,
            showOtherMonths: true,
            selectOtherMonths: true,
            numberOfMonths: 1,
            dateFormat: "yy-mm-dd"
        });

    $("#to").datepicker({
        defaultDate: "+1w",
        changeMonth: true,
        changeYear: true,
        showOtherMonths: true,
        selectOtherMonths: true,
        numberOfMonths: 1,
        dateFormat: "yy-mm-dd"
    })
});

function filterDateRange(){
    var from = $("#from").val();
    var to = $("#to").val();
    if (from == "" || to == ""){
        alert("Please provide a date range.")
    }
    $("#examInfoGrid").jqGrid('setGridParam',{datatype:'json', url:'/getExamInfo?From='+from+'&To='+to}).trigger('reloadGrid');
}

function parseInstructor( params, postData ){
    var instructor = postData.Instructor.split(" ");
    return {"StaffNum":instructor[2]};
}

$(window).resize(function(){
    var gridWidth = $("#examInfoGridDiv").width();
    var gridHeight = $("#examInfoGridDiv").height() - 125;
    $("#examInfoGrid").setGridWidth(gridWidth).setGridHeight(gridHeight);
    $("#dateRange").width($("body").width());
});