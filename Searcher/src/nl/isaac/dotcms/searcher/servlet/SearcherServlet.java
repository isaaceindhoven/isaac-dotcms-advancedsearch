package nl.isaac.dotcms.searcher.servlet;
/**
* dotCMS Searcher plugin by ISAAC - The Full Service Internet Agency is licensed 
* under a Creative Commons Attribution 3.0 Unported License
* - http://creativecommons.org/licenses/by/3.0/
* - http://www.geekyplugins.com/
* 
* @copyright Copyright (c) 2012 ISAAC Software Solutions B.V. (http://www.isaac.nl)
*/

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.isaac.dotcms.searcher.SearchResult;
import nl.isaac.dotcms.searcher.viewtool.SearcherViewtool;

import com.dotmarketing.beans.Host;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.web.WebAPILocator;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.util.Logger;
import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;

public class SearcherServlet extends HttpServlet {
	
	private Collection<SearchResult> contentlets;
	private Collection<SearchResult> containers;
	private Collection<SearchResult> structures;
	private Collection<SearchResult> templates;
	private Collection<SearchResult> files;
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String text = req.getParameter("text");
		if(text == null) {
			throw new ServletException("Missing parameter 'text'");
		}
		
		String redirectPage = req.getParameter("redirectPage");
		if(redirectPage == null) {
			throw new ServletException("Missing parameter 'redirectPage'");
		}

		String hostString = req.getParameter("hostName");
		if(hostString == null) {
			hostString = getCurrentHost(req).getHostname();
		}
		
		Logger.info(this.getClass(), "Searching for '" + text + "' on " + hostString);
		
		this.contentlets = new ArrayList<SearchResult>();
		this.containers = new ArrayList<SearchResult>();
		this.structures = new ArrayList<SearchResult>();
		this.templates = new ArrayList<SearchResult>();
		this.files = new ArrayList<SearchResult>();
		
		SearcherViewtool searcher = new SearcherViewtool();

		try {
			if(hostString.equals("all_hosts")) {
				for(Host host: APILocator.getHostAPI().findAll(APILocator.getUserAPI().getSystemUser(), false)) {
					handleSearchResultsForHost(host, text, searcher);
				}
			} else {
				Host host = APILocator.getHostAPI().findByName(hostString, APILocator.getUserAPI().getSystemUser(), false);
				handleSearchResultsForHost(host, text, searcher);
			}
		} catch (DotSecurityException e) {
			throw new ServletException(e);
		} catch (DotDataException e) {
			throw new ServletException(e);
		}
		
		req.getSession().setAttribute("searcher_contentlets", contentlets);
		req.getSession().setAttribute("searcher_containers", containers);
		req.getSession().setAttribute("searcher_structures", structures);
		req.getSession().setAttribute("searcher_templates", templates);
		req.getSession().setAttribute("searcher_files", files);
		req.getSession().setAttribute("searcher_time", Calendar.getInstance().getTime());
		req.getSession().setAttribute("searcher_text", text);
		req.getSession().setAttribute("searcher_host", hostString);
		req.getSession().setAttribute("searcher_errors", searcher.getErrors());
		
		resp.sendRedirect(redirectPage);
	}
	
	private Host getCurrentHost(HttpServletRequest request) throws ServletException {
		try {
			return WebAPILocator.getHostWebAPI().getCurrentHost(request);
		} catch (PortalException e) {
			throw new ServletException(e);
		} catch (SystemException e) {
			throw new ServletException(e);
		} catch (DotDataException e) {
			throw new ServletException(e);
		} catch (DotSecurityException e) {
			throw new ServletException(e);
		}
	}
	
	private void handleSearchResultsForHost(Host host, String text, SearcherViewtool searcher) {
		this.contentlets.addAll(searcher.getContentletsContaining(text, host));
		this.containers.addAll(searcher.getContainersContaining(text, host));
		this.structures.addAll(searcher.getStructuresContaining(text, host));
		this.templates.addAll(searcher.getTemplatesContaining(text, host));
		this.files.addAll(searcher.getFilesContaining(text, host));
	}
	
}
