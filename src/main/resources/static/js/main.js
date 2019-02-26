$(function () {
    $('#newSummonerBtn').click(newSummonerPost);
});

$(function () {
    $('.deleteBtnSpan').click(setModalDelAccountTexts);
});

$(function () {
    $('#deleteConfirmBtn').click(deleteSummoner);
});

$(function () {
    reloadCards();
});

//Loads the summoner cards in
function reloadCards() {
    $("#cardsContainer").load('/summonerCards', function () {
        $('[data-toggle="tooltip"]').tooltip();
        $('.deleteBtnSpan').click(setModalDelAccountTexts);
        $('.validationModalOpenBtn').click(setModalValidateAccountTexts);
    });
}

$(function () {
    $('#summonerName').on("keyup", newSummonerModalButtonEnabler);
});

function newSummonerModalButtonEnabler() {
    if ($('#summonerName').val().length > 0) {
        $('#newSummonerBtn').prop("disabled", false);
    } else {
        $('#newSummonerBtn').prop("disabled", true);
    }
}

function newSummonerPost() {
    var csrfHeader = $("meta[name='_csrf_header']").attr("content");
    var csrfToken = $("meta[name='_csrf']").attr("content");
    var headers = {};
    headers[csrfHeader] = csrfToken;

    $.ajax({
        method: "POST",
        headers: headers,
        url: "/addSummoner",
        data: $('#newSummonerForm').serialize(),
        success: function (status) {
            if (status === "ok") {
                $('#modalNewSummoner').modal('toggle');
                $('#summonerName').val("");
                $('#newSummonerErrorText').text("");
                reloadCards();
            } else {
                $('#newSummonerErrorText').text(status);
            }
            console.log("success thing: " + status);
        },
        error: function (status) {
            console.log("fail thing: " + JSON.stringify(status));
        }
    });
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

var accountId;
var removableDiv;
function setModalDelAccountTexts() {
    var id = $(this).parent().parent().parent().attr("id");
    var summonerName = $(this).parent().parent().children('.summonerName').text();
    var server = $(this).parent().parent().children('.server').text();
    accountId = id;
    removableDiv = $(this).parent().parent().parent();
    $('#deleteSummonerTexts').text(summonerName + " (" + server + ")");
}

function deleteSummoner() {
    var csrfHeader = $("meta[name='_csrf_header']").attr("content");
    var csrfToken = $("meta[name='_csrf']").attr("content");
    var headers = {};
    headers[csrfHeader] = csrfToken;

    $.ajax({
        method: "POST",
        headers: headers,
        url: "/deleteSummoner",
        data: {"id": accountId},
        success: function (status) {
            if (status === "ok") {
                removableDiv.fadeOut("slow", function () {
                    removableDiv.remove();
                });
                $('#deleteSummonerErrorText').text("");
                $('#deleteSummoner').modal('toggle');
                accountId = "";
            } else {
                $('#deleteSummonerErrorText').text(status);
            }
            console.log("success thing: " + status);
        },
        error: function (status) {
            accountId = "";
            console.log("fail thing: " + JSON.stringify(status));
        }
    });
}

function setModalValidateAccountTexts() {
    var validationCode = $(this).attr("validation-code");
    var summonerName = $(this).parent().parent().children('.summonerName').text();
    var server = $(this).parent().parent().children('.server').text();
    $('#validationModalTexts').text(summonerName + " (" + server + ")");
    $('#ValidationCodeText').text(validationCode);
}
