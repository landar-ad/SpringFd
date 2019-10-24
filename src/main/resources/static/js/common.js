Amel = {
	// Идентификаторы управляющих элементов
	// Форма таблицы 
	formId: "formSubmit",
	// Вся страница таблицы
	listId: "listTop",
	// Таблицы
	tableId: "objTable",
	// Цвет выделенной строки
	rowSelection: "table-primary",
	// Кнопка submit на форме с контролем обязательных полей
	submitButtonId: "submitButton",
	// Строка фильтров 
	filterRowId: "filterRow",
	// Кнопка показа фильтров
	filterButtonId: "filterButton",
	// Кнопка очистки фильтров
	clearFilterId: "clear-filter",
	// Кнопка поиска
	findButtonId: "findButton",
	// Кнопка изменения видимости колонок
	setVisibleId: "set-visible",
	// Стек элементов для модального диалога
	modalStack: [],
	// Добавление обработчика (отвязать старый и привязать новый)
	add_on: function(s, e, f) { s.unbind(e); s.on(e, f); },
	get_modal: function() {
		var ret = this.modalStack.length > 0 ? this.modalStack.pop() : null;
		if (!ret) {
			ret = document.createElement("div");
			$(ret).addClass("modal");
			document.body.appendChild(ret);
			ret = $(ret);
		}
		return ret;
	},
	put_modal: function(modal) {
		if (!modal) return;
		this.modalStack.push(modal);
	},
	question: function(title, text, buttons, sz, callback) {
		var target = this;
		var data = {
				p_title: title,
				p_text: text,
				p_buttons: buttons,
				p_sz: sz 
			};
		$.ajax({ method: "POST", url: "popupQuestion", 
			data: data,
			success: function(result) {
				var div = $('<div></div>');
				div.html(result);
				var modal = target.get_modal();
				modal.html(div.find(".modal").html());
				modal.modal();
				target.add_on(modal.find("button"), "click", function() {
					if (callback) callback($(this).attr("data-param"));
				});
				target.add_on(modal, "hidden.bs.modal", function (e) {
					  target.put_modal(modal);
				});
			},
			error: function(xhr) {
				if (xhr.status == 401) window.location = "login";
			}
		});
	},
	// Добавление обработчика: после выбора файла показать его имя 
	file_on: function() {
		this.add_on($('.custom-file-input'), "change", function() { 
		   var fileName = $(this).val().split('\\').pop(); 
		   $(this).next('.custom-file-label').addClass("selected").html(fileName); 
		});
	},
	// Инициализация элементов показа даты
	date_on: function() { $('.input_date').datetimepicker({locale: "ru", format: 'L'}); },
	// Инициализация элементов показа даты и времени
	time_on: function() { $('.input_time').datetimepicker({locale: "ru"}); },
	// Загрузка данных из файла
	upload_file: function(e) {
		var file = $(e).prop("files")[0], t = $(e).attr("data-target");
		var fileReader = new FileReader();
		fileReader.onload = function(e) { $("#" + t).val(e.target.result); };
		fileReader.readAsText(file, "UTF-8");
	},
	// Выгрузка данных в файл
	download_file: function(t, fileName) {
		var saveText = $("#" + t).val(); 
		var textBLOB = new Blob([saveText], {type: "text/" + (fileName.indexOf(".java") > 0 ? "java" : "plain")});
		var link = document.createElement("a");
		link.download = fileName;
		link.innerHTML = "Выгрузить файл";
		if (window.URL != null) link.href = window.URL.createObjectURL(textBLOB);
		link.onclick = function(e) { document.body.removeChild(e.target); };
		link.style.display = "none";
		document.body.appendChild(link);
		link.click();
	},
	scroll_bar_size: function() {
		var div = $('<div>').css({visibility: 'hidden', width: 100, height: 100, overflow: 'scroll', position: 'absolute'}).appendTo('body');
		var inner = $('<div>').css({width: '100%', height: '100%'}).appendTo(div);
		var ret = { width: 100 - inner.outerWidth(), height: 100 - inner.outerHeight() };
		div.remove();
		return ret;
	},
	// Изменение списка через отправку формы
	form_submit: function() {
		var target = this, form = $('#' + target.formId);
		$.ajax({ method: form.attr('method'), url: form.attr('action'), data: form.serialize(),
			success: function(result) {
				var div = $('<div></div>');
				div.html(result);
				$('#' + target.listId).html(div.find('#' + target.listId).html());
				target.list_init();
				target.size_init();
			}
		});
	},
	// Переход по ссылке
	exec_obj: function(op, param) {
		var target = this, rn = $('input[name="rn"]').val(), clazz = $('#clazz').val();
		if (!clazz) clazz = $('input[name="clazz"]').val();
		if (!(rn > 0) && (op=="update" || op=="edit" || op=="delete" || op=="remove" || op=="view")) return;
		else if (op=="add") rn = null;
		if (op=="add") {
			$.ajax({ method: "GET", 
				url: "popupClasses",
				data: {
					clazz: clazz,
					p_sz: 4
				},
				success: function(result) {
					var div = $('<div></div>');
					div.html(result);
					var zz = $(div).find(".text-select");
					if (zz.length > 1) {
						var modal = target.get_modal();
						modal.html(div.find('.modal').html());
						modal.modal();
						modal.find(".modal-body").outerHeight($(document.body).outerHeight(true) * 2 / 3);
						modal.find(".modal-body").css("overflow-y", "auto");
						target.set_header_width(modal.find("table"));
						modal.find(".save-button").prop("disabled", true);
						target.add_on(modal.find(".check-select > input[type='checkbox']"), "change", function() {
							var p = $(this).prop("checked");
							modal.find(".check-select > input[type='checkbox']").prop("checked", false);
							$(this).prop("checked", p);
							modal.find(".save-button").prop("disabled", !p);
						});
						target.add_on(modal.find(".save-button"), "click", function() {
							clazz = "";
							modal.find("table tbody tr").each(function() {
								var cl = $(this).find(".d-none").first().text();
								var c = $(this).find(".check-select > input[type='checkbox']").prop("checked");
								if (c) {
									clazz = cl; 
									return false;
								}
							});
							if (clazz) {
								window.location = "detailsObj?clazz=" + clazz;
							}
						});
						target.add_on(modal, "hidden.bs.modal", function (e) {
							  target.put_modal(modal);
						});
					}
					else window.location = "detailsObj?clazz=" + clazz;
				},
				error: function(xhr) {
					if (xhr.status == 401) window.location = "login";
				}
			});
			return;
		}
		var url = "detailsObj";
		if (op=="remove") url = "removeObj";
		if (op=="execute") url = "executeObj";
		var z = "?";
		if (rn > 0) { url += z + "rn=" + rn; z = "&"; }
		if (clazz) { url += z + "clazz=" + clazz; z = "&"; }
		if (op=="view") url += z + "readonly=1";
		if (op=="execute") url += z + "param=" + param;
		window.location = url;
	},
	// Клик по строке таблицы
	click_row: function(a, force) {
		var target = this, b = $(a).hasClass(target.rowSelection), rn = "";
		$("#" + target.tableId + " tbody tr").removeClass(target.rowSelection);
		$("#" + target.tableId + " td .max-width").addClass('one-line');
		if (!b || force) {
			$(a).addClass(target.rowSelection);
			$(a).find(".max-width").removeClass('one-line');
			rn = $(a).find("td.d-none").first().text();
		}
		$('input[name="rn"]').val(rn);
		target.set_buttons(rn);
		target.set_header_width($("#" + target.tableId));
	},
	// Проверка, доступны ли операции
	check_execute: function(rn, param, fun) {
		if (!param) return;
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
			error: function(xhr) {
				if (xhr.status == 401) window.location = "login";
				fun("0");
			}
		});
	},
	// Обработка доступности кнопок
	set_buttons: function(rn) {
		var param = "", target = this;
		$(".execute_obj").each(function() { if (param) param += ","; param += $(this).attr("data-param"); });
		target.check_execute(rn, param, function(b) {
			var a = b.split(","), i = 0;
			$(".execute_obj").each(function() { $(this).prop('disabled', a.length < (i + 1) || a[i] != "1"); i++; });			
		});
	},
	// Установка и изменение сортировки
	sort_fill: function() {
		$(".sorting,.sorting_asc,.sorting_desc").each(function() {
			var v = $(this).find("input[type='hidden']").first().val();
			$(this).removeClass("sorting sorting_asc sorting_desc");
			$(this).addClass(v == "ASC" ? 'sorting_asc' : (v == "DESC" ? 'sorting_desc' : 'sorting'));
		});
	},
	// Установка фокуса в строке фильтров
	filter_focus: function() {
		var target = this, a = null, b = $("#" + target.filterRowId);
		$("#" + target.tableId + " th input[type='text'],#" + target.tableId + " th select").each(function() { if ($(this).val()) { a = $(this); return false; } });
		if (!a) $("#" + target.tableId + " th input[type='checkbox']").each(function() { if ($(this).is(':checked')) { a = $(this); return false; } });
		if (!a) b.hide(); else { b.show(); a.focus(); }
		$("#" + target.clearFilterId).prop('disabled', target.filter_class());
	},
	// Изменение класса кнопки фильтра
	filter_class: function() {
		var target = this, a = $("#" + target.filterRowId), c = $("#" + target.filterButtonId + " > i.fa"), b = a.is(':hidden');
		c.removeClass("fa-angle-double-down fa-angle-double-up");
		c.addClass(b ? 'fa-angle-double-down' : 'fa-angle-double-up');
		return b;
	},
	// Очистка строки фильтра
	clear_filter: function() {
		var target = this;
		$("#" + target.tableId + " th input[type='text']").val("");
		$("#" + target.tableId + " th select option").prop('selected', false);
		$("#" + target.tableId + " th input[type='checkbox']").prop('checked', false);
	},
	// Установка размеров колонок заголовка таблицы по колонкам данных
	set_header_width: function(table, nolast) {
		var c = table.find("tbody tr").first().find("td"), target = this;
		c.each(function(i) {
			if (nolast && i == c.length - 1) return false;
			var a = table.find("thead tr th:eq(" + i + ")");
			var w = $(this).outerWidth(true), wh = a.outerWidth(true);
			if (wh > w) {
				$(this).css("max-width", wh);
				$(this).css("min-width", wh);
				w = $(this).outerWidth(true);
			}
			a.css("max-width", w);
			a.css("min-width", w);
		});
	},
	set_max_width: function(table) {
		var target = this;
		table.find("td .max-width").addClass('one-line');
		//if (table.find("tbody tr").length > 0) table.find("th").addClass('one-line');
		// Установка максимального размера колонки
		var wt = 0, sc = table.outerWidth() - 3 * target.scroll_bar_size().width, max = sc / 4;
		// Установка размера колонок - предварительно последнюю увеличиваем для скроллинга
		target.set_header_width(table);
		table.find("thead tr").each(function() {
			wt = 0;
			$(this).find("th").each(function() { wt += $(this).outerWidth(true); });
			if (wt > 0) {
				$(this).find("th").each(function() {
					var m = $(this).outerWidth(true) * sc / wt;
					if (m > max) max = m;
					$(this).css("min-width", m);
					$(this).css("max-width", m);
				});
			}
		});
		table.find("tbody tr").each(function() {
			wt = 0;
			$(this).find("td").each(function() { wt += $(this).outerWidth(true); });
			if (wt > 0) {
				$(this).find("td").each(function() {
					var m = $(this).outerWidth(true) * sc / wt;
					if (m > max) max = m;
				});
			}
		});
		table.find(".max-width").css("max-width", "" + max + "px");
	},
	// Начало редактирования
	start_edit: function(c) {
		var target = this;
		$.each($(".edited"), function() { target.cancel_edit(this); });
		var a = $(c).find(".not-visible").first(), q = a;
		var s = $(c).find("span").first();
		var b = $(c).closest("tr"); 
		if (a.length > 0) {
			s.hide();
			q.show();
			if (q.prop("tagName").toLowerCase() == "div") a = q.find("input,select,textarea").first();
			a.val(s.text());
			a.focus();
			target.add_on($(a), "keypress", function(e) {
				if (e.which == 13) {
					target.stop_edit(c);
					e.preventDefault();
				}
				if (e.which == 27) {
					target.cancel_edit(c);
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
				target.popup_select(a, s);
			}
		}
	},
	// Завершение редактирования с сохранением
	stop_edit: function(c) {
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
	},
	// Выход из редактирования без сохранения
	cancel_edit: function(c) {
		var a = $(c).find("input,div,select,textarea").first(), q = a;
		var s = $(c).find("span").first();
		q.hide();
		s.show();
	},
	cli: false,
	// Редактирование в ячейках таблицы
	table_edit: function(c) {
		var target = this;
		if (!c) return;
		var a = c.find("input[type='text'],input[type='password'],input[type='date'],input[type='checkbox'],select,.custom-file,textarea,.custom-date,.pbutton"), b = c.find(">label,>span").first();
		if (a.length < 1) return; 
		a = a.first();
		if (!a.is(':hidden')) {
			if (a.hasClass("choose_obj")) {
				target.choose_object(a);
			}
			return;
		}
		var q = a, h = c.find("input[type='hidden']").first();
		a.show();
		if (a.prop("tagName").toLowerCase() == "div") q = a.find("input,select,textarea").first();
		q.focus();
		if (h.length > 0) {
			h.val(q.val());
			h.text(b.text());
		}
		target.cli = false;
		target.add_on(a, 'mousedown', function() {
			target.cli = true;
		});
		$(c).removeClass("edited");
		target.add_on(q.first(), "keydown blur", function(e) {
			var k = e.keyCode ? e.keyCode : 13;
			if (k != 9 && k != 13 && k != 27) return;
			if (target.table_edit_end($(this), e) !== false) return;
			return false;
		});
		b.hide();
		var t = q.attr("type");
		if (a.hasClass("custom-date")) t = "cdate";
		if ("file" == t) q.click();
		else if ("cdate" == t) {
			//a.find(".input-group-append").click();
		}
		else if (q.prop("tagName").toLowerCase() == "select") { 
			target.add_on(q, "keypress", function(e) {
				e.originalEvent.isTrusted = true;
				return;
			});
			var event = document.createEvent("Event");
			event.initEvent("keypress", true, true);
			event.view = document.defaultView;
			event.altKey = false;
			event.ctrlKey = false;
			event.shiftKey = false;
			event.metaKey = false;
			event.key = " ";
			q[0].dispatchEvent(event);
		}
	},
	table_edit_end: function(q, e) {
		var k = e.keyCode ? e.keyCode : 13;
		if (k != 9 && k != 13 && k != 27) return true;
		var target = this;
		var c = q.closest(".td-edited"), b = c.find(">label,>span").first();
		var a = c.find("input[type='text'],input[type='password'],input[type='date'],input[type='checkbox'],select,.custom-file,textarea,.custom-date");
		if (a.length < 1) return false;
		a = a.first();
		var h = c.find("input[type='hidden']").first()
		var t = q.attr("type");
		if (a.hasClass("custom-date")) t = "cdate";
		if (a.prop("tagName").toLowerCase() == "select") t = "select";
		if (a.prop("tagName").toLowerCase() == "textarea" && (k == 13) && e.keyCode && !e.ctrlKey) return true;
		if ((t == "file" || t == "cdate") && !e.keyCode) { 
			if (t == "cdate") {
				if (target.cli) {
					q.focus(); 
					target.cli = false;
					return true;
				}
			}
			else return true; 
		}
		if (b.length > 0) {
			b.show();
			a.hide();
		}
		$(c).addClass("edited");
		if (k != 27) {
			var v = q.val();
			if (t == "date" && v && v.length >= 10) {
				v = v.substring(8,10) + "." + v.substring(5,7) + "." + v.substring(0,4);
			}
			if (t == "file") {
				if (v) v = v.split('\\').pop();
				else v = b.text();
			}
			if (t == "select") {
				v = q.find("option:selected").text();
			}
			if (t == "password") {
				v = "";
				for (var i=0; v && i<v.length; i++) v += "*";
			}
			b.text(v);
			var zz = $(c).closest("tr").find(".d-none > input[name$='p_cmd']");
			if (!zz.val()) zz.val("update");
			target.calculate();
		}
		else {
			var v = b.text();
			if (t == "date" && v && v.length >= 10) {
				v = v.substring(6,10) + "-" + v.substring(3,5) + "-" + v.substring(0,2);
			}
			if (t == "select") {
				v = h.val();
				b.text(h.text());
			}
			if (t == "file" || t == "password") v = "";
			q.val(v);
		}
		if (k == 9) {
			var p = $(".td-edited"), pa = [];
			for (var j=0; j<p.length; j++) {
				var zz = $(p[j]).find("input[type='text'],input[type='password'],input[type='date'],select,.custom-file,textarea,.custom-date");
				if (zz.length == 0) continue;
				var zz1 = $(p[j]).find(">label,>span");
				if (zz1.length == 0) continue;
				if (zz.is(':hidden') && zz1.is(':hidden')) continue;
				pa[pa.length] = p[j]; 
			}
			for (var j=0; j<pa.length; j++) {
				var s = pa[j];
				if (s == c[0]) {
					if (e.shiftKey) j = j == 0 ? pa.length - 1 : j - 1;
					else j = j == pa.length - 1 ? 0 : j + 1;
					c = $(pa[j]);
					break;
				}
			}
			setTimeout(function() { target.table_edit(c); }, 10); 
		}
		return false;
	},
	table_edit_focus: function(e) {
		var target = this, p = e ? e.find(".td-edited") : $(".td-edited");
		for (var j=0; j<p.length; j++) {
			var zz = $(p[j]).find("input[type='text'],input[type='password'],input[type='date'],select,.custom-file,textarea,.custom-date");
			if (zz.length == 0) continue;
			var zz1 = $(p[j]).find(">label,>span");
			if (zz1.length == 0) continue;
			if (zz.is(':hidden') && zz1.is(':hidden')) continue;
			setTimeout(function() { target.table_edit($(p[j])); }, 10);
			break;
		}
	},
	table_ret: function(c, q, b) {
		var target = this, v = q.val(), t = q.attr("type");
		if (q.prop("tagName").toLowerCase() == "select") t = "select";
		if (t == "date" && v && v.length >= 10) {
			v = v.substring(8,10) + "." + v.substring(5,7) + "." + v.substring(0,4);
		}
		if (t == "file") {
			if (v) v = v.split('\\').pop();
			else v = b.text();
		}
		if (t == "select") {
			v = q.find("option:selected").text();
		}
		if (t == "password") {
			v = "";
			for (var i=0; v && i<v.length; i++) v += "*";
		}
		b.text(v);
		var zz = $(c).closest("tr").find(".d-none > input[name$='p_cmd']");
		if (!zz.val()) zz.val("update");
		target.calculate();
	},
	row_to_modal: function(tr, modal, targetId) {
		if (!tr || !modal || !targetId) return;
		var target = this;
		tr.find("[name^='" + targetId + "__']").each(function() {
			var name = $(this).attr("name");
			var k = name.indexOf("__");
			if (k > 0) name = name.substring(k + 2);
			var el = modal.find("[name='" + name + "']");
			el.val($(this).val());
			var c = el.closest(".td-edited"), q = el, b = c.find(">label,>span").first();
			if (c.length > 0) target.table_ret(c, q, b);
		});
	},
	modal_to_row: function(modal, tr, targetId) {
		if (!tr || !modal || !targetId) return;
		var target = this;
		tr.find("[name^='" + targetId + "__']").each(function() {
			var name = $(this).attr("name");
			var k = name.indexOf("__");
			if (k > 0) name = name.substring(k + 2);
			var el = $(this);
			var c = el.closest(".td-edited"), q = el, b = c.find(">label,>span").first();
			if (c.length == 0) return;
			el.val(modal.find("[name='" + name + "']").val());
			target.table_ret(c, q, b);
		});
	},
	in_array: function(rn, arr_rn) {
		if (!rn || !arr_rn || arr_rn.length < 0) return false;
		for (var i=0; i<arr_rn.length; i++) if (arr_rn[i] == rn) return true;
		return false;
	},
	button_command: function(b) {
		var target = this;
		if (!b) return;
		var command = b.attr("data-command"), targetId = b.attr("data-target"), names = b.attr("data-name"), p_sz = b.attr("data-size"), ena = b.attr("data-enabled");
		if (!command) return;
		var table = $("#" + targetId);
		if (!table) return;
		if (ena != "1") return;
		if ("add" == command) {
			var tr = table.find(".last-row");
			if (tr.length == 0) return;
			var c = tr.clone().insertBefore(tr);
			c.removeClass("not-visible last-row");
			$(c).find("input[name='" + targetId + "__p_cmd']").val("add");
			c.show();
			target.edit_init();
			setTimeout(function() { 
				c.find(".td-edited .td-check").prop("checked", true);
				target.button_enabled();
				var zz = c.find(".td-edited .custom-file");
				if (zz.length > 0) zz = zz.closest(".td-edited");
				else {
					zz = c.find(".td-edited .choose_obj").first();
					if (zz.length > 0) {
						$(zz).click();
						return;
					}
					else zz = c.find(".td-edited:eq(1)");
				}
				target.table_edit(zz); 
			}, 10);
		}
		if ("copy" == command) {
			var tr = table.find(".td-edited .td-check:checked").closest("tr");
			if (tr.length == 0) return;
			var c = tr.clone().insertAfter(tr);
			c.removeClass("not-visible last-row");
			$(c).find("input[name='" + targetId + "__p_cmd']").val("add");
			$(c).find("input[name='" + targetId + "__rn']").val("");
			$(c).find("input[name='" + targetId + "__rnOld']").val("");
			c.show();
			target.file_on();
			target.popup_init();
			target.table_edit_init();
			setTimeout(function() { 
				tr.find(".td-edited .td-check").prop("checked", false);
				c.find(".td-edited .td-check").prop("checked", true);
				target.button_enabled();
				var zz = c.find(".td-edited:eq(1)");
				target.table_edit(zz); 
			}, 10);
		}
		else if ("remove" == command || "delete" == command) {
			table.find("tr").each(function() {
				var tr = $(this);
				var c = tr.find(".td-edited .td-check").prop("checked");
				if (!c) return;
				tr.find(".td-edited .td-check").prop("checked", false);
				tr.find("input[name='" + targetId + "__p_cmd']").val("remove");
				tr.addClass("not-visible");
			});
			target.button_enabled();
		}
		else if ("update" == command || "edit" == command) {
			table.find("tr").each(function() {
				var tr = $(this);
				var c = tr.find(".td-edited .td-check").prop("checked");
				if (!c) return;
				var rn = tr.find("input[name='" + targetId + "__rn']").val();
				var clazz = tr.find("input[name='" + targetId + "__clazz']").val();
				if (!rn && !clazz) return;
				var div = null;
				$.ajax({ method: "GET", url: "popupEdit?p_sz=" + (p_sz ? p_sz : "12"), 
					success: function(result) {
						div = $('<div></div>');
						div.html(result);
						var p = {};
						if (rn) p["rn"] = rn;
						if (clazz) p["clazz"] = clazz;
						tr.find("[name^='" + targetId + "__']").each(function() {
							var name = $(this).attr("name");
							var k = name.indexOf("__");
							if (k > 0) name = name.substring(k + 2);
							if (!name || name=="rn" || name=="clazz" || p[name]) return;
							p[name] = $(this).val();
						});
						tr.closest("form").find("input,select").each(function() {
							var name = $(this).attr("name");
							if (!name) return;
							if (name.indexOf("__") >= 0) return;
							if (name=="rn" || name=="clazz" || p[name] || (names && names.indexOf(name) < 0)) return;
							p[name] = $(this).val();
						});
						/*processData: false,
  						contentType: false,*/
						$.ajax({ method: "GET", url: "detailsObj", data: p,
							success: function(result) {
								var modal = target.get_modal();
								modal.html(div.find('.modal').html());
								div = $('<div></div>');
								div.html(result);
								modal.find(".modal-body").html(div.find('.table-modal').html());
								//target.row_to_modal(tr, modal, targetId);
								target.edit_init();
								modal.modal();
								target.add_on(modal.find("#cancelButton"), "click", function(e) {
									modal.modal('hide');
									return false;
								});
								target.add_on(modal.find("form"), "submit", function(e) {
									e.preventDefault();
									return false;
								});
								target.add_on(modal.find("#submitButton"), "click", function(e) {
									if (rn) {
										var form = modal.find("form");
										$.ajax({ method: form.attr('method'), 
											url: form.attr('action'), 
											data: new FormData(form[0]),
											processData: false,
											contentType: false,
											success: function(result) {
												var div = $('<div></div>');
												div.html(result);
												var src = null, dest = null;
												var tableDest = $("#" + targetId);
												tableDest.find("tr").each(function() {
													var zz = $(this);
													if (rn == zz.find("input[name='" + targetId + "__rn']").val()) {
														dest = zz;
														return false;
													}
												});
												var tableSrc = div.find("#" + targetId);
												tableSrc.find("tr").each(function() {
													var zz = $(this);
													if (rn == zz.find("input[name='" + targetId + "__rn']").val()) {
														src = zz;
														return false;
													}
												});
												if (dest && src) dest.html(src.html());
												if (dest) dest.find(".td-edited .td-check").prop("checked", true);
												target.edit_init();
											},
											error: function(xhr) {
												if (xhr.status == 401) window.location = "login";
											}
										});
									}
									else {
										target.modal_to_row(modal, tr, targetId);
										
									}
									modal.modal('hide');
									return false;
								});
								target.add_on(modal, "hidden.bs.modal", function (e) {
									  target.put_modal(modal);
								});
								target.table_edit_focus(modal);
							},
							error: function(xhr) {
								if (xhr.status == 401) window.location = "login";
							}
						});
					},
					error: function(xhr) {
						if (xhr.status == 401) window.location = "login";
					} 
				});
				return false;
			});
		}
	},
	calculate: function() {
		$(".td-calc").each(function() {
			var sum = 0., cnt = 0;
			var op = $(this).attr("data-op");
			if (!op) op = "sum";
			if (op == "min" || op == "max") sum = null; 
			var nn = $(this).attr("data-name"), d = 0;
			if (nn) {
				$("label[data-name='" + nn + "']").each(function() {
					var qq = $(this).text(), s = '';
					if (qq) s = parseFloat(qq);
					if (typeof s == "number") {
						var zz = qq.indexOf(".");
						if (zz > 0) {
							zz = qq.length - zz - 1;
							if (zz > d) d = zz;
						}
						if (op == "sum" || op == "avg") sum += s;
						if (op == "max") if (sum == null || sum < s) sum = s;
						if (op == "min") if (sum == null || sum > s) sum = s;
						cnt++;
					}
				});
			}
			if (op == "avg" && cnt > 0) sum = sum / cnt;
			$(this).text("" + sum.toFixed(d));
		});
	},
	button_enabled: function() {
		$(".xbutton").each(function() {
			var e = false, b = $(this);
			for (; ;) {
				var command = b.attr("data-command"), targetId = b.attr("data-target"), ena = b.attr("data-enabled");
				if (!command || !targetId || ena != "1") break;
				var table = $("#" + targetId);
				if (!table) break;
				if ("add" == command) {
					var tr = table.find(".last-row");
					e = tr.length > 0;
				}				
				else if ("copy" == command) {
					var tr = table.find(".last-row");
					if (tr.length == 0) break;
					var c = table.find(".td-edited .td-check:checked");
					e = c.length > 0;
				}
				else if ("update" == command || "edit" == command) {
					var c = table.find(".td-edited .td-check:checked");
					if (c.length == 0) break;
					c.each(function() {
						var tr = c.closest("tr");
						var rn = tr.find("input[name='" + targetId + "__rn']").val();
						var clazz = tr.find("input[name='" + targetId + "__clazz']").val();
						if (rn || clazz) {
							e = true;
							return false;
						}						
					});
				}
				else if ("remove" == command || "delete" == command) {
					var c = table.find(".td-edited .td-check:checked");
					e = c.length > 0;
				}
				break;
			}
			b.prop("disabled", !e);
		});
	},
	// Вызов всплывающего окна для выбора объекта
	popup_select: function(el, s) {
		var target = this;
		var filter = el.attr("data-filter");
		var data = {
				clazz: el.attr("data-clazz"),
				p_sz: 12,
				p_title: el.attr("data-title"),
				p_column: el.attr("data-column"),
				p_filter: filter
			};
		$.ajax({ method: "POST", url: "popupSelect", 
			data: data,
			success: function(result) {
				var div = $('<div></div>');
				div.html(result);
				var modal = target.get_modal();
				modal.html(div.find(".modal").html());
				modal.modal();
				var h = modal.outerHeight(true), a = modal.find("table tbody");
				a.outerHeight(h / 2);
				target.set_header_width(modal.find("table"));
				target.add_on(modal.find("table"), "scroll", function() { $(this).find("tbody").width($(this).width() + $(this).scrollLeft()); });
				target.add_on(modal.find(".save-button"), "click", function() {
					modal.find("table tbody tr").each(function() {
						var rn = $(this).find(".d-none").first().text();
						var c = $(this).find(".check-select > input[type='checkbox']").prop("checked");
						var t = $(this).find(".text-select").text();
						if (c) {
							el.val(rn);
							s.text(t);
							var b = a.closest("tr"); 
							var cmd = $(b).find(".cmd > input").val();
							if (cmd != "add") $(b).find(".cmd > input").val("update");
							target.calculate();
							return false;
						}
					});
				});
				target.add_on(modal, "hidden.bs.modal", function (e) {
					  target.put_modal(modal);
				});
			},
			error: function(xhr) {
				if (xhr.status == 401) window.location = "login";
			}
		});
	},
	// Подгонка высоты окна
	size_fit: function() {
		var target = this;
		$('.fit-height').each(function () {
			$(this).hide();
			var p = $(this).parents();
			if (p.length > 0) for (var i=p.length-1; i>=0; i--) {
				var e = p[i];
				var tag = $(e).prop("tagName").toLowerCase();
				if (tag == "html" || tag == "body") continue;
				target.calc_height(e, false);
			}
			var h = target.calc_height(this, false);
			if ($(this).find(".table-fixed").length == 0) {
				$(this).css("overflow-y", "auto");
				$(this).show();
			}
			else {
				$(this).show();
				var tb = $(this).find("tbody");
				tb.css("overflow-y", "auto");
				tb.outerHeight($("footer").offset().top - tb.offset().top);
				var a = $(this).find(".table-fixed");
				a.find("tbody").width(a.width() + a.scrollLeft());
				if (tb.get(0).scrollWidth > tb.innerWidth()) tb.outerHeight(tb.outerHeight() - target.scroll_bar_size().height);
			}
			return false;
		});
	},
	// Установка высоты окна
	calc_height: function(a, m) {
		var h = 0, pa = $(a).parent(), target = this;
		pa.children().filter(':visible').each(function() {
			if ($(a)[0] != $(this)[0]) h += $(this).outerHeight(true);
		});
		var ph = pa[0].clientHeight - (pa.outerHeight(true) - pa.outerHeight()) - h;
		if (m) ph -= target.scroll_bar_size().height;
		$(a).outerHeight(ph);
		return ph;
	},
	// Скроллинг к выбранному элементу
	scrollTo: function(e) {
		var a = e.find("table tbody");
		e.find('table tbody tr').each(function() {
			var c = $(this).find(".check-select > input[type='checkbox']").prop("checked");
			if (c) {
				var off = $(this).offset().top - a.offset().top;
				a.animate({scrollTop: off});
				return false;
			}
		});
	},
	choose_object: function(t) {
		if (!t) return;
		var target = this, rn = $(t).parent().find("input[type='hidden']").val();
		var multiple = t.attr("data-multiple");
		var unique = t.attr("data-unique");
		unique = unique != "false" && unique != "no" ? true : false;
		var clazz = t.attr("data-clazz");
		if (!clazz) clazz = $("input[name='clazz']").val();
		if (!clazz) return;
		var filter = t.attr("data-filter");
		var data = {
				clazz: clazz,
				rn: rn,
				p_sz: 8,
				p_title: t.attr("data-title"),
				p_column: t.attr("data-column"),
				p_filter: filter
			};
		if (filter && filter.indexOf("$") >= 0) {
			var k = filter.indexOf("$"), e = filter.indexOf("$", k + 1);
			if (e > 0) {
				var r = filter.substring(k + 1, e);
				if (r) {
					var v = null;
					var zz = $(t).closest("tr").find("input[name$='" + r + "']");
					if (zz.length > 0) v = zz.val();
					if (!v) {
						zz = $(t).closest("form").find("input[name='" + r + "']");
						if (zz.length > 0) v = zz.val();
					}
					if (v) data[r] = v;
				}
			}
		}
		var arrRn = [];
		$(t).closest("table").find(".choose_obj").each(function() {
			var z_rn = $(this).parent().find("input[type='hidden']").val();
			if (z_rn > 0) arrRn[length] = z_rn;
		});
		$.ajax({ method: "POST", url: "popupSelect", 
			data: data,
			success: function(result) {
				var div = $('<div></div>');
				div.html(result);
				if (div.find('.modal').length == 0) return;
				var modal = target.get_modal();
				modal.html(div.find('.modal').html());
				modal.modal();
				var h = modal.outerHeight(true), a = modal.find("table tbody");
				a.outerHeight(h / 2);
				var table = modal.find("table");
				target.set_max_width(table);
				target.add_on(table, "scroll", function() { $(this).find("tbody").width($(this).width() + $(this).scrollLeft()); });
				table.find("tbody td:last-child").first().each(function() {
					var w = $(this).outerWidth(true) + target.scroll_bar_size().width;
					$(this).css("max-width", w);
					$(this).css("min-width", w);
				});
				target.set_header_width(table);
				target.scrollTo(modal);
				target.add_on(modal.find(".check-select > input[type='checkbox']"), "change", function() {
					var p = $(this).prop("checked");
					if (!multiple) modal.find(".check-select > input[type='checkbox']").prop("checked", false);
					$(this).prop("checked", p);
				});
				target.add_on(modal.find(".save-button"), "click", function() {
					var prn = [], psource = [];
					modal.find("table tbody tr").each(function() {
						var c = $(this).find(".check-select > input[type='checkbox']").prop("checked");
						if (c) {
							var rn = $(this).find(".d-none").first().text();
							if (unique && target.in_array(rn, arrRn)) return;
							prn[prn.length] = rn;
							psource[psource.length] = this;
						}
					});
					for (var j=0; j<prn.length; j++) {
						var rn = prn[j], source = psource[j];
						if (i > 0) {
							var tr = $(t).closest("table").find(".last-row");
							if (tr.length == 0) break;
							var c = tr.clone().insertBefore(tr);
							c.removeClass("not-visible last-row");
							$(c).find("input[name$='p_cmd']").val("add");
							t = $(c).find(".choose_obj");
							c.find(".td-edited .td-check").prop("checked", true);
							target.edit_init();
						}
						
						var p = $(t).parent();
						p.find("input[type='hidden']").val(rn > 0 ? rn : "");
						p = $(p).closest(".parent-popup");
						target.calculate();
						
						var arr = $(source).find(".text-select");
						for (var i=0; i<arr.length; i++) {
							var tt = rn > 0 ?  $(arr[i]).text() : "";
							var idx = null;
							try {idx = parseInt($(arr[i]).attr("data-target")); } catch(e) { }
							if (idx == null || isNaN(idx)) idx = i;
							var oo = $(p).find(".td-label:eq(" + idx + ")");
							oo.text(tt);
							if (oo.length == 0) $(p).find("input:eq(" + idx + ")").val(tt);
						}
						
						var zz = $(t).closest("tr").find(".d-none > input[name$='p_cmd']");
						if (!zz.val()) zz.val("update");
						
						if (!multiple) break;
					}
				});
				target.add_on(modal.find(".filter-popup"), "input", function() {
					var t = $(this);
					modal.find("table tbody tr").show();
					var tt = t.val();
					if (tt) {
						tt = tt.toLowerCase();
						modal.find("table tbody tr").each(function() {
							var b = false;
							$(this).find("td").each(function() {
								var ttt = $(this).text();
								if (ttt) {
									ttt = ttt.toLowerCase();
									if (ttt.indexOf(tt) >= 0) {
										b = true;
										return false;
									}
								}
							});
							if (!b) $(this).hide(); 
						});
					}
					target.scrollTo(modal);
				});
				target.add_on(modal, "hidden.bs.modal", function() {
					target.put_modal(modal);
				});
			},
			error: function(xhr) {
				if (xhr.status == 401) window.location = "login";
			}
		});
	},
	// Функция инициализации окна редактирования (просмотра) объекта
	details_init: function() {
		var target = this;
		// Каждый раз 
		target.edit_init();
		// Только в начале
		target.size_init();
		target.buttons_init();
		target.table_edit_focus();
	},
	get_value: function() {
		var ret = "";
		$("form").first().find("[name]").each(function() { ret += $(this).val(); });
		return ret;
	},
	// Инициализация окна редактирования (просмотра) объекта
	edit_init: function() {
		var target = this;
		target.date_on();
		target.time_on();
		target.file_on();
		target.address_on();
		target.page_init();
		target.popup_init();
		target.table_edit_init();
		target.require_init();
		target.valueSource = target.get_value();
		target.add_on($(".cancel-button"), "click", function() {
			var b = $(this);
			if (target.get_value() != target.valueSource) {
				target.question("Сообщение", "Данные на форме были отредактированы.<br>Сохранить?", "yes=Да;no=Нет;cancel=Отмена", "3", function(r) {
					if (r == "cancel") return;
					else if (r == "no") window.location = b.attr("href");
					else b.closest("form").submit();
				});
				return false;
			}
		});
	},
	// Инициализация кнопок
	buttons_init: function() {
		var target = this,rn = $('input[name="rn"]').val();
		target.set_buttons(rn);
		target.add_on($('.execute_obj'), "click", function() { 
			var param = $(this).attr("data-param"), op = "execute";
			if (param == "save" || param == "refresh" || param == "cancel") op = param;
			target.exec_obj(op, param); 
		});
	},
	// Инициализация строк ввода адреса
	address_on: function() {
		if ($.isFunction($.fn.fias)) $(".address").fias({ oneString: true });
	},
	// Инициализация работы с файлами через окно на форме
	file_init: function() {
		var target = this;
		$('.custom-file-input').on("change", function() { 
		   var fileName = $(this).val().split('\\').pop(); 
		   $(this).next('.custom-file-label').addClass("selected").html(fileName); 
		   target.upload_file(this);
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
			var t = $(this).attr("data-target");
			target.download_file(t, fileName);
		});
	},
	// Инициализация таблицы
	list_init: function() {
		var target = this;
		// Клик по строке
		var rn = $('input[name="rn"]').val(), selected = false;
		$("#" + target.tableId + " tbody tr").each(function() {
			$(this).removeClass(target.rowSelection);
			if (rn && rn == $(this).find("td.d-none").first().text()) {
				var a = $(".fit-height").find(".table-fixed");
				if (a.length == 0) a = $(".fit-height");
				if (a.length > 0) {
					var off = $(this).offset().top - a.offset().top;
					a.animate({scrollTop: off});
				}
				target.click_row($(this)[0]);
				selected = true;
			}
		});
		if (!selected) {
			rn = null;
			$('input[name="rn"]').val(null);
		}
		// Кнопки
		target.set_buttons(rn);
		target.add_on($('.execute_obj'), "click", function() { 
			var param = $(this).attr("data-param"), op = "execute";
			if (param == "edit" || param == "view" || param == "add" || param == "remove") op = param;
			target.exec_obj(op, param); 
		});
		target.add_on($("#" + target.tableId + " tbody tr"), "click", function() { target.click_row(this); });
		target.add_on($("#" + target.tableId + " tbody tr"), "dblclick", function() { target.click_row(this, true); target.exec_obj("edit"); });
		// Установка однострочного содержимого данных
		target.set_max_width($("#" + target.tableId));
		// Изменение размера области данных после скроллинга
		target.add_on($("#" + target.tableId), "scroll", function() {
			$(this).find("tbody").width($(this).width() + $(this).scrollLeft());
		});
		// Очистка фильтров
		target.add_on($("#" + target.clearFilterId), "click", function() { target.clear_filter(); });
		// Поиск и фильтрация
		target.add_on($("#" + target.filterButtonId), "click", function() {
			$("#" + target.filterRowId).toggle();
			var b = target.filter_class();
			if (!b)	{
				var a = null;
				$("#" + target.tableId + " th input[type='text'],#" + target.tableId + " th select").each(function() { if ($(this).val()) { a = $(this); return false; } });
				if (!a) $("#" + target.tableId + " th input[type='checkbox']").each(function() { if ($(this).is(':checked')) { a = $(this); return false; } });
				if (a) a.focus(); else $("th input[type='text'],th input[type='checkbox'],th select").first().focus();
			}
			$("#" + target.clearFilterId).prop('disabled', b);
			target.set_header_width($("#" + target.tableId));
			// Размер скроллируемой области
			var tb = $(".table-fixed tbody");
			if (tb.length > 0) {
				tb.outerHeight($("footer").offset().top - tb.offset().top);
				if (tb.get(0).scrollWidth > tb.innerWidth()) tb.outerHeight(tb.outerHeight() - target.scroll_bar_size().height);
			}
		});
		target.add_on($("input[name='p_all']"), "click", function() { target.form_submit(); });
		target.add_on($("#" + target.findButtonId), "click", function() { target.form_submit(); });
		target.add_on($("#" + target.tableId + " th input[type='text'],#" + target.tableId + " th input[type='checkbox'],#" + target.tableId + " th select"), "keypress", function(e) {
			if (e.which == 13) target.form_submit();
		}); 
		target.filter_focus();
		// Размер страницы
		target.add_on($(".p_page"), "change", function() {
			$("input[name='p_page']").val($(this).val());
			target.form_submit();
		});
		// Сортировка
		target.add_on($(".sorting,.sorting_asc,.sorting_desc"), "click", function() {
			var v = $(this).find("input[type='hidden']").first().val();
			if (v == "ASC") v = "DESC"; else if (v == "DESC") v = "NONE"; else v = "ASC";
			$(this).find("input[type='hidden']").first().val(v);
			target.sort_fill();
			target.form_submit();
		});
		target.sort_fill();
		// Пейджинг
		target.add_on($(".page-link"), "click", function() {
			$('input[name="p_off"]').val($(this).attr("data-value"));
			target.form_submit();
		});
		// Переход на список - подстановка выделенного объекта
		target.add_on($(".select_obj"), "click", function() {
			var rn = $('input[name="rn"]').val();
			if (rn > 0) {
				var href = $(this).attr("href") + "&rn=" + rn;
				window.location = href;
				return false;
			}
		});
		// Колонки
		target.add_on($("#" + target.setVisibleId), "click", function() {
			$.ajax({ method: "GET", url: "popupVisible", 
				data: {
					clazz: $('#clazz').val(),
					p_sz: 6
				},
				success: function(result) {
					var div = $('<div></div>');
					div.html(result);
					var modal = target.get_modal();
					modal.html(div.find('.modal').html());
					modal.modal();
					var h = modal.outerHeight(true);
					var a = modal.find("table tbody");
					a.outerHeight(h / 2);
					modal.find("table tbody").css("overflow-y", "auto");
					target.set_header_width($(".modal").find("table"));
					target.add_on(modal.find(".td-visible"), "click", function() { 
						$(this).text($(this).text() == "да" ? "нет" : "да"); 
					});
					target.add_on(modal.find(".save-button"), "click", function() {
						var v = "";
						modal.find("table tbody tr").each(function() {
							if ($(this).find(".td-visible").first().text() == "да") {
								if (v) v += ",";
								v += $(this).find(".d-none").first().text();
							}
						});
						$("input[name='p_listVisible']").val(v);
						target.form_submit();
						$("input[name='p_listVisible']").val("");
					});	
					target.add_on(modal, "hidden.bs.modal", function() {
						target.put_modal(modal);
					});
				},
				error: function(xhr) {
					if (xhr.status == 401) window.location = "login";
				}
			});
		});
		// Установка размера колонок - предварительно последнюю увеличиваем для скроллинга
		$("#" + target.tableId).find("tbody td:last-child").first().each(function() {
			var w = $(this).outerWidth(true) + target.scroll_bar_size().width;
			$(this).css("max-width", w);
			$(this).css("min-width", w);
		});
		target.set_header_width($("#" + target.tableId));
	},
	// Инициализация редактирования по месту
	page_init: function() {
		var target = this;
		target.add_on($('.edited'), 'click', function(event) {
			target.start_edit(this);
		});
		target.add_on($('.edited .custom-file-input'), "change", function() { 
			var fileName = $(this).val().split('\\').pop(); 
			$(this).next('.custom-file-label').addClass("selected").html(fileName); 
			target.stop_edit($(this).closest(".edited"));   
		});
		target.add_on($('.add-item'), 'click', function(event) {
			var table = $(event.delegateTarget).closest("table");
			var c = $(table).find(".first-row").clone().insertBefore($(table).find(".last-row"));
			c.removeClass("first-row");
			$(c).find(".cmd > input").val("add");
			c.show();
			target.add_on(c.find('.remove-item'), 'click', function(event) {
				var c = $(event.delegateTarget).closest("tr");
				$(c).find(".cmd > input").val("remove");
				c.hide();
			});
			target.add_on(c.find('.custom-file-input'), "change", function() { 
				var fileName = $(this).val().split('\\').pop(); 
				$(this).next('.custom-file-label').addClass("selected").html(fileName); 
				target.stop_edit($(this).closest(".edited"));   
			});
			c.find('.input_date').datepicker({language: "ru"});
			target.add_on(c.find('.edited'), 'click', function(event) {
				target.start_edit(this);
			});
			target.start_edit(c.find('.edited').first());
		});
		target.add_on($('.remove-item'), 'click', function(event) {
			var c = $(event.delegateTarget).closest("tr");
			$(c).find(".cmd > input").val("remove");
			c.hide();
		});
		target.add_on($('.view-item'), 'click', function(event) {
			var url = $(event.delegateTarget).attr("data-item");
			window.open(url, '_blank');
		});
	},
	// Инициализация всплывающих окон
	popup_init: function() {
		var target = this;
		target.add_on($(".choose_obj"), "click", function(event) {
			target.choose_object($(this));
			return false;
		});
	},
	// Инициализация контроля обязательных полей
	require_init: function() {
		var target = this;
		target.add_on($("#" + target.submitButtonId), "click", function () {
			var b = true;
			$(':required:invalid').each(function () {
				var id = $(this).closest('.tab-pane').attr('id');
				if (b) {
					var a = $(this), c = $('.nav a[href="#' + id + '"]'); 
					if (c.length > 0) c.tab('show');
					var h = a.is(':hidden');
					if (h) {
						c = a.closest(".td-edited");
						if (c.length > 0) target.table_edit(c);
						else a.show();
					}
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
			return b;
		});
	},
	// Иницициализация размещения окон
	size_init: function() {
		var target = this;
		target.add_on($(window), "resize", function() { target.size_fit(); });
		setTimeout(function() { target.size_fit(); }, 40);
	},
	// Инициализация редактирования в таблице
	table_edit_init: function() {
		var target = this;
		$(".td-edited").each(function() {
			if ($(this).hasClass("readonly")) return;
			var zz = $(this).find("input[type='text'],input[type='password'],input[type='date'],input[type='checkbox'],select,.custom-file,textarea,.custom-date,.pbutton");
			if (zz.length == 0) return;
			$(this).addClass("edited");	
		});
		target.add_on($(".td-edited"), "click", function() {
			target.table_edit($(this));
		});
		target.add_on($(".xbutton"), "click", function() {
			target.button_command($(this));
		});
		target.add_on($(".td-edited select"), "change", function() {
			var q = $(this);
			setTimeout(function() {
				target.table_edit_end(q, { keyCode: 13, shiftKey: false, ctrlKey: false });
			}, 10);
		});
		target.add_on($(".td-edited input[type='checkbox']"), "change", function() {
			var q = $(this);
			if (q.hasClass("td-check")) { target.button_enabled(); return; }
			var zz = $(q).closest("tr").find(".d-none > input[name$='p_cmd']");
			if (!zz.val()) zz.val("update");
		});
		target.add_on($('.td-edited .custom-file-input'), "change", function() { 
			var fileName = $(this).val().split('\\').pop(); 
			$(this).next('.custom-file-label').addClass("selected").html(fileName);
			target.table_edit_end($(this), { keyCode: 13, shiftKey: false, ctrlKey: false });
		});
		target.add_on($('.td-edited .custom-date .date'), "change.datetimepicker", function(e) { 
			var t = $(this).closest(".td-edited").find(">label,>span").first().text();
			if (t || e.oldDate) target.table_edit_end($(this).find("input"), { keyCode: 13, shiftKey: false, ctrlKey: false });
		});
		target.add_on($(".expand"), "click", function(e) {
			var dt = $(this).attr("data-target");
			if (!dt) return false;
			var a = $("#" + dt);
			if (!a) return false;
			var b = a.is(':hidden');
			if (b) a.show(); else a.hide();
			$(this).find("i").removeClass("fa-plus fa-minus");
			$(this).find("i").addClass(b ? "fa-minus" : "fa-plus");
			return false;
		});
		$(".table-edited td,.table-edited th").resizable({handles: "e"});
		target.button_enabled();
		target.calculate();
	}
};
