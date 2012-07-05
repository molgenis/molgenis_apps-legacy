package org.molgenis.datatable.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;

public abstract class AbstractFilterableTupleTable implements FilterableTupleTable
{
	private List<QueryRule> filters = new ArrayList<QueryRule>();

	protected AbstractFilterableTupleTable()
	{
	}

	protected AbstractFilterableTupleTable(List<QueryRule> rules)
	{
		if (rules != null)
		{
			this.filters = rules;
		}
	}

	@Override
	public void setFilters(List<QueryRule> rules)
	{
		if(rules == null) throw new NullPointerException("rules cannot be null");
		this.filters = rules;
	}

	@Override
	public List<QueryRule> getFilters()
	{
		return filters;
	}

	@Override
	public int getLimit()
	{
		return (Integer) getRuleValue(Operator.LIMIT);
	}

	@Override
	public int getOffset()
	{
		return (Integer) getRuleValue(Operator.OFFSET);
	}

	@Override
	public QueryRule getSortRule()
	{
		final QueryRule sortAsc = getRule(Operator.SORTASC);
		if (sortAsc != null)
		{
			return sortAsc;
		}
		else
		{
			return getRule(Operator.SORTDESC);
		}
	}
	

	@Override
	public void setLimit(int limit)
	{
		if(this.getRule(Operator.LIMIT) != null)
		{
			this.getRule(Operator.LIMIT).setValue(limit);
		}
		else
		{
			this.getFilters().add(new QueryRule(Operator.LIMIT, limit));
		}
	}

	@Override
	public void setOffset(int offset)
	{
		if(this.getRule(Operator.OFFSET) != null)
		{
			this.getRule(Operator.OFFSET).setValue(offset);
		}
		else
		{
			this.getFilters().add(new QueryRule(Operator.OFFSET, offset));
		}
	}
	
	@Override
	public void setLimitOffset(int limit, int offset)
	{
		this.setLimit(limit);
		this.setOffset(offset);
	}
	
	// UTIL
	
	private Object getRuleValue(final Operator operator)
	{
		if (CollectionUtils.isEmpty(filters))
		{
			return -1;
		}
		else
		{
			final QueryRule rule = getRule(operator);
			return rule == null ? -1 : rule.getValue();
		}
	}

	private QueryRule getRule(final Operator operator)
	{
		final QueryRule rule = (QueryRule) CollectionUtils.find(filters, new Predicate()
		{
			@Override
			public boolean evaluate(Object arg0)
			{
				return ((QueryRule) arg0).getOperator() == operator;
			}
		});
		return rule;
	}	
}
