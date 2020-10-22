$(function () {
    reloadCards();
    $('#newSummonerBtn').click(newSummonerPost);
    $('.deleteBtnSpan').click(setModalDelAccountTexts);
    $('#deleteConfirmBtn').click(deleteSummoner);
    $('#summonerName').on("keyup", newSummonerModalButtonEnabler);
    $('#region').on("change", newSummonerModalButtonEnabler);

    $('#videoExampleToggle').click(videoPlayToggle);
    $("#verificationVideo").hide();
    $('#modalValidate').on('hidden.bs.modal', validationModalClose);
    $('#modalNewSummoner').on('hidden.bs.modal', newSummonderModalClose);
});

function newSummonderModalClose() {
    $('#newSummonerErrorText').text("");
    $('#summonerName').val("");
}

function validationModalClose() {
    $("#validationExampleCollapse").removeClass('collapse show').addClass('collapse');
    $("#verificationVideo").hide();
}

function videoPlayToggle() {
    if ($('#validationExampleCollapse').is(":hidden")) {
        $("#verificationVideo").show();
        $("#verificationVideo")[0].currentTime = 0;
        $("#verificationVideo")[0].play();
    } else {
        $("#verificationVideo")[0].pause();
    }
}

//Loads the summoner cards in
function reloadCards() {
    $("#cardsContainer").load('/summonerCards', function () {
        //this one is required for the fancy tooltips
        $('[data-toggle="tooltip"]').tooltip();
        $('.deleteBtnSpan').click(setModalDelAccountTexts);
        $('.validationModalOpenBtn').click(setModalValidateAccountTexts);
    });
}

function newSummonerModalButtonEnabler() {
    if ($('#summonerName').val().length > 0) {
        $('#newSummonerBtn').prop("disabled", false);
    } else {
        $('#newSummonerBtn').prop("disabled", true);
    }
}

function newSummonerPost() {
    $('#newSummonerBtn').text('Loading..');
    $('#newSummonerBtn').prop("disabled", true);

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
                showNoty();
                reloadCards();
            } else {
                $('#newSummonerErrorText').text(status);
            }
            $('#newSummonerBtn').text('Register');
        },
        error: function (status) {
            console.log("failed: " + JSON.stringify(status));
        }
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
        },
        error: function (status) {
            accountId = "";
            console.log("failed: " + JSON.stringify(status));
        }
    });
}

function setModalValidateAccountTexts() {
    var validationCode = $(this).attr("validation-code");
    var summonerName = $(this).parent().parent().children('.summonerName').text();
    var server = $(this).parent().parent().children('.server').text();
    $('#validationModalTexts').text(summonerName + " (" + server + ")");
    $('#validationCodeText').text(validationCode);
}

function showNoty() {
    new Noty({
        type: 'info',
        layout: 'topRight',
        theme: 'sunset',
        text: 'Summoner registered successfully',
        timeout: '3000',
        progressBar: true,
        closeWith: ['click'],
        killer: true
    }).show();
}