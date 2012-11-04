function selectDataSet(id) {
	// get/create data sets
	var dataSets = $(document).data('datasets');
	if(typeof dataSets === 'undefined') {
		dataSets = {};
		$(document).data('datasets', dataSets);
	}
	
	if(!dataSets[id]) {
		// load and store data set
		$.getJSON('molgenis.do?__target=ProtocolViewer&__action=download_json_getdataset&datasetid=' + id, function(data) {
			dataSets[id] = data;
			updateDataSet(data, id);
		});
	} else {
		// use stored data set
		updateDataSet(dataSets[id], id);
	}
}

function updateDataSet(data, id) {
	// store current dataset
	$(document).data('dataset', data);
	$('#feature-details').empty();
	$('#feature-selection').empty();
	updateDataSetView(data, id);
}

function updateDataSetView(data) {
	$('#dataset-view').empty();
	$('#dataset-view').data('id', data.id);
	
	if(typeof data.protocol === 'undefined') {
		$('#dataset-view').append("<p>Catalog does not describe variables</p>");
		return;
	}
	
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
	var table = $('<table />');
	table.append('<tr><td>' + "Current selection:" + '</td><td>' + data.name + '</td></tr>');
	table.append('<tr><td>' + "Description:" + '</td><td>' + data.description + '</td></tr>');
	table.append('<tr><td>' + "Data type:" + '</td><td>' + data.dataType + '</td></tr>');
	
	table.addClass('listtable feature-table');
	table.find('td:first-child').addClass('feature-table-col1');
	$('#feature-details').append(table);
	
	if(data.categories) {
		var categoryTable = $('<table />');
		$('<thead />').append('<th>Code</th><th>Label</th><th>Description</th>').appendTo(categoryTable);
		$.each(data.categories, function(i, category){
			var row = $('<tr />');
			$('<td />').text(category.code).appendTo(row);
			$('<td />').text(category.label).appendTo(row);
			$('<td />').text(category.description).appendTo(row);
			row.appendTo(categoryTable);		
		});
		
		categoryTable.addClass('listtable');
		$('#feature-details').append(categoryTable);
	}
}

function updateFeatureSelection() {
	$('#feature-selection').empty();
	var table = $('<table />');
	$('<thead />').append('<th>Variables</th><th>Description</th><th>Protocol</th><th></th>').appendTo(table);
	var odd = false;
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
		
		if(odd)
			row.addClass('form_listrow1');
		odd = !odd;
		
		row.appendTo(table);
	});
	table.addClass('listtable selection-table');
	$('#feature-selection').append(table);
}

function getSelectedFeaturesURL(format) {
 	var features = [];
	$('#protocol-tree input:checkbox[name=feature]:checked').each(function() {
		features.push($(this).data('id'));	
	});
	var id = $(document).data('dataset').id;
	return '${url_base}&__action=download_' + format + '&datasetid=' + id + '&features=' + features.join();
}

function processSearch(query) {
	if(query) {
		var dataSet = $(document).data('dataset');
		if(dataSet && dataSet.protocol) {
			searchProtocol(dataSet.protocol, new RegExp(query, 'i'));
		}
	}
}

function searchProtocol(protocol, regexp) {
	if(matchProtocol(protocol, regexp))
		console.log("found protocol: " + protocol.name);
	
	if(protocol.features) {
		$.each(protocol.features, function(i, feature) {
			if(matchFeature(feature, regexp))
				console.log("found feature: " + feature.name);
		});
	}
	
	if(protocol.subProtocols) {
		$.each(protocol.subProtocols, function(i, subProtocol) {
			searchProtocol(subProtocol, regexp);
		});
	}
}

function matchProtocol(protocol, regexp) {
	return protocol.name && protocol.name.search(regexp) != -1;
}

function matchFeature(feature, regexp) {
	if(feature.name && feature.name.search(regexp) != -1)
		return true;
	if(feature.description && feature.description.search(regexp) != -1)
		return true;
	
	if(feature.categories) {
		$.each(feature.categories, function(i, category) {
			if(matchCategory(category, regexp))
				console.log("found category: " + category);
		});
	}
}

function matchCategory(category, regexp) {
	if(category.description && category.description.search(regexp) != -1)
		return true;
	if(category.label && category.label.search(regexp) != -1)
		return true;
}
