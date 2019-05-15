size_init = function() {
	size_fit = function() {
		$('.fit-height').each(function () {
			var h = 0;
			$(this).parent().children().filter(':visible').each(function() {
				if (!$(this).hasClass("fit-height")) h += $(this).outerHeight();
			});
			$(this).outerHeight($(this).parent().outerHeight() - h);
			$(this).css("overflow-y", "auto");
			return false;
		});
	};
	$(window).on('resize', size_fit);
	setTimeout(function() { size_fit(); }, 40);
};