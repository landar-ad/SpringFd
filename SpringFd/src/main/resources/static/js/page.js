page_init = function(list, clazz) {
	$('.add-item,.remove-item').on('click', function(event) {
		var r = $(event.delegateTarget).hasClass("remove-item");
		$("input[name='list']").val(list);
		$("input[name='clazzItem']").val(clazz);
		$("input[name='cmdItem']").val(r ? "remove" : "add");
		$("input[name='rnItem']").val(r ? $(event.delegateTarget).attr("data-item") : "");
		var h = $('.fit-height').outerHeight();
		var form = $('.form');
		var b = true;
		$(':required:invalid').each(function () { b = false; });
		if (!b) return;
		$.ajax({ method: form.attr('method'), url: form.attr('action'), data: form.serialize(),
			success: function(result) {
				$(document).html(result);
				$('.fit-height').outerHeight(h);
				page_init();
			}
		});
	});
};