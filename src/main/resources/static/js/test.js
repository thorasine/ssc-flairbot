$(function () {
    $('#testBtn').click(testFunction);
    $('#testBtn2').click(testFunction2);
});

function testFunction() {
    var csrfHeader = $("meta[name='_csrf_header']").attr("content");
    var csrfToken = $("meta[name='_csrf']").attr("content");
    var headers = {};
    headers[csrfHeader] = csrfToken;

    $.ajax({
        method: "POST",
        headers: headers,
        url: "/testFunction",
        //data: {"id": accountId},
        success: function (status) {
            console.log("success: " + JSON.stringify(status));
        },
        error: function (status) {
            console.log("failed: " + JSON.stringify(status));
        }
    });
}

function testFunction2() {
    var csrfHeader = $("meta[name='_csrf_header']").attr("content");
    var csrfToken = $("meta[name='_csrf']").attr("content");
    var headers = {};
    headers[csrfHeader] = csrfToken;

    $.ajax({
        method: "POST",
        headers: headers,
        url: "/testFunction2",
        //data: {"id": accountId},
        success: function (status) {
            console.log("success: " + JSON.stringify(status));
        },
        error: function (status) {
            console.log("failed: " + JSON.stringify(status));
        }
    });
}