package nl.isaac.dotcms.searcher.osgi;

import nl.isaac.dotcms.searcher.servlet.SearcherServletCSV;
import org.osgi.framework.BundleContext;

import nl.isaac.dotcms.searcher.servlet.SearcherServlet;
import nl.isaac.dotcms.searcher.viewtool.PortletViewtool;
import nl.isaac.dotcms.util.osgi.ExtendedGenericBundleActivator;
import nl.isaac.dotcms.util.osgi.ViewToolScope;

public class AdvancedSearchActivator extends ExtendedGenericBundleActivator {

	@Override
	public void init(BundleContext context) {

		// Add the viewtools
		addViewTool(context, PortletViewtool.class, "portletviewtool", ViewToolScope.APPLICATION);

		// Register the portlet
		addPortlets(context);

		// Register the servlet
		addServlet(context, SearcherServlet.class, "/servlets/SearcherServlet");
		addServlet(context, SearcherServletCSV.class, "/servlets/SearcherServletCSV");

		// Register language variables (portlet name)
		addLanguageVariables(context);

	}

	@Override
	public void stop(BundleContext context) throws Exception {
		unpublishBundleServices();
	}

}
