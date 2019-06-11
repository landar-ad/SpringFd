popup_init = function() {
	add_on($('.choose_obj'), 'click', function(event) {
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
					var rn = "", source = null;
					$("#columnTable tbody tr").each(function() {
						var c = $(this).find(".check-select > input[type='checkbox']").prop("checked");
						if (c) {
							rn = $(this).find(".d-none").first().text();
							source = this;
							return false;
						}
					});
					var p = $(target).parent();
					p.find("input[type='hidden']").val(rn > 0 ? rn : "");	
					p = $(p).parent();
					var arr = data.p_column.split(";");
					for (i=0; i<arr.length; i++) {
						var t = rn > 0 ?  $(source).find(".text-select:eq("+ i + ")").text() : "";
						$(p).find("input:eq(" + i + ")").val(t);
					}
				});
				add_on($(".modal").find(".filter-popup"), "input", function() {
					var target = $(this);
					$("#columnTable tbody tr").show();
					var t = target.val();
					if (t) {
						t = t.toLowerCase();
						$("#columnTable tbody tr").each(function() {
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
				});
			},
			error: function() {
			}
		});
	});
};