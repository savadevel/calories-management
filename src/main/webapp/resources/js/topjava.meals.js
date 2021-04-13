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

$(document).on("ajaxSend", function (event, jqxhr, settings) {
    if (settings.url === mealAjaxUrl && settings.type === "POST") {
        const controlDateTime = $("#dateTime")
        const dateTime = controlDateTime.val();
        controlDateTime.val(dateTime.replace(/\s+/, "T"));
        settings.data = $('#detailsForm').serialize();
        controlDateTime.val(dateTime);
    }
}).on("ajaxSuccess", function (event, jqxhr, settings) {
    if (jqxhr.hasOwnProperty("responseJSON") && jqxhr.responseJSON.hasOwnProperty("dateTime")) {
        $("#dateTime").val(convertDateTimeFromIsoToUi(jqxhr.responseJSON.dateTime));
    }

});

function clearFilter() {
    $("#filter")[0].reset();
    $.get("profile/meals/", updateTableByData);
}

$(function () {
    setDateRange("#startDate", "#endDate");
    setTimeRange("#startTime", "#endTime");
    setDateTimeFormat("#dateTime");

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
                            return '<span>' + convertDateTimeFromIsoToUi(data) + '</span>';
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

function setDateRange(startId, endId) {
    setDateFormat(startId);
    setDateFormat(endId);
    setLimit(startId, endId, "maxDate");
    setLimit(endId, startId, "minDate");
}

function setTimeRange(startId, endId) {
    setTimeFormat(startId);
    setTimeFormat(endId);
    setLimit(startId, endId, "maxTime");
    setLimit(endId, startId, "minTime");
}

function setLimit(startId, endId, limit) {
    $(startId).datetimepicker({
        onShow: function () {
            this.setOptions({
                [limit]: $(endId).val() ? $(endId).val() : false
            });
        }
    });

}

function setTimeFormat(id) {
    $(id).datetimepicker({datepicker: false, format: "H:i"});
}

function setDateFormat(id) {
    $(id).datetimepicker({timepicker: false, format: "Y-m-d"});
}

function setDateTimeFormat(id) {
    $(id).datetimepicker({format: "Y-m-d H:i"});
}

function convertDateTimeFromIsoToUi(dateTime) {
    return dateTime.substring(0, 16).replace("T", " ");
}