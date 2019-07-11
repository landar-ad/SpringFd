Amel = {};
Amel.add_on = function(s, e, f) {
	s.unbind(e);
	s.on(e, f);
};
Amel.file_on = function() {
	this.add_on($('.custom-file-input'), "change", function() { 
		   var fileName = $(this).val().split('\\').pop(); 
		   $(this).next('.custom-file-label').addClass("selected").html(fileName); 
	});
};
Amel.date_on = function() {
	$('.input_date').datepicker({language: "ru"});
};
Amel.time_on = function() {
	$('.input_time').datetimepicker({locale: "ru"});
};
Amel.upload_file = function(e) {
	var file = $(e).prop("files")[0], target = $(e).attr("data-target");
	var fileReader = new FileReader();
	fileReader.onload = function(e) { $("#" + target).val(e.target.result); };
	fileReader.readAsText(file, "UTF-8");
};
Amel.download_file = function(target, fileName) {
	var saveText = $("#" + target).val(); 
	var textBLOB = new Blob([saveText], {type: "text/" + (fileName.indexOf(".java") > 0 ? "java" : "plain")});
	var link = document.createElement("a");
	link.download = fileName;
	link.innerHTML = "Выгрузить файл";
	if (window.URL != null) link.href = window.URL.createObjectURL(textBLOB);
	link.onclick = function(e) { document.body.removeChild(e.target); };
	link.style.display = "none";
	document.body.appendChild(link);
	link.click();
};
Amel.form_submit = function() {
	var form = $('#formSubmit');
	$.ajax({ method: form.attr('method'), url: form.attr('action'), data: form.serialize(),
		success: function(result) {
			var div = $('<div></div>');
			div.html(result);
			$('#listTop').html(div.find('#listTop').html());
			Amel.list_init();
			Amel.size_init();
		}
	});
};
Amel.exec_obj = function(op, param) {
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
Amel.click_row = function(a, force) {
	var b = $(a).hasClass('table-success'), rn = "";
	$("#objTable tbody tr").removeClass('table-success');
	$("#objTable td .max-width").addClass('one-line');
	if (!b || force) {
		$(a).addClass('table-success');
		$(a).find(".max-width").removeClass('one-line');
		rn = $(a).find("td.d-none").first().text();
	}
	$('input[name="rn"]').val(rn);
	Amel.set_buttons(rn);
	Amel.set_header_width();
};
Amel.check_execute = function(rn, param, fun) {
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
Amel.set_buttons = function(rn) {
	var param = "edit,remove,view";
	$(".execute_obj").each(function() { param += "," + $(this).attr("data-param"); });
	this.check_execute(rn, param, function(b) {
		var a = b.split(","); 
		$('#edit_obj').prop('disabled', a.length < 1 || a[0] != "1");
		$('#remove_obj').prop('disabled', a.length < 2 || a[1] != "1");
		$('#view_obj').prop('disabled', a.length < 3 || a[2] != "1");
		var i = 3;
		$(".execute_obj").each(function() { $(this).prop('disabled', a.length < (i + 1) || a[i] != "1"); i++; });			
	});
};
Amel.sort_fill = function() {
	$(".sorting,.sorting_asc,.sorting_desc").each(function() {
		var v = $(this).find("input[type='hidden']").first().val();
		$(this).removeClass("sorting sorting_asc sorting_desc");
		$(this).addClass(v == "ASC" ? 'sorting_asc' : (v == "DESC" ? 'sorting_desc' : 'sorting'));
	});
};
Amel.filter_focus = function() {
	var a = null, b = $("#filterRow");
	$("#objTable th input[type='text'],#objTable th select").each(function() { if ($(this).val()) { a = $(this); return false; } });
	if (!a) $("#objTable th input[type='checkbox']").each(function() { if ($(this).is(':checked')) { a = $(this); return false; } });
	if (!a) b.hide(); else { b.show(); a.focus(); }
	$("#clear-filter").prop('disabled', filter_class());
};
Amel.filter_class = function() {
	var a = $("#filterRow"), c = $("#filterButton > i.fa");
	var b = a.is(':hidden');
	c.removeClass("fa-angle-double-down fa-angle-double-up");
	c.addClass(b ? 'fa-angle-double-down' : 'fa-angle-double-up');
	return b;
};
// Очистка строки фильтра
Amel.clear_filter = function() {
	$("#objTable th input[type='text']").val("");
	$("#objTable th select option").prop('selected', false);
	$("#objTable th input[type='checkbox']").prop('checked', false);
};
// Установка размеров колонок заголовка по данным
Amel.set_header_width = function() {
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
Amel.start_edit = function(c) {
	$.each($(".edited"), function() { Amel.cancel_edit(this); });
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
				Amel.stop_edit(c);
				e.preventDefault();
			}
			if (e.which == 27) {
				Amel.cancel_edit(c);
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
			Amel.popup_select(a, s);
		}
	}
};
Amel.stop_edit = function(c) {
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
Amel.cancel_edit = function(c) {
	var a = $(c).find("input,div,select,textarea").first(), q = a;
	var s = $(c).find("span").first();
	q.hide();
	s.show();
};
Amel.popup_select = function(a, s) {
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
Amel.size_fit = function() {
	$('.fit-height').each(function () {
		var p = $(this).parents();
		if (p.length > 0) for (var i=p.length-1; i>=0; i--) {
			var e = p[i];
			var tag = $(e).prop("tagName").toLowerCase();
			if (tag == "html" || tag == "body") continue;
			Amel.calc_height(e);
		}
		var h = Amel.calc_height(this);
		if ($(this).find(".table-fixed").length == 0) $(this).css("overflow-y", "auto");
		else {
			$(this).find("tbody").outerHeight($("footer").offset().top - $(this).find("tbody").offset().top - 10);
			var a = $(this).find(".table-fixed");
			a.find("tbody").width(a.width() + a.scrollLeft());
		}
		return false;
	});
};
Amel.calc_height = function(a) {
	var h = 0;
	var pa = $(a).parent();
	pa.children().filter(':visible').each(function() {
		if ($(a)[0] != $(this)[0]) h += $(this).outerHeight(true);
	});
	var ph = pa.outerHeight() - h - 10;
	$(a).outerHeight(ph);
	return ph;
};
Amel.scrollTo = function() {
	var a = $(".modal").find("table tbody");
	$(".modal").find('table tbody tr').each(function() {
		var c = $(this).find(".check-select > input[type='checkbox']").prop("checked");
		if (c) {
			var off = $(this).offset().top - a.offset().top;
			a.animate({scrollTop: off});
			return false;
		}
	});
};
Amel.details_init = function() {
	Amel.date_on();
	Amel.time_on();
	Amel.file_on();
	Amel.size_init();
	Amel.page_init();
	Amel.popup_init();
};
Amel.file_init = function() {
	$('.custom-file-input').on("change", function() { 
	   var fileName = $(this).val().split('\\').pop(); 
	   $(this).next('.custom-file-label').addClass("selected").html(fileName); 
	   Amel.upload_file(this);
	});
	$('.download').on("click", function() {
		var fileName = $('input[name="code"]').val();
		if (!fileName) fileName = "text";
		var fileExt = $('input[name="filetype"]').val();
		if (!fileExt) {
			if (fileName.indexOf("listener") > 0 || fileName.indexOf("handler") > 0) fileext = "java";
			else fileext = "txt";
		}
		fileName += "." + fileExt;
		var target = $(this).attr("data-target");
		Amel.download_file(target, fileName);
	});
};
Amel.list_init = function() {
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
			Amel.click_row($(this)[0]);
			selected = true;
		}
	});
	if (!selected) {
		rn = null;
		$('input[name="rn"]').val(null);
	}
	// Кнопки
	Amel.set_buttons(rn);
	Amel.add_on($('#edit_obj'), "click", function() { exec_obj("edit"); });
	Amel.add_on($('#remove_obj'), "click", function() { exec_obj("remove"); });
	Amel.add_on($('#view_obj'), "click", function() { exec_obj("view"); });
	Amel.add_on($('.execute_obj'), "click", function() { exec_obj("execute", $(this).attr("data-param")); });
	Amel.add_on($('#objTable tbody tr'), "click", function() { click_row(this); });
	Amel.add_on($('#objTable tbody tr'), "dblclick", function() { click_row(this, true); exec_obj("edit"); });
	// Установка однострочного содержимого данных
	$("#objTable td .max-width").addClass('one-line');
	// Установка максимального размера колонки
	$("#objTable .max-width").css("max-width", "" + (screen.width / 5) + "px");
	Amel.set_header_width();
	// Изменение размера области данных после скроллинга
	Amel.add_on($(".table-fixed"), "scroll", function() {
		$(this).find("tbody").width($(this).width() + $(this).scrollLeft());
	});
	// Очистка фильтров
	Amel.add_on($("#clear-filter"), "click", function() { clear_filter(); });
	// Поиск и фильтрация
	Amel.add_on($("#filterButton"), "click", function() {
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
	Amel.add_on($("#findButton"), "click", function() { Amel.form_submit(); });
	Amel.add_on($("#objTable th input[type='text'],#objTable th input[type='checkbox'],#objTable th select"), "keypress", function(e) {
		if (e.which == 13) Amel.form_submit();
	}); 
	Amel.filter_focus();
	// Размер страницы
	Amel.add_on($(".p_page"), "change", function() {
		$("input[name='p_page']").val($(this).val());
		Amel.form_submit();
	});
	// Сортировка
	Amel.add_on($(".sorting,.sorting_asc,.sorting_desc"), "click", function() {
		var v = $(this).find("input[type='hidden']").first().val();
		if (v == "ASC") v = "DESC";
		else if (v == "DESC") v = "NONE";
		else v = "ASC";
		$(this).find("input[type='hidden']").first().val(v);
		Amel.sort_fill();
		Amel.form_submit();
	});
	Amel.sort_fill();
	// Пейджинг
	Amel.add_on($(".page-link"), "click", function() {
		$('input[name="p_off"]').val($(this).attr("data-value"));
		Amel.form_submit();
	});
	// Колонки
	Amel.add_on($("#set-visible"), "click", function() {
		$(".modal").modal();
		var h = $(".modal").outerHeight(true);
		var a = $(".modal").find("table tbody");
		a.outerHeight(h / 2);
		$(".modal").find("table tbody").css("overflow-y", "auto");
		$(".modal").outerWidth($(document.body).outerWidth() / 4);
		$(".modal").css({ "left": ((($(window).width() - a.outerWidth()) / 2) + $(window).scrollLeft() + "px") });
	});
	Amel.add_on($(".td-visible"), "click", function() { 
		$(this).text($(this).text() == "да" ? "нет" : "да"); 
	});
	Amel.add_on($("#save-visible"), "click", function() {
		var v = "";
		$("#columnTable tbody tr").each(function() {
			if ($(this).find(".td-visible").first().text() == "да") {
				if (v) v += ",";
				v += $(this).find(".d-none").first().text();
			}
		});
		$("input[name='p_listVisible']").val(v);
		Amel.form_submit();
		$("input[name='p_listVisible']").val("");
	});	
};
Amel.page_init = function() {
	Amel.add_on($('.edited'), 'click', function(event) {
		Amel.start_edit(this);
	});
	Amel.add_on($('.custom-file-input'), "change", function() { 
		var fileName = $(this).val().split('\\').pop(); 
		$(this).next('.custom-file-label').addClass("selected").html(fileName); 
		Amel.stop_edit($(this).closest(".edited"));   
	});
	Amel.add_on($('.add-item'), 'click', function(event) {
		var table = $(event.delegateTarget).closest("table");
		var c = $(table).find(".first-row").clone().insertBefore($(table).find(".last-row"));
		c.removeClass("first-row");
		$(c).find(".cmd > input").val("add");
		c.show();
		Amel.add_on(c.find('.remove-item'), 'click', function(event) {
			var c = $(event.delegateTarget).closest("tr");
			$(c).find(".cmd > input").val("remove");
			c.hide();
		});
		Amel.add_on(c.find('.custom-file-input'), "change", function() { 
			var fileName = $(this).val().split('\\').pop(); 
			$(this).next('.custom-file-label').addClass("selected").html(fileName); 
			Amel.stop_edit($(this).closest(".edited"));   
		});
		c.find('.input_date').datepicker({language: "ru"});
		Amel.add_on(c.find('.edited'), 'click', function(event) {
			Amel.start_edit(this);
		});
		Amel.start_edit(c.find('.edited').first());
	});
	Amel.add_on($('.remove-item'), 'click', function(event) {
		var c = $(event.delegateTarget).closest("tr");
		$(c).find(".cmd > input").val("remove");
		c.hide();
	});
	Amel.add_on($('.view-item'), 'click', function(event) {
		var url = $(event.delegateTarget).attr("data-item");
		window.open(url, '_blank');
	});
};
Amel.popup_init = function() {
	Amel.add_on($('.choose_obj'), 'click', function(event) {
		var target = $(this), rn = $(target).parent().find("input[type='hidden']").val();
		var multiple = target.attr("data-multiple")
		var clazz = target.attr("data-clazz");
		if (!clazz) clazz = $("input[name='value'],textarea[name='value']").val();
		if (!clazz) return;
		var data = {
				clazz: clazz,
				rn: rn,
				p_title: target.attr("data-title"),
				p_column: target.attr("data-column"),
				p_filter: target.attr("data-filter")
			};
		$.ajax({ method: "POST", url: "popupSelect", 
			data: data,
			success: function(result) {
				var div = $('<div></div>');
				div.html(result);
				if (div.find('.modal').length == 0) return;
				$(".modal").html(div.find('.modal').html());
				$(".modal").modal();
				var h = $(".modal").outerHeight(true);
				var a = $(".modal").find("table tbody");
				a.outerHeight(h / 2);
				Amel.scrollTo();
				Amel.add_on($(".modal").find(".check-select > input[type='checkbox']"), "change", function() {
					var p = $(this).prop("checked");
					if (!multiple) $(".check-select > input[type='checkbox']").prop("checked", false);
					$(this).prop("checked", p);
				});
				Amel.add_on($(".modal").find("#save-button"), "click", function() {
					var rn = "", source = null;
					$('.modal').find("table tbody tr").each(function() {
						var c = $(this).find(".check-select > input[type='checkbox']").prop("checked");
						if (c) {
							rn = $(this).find(".d-none").first().text();
							source = this;
							return false;
						}
					});
					var p = $(target).parent();
					p.find("input[type='hidden']").val(rn > 0 ? rn : "");	
					p = $(p).closest(".parent-popup");
					var arr = data.p_column.split(";");
					for (i=0; i<arr.length; i++) {
						var t = rn > 0 ?  $(source).find(".text-select:eq("+ i + ")").text() : "";
						$(p).find("input:eq(" + i + ")").val(t);
					}
				});
				Amel.add_on($(".modal").find(".filter-popup"), "input", function() {
					var target = $(this);
					$(".modal").find("table tbody tr").show();
					var t = target.val();
					if (t) {
						t = t.toLowerCase();
						$(".modal").find("table tbody tr").each(function() {
							var b = false;
							$(this).find("td").each(function() {
								var tt = $(this).text();
								if (tt) {
									tt = tt.toLowerCase();
									if (tt.indexOf(t) >= 0) {
										b = true;
										return false;
									}
								}
							});
							if (!b) $(this).hide(); 
						});
					}
					Amel.scrollTo();
				});
			},
			error: function() {
			}
		});
	});
};
Amel.require_init = function() {
	Amel.add_on($('#submitButton'), "click", function () {
		var b = true;
		$(':required:invalid').each(function () {
			var id = $(this).closest('.tab-pane').attr('id');
			if (b) {
				var a = $(this), c = $('.nav a[href="#' + id + '"]'); 
				if (c) c.tab('show');
				a.tooltip({
					'trigger': 'focus', 
					'placement': 'bottom', 
					'title': 'Заполните обязательное поле',
					'offset': 10,
					'delay' : 100
				});
				setTimeout(function() { 
					a.focus();
					setTimeout(function() { a.tooltip('dispose'); }, 3000);
				}, 500);
				b = false;
			}
		});
		if (b) $('#form').submit();
	});
};
Amel.size_init = function() {
	$(window).on('resize', Amel.size_fit);
	setTimeout(function() { Amel.size_fit(); }, 40);
};