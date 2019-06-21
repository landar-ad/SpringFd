page_init = function() {
	start_edit = function(c) {
		$.each($(".edited"), function() { cancel_edit(this); });
		var a = $(c).find(".not-visible").first(), q = a;
		var s = $(c).find("span").first();
		var b = $(c).closest("tr"); 
		if (a.length > 0) {
			s.hide();
			q.show();
			if (q.prop("tagName").toLowerCase() == "div") a = q.find("input,select,textarea").first();
			a.val(s.text());
			a.focus();
			add_on($(a), "keypress", function(e) {
				if (e.which == 13) {
					stop_edit(c);
					e.preventDefault();
				}
				if (e.which == 27) {
					cancel_edit(c);
					e.preventDefault();
				}
			});
		}
		else {
			var a = $(c).find('input[type="hidden"]').first(), t = a.attr("data-type");
			if (t == "checkbox") {
				var v = a.val();
				v = (v == '0') ? '1' : '0';
				a.val(v);
				var b = $(c).find('i');
				b.removeClass("fa-times");
				b.removeClass("fa-check");
				b.addClass(v == '1' ? "fa-check" : "fa-times");
				b = a.closest("tr"); 
				var cmd = $(b).find(".cmd > input").val();
				if (cmd != "add") $(b).find(".cmd > input").val("update");
			}
			else if (t == "select") {
				popup_select(a, s);
			}
		}
	};
	stop_edit = function(c) {
		var a = $(c).find(".not-visible").first(), q = a;
		var s = $(c).find("span").first();
		var b = $(c).closest("tr"); 
		if (q.prop("tagName").toLowerCase() == "div") a = q.find("input,select,textarea").first();
		var t = $(a).val();
		if (q.prop("tagName").toLowerCase() == "div") t = t.split('\\').pop(); 
		s.text(t);
		q.hide();
		s.show();
		var cmd = $(b).find(".cmd > input").val();
		if (cmd != "add") $(b).find(".cmd > input").val("update");
	};
	cancel_edit = function(c) {
		var a = $(c).find("input,div,select,textarea").first(), q = a;
		var s = $(c).find("span").first();
		q.hide();
		s.show();
	};
	popup_select = function(a, s) {
		var data = {
			clazz: a.attr("data-clazz"),
			p_title: a.attr("data-title"),
			p_column: a.attr("data-column"),
			p_filter: a.attr("data-filter")
		};
		$.ajax({ method: "POST", url: "popupSelect", 
			data: data,
			success: function(result) {
				var div = $('<div></div>');
				div.html(result);
				$('.modal').html(div.find('.modal').html());
				$(".modal").modal();
				$(".modal-body").outerHeight($(document.body).outerHeight(true) * 2 / 3);
				$(".modal-body").css("overflow-y", "auto");
				add_on($(".modal").find("#save-button"), "click", function() {
					$("#columnTable tbody tr").each(function() {
						var rn = $(this).find(".d-none").first().text();
						var c = $(this).find(".check-select > input[type='checkbox']").prop("checked");
						var t = $(this).find(".text-select").text();
						if (c) {
							a.val(rn);
							s.text(t);
							var b = a.closest("tr"); 
							var cmd = $(b).find(".cmd > input").val();
							if (cmd != "add") $(b).find(".cmd > input").val("update");
							return false;
						}
					});
				});
			},
			error: function() {
			}
		});
	};
	add_on($('.edited'), 'click', function(event) {
		start_edit(this);
	});
	add_on($('.custom-file-input'), "change", function() { 
		var fileName = $(this).val().split('\\').pop(); 
		$(this).next('.custom-file-label').addClass("selected").html(fileName); 
		stop_edit($(this).closest(".edited"));   
	});
	add_on($('.add-item'), 'click', function(event) {
		var table = $(event.delegateTarget).closest("table");
		var c = $(table).find(".first-row").clone().insertBefore($(table).find(".last-row"));
		c.removeClass("first-row");
		$(c).find(".cmd > input").val("add");
		c.show();
		add_on(c.find('.remove-item'), 'click', function(event) {
			var c = $(event.delegateTarget).closest("tr");
			$(c).find(".cmd > input").val("remove");
			c.hide();
		});
		add_on(c.find('.custom-file-input'), "change", function() { 
			var fileName = $(this).val().split('\\').pop(); 
			$(this).next('.custom-file-label').addClass("selected").html(fileName); 
			stop_edit($(this).closest(".edited"));   
		});
		c.find('.input_date').datepicker({language: "ru"});
		add_on(c.find('.edited'), 'click', function(event) {
			start_edit(this);
		});
		start_edit(c.find('.edited').first());
	});
	add_on($('.remove-item'), 'click', function(event) {
		var c = $(event.delegateTarget).closest("tr");
		$(c).find(".cmd > input").val("remove");
		c.hide();
	});
	add_on($('.view-item'), 'click', function(event) {
		var url = $(event.delegateTarget).attr("data-item");
		window.open(url, '_blank');
	});
};