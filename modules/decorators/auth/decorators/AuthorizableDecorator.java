/* Date:        July 29, 2011
 * Template:	MapperDecoratorGen.java.ftl
 * generator:   org.molgenis.generators.db.MapperDecoratorGen 4.0.0-testing
 *
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package decorators;

import java.util.List;

import org.molgenis.auth.Authorizable;
import org.molgenis.auth.MolgenisGroup;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Mapper;
import org.molgenis.framework.db.MapperDecorator;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.security.Login;
import org.molgenis.framework.security.SimpleLogin;
import org.molgenis.util.Entity;

public class AuthorizableDecorator<E extends Entity> extends MapperDecorator<E>
{
	// JDBCMapper is the generate thing
	// public AuthorizableDecorator(JDBCMapper generatedMapper)
	// {
	// super(generatedMapper);
	// }

	// Mapper is the generate thing
	public AuthorizableDecorator(Mapper<E> generatedMapper)
	{
		super(generatedMapper);
	}

	@Override
	public int add(List<E> entities) throws DatabaseException
	{
		// add your pre-processing here
		this.getDatabase().getLogin().setAdmin(entities, this.getDatabase());

		// here we call the standard 'add'
		int count = super.add(entities);

		// add your post-processing here
		// if you throw and exception the previous add will be rolled back

		return count;
	}

	@Override
	public int update(List<E> entities) throws DatabaseException
	{

		// add your pre-processing here, e.g.
		// for (org.molgenis.organization.Investigation e : entities)
		// {
		// e.setTriggeredField("Before update called!!!");
		// }

		Login login = this.getDatabase().getLogin();
		if (login != null && !(login instanceof SimpleLogin))
		{
			if (login.getUserId() != null)
			{
				int userId = this.getDatabase().getLogin().getUserId();

				for (E e : entities)
				{
					// Set ownership of new record to current user
					((Authorizable) e).setOwns_Id(userId);

					// Give group "AllUsers" read-rights on the new record
					try
					{
						MolgenisGroup mg = getDatabase().find(MolgenisGroup.class,
								new QueryRule(MolgenisGroup.NAME, Operator.EQUALS, "AllUsers")).get(0);
						((Authorizable) e).setCanRead_Id(mg.getId());
					}
					catch (Exception ex)
					{
						// When running from Hudson, there will be no group
						// "AllUsers" so we prevent
						// an error, to keep our friend Hudson from breaking
					}
				}
			}
		}

		// here we call the standard 'update'
		int count = super.update(entities);

		// add your post-processing here
		// if you throw and exception the previous add will be rolled back

		return count;
	}

	@Override
	public int remove(List<E> entities) throws DatabaseException
	{
		// add your pre-processing here

		// here we call the standard 'remove'
		int count = super.remove(entities);

		// add your post-processing here, e.g.
		// if(true) throw new
		// SQLException("Because of a post trigger the remove is cancelled.");

		return count;
	}
}
