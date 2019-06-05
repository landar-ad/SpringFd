popup_init = function() {
	add_on($('.choose_document'), 'click', function(event) {
		var target = $(this);
		$.ajax({ method: "POST", url: "popupSelect", 
			data: {
				clazz: "Document",
				p_title: "Выберите документ",
				p_column: "doc_type__name=Тип документа;doc_number=№ документа;doc_date=Дата документа",
				p_filter: ""
			},
			success: function(result) {
				var div = $('<div></div>');
				div.html(result);
				$('.modal').html(div.find('.modal').html());
				$(".modal").modal();
				$(".modal-body").outerHeight($(document.body).outerHeight() * 2 / 3);
				$(".modal-body").css("overflow-y", "auto");
				add_on($(".modal").find("#save-button"), "click", function() {
					$("#columnTable tbody tr").each(function() {
						var rn = $(this).find(".d-none").first().text();
						var c = $(this).find(".check-select > input[type='checkbox']").prop("checked");
						if (c) {
							var p = $(target).parent();
							p.find("input[name='parent_doc']").val(rn);	
							p = $(p).parent();
							var t1 = $(this).find(".text-select:eq(0)").text();
							$(p).find("input:eq(0)").val(t1);
							var t2 = $(this).find(".text-select:eq(1)").text();
							$(p).find("input:eq(1)").val(t2);
							var t3 = $(this).find(".text-select:eq(2)").text();
							$(p).find("input:eq(2)").val(t3);
							return false;
						}
					});
				});
			},
			error: function() {
			}
		});
	});
};