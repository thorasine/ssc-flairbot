$(function () {
    $('#submit-new-period').click(saveForm);
});

$(function () {
    $('#submit-request-user').click(getUserById);
});

$(function () {
    $('#delete').click(changeDeleteModal);
});

function saveForm() {
    var csrfHeader = $("meta[name='_csrf_header']").attr("content");
    var csrfToken = $("meta[name='_csrf']").attr("content");
    var headers = {};
    headers[csrfHeader] = csrfToken;

    $.ajax({
        method: "POST",
        headers: headers,
        url: "/addSummoner",
        data: $('#new-period-form').serialize(),
        success: function (status) {     
            $('#error-message').text("Summoner doesnt exist!");
            console.log("success thing: " + status);
        },
        error: function (status) {
            console.log("fail thing: " + JSON.stringify(status));
        }
    });
}

//This works too
function saveForm2() {
    var data = $('#new-period-form').serialize();
    // Add parameter and index of item that is going to be removed.
    //data += 'removeItem=' + $(this).val();
    $.post('/addSummoner', data);
}

function getSomething() {
    $.get("/allUsers", function (data, status) {
        for (var item in data) {
            console.log(data[item]);
        }
    });
}

function getUserById() {
    var bla = $('#request-input').val();
    $.get("/userById?id=" + bla, function (data, status) {
        console.log(data);
    });
}

function changeDeleteModal() {
    var removableDiv = $(this).parent();
    var accountId = $(this).parent().attr('id');
    var summonerName = $(this).attr('summonerName');
    var server = $(this).parent().attr('server');
    console.log(accountId);
    console.log(summonerName);
    console.log(server);
}