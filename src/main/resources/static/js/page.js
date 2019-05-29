page_init = function(list, clazzItem) {
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
		$(".first-row").clone().insertBefore($(".last-row")).show();
		add_on($('.custom-file-input'), "change", function() { 
			   var fileName = $(this).val().split('\\').pop(); 
			   $(this).next('.custom-file-label').addClass("selected").html(fileName); 
		});
	});
	add_on($('.remove-item'), 'click', function(event) {
		$(event.delegateTarget).closest("tr").remove();
	});
	add_on($('.view-item'), 'click', function(event) {
		var url = document.baseURI + "/fileView?rn=" + $(event.delegateTarget).attr("data-item");
		window.open(url, '_blank');
	});
	/*
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