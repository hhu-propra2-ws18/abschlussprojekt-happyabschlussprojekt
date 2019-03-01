$(document).ready(function () {
    var time = new Date();
    var day = ("0" + time.getDate()).slice(-2);
    var month = ("0" + (time.getMonth() + 1)).slice(-2);
    var today = time.getFullYear() + "-" + (month) + "-" + (day);
    $('#mietezeitpunktStart').val(today);
    $('#mietezeitpunktEnd').val(today);
})

function makeTrue() {
    var a = true;
    $('#forsale').val(a);
}

function makeFalse() {
    var a = false;
    $('#forsale').val(a);
}

function checkKosten() {
    var kosten = $("#kosten").val();
    if (kosten == "") {
        alert("Bitte eingabe Kosten pro Tag!");
        $("#kosten").val("");
        return false;
    } else if (kosten <= "0") {
        alert("The nummber must be bigger than 0");
        $("#kosten").val("");
        return false;
    } else {
        for (i = 0; i < kosten.length; i++) {
            if ((kosten.charAt(i) < '0') || (kosten.charAt(i) > '9')) {
                alert("Eingabe muss eine Integer sein!");
                $("#kosten").val("");
                return false;
            }
        }
        return true;
    }
}

function checkKaution() {
    var kaution = $("#kaution").val();
    if (kaution == "") {
        alert("Bitte eingabe Kaution!");
        $("#kaution").val("");
        return false;
    } else if (kaution < "0") {
        alert("The nummber must be bigger than 0");
        $("#kaution").val("");
        return false;
    } else {
        for (i = 0; i < kaution.length; i++) {
            if ((kaution.charAt(i) < '0') || (kaution.charAt(i) > '9')) {
                alert("Eingabe muss eine Integer sein!");
                $("#kaution").val("");
                return false;
            }
        }
        return true;
    }
}

function checkStartTime() {
    var startTime = $("#mietezeitpunktStart").val();
    var array = startTime.split("-");
    var startDate = new Date(array[0], parseInt(array[1]) - 1, array[2]);

    var endTime = $("#mietezeitpunktEnd").val().toString();

    var nowDate = Date.now();
    if (startDate < (nowDate - (24 * 60 * 60 * 1000))) {
        alert("Das Startdatum darf nicht vor heute liegen.");
        $("#mietezeitpunktStart").val("");
        return false;
    } else {
        var arrayForEndTime = endTime.split("-");
        var endDate = new Date(arrayForEndTime[0], parseInt(arrayForEndTime[1]) - 1, arrayForEndTime[2]);
        if (startDate > endDate) {
            alert("Das StartDatum muss vor dem EndDatum liegen.");
            $("#mietezeitpunktStart").val("");
            return false;
        } else {
            return true;
        }
    }
}

function checkEndTime() {
    var endTime = $("#mietezeitpunktEnd").val();
    var array = endTime.split("-");
    var endDate = new Date(array[0], parseInt(array[1]) - 1, array[2]);

    var startTime = $("#mietezeitpunktStart").val().toString();

    var nowDate = Date.now();
    if (endDate < (nowDate - (24 * 60 * 60 * 1000))) {
        alert("Das Enddatum darf nicht vor heute liegen.");
        $("#mietezeitpunktEnd").val("");
        return false;
    } else {
        var arrayForStartTime = startTime.split("-");
        var startDate = new Date(arrayForStartTime[0], parseInt(arrayForStartTime[1]) - 1, arrayForStartTime[2]);
        if (startDate > endDate) {
            alert("Das EndDatum muss nach dem StartDatum liegen.");
            $("#mietezeitpunktEnd").val("");
            return false;
        } else {
            return true;
        }
    }
}

function checkFile(el) {
    var files = el.files;
    var allowTypes = ["image/jpeg", "image/png", "image/jpg", "image/gif"];
    var allowUpload = true;
    var errorMessage = "";
    for (var i = 0; i < files.length; i++) {
        var fileName = files[i].name;
        var fileType = files[i].type;
        var typeAccepted = false;
        for (var j = 0; j < allowTypes.length; j++) {
            if (allowTypes[j] == fileType) {
                typeAccepted = true;
                break;
            }
        }
        if (typeAccepted != true) {
            errorMessage += fileName + " nicht erlaubt. Nur jpeg,jpg,png,gif.";
            allowUpload = false;
        }
    }
    if (allowUpload != true) {
        el.outerHTML = el.outerHTML;
        alert(errorMessage);
    }
}
