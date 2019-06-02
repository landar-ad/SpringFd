list_init = function() {
	form_submit = function() {
		var h = $('.fit-height').outerHeight();
		var form = $('#formSubmit');
		$.ajax({ method: form.attr('method'), url: form.attr('action'), data: form.serialize(),
			success: function(result) {
				var div = $('<div></div>');
				div.html(result);
				$('.fit-height').html(div.find('.fit-height').html());
				$('.fit-height').outerHeight(h);
				list_init();
			}
		});
	};
	exec_obj = function(op, param) {
		var rn = $('input[name="rn"]').val();
		if (rn > 0) {
			var url = "detailsObj";
			if (op=="remove") url = "removeObj";
			if (op=="execute") url = "executeObj";
			url += "?clazz=" + $('#clazz').val();
			if (rn > 0) url += "&rn=" + rn;
			if (op=="view") url += "&readonly=1";
			if (op=="execute") url += "&param" + param;
			window.location = url;
		}
	};
	click_row = function(a) {
		var b = $(a).hasClass('table-success'), rn = "";
		$('#objTable tbody tr').removeClass('table-success');
		$('td .max-width').addClass('one-line');
		if (!b) {
			$(a).addClass('table-success');
			$(a).find('.max-width').removeClass('one-line');
			rn = $(a).find("td.d-none").first().text();
		}
		$('input[name="rn"]').val(rn);
		check_execute(rn, "edit", function(b) { $('#edit_obj').prop('disabled', !b); });
		check_execute(rn, "remove", function(b) { $('#remove_obj').prop('disabled', !b); });
		check_execute(rn, "view", function(b) { $('#view_obj').prop('disabled', !b); });
		$(".execute_obj").each(function() {
			var target = $(this), param = $(this).prop("data-param");
			check_execute(rn, param, function(b) { target.prop('disabled', !b); });
		});
	};
	check_execute = function(rn, param, fun) {
		var clazz = $('#clazz').val();
		$.ajax({ method: "GET", 
			url: "checkExecuteObj",
			data: {
				clazz: clazz,
				rn: rn,
				param: param
			},
			success: function(result) {
				fun(result == "1");
			},
			error: function() {
				fun(false);
			}
		});
	};
	sort_fill = function() {
		$(".sorting,.sorting_asc,.sorting_desc").each(function() {
			var v = $(this).find("input[type='hidden']").first().val();
			$(this).removeClass('sorting');
			$(this).removeClass('sorting_asc');
			$(this).removeClass('sorting_desc');
			$(this).addClass(v == "ASC" ? 'sorting_asc' : (v == "DESC" ? 'sorting_desc' : 'sorting'));
		});
	};
	filter_focus = function() {
		var a = null, b = $("#filterRow");
		$("th input[type='text'],th select").each(function() { if ($(this).val()) { a = $(this); return false; } });
		if (!a) $("th input[type='checkbox']").each(function() { if ($(this).is(':checked')) { a = $(this); return false; } });
		if (!a) b.hide(); else { b.show(); a.focus(); }
		$("#clear-filter").prop('disabled', filter_class());
	};
	filter_class = function() {
		var a = $("#filterRow"), c = $("#filterButton > i.fa");
		var b = a.is(':hidden');
		c.removeClass('fa-angle-double-down');
		c.removeClass('fa-angle-double-up');
		c.addClass(b ? 'fa-angle-double-down' : 'fa-angle-double-up');
		return b;
	};
	clear_filter = function() {
		$("th input[type='text']").val("");
		$("th select option").prop('selected', false);
		$("th input[type='checkbox']").prop('checked', false);
	};
	// Выделение строк, редактирование и удаление
	$('#edit_obj,#remove_obj,#view_obj,.execute_obj').prop('disabled', true);
	add_on($('#edit_obj'), "click", function() { exec_obj("edit"); });
	add_on($('#remove_obj'), "click", function() { exec_obj("remove"); });
	add_on($('#view_obj'), "click", function() { exec_obj("view"); });
	add_on($('.execute_obj'), "click", function() { exec_obj("execute", $(this).prop("data-param")); });
	add_on($('#objTable tbody tr'), "click", function() { click_row(this); });
	add_on($('#objTable tbody tr'), "dblclick", function() { click_row(this); exec_obj("edit"); });
	// Выделение строки по идентификатору выделенного объекта
	$('td .max-width').addClass('one-line');
	$('.max-width').css("max-width", "" + (screen.width / 5) + "px");
	$('#objTable tbody tr').each(function() {
		$(this).removeClass('table-success');
		var rn = $('input[name="rn"]').val();
		if (rn && rn == $(this).find("td.d-none").first().text()) {
			$(window).scrollTop($(this).offset().top);
			click_row($(this)[0]);
		}
	});
	// Очистка фильтров
	add_on($("#clear-filter"), "click", function() { clear_filter(); });
	// Поиск и фильтрация
	add_on($("#filterButton"), "click", function() {
		$("#filterRow").toggle();
		var b = filter_class();
		if (!b)	{
			var a = null;
			$("th input[type='text'],th select").each(function() { if ($(this).val()) { a = $(this); return false; } });
			if (!a) $("th input[type='checkbox']").each(function() { if ($(this).is(':checked')) { a = $(this); return false; } });
			if (a) a.focus(); else $("th input[type='text'],th input[type='checkbox'],th select").first().focus();
		}
		$("#clear-filter").prop('disabled', b); 
	});
	add_on($("#findButton"), "click", function() { form_submit(); });
	add_on($("th input[type='text'],th input[type='checkbox'],th select"), "keypress", function(e) {
		if (e.which == 13) form_submit();
	}); 
	filter_focus();
	// Размер страницы
	add_on($(".p_page"), "change", function() {
		$("input[name='p_page']").val($(this).val());
		form_submit();
	});
	// Сортировка
	add_on($(".sorting,.sorting_asc,.sorting_desc"), "click", function() {
		var v = $(this).find("input[type='hidden']").first().val();
		if (v == "ASC") v = "DESC";
		else if (v == "DESC") v = "NONE";
		else v = "ASC";
		$(this).find("input[type='hidden']").first().val(v);
		sort_fill();
		form_submit();
	});
	sort_fill();
	// Пейджинг
	add_on($(".page-link"), "click", function() {
		$('input[name="p_off"]').val($(this).attr("data-value"));
		form_submit();
	});
	// Колонки
	add_on($("#set-visible"), "click", function() {
		$(".modal").modal();
		$(".modal-body").outerHeight($(document.body).outerHeight() * 2 / 3);
		$(".modal-body").css("overflow-y", "auto");
	});
	add_on($(".td-visible"), "click", function() { 
		$(this).text($(this).text() == "да" ? "нет" : "да"); 
	});
	add_on($("#save-visible"), "click", function() {
		var v = "";
		$("#columnTable tbody tr").each(function() {
			if ($(this).find(".td-visible").first().text() == "да") {
				if (v) v += ",";
				v += $(this).find(".d-none").first().text();
			}
		});
		$("input[name='p_listVisible']").val(v);
		form_submit();
		$("input[name='p_listVisible']").val("");
	});	
};