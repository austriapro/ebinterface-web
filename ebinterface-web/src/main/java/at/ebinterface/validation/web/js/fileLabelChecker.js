document.addEventListener("load", function () {
    var fileInputs = document.querySelectorAll(".inputfile");
    for (var i = 0; i < fileInputs.length; i++) {
        var fileInput = fileInputs[i];
        fileInput.addEventListener("change", function (event) {
            var target = event.target;
            var label = target.parentElement.querySelector(".upload-label");
            if (target.files.length === 0) {
                label.textContent = "Keine ausgewählt";
            } else {
                var file = target.files[0];
                label.textContent = file.name;
            }
        });
    }
});

function getFileName() {
    console.log('get it1');
    var x = document.getElementById('fileInput1');
    document.getElementById('uploadText').innerHTML = x.value.split('\\').pop();
}
