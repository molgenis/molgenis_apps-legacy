package org.molgenis.datatable.view;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.molgenis.datatable.model.TupleTable;
import org.molgenis.datatable.util.JQueryUtil;

import org.molgenis.framework.ui.html.HtmlWidget;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

public class JQGridView extends HtmlWidget
{
	private final TupleTable tupleTable;
	
	public JQGridView(String name, TupleTable tupleTable)
	{
		super(name);
		this.tupleTable = tupleTable;
	}
	
	
	@Override
	public String toHtml() {
		try
		{
			final Map<String, Object> args = new HashMap<String, Object>();
			
			args.put("tableId", getId());

			args.put("columns", tupleTable.getColumns());
			args.put("viewFactoryClassName", ViewFactoryImpl.class.getName());
			args.put("backendUrl", "molgenis.do");
			args.put("sortName", tupleTable.getColumns().get(0).getSqlName());
			args.put("treeModel", JQueryUtil.getDynaTreeNodes(tupleTable.getColumns()));

			
			final Configuration cfg = new Configuration();
			cfg.setObjectWrapper(new DefaultObjectWrapper());
			cfg.setClassForTemplateLoading(JQGridView.class, "");
			final Template template = cfg.getTemplate(JQGridView.class.getSimpleName() + ".ftl");
			final Writer out = new StringWriter();
			template.process(args, out);
			out.flush();  
			return out.toString();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
