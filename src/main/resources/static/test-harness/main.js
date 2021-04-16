(function(){
	var rowCreationHandler = function(data) {
		$.each(data, function(i, item){
			var idCell = $("<td>").text(item.id);
			var emailCell = $("<td>").text(item.email);
			var givenNameCell = $("<td>").text(item.givenName);
			var familyNameCell = $("<td>").text(item.familyName);
			var createdCell = $("<td>").text(item.created);
			$("#users_table_body").append($("<tr>").attr("class","table-secondary").append(idCell).append(emailCell).append(givenNameCell).append(familyNameCell).append(createdCell));
		});
	};
	
	$(document).ajaxSend(function(){
		$("#error_box").hide();
	});
	$(document).ajaxError(function(event, jqXHR, ajaxSettings, thrownError) {
		var responseText = jqXHR.responseText;
		if ((responseText.length > 0) && (responseText.charAt(0) == '{')) {
			var error = JSON.parse(responseText);
			var message = error.message;
			if (message) {
				var errorHtml = "";
				if (error.errorCode) {
					errorHtml = error.errorCode + "<br/>";
				}
				errorHtml = errorHtml + message;
				if (error.erroneousValue) {
					errorHtml = errorHtml + "<br/>Erroneous value: " + error.erroneousValue;
				}
				$("#error_text").html(errorHtml);
				$("#error_box").show();
			}
		}
	});

	$(document).ready(function(){
		$("#num_users").keyup(function(){
			if ($(this).val().length == 0) {
				$("#random_data").prop("disabled", true);
			} else {
				$("#random_data").prop("disabled", false);
			}
		});
		$("#offset").keyup(function(){
			if (($(this).val().length == 0) && ($("#limit").val().length == 0)) {
				$("#custom_refresh").prop("disabled", true);
			} else {
				$("#custom_refresh").prop("disabled", false);
			}
		});
		$("#limit").keyup(function(){
			if (($(this).val().length == 0) && ($("#offset").val().length == 0)) {
				$("#custom_refresh").prop("disabled", true);
			} else {
				$("#custom_refresh").prop("disabled", false);
			}
		});
		$("#id_to_show").keyup(function(){
			if ($(this).val().length == 0) {
				$("#show_specific").prop("disabled", true);
			} else {
				$("#show_specific").prop("disabled", false);
			}
		});
		$("#email_create").keyup(function(){
			if (($(this).val().length == 0) || ($("#given_create").val().length == 0) || ($("#family_create").val().length == 0)) {
				$("#create").prop("disabled", true);
			} else {
				$("#create").prop("disabled", false);
			}
		});
		$("#given_create").keyup(function(){
			if (($(this).val().length == 0) || ($("#email_create").val().length == 0) || ($("#family_create").val().length == 0)) {
				$("#create").prop("disabled", true);
			} else {
				$("#create").prop("disabled", false);
			}
		});
		$("#family_create").keyup(function(){
			if (($(this).val().length == 0) || ($("#email_create").val().length == 0) || ($("#given_create").val().length == 0)) {
				$("#create").prop("disabled", true);
			} else {
				$("#create").prop("disabled", false);
			}
		});
		$("#id_update").keyup(function(){
			if (($(this).val().length == 0) || ($("#email_update").val().length == 0) || ($("#given_update").val().length == 0) || ($("#family_update").val().length == 0)) {
				$("#update").prop("disabled", true);
			} else {
				$("#update").prop("disabled", false);
			}
		});
		$("#email_update").keyup(function(){
			if (($(this).val().length == 0) || ($("#id_update").val().length == 0) || ($("#given_update").val().length == 0) || ($("#family_update").val().length == 0)) {
				$("#update").prop("disabled", true);
			} else {
				$("#update").prop("disabled", false);
			}
		});
		$("#given_update").keyup(function(){
			if (($(this).val().length == 0) || ($("#id_update").val().length == 0) || ($("#email_update").val().length == 0) || ($("#family_update").val().length == 0)) {
				$("#update").prop("disabled", true);
			} else {
				$("#update").prop("disabled", false);
			}
		});
		$("#family_update").keyup(function(){
			if (($(this).val().length == 0) || ($("#id_update").val().length == 0) || ($("#email_update").val().length == 0) || ($("#given_update").val().length == 0)) {
				$("#update").prop("disabled", true);
			} else {
				$("#update").prop("disabled", false);
			}
		});
		$("#id_to_delete").keyup(function(){
			if ($(this).val().length == 0) {
				$("#delete").prop("disabled", true);
			} else {
				$("#delete").prop("disabled", false);
			}
		});
		
		$("#reset").click(function(){
			$.ajax({
			  type: "POST",
			  url: "http://localhost:8080/db-mgmt/user-stats",
			  data: JSON.stringify({"numUsers": 0}),
			  dataType: "json",
			  contentType: "application/json"
			});
			$("#users_table_body").empty();
		});
		$("#default_data").click(function(){
			$.ajax({
			  type: "POST",
			  url: "http://localhost:8080/db-mgmt/test-datasets/0/activated",
			  data: JSON.stringify({"activated": true}),
			  dataType: "json",
			  contentType: "application/json"
			});
			$("#default_data").prop("disabled", true);
		});
		$("#random_data").click(function(){
			var numUsersStr = $("#num_users").val();
			var numUsers;
			if (!isNaN(numUsersStr)) {
				numUsers = parseInt(numUsersStr, 10);
			} else {
				numUsers = numUsersStr; // Here it will be a string, but this may be useful for testing
			}
			$.ajax({
			  type: "POST",
			  url: "http://localhost:8080/db-mgmt/test-datasets",
			  data: JSON.stringify({"activated": true, "random": true, "numUsers": numUsers}),
			  dataType: "json",
			  contentType: "application/json",
			  success: function(){
				$("#num_users").val("");
				$("#random_data").prop("disabled", true);
			  }
			});
		});
		$("#default_refresh").click(function(){
			$("#users_table_body").empty();
			$.getJSON("http://localhost:8080/user-mgmt/users", rowCreationHandler);
		});
		$("#custom_refresh").click(function(){
			$("#users_table_body").empty();
			var offset = $("#offset").val();
			var limit = $("#limit").val();
			var url = "http://localhost:8080/user-mgmt/users?";
			if (offset.length > 0) {
				url = url + "offset=" + offset;
				if (limit.length > 0) {
					url = url + "&";
				}
			}
			if (limit.length > 0) {
				url = url + "limit=" + limit
			}
			$.getJSON(url, function(data){
				rowCreationHandler(data);
				$("#offset").val("");
				$("#limit").val("");
				$("#custom_refresh").prop("disabled", true);
			});
		});
		$("#show_specific").click(function(){
			$("#users_table_body").empty();
			var url = "http://localhost:8080/user-mgmt/users/" + $("#id_to_show").val();
			$.getJSON(url, function(data){
				rowCreationHandler([data]);
				$("#id_to_show").val("");
				$("#show_specific").prop("disabled", true);
			});
		});
		$("#create").click(function(){
			$.ajax({
			  type: "POST",
			  url: "http://localhost:8080/user-mgmt/users",
			  data: JSON.stringify({"email": $("#email_create").val(), "givenName": $("#given_create").val(), "familyName": $("#family_create").val()}),
			  dataType: "json",
			  contentType: "application/json",
			  success: function(){
				$("#email_create").val("");
				$("#given_create").val("");
				$("#family_create").val("");
				$("#create").prop("disabled", true);
			  }
			});
		});
		$("#update").click(function(){
			var url = "http://localhost:8080/user-mgmt/users/" + $("#id_update").val();
			$.ajax({
			  type: "PUT",
			  url: url,
			  data: JSON.stringify({"email": $("#email_update").val(), "givenName": $("#given_update").val(), "familyName": $("#family_update").val()}),
			  dataType: "json",
			  contentType: "application/json",
			  success: function(){
				$("#id_update").val("");
				$("#email_update").val("");
				$("#given_update").val("");
				$("#family_update").val("");
				$("#update").prop("disabled", true);
			  }
			});
		});
		$("#delete").click(function(){
			var url = "http://localhost:8080/user-mgmt/users/" + $("#id_to_delete").val();
			$.ajax({type: "DELETE", url: url, success: function(){
				$("#id_to_delete").val("");
				$("#delete").prop("disabled", true);
			}});
		});
	});
})();