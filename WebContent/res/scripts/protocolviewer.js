function updateDataSetView(data) {
	$('#dataset-view').empty();
	$('#dataset-view').data('id', data.id);
	
	if(typeof data.protocol === 'undefined') {
		$('#dataset-view').append("<h3>Catalog does not describe variables</h3>");
		return;
	}
	
	$('#dataset-view').append("<h3>Catalog: " + data.name + "</h3>");
	
	// recursively build tree for protocol (+ store data with elements)	
	function buildTree(protocol) {
		// add protocol
		var item = $('<li />');
		var input = $('<input type="checkbox" name="protocol" class="folder">').attr('id', 'protocol' + protocol.id);
		input.data({'id': protocol.id, 'name': protocol.name, 'description': protocol.description});
		var label = $('<label />').attr('for', 'protocol' + protocol.id).text(protocol.name);
		item.append(input).append(label);
		
		var list = $('<ul />');
		
		// add protocol: features
		if(protocol.features) {
			$.each(protocol.features, function(i, feature){
				var item = $('<li />');
				var input = $('<input type="checkbox" name="feature" class="point">').attr('id', 'feature' + feature.id);
				input.data({'id': feature.id, 'name': feature.name, 'description': feature.description, 'protocol_name': protocol.name});
				var label = $('<label />').attr('for', 'feature' + feature.id).text(feature.name);
				item.append(input).append(label).appendTo(list);
			});
		}
		
		// add protocol: subprotocols
		if(protocol.subProtocols) {
			$.each(protocol.subProtocols, function(i, subProtocol){
				list.append(buildTree(subProtocol));
			});
		}
		
		item.append(list);
		return item;
	};
	
	// append tree to DOM
	var tree =  $('<ul id="protocol-tree"/>').append(buildTree(data.protocol));
	$('#dataset-view').append(tree);
	
	// add protocol click handlers
	$('#protocol-tree .folder').change(function(){
		console.log("change");
		if($(this).is(":checked")) {
			// expand all children
			$(this).siblings('ul').show();
			var list = $(this).nextAll('ul');
			list.find('input:checkbox:unchecked').attr('checked', true).trigger('change'); // note: setting attr doesn't fire change event
		} else {
			var list = $(this).nextAll('ul');
			list.find('input:checkbox:checked').attr('checked', false).trigger('change');
		}
		updateFeatureSelection();
	});
	
	// add feature click handlers
	$('#protocol-tree .point').change(function(){
		if($(this).is(":checked")) {
			var featureId = $(this).data('id');
			$.getJSON('molgenis.do?__target=ProtocolViewer&__action=download_json_getfeature&featureid=' + featureId, function(data) {
				if(data != null)
					updateFeatureDetails(data);
			});
		}
		updateFeatureSelection();
	});
	
	// render tree and open first branch
	$('#protocol-tree li').first().addClass('open');
	$('#protocol-tree').treeview({'collapsed': true});
}

function updateFeatureDetails(data) {
	$('#feature-details').empty();
	var table = $('<table class="feature-table" />');
	table.append('<tr><td>' + "Current selection:" + '</td><td>' + data.name + '</td></tr>');
	table.append('<tr><td>' + "Description:" + '</td><td>' + data.description + '</td></tr>');
	table.append('<tr><td>' + "Data type:" + '</td><td>' + data.dataType + '</td></tr>');
	
	if(data.categories) {
		$('#details').append('<h3>Categories</h3>');
		var innertable = $('<table class="category-table" />');
		$('<thead />').append('<th>Code</th><th>Label</th><th>Description</th><th></th>').appendTo(innertable);
		$.each(data.categories, function(i, category){
			var row = $('<tr />');
			$('<td />').text(category.code).appendTo(row);
			$('<td />').text(category.label).appendTo(row);
			$('<td />').text(category.description).appendTo(row);
			row.appendTo(innertable);		
		});
		var row = $('<tr />');
		$('<td />').text("Category:").appendTo(row);
		$('<td />').append(innertable).appendTo(row);
		row.appendTo(table);
	}
	
	$('#feature-details').append(table);
}

function updateFeatureSelection() {
	$('#feature-selection').empty();
	var table = $('<table class="featureselecttable" />');
	$('<thead />').append('<th>Variables</th><th>Description</th><th>Protocol</th><th></th>').appendTo(table);
	$('#protocol-tree input:checkbox[name=feature]:checked').each(function() {
		var name = $(this).data('name');
		var description = $(this).data('description');
		var protocol_name = $(this).data('protocol_name');
		
		var row = $('<tr />');
		$('<td />').text(name !== undefined ? name : "").appendTo(row);
		$('<td />').text(description !== undefined ? description : "").appendTo(row);
		$('<td />').text(protocol_name != undefined ? protocol_name : "").appendTo(row);
		
		var deleteButton = $('<input type="image" src="generated-res/img/cancel.png" alt="delete">');
		deleteButton.click($.proxy(function() {
			$('#feature' + $(this).data('id')).attr('checked', false);
			updateFeatureSelection();
			return false;
		}, this));
		$('<td />').append(deleteButton).appendTo(row);
		
		row.appendTo(table);
	});
	$('#feature-selection').append(table);
}

function getSelectedFeaturesURL(format) {
 	var features = [];
	$('#protocol-tree input:checkbox[name=feature]:checked').each(function() {
		features.push($(this).data('id'));	
	});
	var dataSetId = $('#dataset-view').data('id');
	return '${url_base}&__action=download_' + format + '&datasetid=' + dataSetId + '&features=' + features.join();
}