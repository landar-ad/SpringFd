list_init = function() {
	form_submit = function() {
		var form = $('#formSubmit');
		$.ajax({ method: form.attr('method'), url: form.attr('action'), data: form.serialize(),
			success: function(result) {
				var div = $('<div></div>');
				div.html(result);
				$('#listTop').html(div.find('#listTop').html());
				list_init();
				size_init();
			}
		});
	};
	exec_obj = function(op, param) {
		var rn = $('input[name="rn"]').val();
		if (!(rn > 0) && (op=="edit" || op=="remove" || op=="view")) return;
		var url = "detailsObj";
		if (op=="remove") url = "removeObj";
		if (op=="execute") url = "executeObj";
		url += "?clazz=" + $('#clazz').val();
		if (rn > 0) url += "&rn=" + rn;
		if (op=="view") url += "&readonly=1";
		if (op=="execute") url += "&param=" + param;
		window.location = url;
	};
	click_row = function(a, force) {
		var b = $(a).hasClass('table-success'), rn = "";
		$("#objTable tbody tr").removeClass('table-success');
		$("#objTable td .max-width").addClass('one-line');
		if (!b || force) {
			$(a).addClass('table-success');
			$(a).find(".max-width").removeClass('one-line');
			rn = $(a).find("td.d-none").first().text();
		}
		$('input[name="rn"]').val(rn);
		set_buttons(rn);
		set_header_width();
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
				fun(result);
			},
			error: function() {
				fun("0");
			}
		});
	};
	set_buttons = function(rn) {
		var param = "edit,remove,view";
		$(".execute_obj").each(function() { param += "," + $(this).attr("data-param"); });
		check_execute(rn, param, function(b) {
			var a = b.split(","); 
			$('#edit_obj').prop('disabled', a.length < 1 || a[0] != "1");
			$('#remove_obj').prop('disabled', a.length < 2 || a[1] != "1");
			$('#view_obj').prop('disabled', a.length < 3 || a[2] != "1");
			var i = 3;
			$(".execute_obj").each(function() { $(this).prop('disabled', a.length < (i + 1) || a[i] != "1"); });			
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
		$("#objTable th input[type='text'],#objTable th select").each(function() { if ($(this).val()) { a = $(this); return false; } });
		if (!a) $("#objTable th input[type='checkbox']").each(function() { if ($(this).is(':checked')) { a = $(this); return false; } });
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
	// Очистка строки фильтра
	clear_filter = function() {
		$("#objTable th input[type='text']").val("");
		$("#objTable th select option").prop('selected', false);
		$("#objTable th input[type='checkbox']").prop('checked', false);
	};
	// Установка размеров колонок заголовка по данным
	set_header_width = function() {
		$(".table-fixed tbody tr").first().find("td").each(function(i) {
			var a = $(".table-fixed thead tr th:eq(" + i + ")");
			var w = $(this).outerWidth(true), wh = a.outerWidth(true);
			if (wh > w) {
				$(this).css("max-width", wh);
				$(this).css("min-width", wh);
				w = $(this).outerWidth(true);
			}
			a.css("max-width", w);
			a.css("min-width", w);
		});
	};
	// Клик по строке
	var rn = $('input[name="rn"]').val(), selected = false;
	$('#objTable tbody tr').each(function() {
		$(this).removeClass('table-success');
		if (rn && rn == $(this).find("td.d-none").first().text()) {
			var a = $(".fit-height").find(".table-fixed");
			if (a.length == 0) a = $(".fit-height");
			if (a.length > 0) {
				var off = $(this).offset().top - a.offset().top;
				a.animate({scrollTop: off});
			}
			click_row($(this)[0]);
			selected = true;
		}
	});
	if (!selected) {
		rn = null;
		$('input[name="rn"]').val(null);
	}
	// Кнопки
	set_buttons(rn);
	add_on($('#edit_obj'), "click", function() { exec_obj("edit"); });
	add_on($('#remove_obj'), "click", function() { exec_obj("remove"); });
	add_on($('#view_obj'), "click", function() { exec_obj("view"); });
	add_on($('.execute_obj'), "click", function() { exec_obj("execute", $(this).attr("data-param")); });
	add_on($('#objTable tbody tr'), "click", function() { click_row(this); });
	add_on($('#objTable tbody tr'), "dblclick", function() { click_row(this, true); exec_obj("edit"); });
	// Установка однострочного содержимого данных
	$("#objTable td .max-width").addClass('one-line');
	// Установка максимального размера колонки
	$("#objTable .max-width").css("max-width", "" + (screen.width / 5) + "px");
	set_header_width();
	// Изменение размера области данных после скроллинга
	add_on($(".table-fixed"), "scroll", function() {
		$(this).find("tbody").width($(this).width() + $(this).scrollLeft());
	});
	// Очистка фильтров
	add_on($("#clear-filter"), "click", function() { clear_filter(); });
	// Поиск и фильтрация
	add_on($("#filterButton"), "click", function() {
		$("#filterRow").toggle();
		var b = filter_class();
		if (!b)	{
			var a = null;
			$("#objTable th input[type='text'],#objTable th select").each(function() { if ($(this).val()) { a = $(this); return false; } });
			if (!a) $("#objTable th input[type='checkbox']").each(function() { if ($(this).is(':checked')) { a = $(this); return false; } });
			if (a) a.focus(); else $("th input[type='text'],th input[type='checkbox'],th select").first().focus();
		}
		$("#clear-filter").prop('disabled', b); 
		// Размер скроллируемой области
		$(".table-fixed tbody").outerHeight($("footer").offset().top - $(".table-fixed tbody").offset().top - 10);
	});
	add_on($("#findButton"), "click", function() { form_submit(); });
	add_on($("#objTable th input[type='text'],#objTable th input[type='checkbox'],#objTable th select"), "keypress", function(e) {
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
		$(".modal-body").outerHeight($(document.body).outerHeight(true) * 2 / 3);
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