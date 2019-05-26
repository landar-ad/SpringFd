page_init = function(list, clazz) {
	clear_param = function() {
		$("input[name='list']").val("");
		$("input[name='clazzItem']").val("");
		$("input[name='cmdItem']").val("");
		$("input[name='rnItem']").val("");
	};
	add_on($('.add-item,.remove-item'), 'click', function(event) {
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
				var div = $('<div></div>');
				div.html(result);
				$('.fit-height').html(div.find('.fit-height'));
				$('.fit-height').outerHeight(h);
				page_init(list, clazz);
				clear_param();
			},
			error: function() {
				clear_param();
			}
		});
	});
};