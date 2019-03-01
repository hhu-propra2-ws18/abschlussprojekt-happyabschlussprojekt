function checkTimeAndAccount() {
    obj = document.getElementsByName('pickTime[]');
    check_val = [];
    for (k in obj) {
        if (obj[k].checked) {
            check_val.push(obj[k].value);
        }
    }
    var str = check_val.toString();

    var time = str.split(" ");
    var startTimeMax = time[0];
    var endTimeMax = time[1];

    var array1 = startTimeMax.split("-");
    var startDateMax = new Date(array1[0], parseInt(array1[1]) - 1, array1[2]);

    var array2 = endTimeMax.split("-");
    var endDateMax = new Date(array2[0], parseInt(array2[1]) - 1, array2[2])

    var startDateString = $("#mietezeitpunktStart").val();
    var endDateString = $("#mietezeitpunktEnd").val();

    var array3 = startDateString.split("-");
    var startDate = new Date(array3[0], parseInt(array3[1]) - 1, array3[2]);

    var array4 = endDateString.split("-");
    var endDate = new Date(array4[0], parseInt(array4[1]) - 1, array4[2]);

    var account = $("#account").val();
    var kost = $("#kost").val();
    var kaution = $("#kautionen").val();

    var timeIntervar = Math.round((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24));
    var sumKosten = ((timeIntervar+1) * kost) + parseInt(kaution);
    var nowDate = Date.now();

    if (startDate < startDateMax || startDate >= endDateMax || endDate <= startDateMax || endDate > endDateMax || startDate > endDate) {
        alert("Bitte notieren Sie sich die verfügbare Zeit und vergewissern Sie sich, dass das Datum korrekt ist！");
        $("#mietezeitpunktStart").val("");
        $("#mietezeitpunktEnd").val("");
        return false;
    } else if (startDate < (nowDate - (24 * 60 * 60 * 1000))) {
        alert("Start Datum muss nach heute sein!");
        $("#mietezeitpunktStart").val("");
        $("#mietezeitpunktEnd").val("");
        return false;
    } else {
        if (account < parseFloat(sumKosten)) {
            $("#mietezeitpunktStart").val("");
            $("#mietezeitpunktEnd").val("");
            alert("Ihr Kontostand reicht nicht aus. Bitte gehen Sie auf Propay, um es aufzuladen！" + parseInt(account) + ' ' + sumKosten);
            return false;
        } else {
            return true;
        }
    }
    return true;
}