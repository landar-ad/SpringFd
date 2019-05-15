require_init = function() {
	$('#submitButton').on("click", function () {
		var b = true;
		$(':required:invalid').each(function () {
			var id = $(this).closest('.tab-pane').attr('id');
			if (b) {
				var a = $(this), c = $('.nav a[href="#' + id + '"]'); 
				if (c) c.tab('show');
				a.tooltip({
					'trigger': 'focus', 
					'placement': 'bottom', 
					'title': 'Заполните обязательное поле',
					'offset': 10,
					'delay' : 100
				});
				setTimeout(function() { 
					a.focus();
					setTimeout(function() { a.tooltip('dispose'); }, 3000);
				}, 500);
				b = false;
			}
		});
		if (b) $('#form').submit();
	});
};