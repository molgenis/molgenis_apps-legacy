package org.molgenis.gonl.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nullable;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.variant.SequenceVariant;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class VariantAggregator
{
	private Database db;

	@Deprecated
	public VariantAggregator()
	{

	}

	public VariantAggregator(Database db)
	{
		if (db == null) throw new IllegalArgumentException();
		this.db = db;
	}

	public List<VariantAggregate> aggregate(List<SequenceVariant> variants) throws DatabaseException
	{
		if (variants == null || variants.isEmpty()) return null;

		// get feature ids
		List<Integer> variantIds = Lists.transform(variants, new Function<SequenceVariant, Integer>()
		{
			@Override
			@Nullable
			public Integer apply(@Nullable
			SequenceVariant arg0)
			{
				return arg0 != null ? arg0.getId() : null;
			}
		});

		// get values for features
		List<ObservedValue> values = db.query(ObservedValue.class).in(ObservedValue.FEATURE, variantIds).find();

		// aggregate values per variant
		List<VariantAggregate> variantAggregates = new ArrayList<VariantAggregate>();
		if (values != null)
		{
			// group values by variant
			Map<String, List<ObservedValue>> variantValueMap = new LinkedHashMap<String, List<ObservedValue>>();
			for (ObservedValue value : values)
			{
				String featureName = value.getFeature_Name();
				List<ObservedValue> valueList = variantValueMap.get(featureName);
				if (valueList == null)
				{
					valueList = new ArrayList<ObservedValue>();
					variantValueMap.put(featureName, valueList);
				}
				valueList.add(value);
			}

			// aggregate values by variant
			for (SequenceVariant variant : variants)
			{
				VariantAggregate variantAggregate = new VariantAggregate();

				List<ObservedValue> variantValues = variantValueMap.get(variant.getName());

				Set<String> panels = new LinkedHashSet<String>();
				AlleleCount aggregateCount = new AlleleCount();
				for (ObservedValue variantValue : variantValues)
				{
					aggregateCount.add(AlleleCount.parse(variantValue.getValue()));
					panels.add(variantValue.getTarget_Name());
				}

				variantAggregate.setVariant(variant);
				variantAggregate.setPanels(panels);
				variantAggregate.setHomRefCount(aggregateCount.getHomRefCount());
				variantAggregate.setHetCount(aggregateCount.getHetCount());
				variantAggregate.setHomAltCount(aggregateCount.getHomAltCount());

				variantAggregates.add(variantAggregate);
			}
		}

		return variantAggregates;

	}

	public static class VariantAggregate
	{
		private SequenceVariant variant;
		private Iterable<String> panelNames;
		private int homRefCount;
		private int hetCount;
		private int homAltCount;

		public SequenceVariant getVariant()
		{
			return variant;
		}

		public void setVariant(SequenceVariant variant)
		{
			this.variant = variant;
		}

		public Iterable<String> getPanels()
		{
			return panelNames;
		}

		public void setPanels(Iterable<String> panelNames)
		{
			this.panelNames = panelNames;
		}

		public int getHomRefCount()
		{
			return homRefCount;
		}

		public void setHomRefCount(int homRefCount)
		{
			this.homRefCount = homRefCount;
		}

		public int getHetCount()
		{
			return hetCount;
		}

		public void setHetCount(int hetCount)
		{
			this.hetCount = hetCount;
		}

		public int getHomAltCount()
		{
			return homAltCount;
		}

		public void setHomAltCount(int homAltCount)
		{
			this.homAltCount = homAltCount;
		}
	}

	private static class AlleleCount
	{
		private final AtomicInteger homRefCount;
		private final AtomicInteger hetCount;
		private final AtomicInteger homAltCount;

		private AlleleCount()
		{
			this(new AtomicInteger(0), new AtomicInteger(0), new AtomicInteger(0));
		}

		private AlleleCount(AtomicInteger homRefCount, AtomicInteger hetCount, AtomicInteger homAltCount)
		{
			this.homRefCount = homRefCount;
			this.hetCount = hetCount;
			this.homAltCount = homAltCount;
		}

		public int getHomRefCount()
		{
			return homRefCount.get();
		}

		public int getHetCount()
		{
			return hetCount.get();
		}

		public int getHomAltCount()
		{
			return homAltCount.get();
		}

		public void add(AlleleCount other)
		{
			homRefCount.addAndGet(other.getHomRefCount());
			hetCount.addAndGet(other.getHetCount());
			homAltCount.addAndGet(other.getHomAltCount());
		}

		public static AlleleCount parse(String str)
		{
			AtomicInteger homRefCount = new AtomicInteger(0);
			AtomicInteger hetCount = new AtomicInteger(0);
			AtomicInteger homAltCount = new AtomicInteger(0);

			if (str.equals("0/0") || str.equals("0|0")) homRefCount.incrementAndGet();
			else if (str.equals("0/1") || str.equals("0|1")) hetCount.incrementAndGet();
			else
			{
				int off = str.indexOf('/');
				if (off == -1) off = str.indexOf('|');

				String part1 = str.substring(0, off);
				String part2 = str.substring(off + 1);
				if (part1.equals(part2)) homAltCount.incrementAndGet(); // e.g.
																		// 1/1
				else
					hetCount.incrementAndGet(); // e.g. 1/2

			}
			return new AlleleCount(homRefCount, hetCount, homAltCount);
		}
	}

	@Deprecated
	public void setDatabase(Database db)
	{
		if (db == null) throw new IllegalArgumentException("db is null");
		this.db = db;
	}
}
