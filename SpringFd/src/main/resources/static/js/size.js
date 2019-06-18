size_init = function() {
	size_fit = function() {
		$('.fit-height').each(function () {
			var p = $(this).parents();
			if (p.length > 0) for (var i=p.length-1; i>=0; i--) {
				var e = p[i];
				var tag = $(e).prop("tagName").toLowerCase();
				if (tag == "html" || tag == "body") continue;
				calc_height(e);
			}
			var h = calc_height(this);
			if (!$(this).hasClass("fixed_header")) $(this).css("overflow-y", "auto");
			else {
				var a = $(".fixed_header tbody").first();
				if (a.length > 0) {
					$(a).outerHeight(h - $(".fixed_header th tr").outerHeight() * 2);
				}
			}
			return false;
		});
	};
	size_fit_old = function() {
		$('.fit-height').each(function () {
			var h = 0;
			$(this).parent().children().filter(':visible').each(function() {
				if (!$(this).hasClass("fit-height")) h += $(this).outerHeight();
			});
			$(this).outerHeight($(this).parent().outerHeight() - h);
			$('.fit-height').css("overflow-y", "auto");
			return false;
		});
	};
	calc_height = function(a) {
		var h = 0;
		var pa = $(a).parent();
		pa.children().filter(':visible').each(function() {
			if ($(a)[0] != $(this)[0]) h += $(this).outerHeight();
		});
		var ph = pa.outerHeight();
		$(a).outerHeight(ph - h);
		return ph - h;
	}
	$(window).on('resize', size_fit);
	setTimeout(function() { size_fit(); }, 40);
};