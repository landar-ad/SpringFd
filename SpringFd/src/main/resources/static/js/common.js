add_on = function(s, e, f) {
		s.unbind(e);
		s.on(e, f);
};