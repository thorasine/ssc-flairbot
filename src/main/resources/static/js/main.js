$(function () {
    $('#newSummonerBtn').click(newSummonerPost);
});

$(function () {
    $('.deleteBtnSpan').click(modalDelSetAccountIdAndTexts);
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
        $('.deleteBtnSpan').click(modalDelSetAccountIdAndTexts);
    });
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
            $('#modalNewSummoner').modal('toggle');
            $('#summonerName').val("");
            reloadCards();
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
function modalDelSetAccountIdAndTexts() {
    var id = $(this).parent().parent().parent().attr("id");
    var summonerName = $(this).parent().parent().children('.summonerName').text();
    var server = $(this).parent().parent().children('.server').text();
    accountId = id;
    removableDiv = $(this).parent().parent().parent();
    console.log("accountId: " + accountId);
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
            //$('#error-message').text("Something bad happened!");
            //removableDiv.remove();
            removableDiv.fadeOut("slow", function () {
                removableDiv.remove();
            });

            $('#deleteSummoner').modal('toggle');
            accountId = "";
            console.log("success thing: " + status);
        },
        error: function (status) {
            accountId = "";
            console.log("fail thing: " + JSON.stringify(status));
        }
    });
}

