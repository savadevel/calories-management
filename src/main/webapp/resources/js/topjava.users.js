const userAjaxUrl = "admin/users/";

// https://stackoverflow.com/a/5064235/548473
const ctx = {
    ajaxUrl: userAjaxUrl
};

// $(document).ready(function () {
$(function () {
    makeEditable(
        $("#datatable").DataTable({
            "paging": false,
            "info": true,
            "columns": [
                {
                    "data": "name"
                },
                {
                    "data": "email"
                },
                {
                    "data": "roles"
                },
                {
                    "data": "enabled"
                },
                {
                    "data": "registered"
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
                    "asc"
                ]
            ]
        })
    );

    ctx.datatableApi.on("change", "input", function() {
        const row = $(this).closest('tr');
        const userId = row.attr('id');
        const enabled = $(this).prop('checked');
        $.ajax({
            method: "POST",
            url: ctx.ajaxUrl + userId,
            data: "enabled=" + enabled,
            success : function () {
                row.attr("data-enabled", enabled);
                successNoty(enabled ? "Active" : "Disactive");
            }
        });
    });
});

function updateTable() {
    $.get(ctx.ajaxUrl, function (data) {
        ctx.datatableApi.clear().rows.add(data).draw();
    });
}