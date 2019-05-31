page_init = function(list, clazzItem) {
	start_edit = function(c) {
		$.each($(".edited"), function() { cancel_edit(this); });
		var a = $(c).find("input,div,select,textarea").first(), q = a;
		var s = $(c).find("span").first();
		var b = $(c).closest("tr"); 
		if (a) {
			s.hide();
			q.show();
			if (q.prop("tagName").toLowerCase() == "div") a = q.find("input,select,textarea").first();
			a.val(s.text());
			a.focus();
			add_on($(a), "keypress", function(e) {
				if (e.which == 13) {
					stop_edit(this);
					e.preventDefault();
				}
				if (e.which == 27) {
					cancel_edit(this);
					e.preventDefault();
				}
			});
		}
	};
	stop_edit = function(c) {
		var a = $(c).find("input,div,select,textarea").first(), q = a;
		var s = $(c).find("span").first();
		var b = $(c).closest("tr"); 
		var t = $(c).val();
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
	add_on($('.edited'), 'click', function(event) {
		start_edit(this);
	});
	add_on($('.add-item'), 'click', function(event) {
		var c = $(".first-row").clone().insertBefore($(".last-row"));
		c.removeClass("first-row");
		$(c).find(".cmd > input").val("add");
		c.show();
		add_on(c.find('.remove-item'), 'click', function(event) {
			$(event.delegateTarget).closest("tr").hide();
		});
		add_on(c.find('.custom-file-input'), "change", function() { 
			   var fileName = $(this).val().split('\\').pop(); 
			   $(this).next('.custom-file-label').addClass("selected").html(fileName); 
		});
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
		var url = "/fileView?rn=" + $(event.delegateTarget).attr("data-item");
		window.open(url, '_blank');
	});
	/*
	form_submit_details = function() {
		var h = $('.fit-height').outerHeight();
		var form = $('#formSubmit');
		$.ajax({ method: form.attr('method'), url: form.attr('action'), data: new FormData(form[0]),
			success: function(result) {
				var div = $('<div></div>');
				div.html(result);
				$('.fit-height').html(div.find('.fit-height').html());
				$('.fit-height').outerHeight(h);
			}
		});
	};
	executeItem = function (list, clazzItem, cmd, rnItem) {
		var h = $('.fit-height').outerHeight();
		var form = $('.form').first();
		var clazz = form.find('input[name="clazz"]').val();
		var rn = form.find('input[name="rn"]').val();
		$.ajax({ method: "POST", 
			url: "executeItem",
			data: {
				clazz: clazz,
				rn: rn,
				list: list,
				clazzItem: clazzItem,
				cmdItem: cmd,
				rnItem: rnItem
			},
			success: function(result) {
				var div = $('<div></div>');
				div.html(result);
				$('#' + list).html(div.find('#' + list).html());
				$('.fit-height').outerHeight(h);
				page_init(list, clazzItem);
			},
			error: function() {
			}
		});
	};
	add_on($('.add-item'), 'click', function(event) {
		var popupUrl = $(event.delegateTarget).attr("data-popup-url");
		var editUrl = $(event.delegateTarget).attr("data-edit-url");
		if (!popupUrl || !editUrl) {
			executeItem(list, clazzItem, "add", null);
			return;
		}
		$.ajax({ method: "POST", url: popupUrl, data: "",
			success: function(result) {
				var div = $('<div></div>');
				div.html(result);
				$('.modal').html(div.find('.modal').html());
				$.ajax({ method: "GET", url: editUrl, data: "",
					success: function(result) {
						var div = $('<div></div>');
						div.html(result);
						var html = div.find('.fit-height').html();
						$('.modal').find('.fit-height').html(html);
						$('.modal').find('.custom-file-input').on("change", function() { 
							   var fileName = $(this).val().split('\\').pop(); 
							   $(this).next('.custom-file-label').addClass("selected").html(fileName); 
						});
						$('.modal').find('.btn-group').hide();
						add_on($("#save-button"), "click", function() {
							var form = $('.modal').find('.form');
							form.find("input[name='p_popup']").val("1");
							var data = new FormData(form[0]);
							$.ajax({ 
								method: form.attr('method'), url: 
								form.attr('action'), 
								data: data,
								contentType: false,
								processData: false,
								success: function(result) {
									var div = $('<div></div>');
									div.html(result);
									var rnItem = div.find("input[name='rn']").val();
									if (rnItem > 0) executeItem(list, clazzItem, "add", rnItem);
								},
								error: function() {
								}
							});
						});	
						$(".modal").modal();
						$(".modal-body").outerHeight($(document.body).outerHeight() * 2 / 3);
						$(".modal-body").css("overflow-y", "auto");
					},
					error: function() {
					}
				});
			},
			error: function() {
			}
		});
	});
	add_on($('.remove-item'), 'click', function(event) {
		executeItem(list, clazzItem, "remove", $(event.delegateTarget).attr("data-item"));
	});
	*/
};