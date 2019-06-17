popup_init = function() {
	add_on($('.choose_obj'), 'click', function(event) {
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
				var h = $(".modal").outerHeight();
				var a = $(".modal").find(".fit-height");
				a.outerHeight(h * 3 / 5);
				$(".modal").find('#columnTable tbody tr').each(function() {
					var c = $(this).find(".check-select > input[type='checkbox']").prop("checked");
					if (c) {
						var off = $(this).offset().top - a.offset().top;
						a.animate({scrollTop: off});
						return false;
					}
				});
				add_on($(".modal").find(".check-select > input[type='checkbox']"), "change", function() {
					var p = $(this).prop("checked");
					if (!multiple) $(".check-select > input[type='checkbox']").prop("checked", false);
					$(this).prop("checked", p);
				});
				add_on($(".modal").find("#save-button"), "click", function() {
					var rn = "", source = null;
					$('.modal').find("#columnTable tbody tr").each(function() {
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