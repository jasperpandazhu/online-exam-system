var userLevelRules;
$(document).ready(function () {
    // check for errors
    $.ajax({
        url: '/checkErrors',
        type: "GET",
        success: function(data){
            if(data == "fieldMismatch"){
                alert("The fields in your excel file do not match the columns in the table.\n" +
                    "Note: Please do NOT include the id field in your file.\n" +
                    "Download the template file below for reference.");
            }
        }
    });
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

    $("#instructorGrid").jqGrid({
        url: '/getInstructor',
        editurl: '/editInstructor',
        datatype: "json",
        colModel: [
            {
                label: 'Id',
                name: 'id',
                width: 40,
                key: true,
                editable: false,
                frozen: true
            },
            {
                label: 'Staff Number',
                name: 'StaffNum',
                formatter: 'integer',
                formatoptions: {
                    thousandsSeparator: ""
                },
                width: 150,
                editable: true,
                editrules: {
                    number: true,
                    required: true
                },
                formoptions: {label: 'Staff Number *'},
                frozen: true
            },
            {
                label: 'First Name',
                name: 'FirstName',
                width: 120,
                editable: true,
                edittype: "text",
                editrules: {
                    required: true
                },
                formoptions: {label: 'First Name *'}
            },
            {
                label: 'Last Name',
                name: 'LastName',
                width: 120,
                editable: true,
                edittype: "text"
            },
            {
                label: 'Gender',
                name: 'Gender',
                width: 60,
                editable: true,
                edittype: "custom",
                editoptions: {
                    custom_value: getElementValue,
                    custom_element: createGenderEditElement
                }
            },
            {
                label: 'University',
                name: 'University',
                width: 120,
                editable: true,
                edittype: "select",
                editoptions: {
                    value: getUniversities
                }
            },
            {
                label: 'Faculty',
                name: 'Faculty',
                width: 150,
                editable: true,
                edittype: "select",
                editoptions: {
                    value: "Select a Faculty"
                }
            },
            {
                label: 'Phone',
                name: 'Phone',
                formatter: 'integer',
                formatoptions: {
                    thousandsSeparator: ""
                },
                width: 150,
                editable: true,
                editrules: {
                    number: true
                }
            },
            {
                label: 'Email',
                name: 'Email',
                formatter: 'email',
                width: 200,
                editable: true,
                editrules: {
                    email: true,
                    required: false
                }
            },
            {
                label: 'Password',
                name: 'Password',
                formatter: 'integer',
                hidden: true,
                formatoptions: {
                    thousandsSeparator: ""
                },
                width: 150,
                editable: true,
                edittype: "password",
                editrules: {
                    number: true,
                    edithidden: true
                }
            }
        ],
        viewrecords: true, // show the current page, data range and total records on the toolbar
        width: $("#instructorGridDiv").width(),
        height: $("#instructorGridDiv").height() - 120,
        shrinkToFit: false, // need to tobe set for frozen columns to take effect
        rowNum: 10,
        // rowList: [10,20,30,40],
        rownumbers: false,
        loadonce: true,
        hidegrid: false,
        pager: "#instructorPager",
        emptyrecords: "Nothing to display",
        caption: "Edit Instructors" // set caption to any string you wish and it will appear on top of the grid
    });

    $("#instructorGrid").jqGrid('bindKeys');
    $("#instructorGrid").jqGrid("setFrozenColumns");

    // pager settings
    $("#instructorGrid").navGrid("#instructorPager", userLevelRules,
        // { edit: true, add: true, del: true, search: true, refresh: true, view: false, align: "left" },
        {
            editCaption: "The Edit Dialog",
            recreateForm: true,
            checkOnUpdate : true,
            checkOnSubmit : true,
            beforeSubmit : function(postdata, formid) {
                var selRowId = $("#instructorGrid").jqGrid ('getGridParam', 'selrow');
                var selRowNum = $("#instructorGrid").jqGrid('getInd',selRowId)-1;
                var staffNumArr = $("#instructorGrid").jqGrid('getCol', "StaffNum");
                var uniArr = $("#instructorGrid").jqGrid('getCol', "University");
                var staffNumIndices = staffNumArr.reduce(function(a, e, i) {
                    if (e === postdata.StaffNum)
                        a.push(i);
                    return a;
                }, []);
                for ( var i = 0; i < staffNumIndices.length; i++){
                    if(postdata.University == uniArr[staffNumIndices[i]] && staffNumIndices[i] != selRowNum){
                        return[false,"This staff number already exists."];
                    }
                }
                return [true,""];
            },
            afterShowForm: populateFaculty,
            afterSubmit:function()
            {
                $("#instructorGrid").jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
                return [true,"",''];
            },
            closeAfterEdit: true,
            errorTextFormat: function (data) {
                return 'Error: ' + data.responseText;
            }
        }, // options for the Edit Dialog
        {
            beforeSubmit : function(postdata, formid) {
                var staffNumArr = $("#instructorGrid").jqGrid('getCol', "StaffNum");
                var uniArr = $("#instructorGrid").jqGrid('getCol', "University");
                var staffNumIndices = staffNumArr.reduce(function(a, e, i) {
                    if (e === postdata.StaffNum)
                        a.push(i);
                    return a;
                }, []);
                for ( var i = 0; i < staffNumIndices.length; i++){
                    if(postdata.University == uniArr[staffNumIndices[i]]){
                        return[false,"This staff number already exists."];
                    }
                }
                return [true,""];
            },
            afterShowForm: populateFaculty,
            afterSubmit:function()
            {
                $("#instructorGrid").jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
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
                $("#instructorGrid").jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
                return [true,"",''];
            },
            errorTextFormat: function (data) {
                return 'Error: ' + data.responseText;
            }
        }, // delete options
        { multipleSearch: true }
    ).navButtonAdd('#instructorPager',{
        caption:"",
        buttonicon:"glyphicon-floppy-save",
        onClickButton: function(){
            $("#instructorGrid").jqGrid("exportToExcel",{
                includeLabels : true,
                includeGroupHeader : true,
                includeFooter: true,
                fileName : "InstructorRecords.xlsx",
                maxlength : 40 // maxlength for visible string data
            })},
        position: "last",
        title:"Export to Excel",
        cursor: "pointer"
    }).navButtonAdd('#instructorPager',{
        caption:"",
        buttonicon:"glyphicon-open-file",
        onClickButton: function(){
            $("#importDialog").dialog("open");
        },
        position: "last",
        title:"Import Instructors",
        cursor: "pointer",
        id: "importInstructor"
    }).navButtonAdd('#instructorPager',{
        caption:"",
        buttonicon:"glyphicon-download-alt",
        onClickButton: function(){
            window.open('files/Import_Instructor_Template.xlsx');
        },
        position: "last",
        title:"Download Template",
        cursor: "pointer",
        id: "downloadTemplate"
    });
    $("#importDialog").dialog({
        autoOpen: false,
        show: {
            effect: "blind",
            duration: 600
        },
        hide: {
            effect: "explode",
            duration: 600
        },
        modal: true
    });
});

function getUniversities() {
    var options = "";
    $.ajax({
        url: '/getUniversities',
        type: "GET",
        async: false,
        success: function(data){
            var universities = eval(data);
            options += ":;";
            for (var i = 0; i < universities.length; i++){
                options += universities[i].University + ":" + universities[i].University + ";";
            }
        }
    });
    return options.substr(0, options.length-1);
}
// This function gets called whenever an edit dialog is opened
function populateFaculty() {
    var rowID = $("#instructorGrid").jqGrid('getGridParam',"selrow");
    var rowData = jQuery(this).getRowData(rowID);
    // first update the faculty based on the university
    updateFacultyCallback($("#University").val());
    // set the default faculty to the pre-edited value
    $("#Faculty").val(rowData['Faculty']);
    // then hook the change event of the university dropdown so that it updates faculties all the time
    $("#University").bind("change", function(e) {
        updateFacultyCallback($("#University").val());
    });
}
function updateFacultyCallback(university) {
    $("#Faculty")
        .html("<option value=''>Loading faculties...</option>")
        .attr("disabled", "disabled");
    $.ajax({
        url: "/getFaculties",
        type: "GET",
        async: false,
        data: {"university": university},
        success: function (data) {
            var faculties = eval(data);
            var facultiesHtml = "";
            for( var i = 0; i < faculties.length; i++){
                facultiesHtml += '<option value="' + faculties[i].Faculty + '">' + faculties[i].Faculty + '</option>';
            }
            $("#Faculty").removeAttr("disabled").html(facultiesHtml);
        }
    });
}
function createGenderEditElement(value) {
    var div = $("<div></div>");
    var option1 = $("<span style='margin-right: 40px'></span>");
    var label1 = $("<label for='male'>Male</label>");
    var radio1 = $("<input>", { type: "radio", value: "M", name: "gender", id: "male", style: "width: 20px;", checked: value == "M"});
    option1.append(radio1).append(label1);
    var option2 = $("<span style='margin-right: 40px'></span>");
    var label2 = $("<label for='female'>Female</label>");
    var radio2 = $("<input>", { type: "radio", value: "F", name: "gender", id: "female", style: "width: 20px;", checked: value == "F"});
    option2.append(radio2).append(label2);
    div.append(option1).append(option2);
    radio1.prop("checked",true);
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
function importFile() {
    var excelRegex = /\.xl.*$/;
    if($("#excelFile").val() == "" || excelRegex.test($("#excelFile").val()) == false ){
        $("#excelFileLabel").addClass("text-danger").text("*Please choose an excel file to upload.");
        return false;
    }
    else{
        $("#importForm").attr("action","/importInstructor");
        return true;
    }
}

$(window).resize(function(){
    var gridWidth = $("#instructorGridDiv").width();
    var gridHeight = $("#instructorGridDiv").height() - 120;
    $("#instructorGrid").setGridWidth(gridWidth).setGridHeight(gridHeight);
});