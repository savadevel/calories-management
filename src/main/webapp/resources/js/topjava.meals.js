const mealAjaxUrl = "profile/meals/";

// https://stackoverflow.com/a/5064235/548473
const ctx = {
    ajaxUrl: mealAjaxUrl,
    updateTable: function () {
        $.ajax({
            type: "GET",
            url: "profile/meals/filter",
            data: $("#filter").serialize()
        }).done(updateTableByData);
    }
}

function clearFilter() {
    $("#filter")[0].reset();
    $.get("profile/meals/", updateTableByData);
}

$(function () {
    makeEditable(
        $("#datatable").DataTable({
            ajax: {
                url: ctx.ajaxUrl,
                dataSrc: ''
            },
            paging: false,
            info: true,
            columns: [
                {
                    data: "dateTime",
                    render: function (data, type) {
                        if (type === 'display') {
                            const dateTime = new Date(data).toISOString().substring(0, 16).replace("T", " ");
                            return '<span>' + dateTime + '</span>';
                        }
                        return data;
                    }
                },
                {
                    data: "description"
                },
                {
                    data: "calories"
                },
                {
                    defaultContent: "Edit",
                    orderable: false,
                    render: renderEditBtn

                },
                {
                    defaultContent: "Delete",
                    orderable: false,
                    render: renderDeleteBtn
                }
            ],
            order: [
                [
                    0,
                    "desc"
                ]
            ],
            createdRow: function (row, data) {
                $(row).attr("data-mealExcess", data.excess);
            }
        })
    );
});