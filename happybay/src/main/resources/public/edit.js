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