$(function () {
    $('#submit-new-period').click(getSomething);
});

function saveForm() {
    $.ajax({
        method: "POST",
        url: "/addSummoner",
        data: $('#new-period-form').serialize(),
        success: function (status) {
            console.log("success thing: " + status);
        },
        error: function (status) {
            console.log("fail thing: " + status);
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