package nl.isaac.dotcms.searcher.osgi;

import java.util.Date;

import javax.servlet.ServletException;

import nl.isaac.dotcms.searcher.servlet.SearcherServlet;
import nl.isaac.dotcms.searcher.viewtool.PortletViewtool;
import nl.isaac.dotcms.searcher.viewtool.SearcherViewtool;
import nl.isaac.dotcms.util.osgi.ExtendedGenericBundleActivator;
import nl.isaac.dotcms.util.osgi.ViewToolScope;

import org.apache.felix.http.api.ExtHttpService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.ServiceTracker;

import com.dotmarketing.filters.CMSFilter;
import com.dotmarketing.util.Logger;

public class AdvancedSearchActivator extends ExtendedGenericBundleActivator {

	private SearcherServlet searcherServlet;
	private ServiceTracker<ExtHttpService, ExtHttpService> tracker;

	@Override
	public void start(BundleContext context) throws Exception {

		// Default DotCMS call
		initializeServices(context);

		// Add the viewtools
		addViewTool(context,  PortletViewtool.class, "portletviewtool", ViewToolScope.APPLICATION);
		addViewTool(context, SearcherViewtool.class,        "searcher", ViewToolScope.APPLICATION);

		// Register the portlet
		registerPortlets(context, new String[] { "conf/portlet.xml", "conf/liferay-portlet.xml" });

		// Register the servlet
		registerSearcherServlet(context);
		
		// Register language variables (portlet name)
		registerLanguageVariables(context);

		// And exclude the servlet url from the CMSFilter to prevent rewrite to add a /
		CMSFilter.addExclude("/app/servlets/SearcherServlet");
	}


	/**
	 * Mostly boilerplate to consistently get the httpservice where we can register the {@link #searcherServlet}, we also create the servlet here.
	 * @param context
	 */
	private void registerSearcherServlet(BundleContext context) {
		tracker = new ServiceTracker<ExtHttpService, ExtHttpService>(context, ExtHttpService.class, null) {
			@Override public ExtHttpService addingService(ServiceReference<ExtHttpService> reference) {
				ExtHttpService extHttpService = super.addingService(reference);

				searcherServlet = new SearcherServlet();

				try {

					extHttpService.registerServlet("/servlets/SearcherServlet", searcherServlet, null, null);

				} catch (ServletException e) {
					throw new RuntimeException("Failed to register servlet and filter", e);
				} catch (NamespaceException e) {
					throw new RuntimeException("Failed to register servlet and filter", e);
				}

				Logger.info(this, "Registered searcher servlet on " + new Date(System.currentTimeMillis()));

				return extHttpService;
			}
			@Override public void removedService(ServiceReference<ExtHttpService> reference, ExtHttpService extHttpService) {
				extHttpService.unregisterServlet(searcherServlet);
				super.removedService(reference, extHttpService);
			}
		};
		tracker.open();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		unpublishBundleServices();

	}

}
