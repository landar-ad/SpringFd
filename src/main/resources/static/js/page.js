page_init = function(list, clazz) {
	clear_param = function() {
		$("input[name='list']").val("");
		$("input[name='clazzItem']").val("");
		$("input[name='cmdItem']").val("");
		$("input[name='rnItem']").val("");
	};
	executeItem = function (list, clazz, cmd, rn) {
		$("input[name='list']").val(list);
		$("input[name='clazzItem']").val(clazz);
		$("input[name='cmdItem']").val(cmd);
		$("input[name='rnItem']").val(rn);
		var h = $('.fit-height').outerHeight();
		var form = $('.form').first();
		var b = true;
		$(':required:invalid').each(function () { b = false; });
		if (!b) return;
		$.ajax({ method: form.attr('method'), url: form.attr('action'), data: form.serialize(),
			success: function(result) {
				var div = $('<div></div>');
				div.html(result);
				$('.fit-height').html(div.find('.fit-height'));
				$('.fit-height').outerHeight(h);
				page_init(list, clazz);
				clear_param();
			},
			error: function() {
				clear_param();
			}
		});
	};
	add_on($('.add-item'), 'click', function(event) {
		var popupUrl = $(event.delegateTarget).attr("data-popup-url");
		var editUrl = $(event.delegateTarget).attr("data-edit-url");
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
							//var data = form.serialize();
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
									var rn = div.find("input[name='rn']").val();
									if (rn > 0) executeItem(list, clazz, "add", rn);
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
		executeItem(list, clazz, "remove", $(event.delegateTarget).attr("data-item"));
	});
};