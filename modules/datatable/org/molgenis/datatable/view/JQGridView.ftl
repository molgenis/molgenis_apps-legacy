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

// Main object, wraps JQgrid and stores/manipulates many state variables
var JQGridView = {
    tableSelector : null,
    pagerSelector : null,
    config : null,
    tree : null,
    colModel : null, 
    
    columnPage : 0,				//current column Page
    columnPagerSize : 5,		//current columnPager size
    columnPageEnabled : true,
        
    prevColModel : null,		// Cache to speed up column paging: don't create new colmodel if not necessary
    numOfSelectedNodes : 0,		// # nodes selected in tree
    
    // First-time initialisation; create grid
    init: function(tableSelector, pagerSelector, config) {
    	var self = JQGridView;
    
        this.tableSelector = tableSelector;
        this.pagerSelector = pagerSelector;
        this.config = config;
        this.colModel = this.config.colModel;
        
        this.numOfSelectedNodes = this.config.colModel.length;
        
        this.showVisibleColumns();
        
        this.grid = this.createJQGrid(null);
        this.createExportDialog();

		//load & create Tree
	    $.getJSON(configUrl + "&Operation=LOAD_TREE").done(function(data) { 
	    	 self.tree = self.createTree(data);   
	    });
        return JQGridView;
    },
    
    // Show one page of columns and hide all other data.
    showVisibleColumns : function() {
		if(this.columnPageEnabled) {
			begin = this.columnPage * this.columnPagerSize;
			end = begin + this.columnPagerSize;
			
			columnNames = new Array();
			colCount = 0;
			// Build a column model of which columns to display, excluding hidden columns.
			for(i = 0; i < this.config.colModel.length; ++i) {
				if(!this.config.colModel[i].hidden) {
					if(colCount >= begin && colCount < end) {
						columnNames.push(this.config.colModel[i].name);				
					} else {
						this.config.colModel[i].hidden = true;
					}
					++colCount;					
				}
			}	
			this.config.postData.colNames = columnNames;
		}    
    },
    
    getGrid: function() {
    	return $(this.tableSelector);
    },
    
    
    changeColumns: function(columnModel) {
    	var self = JQGridView;
		
		if(columnModel == null) {
			columnModel = this.prevColModel;
		} else {
			this.prevColModel = columnModel;
		}
		
		if(columnModel != null) { // necessary because prevColModel can be null, on first load
			this.numOfSelectedNodes = columnModel.length;
		}

		//add all columnNames
		var names = new Array();
		$.each(this.config.colModel, function(index, value) {
			names.push(value.name);
		});
		this.config.colNames = names;
		
		var selectedTreeNodeNames = new Array();
		$.each(columnModel, function(index, value) {
			selectedTreeNodeNames.push(value.name);
		});
		this.config.postData.treeSelectColNames = selectedTreeNodeNames;

		// Extract column names from grid and POST them
		if(this.grid != undefined) {
			var columnNames = new Array();
			gridColModel = this.grid.getGridParam("colModel");
	    	for(i = 0; i < gridColModel.length; ++i) {
	    		colName = gridColModel[i].name;
	    		hidden = true;
	    		for(j = 0; j < columnModel.length; ++j) {
	    			if(colName == columnModel[j].name) {
	    				columnNames.push(colName);
	    				hidden = false;
	    				break;
	    			}
	    		}
	    		gridColModel[i].hidden = hidden;
	    	}
	    	this.config.colModel = gridColModel; 
	    	this.config.postData.colNames = columnNames;
		}

		this.showVisibleColumns();		
		
		filters = this.grid.getGridParam("postData").filters;
		
    	$(this.tableSelector).jqGrid('GridUnload');

    	this.grid = this.createJQGrid(filters);
    	
    },
    
    createJQGrid : function(filters) {
    	var self = JQGridView;
    	
		if(filters != null) { // If condition may be redundant?
			this.config.postData.filters = filters; 
		}
    	
    	grid = jQuery(this.tableSelector).jqGrid(this.config)
            .jqGrid('navGrid', this.pagerSelector,
            	this.config.settings,{},{},{},
            	{multipleSearch:true, multipleGroup:true, showQuery: true} // search options
            ).jqGrid('gridResize');
        if(this.columnPageEnabled) {
        	// calculate column page boundaries
        	maxPage = Math.ceil(this.numOfSelectedNodes / this.columnPagerSize);
        	if(this.columnPage >= maxPage) {
        		this.columnPagerLeft();
        		return; //prevent double work
        	}
        	
        	// column paging buttons
        	firstButton = $("<input id='firstColButton' type='button' value='|< Columns' style='height:20px;font-size:-3'/>")
        	prevButton = $("<input id='prevColButton' type='button' value='< Columns' style='height:20px;font-size:-3'/>");
        	nextButton = $("<input id='nextColButton' type='button' value='Column >' style='height:20px;font-size:-3'/>");
        	lastButton = $("<input id='lastColButton' type='button' value='Columns >|' style='height:20px;font-size:-3'/>")        	
        	
        	colPager = $("<div id='columnPager'/>");
        	pageInput = $("<input id='colPageNr' type='text' size='3'>");
        	
        	
        	$(pageInput).attr('value', this.columnPage + 1);

			// handle input of specific column page number
        	$(pageInput).change(function() {
        		value = parseInt($(this).val(), 10);
        		
        		
        		if(value - 1 > 0 && value - 1 < maxPage) {
        			$(this).attr('value', value);
        			self.setColumnPageIndex(value - 1);
        		} else {
        			if(value - 1 >= maxPage) {
        				$(this).attr('value', value);
        				self.setColumnPageIndex(maxPage - 1);
        			}
        			if(value - 1 <= 0) {
        				$(this).attr('value', value);
        				self.setColumnPageIndex(0);        			
        			}
        		}
        	});

        	// Enable/disable forwards and backwards buttons appropriately
        	if(this.columnPage + 1 >= maxPage) {
        		nextButton.attr("disabled","disabled");
        		lastButton.attr("disabled","disabled");
        	}        	
        	if(this.columnPage - 1 < 0) {
        		prevButton.attr("disabled","disabled");
        		firstButton.attr("disabled","disabled");
        	}        	
        	
        	$(firstButton).click(function() {
        		self.setColumnPageIndex(0);
        	});
        	
        	$(prevButton).click(function() {
				self.columnPagerLeft();
        	});
        
        	$(nextButton).click(function() {
        		self.columnPagerRight();
        	});
        	
        	$(lastButton).click(function() {
        		self.setColumnPageIndex(maxPage-1);
        	});
        	
        	// construct GUI
			colPager.append(firstButton);
        	colPager.append(prevButton);
        	colPager.append("Page ");
        	colPager.append(pageInput);
        	colPager.append(" Of " + maxPage);
        	colPager.append(nextButton);
        	colPager.append(lastButton);
        	
        	toolbar = $("#t_jqGridView"); 
        	toolbar.append(colPager);

    	}
        return grid;
	},

	setColumnPageIndex : function(columnPagerIndex) {
		this.columnPage = columnPagerIndex;
		this.changeColumns(null);
	},
	
	columnPagerLeft : function () {
		var self = JQGridView;
		this.columnPage--;
		this.changeColumns(null);
	},	
    
    columnPagerRight : function () {
		var self = JQGridView;
		this.columnPage++;
		
		
		this.changeColumns(null);
		
	},	
    
    getColumnNames : function(colModel) {
    	result = new Array();
    	$.each(colModel, function(index, value) {
    		result.push(value.name);
    	});
    	return result;
    },
    
    createExportDialog : function() {
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
		
						var exportColumnSelection = $("input[name='exportColumnSelection']:checked").val();
		
					
		
		              	var myUrl = $(self.tableSelector).jqGrid('getGridParam', 'url');
						myUrl += "&" +$.param($(self.tableSelector).jqGrid('getGridParam', 'postData'));		              	

		                //e.preventDefault();  //stop the browser from following
		                window.location.href = myUrl + "&viewType=" + viewType + "&exportSelection=" + exportSelection + "&exportColumnSelection=" + exportColumnSelection;
		            },
		            Cancel: function() {
		                $( this ).dialog( "close" );
		            }
		    },
		    close: function() {
		    }
		});
	},
	
	// Build the column selection tree
	createTree : function(nodes) {
		var self = JQGridView;
		return $("#tree3").dynatree({
			checkbox: true,
			selectMode: 3,
			children: nodes,
			onSelect: function(select, node) {
				// Get a list of all selected nodes, and convert to selected Column Model
				var selectedColModel = new Array();        
				var selectedColumns = node.tree.getSelectedNodes();
				var tableNodes = new Array();
				for(i = 0; i < selectedColumns.length; ++i) {
					var treeNode = selectedColumns[i].data;
					
					//handle branch-nodes
					if(treeNode.isFolder) {
						tableNodes.push(treeNode.title);
					} else { // leaf-node
						colModelNode = $.grep(self.colModel, function(item){
      							return item.path == treeNode.path;
							});
						selectedColModel.push(colModelNode[0]);
					}
				}
				
				self.config.postData.tableNames = tableNodes;
				
				self.changeColumns(selectedColModel);        
			},
			onDblClick: function(node, event) {
				node.toggleSelect();
			},
			// escape (?)
			onKeydown: function(node, event) {
				if( event.which == 32 ) {
					node.toggleSelect();
					return false;
				}
			},
		// The following options are only required if we have more than one tree on one page:
		//        initId: "treeData",
			cookieId: "dynatree-Cb3",
			idPrefix: "dynatree-Cb3-"
		});
	}
}




// On first load do:
$(document).ready(function() {
    configUrl = "${url}";
    
    //load JQGrid configuration and creates grid
    $.ajax(configUrl + "&Operation=LOAD_CONFIG").done(function(data) {
        config = data;
        grid = JQGridView.init("table#${tableId}", "#${tableId}Pager", config);
    });
	$('#exportButton').click(function() {
		$("#dialog-form" ).dialog('open');
	});
	$('#exportButton').removeAttr('disabled');	
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
	    </fieldset>
	    <fieldset>
	            <label>Rows</label><br>
	            <input type="radio" name="exportSelection" value="ALL">All rows<br>
	            <input type="radio" name="exportSelection" value="GRID" checked>Visible rows<br> 
		</fieldset>
	    <fieldset>
	            <label>Columns</label><br>
	            <input type="radio" name="exportColumnSelection" value="ALL_COLUMNS">All Columns<br>
	            <input type="radio" name="exportColumnSelection" value="SELECTED_COLUMNS" checked>Selected<br>
	            <input type="radio" name="exportColumnSelection" value="GRID_COLUMNS">Visible Columns<br>
		</fieldset>
		</form>
	</div>
</div>