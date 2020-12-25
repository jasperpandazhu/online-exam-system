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

    $("#studentGrid").jqGrid({
        url: '/getStudent',
        editurl: '/editStudent',
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
                label: 'Student Number',
                name: 'StudentNum',
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
                formoptions: {label: 'Student Number *'},
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
                label: 'Major',
                name: 'Major',
                width: 150,
                editable: true,
                edittype: "select",
                editoptions: {
                    value: "Select a Major"
                }
            },
            {
                label: 'Year',
                name: 'Year',
                formatter: 'integer',
                formatoptions: {
                    thousandsSeparator: ""
                },
                width: 50,
                editable: true,
                edittype: "custom",
                editoptions: {
                    custom_value: getElementValue,
                    custom_element: createYearEditElement
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
            },
            {
                label: 'Courses',
                name: 'Courses',
                width: 300,
                editable: true
            }
        ],
        viewrecords: true, // show the current page, data range and total records on the toolbar
        width: $("#studentGridDiv").width(),
        height: $("#studentGridDiv").height() - 120,
        shrinkToFit: false, // need to tobe set for frozen columns to take effect
        rowNum: 10,
        // rowList: [10,20,30,40],
        rownumbers: false,
        loadonce: true,
        hidegrid: false,
        pager: "#studentPager",
        emptyrecords: "Nothing to display",
        caption: "Edit Students" // set caption to any string you wish and it will appear on top of the grid

    });

    $("#studentGrid").jqGrid('bindKeys');
    $("#studentGrid").jqGrid("setFrozenColumns");

    // pager settings
    $("#studentGrid").navGrid("#studentPager", userLevelRules,
        // { edit: true, add: true, del: true, search: true, refresh: true, view: false, align: "left" },
        {
            editCaption: "The Edit Dialog",
            recreateForm: true,
            checkOnUpdate : true,
            checkOnSubmit : true,
            beforeSubmit : function(postdata, formid) {
                var selRowId = $("#studentGrid").jqGrid ('getGridParam', 'selrow');
                var selRowNum = $("#studentGrid").jqGrid('getInd',selRowId)-1;
                var studentNumArr = $("#studentGrid").jqGrid('getCol', "StudentNum");
                var uniArr = $("#studentGrid").jqGrid('getCol', "University");
                var studentNumIndices = studentNumArr.reduce(function(a, e, i) {
                    if (e === postdata.StudentNum)
                        a.push(i);
                    return a;
                }, []);
                for ( var i = 0; i < studentNumIndices.length; i++){
                    if(postdata.University == uniArr[studentNumIndices[i]] && studentNumIndices[i] != selRowNum){
                        return[false,"This student number already exists."];
                    }
                }
                return [true,""];
            },
            afterShowForm: populateFacultyAndMajor,
            afterSubmit:function()
            {
                $("#studentGrid").jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
                return [true,"",''];
            },
            closeAfterEdit: true,
            errorTextFormat: function (data) {
                return 'Error: ' + data.responseText;
            }
        }, // options for the Edit Dialog
        {
            beforeSubmit : function(postdata, formid) {
                var studentNumArr = $("#studentGrid").jqGrid('getCol', "StudentNum");
                var uniArr = $("#studentGrid").jqGrid('getCol', "University");
                var studentNumIndices = studentNumArr.reduce(function(a, e, i) {
                    if (e === postdata.StudentNum)
                        a.push(i);
                    return a;
                }, []);
                for ( var i = 0; i < studentNumIndices.length; i++){
                    if(postdata.University == uniArr[studentNumIndices[i]]){
                        return[false,"This student number already exists."];
                    }
                }
                return [true,""];
            },
            afterShowForm: populateFacultyAndMajor,
            afterSubmit:function()
            {
                $("#studentGrid").jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
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
                $("#studentGrid").jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
                return [true,"",''];
            },
            errorTextFormat: function (data) {
                return 'Error: ' + data.responseText;
            }
        }, // delete options
        { multipleSearch: true }
    ).navButtonAdd('#studentPager',{
        caption:"",
        buttonicon:"glyphicon-floppy-save",
        onClickButton: function(){
            $("#studentGrid").jqGrid("exportToExcel",{
                includeLabels : true,
                includeGroupHeader : true,
                includeFooter: true,
                fileName : "StudentRecords.xlsx",
                maxlength : 40 // maxlength for visible string data
            })},
        position: "last",
        title:"Export to Excel",
        cursor: "pointer"
    }).navButtonAdd('#studentPager',{
        caption:"",
        buttonicon:"glyphicon-open-file",
        onClickButton: function(){
            $("#importDialog").dialog("open");
        },
        position: "last",
        title:"Import Students",
        cursor: "pointer",
        id: "importStudent"
    }).navButtonAdd('#studentPager',{
        caption:"",
        buttonicon:"glyphicon-download-alt",
        onClickButton: function(){
            window.open('files/Import_Student_Template.xlsx');
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
function populateFacultyAndMajor() {
    var rowID = $("#studentGrid").jqGrid('getGridParam',"selrow");
    var rowData = jQuery(this).getRowData(rowID);
    // first update the faculty based on the university
    updateFacultyCallback($("#University").val());
    // set the default faculty to the pre-edited value
    $("#Faculty").val(rowData['Faculty']);
    // then update the major based on the faculty
    updateMajorCallback($("#University").val(), $("#Faculty").val());
    // set the default major to the pre-edited value
    $("#Major").val(rowData['Major']);
    // hook the change event of the faculty dropdown so that it updates majors all the time
    $("#Faculty").bind("change", function(e) {
        updateMajorCallback($("#University").val(), $("#Faculty").val());
    });
    // hook the change event of the university dropdown so that it updates faculties and majors all the time
    $("#University").bind("change", function(e) {
        updateFacultyCallback($("#University").val());
        updateMajorCallback($("#University").val(), $("#Faculty").val());
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
function updateMajorCallback(university,faculty) {
    $("#Major")
        .html("<option value=''>Loading majors...</option>")
        .attr("disabled", "disabled");
    $.ajax({
        url: "/getMajors",
        type: "GET",
        async: false,
        data: {"university":university,"faculty": faculty},
        success: function (data) {
            var majors = eval(data);
            var majorsHtml = "";
            for( var i = 0; i < majors.length; i++){
                majorsHtml += '<option value="' + majors[i].Major + '">' + majors[i].Major + '</option>';
            }
            $("#Major").removeAttr("disabled").html(majorsHtml);
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
function createYearEditElement(value) {
    var div = $("<div></div>");
    var option1 = $("<span style='margin-right: 40px'></span>");
    var label1 = $("<label for='one'>1</label>");
    var radio1 = $("<input>", { type: "radio", value: "1", name: "year", id: "one", style: "width: 20px;", checked: value == 1});
    option1.append(radio1).append(label1);
    var option2 = $("<span style='margin-right: 40px'></span>");
    var label2 = $("<label for='two'>2</label>");
    var radio2 = $("<input>", { type: "radio", value: "2", name: "year", id: "two", style: "width: 20px;", checked: value == 2});
    option2.append(radio2).append(label2);
    var option3 = $("<span style='margin-right: 40px'></span>");
    var label3 = $("<label for='three'>3</label>");
    var radio3 = $("<input>", { type: "radio", value: "3", name: "year", id: "three", style: "width: 20px;", checked: value == 3});
    option3.append(radio3).append(label3);
    var option4 = $("<span style='margin-right: 40px'></span>");
    var label4 = $("<label for='four'>4</label>");
    var radio4 = $("<input>", { type: "radio", value: "4", name: "year", id: "four", style: "width: 20px;", checked: value == 4});
    option4.append(radio4).append(label4);
    div.append(option1).append(option2).append(option3).append(option4);
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
        $("#importForm").attr("action","/importStudent");
        return true;
    }
}

$(window).resize(function(){
    var gridWidth = $("#studentGridDiv").width();
    var gridHeight = $("#studentGridDiv").height() - 120;
    $("#studentGrid").setGridWidth(gridWidth).setGridHeight(gridHeight);
});