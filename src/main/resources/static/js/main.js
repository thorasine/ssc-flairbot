$(function () {
    $('#newSummonerModalBtn').click(loadNewSummonerModal);
});

//not working
$(function () {
    $('#newSummonerBtn').click(newSummonerPost);
});

$(function () {
    $('.deleteBtnSpan').click(modalDelSetAccountIdAndTexts);
});

$(function () {
    $('#deleteConfirmBtn').click(deleteSummoner);
});

var accountId;
var removableDiv;

//Loads the cards in
$(document).ready(function () {
    $("#cardsContainer").load('/summonerCards');
});

//test
$(function () {
    $("#container").on("click", function () {
        console.log("CLICKED");
        $('[data-toggle="tooltip"]').tooltip();
    });
});

//Making the newSummoner POST button work with dynamic load
$(function () {
    $("#modalContainer").on("click", "#newSummonerBtn", function () {
        newSummonerPost();
    });
});

//Loads the New Summoner modal in
function loadNewSummonerModal() {
    $("#modalContainer").load('/newSummonerModal');
    $("#modalContainer").modal('toggle');
    //$('#newSummonerBtn').click(newSummonerPost);
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
            //$('#modalContainer').modal('toggle');
            $("#cardsContainer").load('/summonerCards');
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

function modalDelSetAccountIdAndTexts() {
    removableDiv = $(this).parent().parent().parent();
    var id = $(this).parent().parent().parent().attr("id");
    var summonerName = $(this).parent().parent().children('.summonerName').text();
    var server = $(this).parent().parent().children('.server').text();
    accountId = id;
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
            //$('#error-message').text("Summoner doesnt exist!");
            //$('#container').load(document.URL + ' #cardsContainer');
            removableDiv.remove();
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

