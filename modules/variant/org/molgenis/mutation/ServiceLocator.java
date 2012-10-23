package org.molgenis.mutation;

import org.molgenis.auth.service.MolgenisUserService;
import org.molgenis.core.service.PublicationService;
import org.molgenis.mutation.service.CmsService;
import org.molgenis.mutation.service.FastaService;
import org.molgenis.mutation.service.GffService;
import org.molgenis.mutation.service.SearchService;
import org.molgenis.mutation.service.StatisticsService;
import org.molgenis.mutation.service.UploadService;
import org.molgenis.pheno.service.PhenoService;
import org.springframework.context.ApplicationContext;

/**
 * Locates and provides all available application services.
 */
public class ServiceLocator
{
	private ServiceLocator()
	{
		// shouldn't be instantiated
	}

	public static final String BEAN_PREFIX = "";

	private static final ServiceLocator instance = new ServiceLocator();

	private static ApplicationContext context = null;

	/**
	 * Gets the shared instance of this Class
	 * 
	 * @return the shared service locator instance.
	 */
	public static final ServiceLocator instance()
	{
		if (context == null)
		{
			context = new org.springframework.context.support.ClassPathXmlApplicationContext();
			// context = new
			// org.springframework.context.annotation.AnnotationConfigApplicationContext();
			// ((org.springframework.context.annotation.AnnotationConfigApplicationContext)
			// context).scan("org.molgenis");
			// ((org.springframework.context.annotation.AnnotationConfigApplicationContext)
			// context).refresh();
		}
		return instance;
	}

	/**
	 * Gets the Spring ApplicationContext.
	 * 
	 * @return ApplicationContext
	 */
	public synchronized ApplicationContext getContext()
	{
		return context;
	}

	public synchronized void shutdown()
	{
		// ((org.springframework.context.annotation.AnnotationConfigApplicationContext)
		// this.getContext()).close();
	}

	public final CmsService getCmsService()
	{
		return (CmsService) this.getContext().getBean("cmsService");
	}

	public final FastaService getFastaService()
	{
		return (FastaService) this.getContext().getBean("fastaService");
	}

	public final GffService getGffService()
	{
		return (GffService) this.getContext().getBean("gffService");
	}

	public MolgenisUserService getMolgenisUserService()
	{
		return (MolgenisUserService) this.getContext().getBean("molgenisUserService");
	}

	public final PhenoService getPhenoService()
	{
		return (PhenoService) this.getContext().getBean("phenoService");
	}

	public final PublicationService getPublicationService()
	{
		return (PublicationService) this.getContext().getBean("publicationService");
	}

	public final SearchService getSearchService()
	{
		return (SearchService) this.getContext().getBean("searchService");
	}

	public final StatisticsService getStatisticsService()
	{
		return (StatisticsService) this.getContext().getBean("statisticsService");
	}

	public final UploadService getUploadService()
	{
		return (UploadService) this.getContext().getBean("uploadService");
	}

	/**
	 * Gets an instance of the given service.
	 * 
	 * @param serviceName
	 * @return getContext().getBean(BEAN_PREFIX + serviceName)
	 */
	public final Object getService(String serviceName)
	{
		return this.getContext().getBean(BEAN_PREFIX + serviceName);
	}
}
