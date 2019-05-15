file_init = function() {
	$('.custom-file-input').on("change", function() { 
	   var fileName = $(this).val().split('\\').pop(); 
	   $(this).next('.custom-file-label').addClass("selected").html(fileName); 
	   upload_file(this);
	});
	$('.download').on("click", function() {
		var fileName = $('input[name="code"]').val();
		if (!fileName) fileName = "text";
		var fileExt = $('input[name="filetype"]').val();
		if (!fileExt) {
			if (fileName.indexOf("listener") > 0 || fileName.indexOf("handler") > 0) fileext = "java";
			else fileext = "txt";
		}
		fileName += "." + fileExt;
		var target = $(this).attr("data-target");
		download_file(target, fileName);
	});
	upload_file = function(e) {
		var file = $(e).prop("files")[0], target = $(e).attr("data-target");
		var fileReader = new FileReader();
		fileReader.onload = function(e) { $("#" + target).val(e.target.result); };
		fileReader.readAsText(file, "UTF-8");
	};
	download_file = function(target, fileName) {
		var saveText = $("#" + target).val(); 
		var textBLOB = new Blob([saveText], {type: "text/" + (fileName.indexOf(".java") > 0 ? "java" : "plain")});
		var link = document.createElement("a");
		link.download = fileName;
		link.innerHTML = "Выгрузить файл";
		if (window.URL != null) link.href = window.URL.createObjectURL(textBLOB);
		link.onclick = function(e) { document.body.removeChild(e.target); };
		link.style.display = "none";
		document.body.appendChild(link);
		link.click();
	};
};