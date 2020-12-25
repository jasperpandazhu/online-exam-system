var userLevelRules;
$(document).ready(function () {
    // get user level
    var userLevel = getCookie("userLevel");
    switch (userLevel) {
        case "1":
            userLevelRules = { edit: true, add: true, del: true, search: true, refresh: true, view: false, align: "left" };
            break;
        case "2":
            userLevelRules = { edit: false, add: false, del: false, search: true, refresh: true, view: false, align: "left" };
            break;
        default:
            userLevelRules = { edit: false, add: false, del: false, search: true, refresh: true, view: false, align: "left" };
            break;
    }

    $("#adminGrid").jqGrid({
        url: '/getAdmin',
        editurl: 'editAdmin',
        datatype: "json",
        colModel: [
            {
                label: 'Id',
                name: 'id',
                width: 40,
                editable: false
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
                label: 'Username',
                name: 'Username',
                width: 120,
                editable: true,
                edittype: "text",
                editrules: {
                    required: true
                },
                formoptions: {label: 'Username *'}
            },

            {
                label: 'Password',
                name: 'Password',
                hidden: true,
                width: 150,
                editable: true,
                edittype: "password",
                editrules: {
                    number: true,
                    edithidden: true
                }
            },
            {
                label: 'User Level',
                name: 'UserLevel',
                width: 80,
                editable: true,
                edittype: "custom",
                editoptions: {
                    custom_value: getElementValue,
                    custom_element: createLevelEditElement
                },
                sorttype: 'number'
            }
        ],
        viewrecords: true, // show the current page, data range and total records on the toolbar
        autowidth: true,
        height: $("#adminGridDiv").height() - 120,
        shrinkToFit: true, // need to tobe set for frozen columns to take effect
        rowNum: 10,
        rownumbers: false,
        loadonce: true,
        hidegrid: false,
        pager: "#adminPager",
        emptyrecords: "Nothing to display",
        caption: "Edit Admin" // set caption to any string you wish and it will appear on top of the grid
    });

    $("#adminGrid").jqGrid('bindKeys');

    $("#adminGrid").navGrid("#adminPager", userLevelRules,
        // { edit: true, add: true, del: true, search: true, refresh: true, view: false, align: "left" },
        {
            editCaption: "The Edit Dialog",
            recreateForm: true,
            checkOnUpdate : true,
            checkOnSubmit : true,
            afterSubmit:function()
            {
                $("#adminGrid").jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
                return [true,"",''];
            },
            closeAfterEdit: true,
            errorTextFormat: function (data) {
                return 'Error: ' + data.responseText
            }
        }, // options for the Edit Dialog
        {
            afterSubmit:function()
            {
                $("#adminGrid").jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
                return [true,"",''];
            },
            closeAfterAdd: true,
            recreateForm: true,
            errorTextFormat: function (data) {
                return 'Error: ' + data.responseText
            }
        }, // options for the Add Dialog
        {
            afterSubmit:function()
            {
                $("#adminGrid").jqGrid('setGridParam',{datatype:'json'}).trigger('reloadGrid');
                return [true,"",''];
            },
            errorTextFormat: function (data) {
                return 'Error: ' + data.responseText
            }
        }, // delete options
        { multipleSearch: true }
    ).navButtonAdd('#adminPager',{
        caption:"",
        buttonicon:"glyphicon-floppy-save",
        onClickButton: function(){
            $("#adminGrid").jqGrid("exportToExcel",{
                includeLabels : true,
                includeGroupHeader : true,
                includeFooter: true,
                fileName : "AdminRecords.xlsx",
                maxlength : 40 // maxlength for visible string data
            })},
        position: "last",
        title:"Export to Excel",
        cursor: "pointer"
    });

    function createLevelEditElement(value) {
        var div = $("<div></div>");
        var option1 = $("<span style='margin-right: 40px'></span>");
        var label1 = $("<label for='one'>1</label>");
        var radio1 = $("<input>", { type: "radio", value: "1", name: "userLevel", id: "one", style: "width: 20px;", checked: value == 1});
        option1.append(radio1).append(label1);
        var option2 = $("<span style='margin-right: 40px'></span>");
        var label2 = $("<label for='two'>2</label>");
        var radio2 = $("<input>", { type: "radio", value: "2", name: "userLevel", id: "two", style: "width: 20px;", checked: value == 2});
        option2.append(radio2).append(label2);
        var option3 = $("<span style='margin-right: 40px'></span>");
        var label3 = $("<label for='three'>3</label>");
        var radio3 = $("<input>", { type: "radio", value: "3", name: "userLevel", id: "three", style: "width: 20px;", checked: value == 3});
        option3.append(radio3).append(label3);
        div.append(option1).append(option2).append(option3);
        radio3.prop("checked",true);
        return div;
    }
    // The javascript executed specified by JQGridColumn.EditTypeCustomGetValue when EditType = EditType.Custom
    // One parameter passed - the custom element created in JQGridColumn.EditTypeCustomCreateElement
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
});

$(window).resize(function(){
    var gridWidth = $("#adminGridDiv").width();
    var gridHeight = $("#adminGridDiv").height() - 120;
    $("#adminGrid").setGridWidth(gridWidth).setGridHeight(gridHeight);
})