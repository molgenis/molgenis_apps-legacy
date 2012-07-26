<script src="jqGrid/grid.locale-en.js" type="text/javascript"></script>
<script src="jqGrid/jquery.jqGrid.min.js" type="text/javascript"></script>
<script src="jqGrid/jquery.json-2.3.min.js" type="text/javascript"></script>

<script src="jquery/development-bundle/ui/jquery-ui-1.8.7.custom.js" type="text/javascript"></script>
<script src="jquery/development-bundle/ui/jquery.ui.dialog.js" type="text/javascript"></script>
<script src="jquery/development-bundle/ui/jquery.ui.datepicker.js" type="text/javascript"></script>

<link rel="stylesheet" type="text/css" media="screen" href="jquery/development-bundle/themes/smoothness/jquery-ui-1.8.7.custom.css">
<link rel="stylesheet" type="text/css" media="screen" href="jqGrid/ui.jqgrid.css">
<link rel="stylesheet" type="text/css" media="screen" href="jqGrid/ui.multiselect.css">

<link href="dynatree-1.2.0/src/skin/ui.dynatree.css" rel="stylesheet" type="text/css" id="skinSheet">
<script src="dynatree-1.2.0/src/jquery.dynatree.js" type="text/javascript"></script>


<script type="text/javascript">
//TODO: place in JS file after dev!
var JQGridView = {
    tableSelector : null,
    pagerSelector : null,
    config : null,
    tree : null,
    colModel : null, 
    
    init: function(tableSelector, pagerSelector, config) {
    	var self = JQGridView;
    
        this.tableSelector = tableSelector;
        this.pagerSelector = pagerSelector;
        this.config = config;
        this.colModel = this.config.colModel;
        
        this.grid = this.createJQGrid();
        this.createDialog();

		//load & create Tree
	    $.getJSON(configUrl + "&Operation=LOAD_TREE")   
	    .done(function(data) { 
	    	 self.tree = self.createTree(data);   
	    });
        
        return JQGridView;
    },
    
    getGrid: function() {
    	return $(this.tableSelector);
    },
    
    changeColumns: function(columnModel) {
    	var self = JQGridView;
		this.config.colModel = columnModel;

		var names = new Array();
		$.each(columnModel, function(index, value) {
			names.push(value.name);
		});
		this.config.colNames = names;

		this.config.postData = {colNames:names};
    	$(this.tableSelector).jqGrid('GridUnload');
    	this.grid = this.createJQGrid();
    },
    
    createJQGrid : function() {
    	return jQuery(this.tableSelector).jqGrid(this.config)
            .jqGrid('navGrid', this.pagerSelector,
            	this.config.toolbar,{},{},{},{multipleSearch:true} // search options
            ).jqGrid('gridResize');
	},
    
    getColumnNames : function(colModel) {
    	result = new Array();
    	$.each(colModel, function(index, value) {
    		result.push(value.name);
    	});
    	return result;
    },
    
    createDialog : function() {
    	var self = JQGridView;
    	$( "#dialog-form" ).dialog({
		    autoOpen: false,
		    height: 300,
		    width: 350,
		    modal: true,
		    buttons: {
		            "Export": function() {
		            	var viewType = $("input[name='viewType']:checked").val();
		            	var exportSelection = $("input[name='exportSelection']:checked").val();
		
		              	var myUrl = $(self.tableSelector).jqGrid('getGridParam', 'url');
						myUrl += "&" +$.param($(self.tableSelector).jqGrid('getGridParam', 'postData'));		              	

		                //e.preventDefault();  //stop the browser from following
		                window.location.href = myUrl + "&viewType=" + viewType + "&exportSelection=" + exportSelection;
		            },
		            Cancel: function() {
		                $( this ).dialog( "close" );
		            }
		    },
		    close: function() {
		    }
		});
	},
	
	createTree : function(nodes) {
		var self = JQGridView;
		return $("#tree3").dynatree({
			checkbox: true,
			selectMode: 3,
			children: nodes,
			onSelect: function(select, node) {
				// Get a list of all selected nodes, and convert to a key array:
				var selectedColModel = new Array();        
				var selectedColumns = node.tree.getSelectedNodes();
				for(i = 0; i < selectedColumns.length; ++i) {
					var treeNode = selectedColumns[i].data;

					if(!treeNode.isFolder) {
						colModelNode = $.grep(self.colModel, function(item){
      							return item.path == treeNode.path;
							});
						selectedColModel.push(colModelNode[0]);
					}
				}
				grid.changeColumns(selectedColModel);        
			},
			onDblClick: function(node, event) {
				node.toggleSelect();
			},
			onKeydown: function(node, event) {
				if( event.which == 32 ) {
				node.toggleSelect();
				return false;
				}
			},
			// The following options are only required, if we have more than one tree on one page:
		//        initId: "treeData",
			cookieId: "dynatree-Cb3",
			idPrefix: "dynatree-Cb3-"
		});
	}
}





$(document).ready(function() {
    configUrl = "${url}";
    
    //load JQGrid configuration and creates grid
    $.ajax(configUrl + "&Operation=LOAD_CONFIG").done(function(data) {
        config = data;
        grid = JQGridView.init("table#${tableId}", "#${tableId}Pager", config);
    });
	$('#exportButton').click(function() {
		$( "#dialog-form" ).dialog('open');
	});
});

</script>

<div id="treeBox">
  <div id="tree3"></div>
</div>

<div id="gridBox">
	<table id="${tableId}"></table>
	<div id="${tableId}Pager"></div>
	<input id="exportButton" type="button" value="export data"/>
	<div id="dialog-form" title="Export data">
		<form>
		<fieldset>
	            <label >File type</label><br>
	            <input type="radio" name="viewType" value="EXCEL" checked>Excel<br>
	            <input type="radio" name="viewType" value="SPSS">Spss<br> 
	            <input type="radio" name="viewType" value="CSV">Csv<br> 
	            <label>Export option</label><br>
	            <input type="radio" name="exportSelection" value="ALL" checked>All rows<br>
	            <input type="radio" name="exportSelection" value="GRID">Visible rows<br> 
		</fieldset>
		</form>
	</div>
</div>