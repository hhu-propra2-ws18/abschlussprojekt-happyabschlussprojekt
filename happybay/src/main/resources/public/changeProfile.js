function checkFoto(upload) {
    var filename = upload.value;
    var mime = filename.toLowerCase().substr(filename.lastIndexOf("."));
    if (mime != ".jpg" && mime != ".jpeg" && mime != ".png" && mime != ".gjf") {
        alert(filename + " nicht erlaut. Nur jpeg, jpg, gif, png.")
    }
}