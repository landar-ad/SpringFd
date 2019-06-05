popup_init = function() {
	add_on($('.choose_document'), 'click', function(event) {
		var target = $(this);
		var data = {
				clazz: target.attr("data-clazz"),
				p_title: target.attr("data-title"),
				p_column: target.attr("data-column"),
				p_filter: target.attr("data-filter")
			};
		$.ajax({ method: "POST", url: "popupSelect", 
			data: data,
			success: function(result) {
				var div = $('<div></div>');
				div.html(result);
				$('.modal').html(div.find('.modal').html());
				$(".modal").modal();
				$(".modal-body").outerHeight($(document.body).outerHeight() * 2 / 3);
				$(".modal-body").css("overflow-y", "auto");
				add_on($(".modal").find("#save-button"), "click", function() {
					var rn = "";
					$("#columnTable tbody tr").each(function() {
						var c = $(this).find(".check-select > input[type='checkbox']").prop("checked");
						if (c) {
							rn = $(this).find(".d-none").first().text();
							return false;
						}
					});
					var p = $(target).parent();
					p.find("input[type='hidden']").val(rn > 0 ? rn : "");	
					p = $(p).parent();
					var arr = data.p_column.split(";");
					for (i=0; i<arr.length; i++) {
						var t = rn > 0 ?  $(this).find(".text-select:eq("+ i + ")").text() : "";
						$(p).find("input:eq(" + i + ")").val(t);
					}
				});
			},
			error: function() {
			}
		});
	});
};