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

    $("#universityGrid").jqGrid({
        url: '/getUniversity',
        editurl: '/editUniversity',
        datatype: "json",
        colModel: [
            { label: 'Uid', name: 'Uid', width: 60, key: true, editable: true },
            { label: 'University', name: 'University', width: 150, editable: true },
            { label: 'Faculties', width: 40, align: 'center', formatter: showFaculty }
        ],
        viewrecords: true, // show the current page, data range and total records on the toolbar
        width: $("#universityGridDiv").width(),
        height: $("#universityGridDiv").height() - 120,
        shrinkToFit: true, // need to tobe set for frozen columns to take effect
        rowNum: 10,
        rownumbers: false,
        loadonce: true,
        hidegrid: false,
        pager: "#universityPager",
        emptyrecords: "Nothing to display",
        caption: "Edit Universities" // set caption to any string you wish and it will appear on top of the grid
    });
    $("#universityGrid").jqGrid('bindKeys');
    // pager settings
    $("#universityGrid").navGrid("#universityPager", userLevelRules,
        // {edit: true, add: true, del: true, search: true, refresh: true, view: false, align: "left"},
        {
            editCaption: "The Edit Dialog",
            recreateForm: true,
            checkOnUpdate: true,
            checkOnSubmit: true,
            afterSubmit: function () {
                $("#universityGrid").jqGrid('setGridParam', {datatype: 'json'}).trigger('reloadGrid');
                return [true, "", ''];
            },
            closeAfterEdit: true,
            errorTextFormat: function (data) {
                return 'Error: ' + data.responseText;
            }
        }, // options for the Edit Dialog
        {
            afterSubmit: function () {
                $("#universityGrid").jqGrid('setGridParam', {datatype: 'json'}).trigger('reloadGrid');
                return [true, "", ''];
            },
            closeAfterAdd: true,
            recreateForm: true,
            errorTextFormat: function (data) {
                return 'Error: ' + data.responseText;
            }
        }, // options for the Add Dialog
        {
            afterSubmit: function () {
                $("#universityGrid").jqGrid('setGridParam', {datatype: 'json'}).trigger('reloadGrid');
                return [true, "", ''];
            },
            errorTextFormat: function (data) {
                return 'Error: ' + data.responseText;
            }
        }, // delete options
        {multipleSearch: true}
    );

    $("#facultyGrid").jqGrid({
        mtype: "GET",
        datatype: "json",
        page: 1,
        colModel: [
            { label: 'Uid', name: 'Uid', width: 60, editable: false },
            { label: 'Fid', name: 'Fid', width: 40, editable: true, key: true },
            { label: 'Faculty', name: 'Faculty', width: 150, editable: true },
            { label: 'Majors', width: 40, align: 'center', formatter: showMajor }
        ],
        viewrecords: true,
        loadonce: false,
        width: $("#universityGridDiv").width()*0.945,
        height: $("#universityGridDiv").height()*0.85 - 70,
        shrinkToFit: true,
        hidegrid: false,
        pager: "#facultyPager"
    });
    $("#facultyGrid").jqGrid('bindKeys');
    // pager settings
    $("#facultyGrid").navGrid("#facultyPager", userLevelRules,
        // {edit: true, add: true, del: true, search: true, refresh: true, view: false, align: "left"},
        {
            editCaption: "The Edit Dialog",
            recreateForm: true,
            checkOnUpdate: true,
            checkOnSubmit: true,
            afterSubmit: function () {
                $("#facultyGrid").jqGrid('setGridParam', {datatype: 'json'}).trigger('reloadGrid');
                return [true, "", ''];
            },
            closeAfterEdit: true,
            errorTextFormat: function (data) {
                return 'Error: ' + data.responseText;
            }
        }, // options for the Edit Dialog
        {
            afterSubmit: function () {
                $("#facultyGrid").jqGrid('setGridParam', {datatype: 'json'}).trigger('reloadGrid');
                return [true, "", ''];
            },
            closeAfterAdd: true,
            recreateForm: true,
            errorTextFormat: function (data) {
                return 'Error: ' + data.responseText;
            }
        }, // options for the Add Dialog
        {
            afterSubmit: function () {
                $("#facultyGrid").jqGrid('setGridParam', {datatype: 'json'}).trigger('reloadGrid');
                return [true, "", ''];
            },
            errorTextFormat: function (data) {
                return 'Error: ' + data.responseText;
            }
        }, // delete options
        {multipleSearch: true});

    $("#majorGrid").jqGrid({
        mtype: "GET",
        datatype: "json",
        page: 1,
        colModel: [
            { label: 'Uid', name: 'Uid', width: 60, editable: false },
            { label: 'Fid', name: 'Fid', width: 40, editable: false },
            { label: 'Mid', name: 'Mid', width: 40, key: true, editable: true },
            { label: 'Major', name: 'Major', width: 150, editable: true }
        ],
        viewrecords: true,
        loadonce: false,
        width: $("#universityGridDiv").width()*0.925,
        height: $("#universityGridDiv").height()*0.8 - 90,
        shrinkToFit: true,
        hidegrid: false,
        pager: "#majorPager"
    });
    $("#majorGrid").jqGrid('bindKeys');
    // pager settings
    $("#majorGrid").navGrid("#majorPager", userLevelRules,
        // {edit: true, add: true, del: true, search: true, refresh: true, view: false, align: "left"},
        {
            editCaption: "The Edit Dialog",
            recreateForm: true,
            checkOnUpdate: true,
            checkOnSubmit: true,
            afterSubmit: function () {
                $("#majorGrid").jqGrid('setGridParam', {datatype: 'json'}).trigger('reloadGrid');
                return [true, "", ''];
            },
            closeAfterEdit: true,
            errorTextFormat: function (data) {
                return 'Error: ' + data.responseText;
            }
        }, // options for the Edit Dialog
        {
            afterSubmit: function () {
                $("#majorGrid").jqGrid('setGridParam', {datatype: 'json'}).trigger('reloadGrid');
                return [true, "", ''];
            },
            closeAfterAdd: true,
            recreateForm: true,
            errorTextFormat: function (data) {
                return 'Error: ' + data.responseText;
            }
        }, // options for the Add Dialog
        {
            afterSubmit: function () {
                $("#majorGrid").jqGrid('setGridParam', {datatype: 'json'}).trigger('reloadGrid');
                return [true, "", ''];
            },
            errorTextFormat: function (data) {
                return 'Error: ' + data.responseText;
            }
        }, // delete options
        {multipleSearch: true});
    $("#facultyDialog").dialog({
        autoOpen: false,
        show: {
            effect: "fadeIn",
            duration: 600
        },
        hide: {
            effect: "fadeOut",
            duration: 600
        },
        width: $("#universityGridDiv").width()*0.98,
        height: $("#universityGridDiv").height()*0.98
    });
    $("#majorDialog").dialog({
        autoOpen: false,
        show: {
            effect: "fadeIn",
            duration: 600
        },
        hide: {
            effect: "fadeOut",
            duration: 600
        },
        width: $("#universityGridDiv").width()*0.96,
        height: $("#universityGridDiv").height()*0.96
    });
});

function showFaculty(cellvalue, options, rowObject) {
    return "<a class='btn btn-primary btn-sm' id='btn" + rowObject.Uid + "' href='javascript: showFacultyGrid(" + rowObject.Uid + ")'>View</a>";
}
function showFacultyGrid(Uid){
    $("#facultyGrid").jqGrid('setGridParam', { url: "/getFaculty?Uid="+Uid, editurl: "/editFaculty?Uid="+Uid }).trigger('reloadGrid');
    $("#facultyDialog").dialog("open");
}

function showMajor(cellvalue, options, rowObject) {
    return "<a class='btn btn-primary btn-sm' style='color: white' id='btn" + rowObject.Fid + "' href='javascript: showMajorGrid(" + rowObject.Uid + ", " + rowObject.Fid + ")'>View</a>";
}
function showMajorGrid(Uid, Fid){
    $("#majorGrid").jqGrid('setGridParam', { url: "/getMajor?Uid="+Uid+"&Fid="+Fid, editurl: "/editMajor?Uid="+Uid+"&Fid="+Fid }).trigger('reloadGrid');
    $("#majorDialog").dialog("open");
}

$(window).resize(function () {
    var divWidth = $("#universityGridDiv").width();
    var divHeight = $("#universityGridDiv").height();
    $("#universityGrid").setGridWidth(divWidth).setGridHeight(divHeight - 120);
    $("#facultyDialog").dialog({ width: divWidth*0.98, height: divHeight*0.98 });
    $("#facultyGrid").setGridWidth($("#facultyDialog").width()).setGridHeight($("#facultyDialog").height() - 85);
    $("#majorDialog").dialog({ width: divWidth*0.96, height: divHeight*0.96 });
    $("#majorGrid").setGridWidth($("#majorDialog").width()).setGridHeight($("#majorDialog").height() - 125);
})