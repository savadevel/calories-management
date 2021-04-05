const userAjaxUrl = "profile/meals/";
let filterForm;


// https://stackoverflow.com/a/5064235/548473
const ctx = {
    ajaxUrl: userAjaxUrl
};

// $(document).ready(function () {
$(function () {
    filterForm = $("#filterForm");
    makeEditable(
        $("#datatable").DataTable({
            "paging": false,
            "info": true,
            "columns": [
                {
                    "data": "dateTime"
                },
                {
                    "data": "description"
                },
                {
                    "data": "calories"
                },
                {
                    "defaultContent": "Edit",
                    "orderable": false
                },
                {
                    "defaultContent": "Delete",
                    "orderable": false
                }
            ],
            "order": [
                [
                    0,
                    "desc"
                ]
            ]
        })
    );
});

function updateTable() {
    $.ajax({
        method: "GET",
        url: ctx.ajaxUrl + "filter",
        data: filterForm.serialize(),
        success : datatableDraw
    });
}

function clearFilter() {
    filterForm.find(":input").val("");
    updateTable();
}