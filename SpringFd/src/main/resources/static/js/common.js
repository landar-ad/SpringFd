add_on = function(s, e, f) {
		s.unbind(e);
		s.on(e, f);
};
file_on = function() {
	add_on($('.custom-file-input'), "change", function() { 
		   var fileName = $(this).val().split('\\').pop(); 
		   $(this).next('.custom-file-label').addClass("selected").html(fileName); 
	});
};
date_on = function() {
	$('.input_date').datepicker({language: "ru"});
};
time_on = function() {
	$('.input_time').datetimepicker({locale: "ru"});
};
details_init = function() {
	date_on();
	time_on();
	file_on();
	size_init();
	page_init();
	popup_init();
}