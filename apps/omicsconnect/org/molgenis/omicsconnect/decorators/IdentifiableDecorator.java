/* Date:        November 16, 2010
 * Template:	MapperDecoratorGen.java.ftl
 * generator:   org.molgenis.generators.db.MapperDecoratorGen 3.3.3
 *
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.omicsconnect.decorators;

import java.util.List;

import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Mapper;
import org.molgenis.framework.db.MapperDecorator;
import org.molgenis.observ.Identifiable;

public class IdentifiableDecorator<E extends Identifiable> extends MapperDecorator<E>
{

	protected boolean strict = false;

	// new kind of constructor to work with latest DB changes
	public IdentifiableDecorator(Mapper<E> generatedMapper)
	{
		super(generatedMapper);
	}

	@Override
	public int add(List<E> entities) throws DatabaseException
	{
		if (strict)
		{
			NameConvention.validateEntityNamesStrict(entities);
		}
		else
		{
			NameConvention.validateEntityNames(entities);
		}

		int count = super.add(entities);

		return count;
	}

	@Override
	public int update(List<E> entities) throws DatabaseException
	{
		if (strict)
		{
			NameConvention.validateEntityNamesStrict(entities);
		}
		else
		{
			NameConvention.validateEntityNames(entities);
		}

		int count = super.update(entities);

		return count;
	}

}
