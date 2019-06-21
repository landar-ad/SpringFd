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
			if ($(this).find(".table-fixed").length == 0) $(this).css("overflow-y", "auto");
			else {
				$(this).find("tbody").outerHeight($("footer").offset().top - $(this).find("tbody").offset().top - 10);
				var a = $(this).find(".table-fixed");
				a.find("tbody").width(a.width() + a.scrollLeft());
			}
			return false;
		});
	};
	calc_height = function(a) {
		var h = 0;
		var pa = $(a).parent();
		pa.children().filter(':visible').each(function() {
			if ($(a)[0] != $(this)[0]) h += $(this).outerHeight(true);
		});
		var ph = pa.outerHeight() - h - 10;
		$(a).outerHeight(ph);
		return ph;
	}
	$(window).on('resize', size_fit);
	setTimeout(function() { size_fit(); }, 40);
};