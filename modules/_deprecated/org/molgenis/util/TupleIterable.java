package org.molgenis.util;

@Deprecated
public interface TupleIterable extends Iterable<Tuple>
{
	Tuple next();
}
