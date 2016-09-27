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
import nl.isaac.dotcms.searcher.shared.Status;
import nl.isaac.dotcms.searcher.shared.Status.StatusEnum;
import nl.isaac.dotcms.searcher.shared.Type;
import nl.isaac.dotcms.searcher.shared.Version;
import nl.isaac.dotcms.searcher.util.ParamValidationUtil;
import nl.isaac.dotcms.searcher.viewtool.SearcherViewtool;

import com.dotmarketing.beans.Host;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.web.WebAPILocator;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.portlets.structure.model.Structure;
import com.dotmarketing.util.Logger;
import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;

public class SearcherServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private Collection<SearchResult> contentlets;
	private Collection<SearchResult> widgets;
	private Collection<SearchResult> containers;
	private Collection<SearchResult> structures;
	private Collection<SearchResult> templates;
	private Collection<SearchResult> files;
	private Collection<SearchResult> htmlPages;
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String text = req.getParameter("text");
		ParamValidationUtil.validateParamNotNull(text, "text");
	
		String languageId = req.getParameter("language_id");
		ParamValidationUtil.validateParamNotNull(languageId, "language_id");

		String statusAsString = req.getParameter("status");
		ParamValidationUtil.validateParamNotNull(statusAsString, "status");
		Status status = new Status();
		status.setStatus(StatusEnum.valueOf(statusAsString.toUpperCase()));

		String versionAsString = req.getParameter("version");
		ParamValidationUtil.validateParamNotNull(versionAsString, "version");
		Version version = Version.valueOf(versionAsString.toUpperCase());
		
		String redirectPage = req.getParameter("redirectPage");

		String type_id = req.getParameter("type_id");
		ParamValidationUtil.validateParamNotNull(type_id, "type_id");

		String hostString = req.getParameter("hostName");
		if(hostString == null) {
			hostString = getCurrentHost(req).getHostname();
		}
		
		Logger.info(this.getClass(), "Searching for '" + text + "' on " + hostString + " for Language: " + languageId
				+ " with status: " + statusAsString + " with version: " + versionAsString + " and type is: " + type_id);
		
		this.contentlets = new ArrayList<SearchResult>();
		this.containers = new ArrayList<SearchResult>();
		this.structures = new ArrayList<SearchResult>();
		this.templates = new ArrayList<SearchResult>();
		this.files = new ArrayList<SearchResult>();
		this.htmlPages = new ArrayList<SearchResult>();
		this.widgets = new ArrayList<SearchResult>();
		
		SearcherViewtool searcher = new SearcherViewtool();

		try {
			if(hostString.equals("all_hosts")) {
				for(Host host: APILocator.getHostAPI().findAll(APILocator.getUserAPI().getSystemUser(), false)) {
					handleSearchResultsForHost(host, text, status, version, languageId, searcher, Type.valueOf(type_id.toUpperCase()));
				}
			} else {
				Host host = APILocator.getHostAPI().findByName(hostString, APILocator.getUserAPI().getSystemUser(), false);
				handleSearchResultsForHost(host, text, status, version, languageId, searcher, Type.valueOf(type_id.toUpperCase()));
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
		req.getSession().setAttribute("searcher_htmlpages", htmlPages);
		req.getSession().setAttribute("searcher_widgets", widgets);
		req.getSession().setAttribute("searcher_files", files);
		req.getSession().setAttribute("searcher_time", Calendar.getInstance().getTime());
		req.getSession().setAttribute("searcher_text", text);
		req.getSession().setAttribute("searcher_host", hostString);
		req.getSession().setAttribute("searcher_status", statusAsString);
		req.getSession().setAttribute("searcher_version", versionAsString);
		req.getSession().setAttribute("searcher_languageId", languageId);
		req.getSession().setAttribute("searcher_typeId", type_id);
		req.getSession().setAttribute("searcher_errors", searcher.getErrors());
		req.getSession().setAttribute("searcher_referer", req.getHeader("referer"));
		
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
	
	private void handleSearchResultsForHost(Host host, String text, Status expectingStatus, Version version, String languageId, SearcherViewtool searcher, Type type) {
		
		switch(type) {
		case ALL:
			Logger.info(this, "Search for Type: 'All'");
			this.contentlets.addAll(searcher.getContentletsContaining(Structure.STRUCTURE_TYPE_CONTENT, text, host, languageId, expectingStatus, version));
			this.widgets.addAll(searcher.getContentletsContaining(Structure.STRUCTURE_TYPE_WIDGET, text, host, languageId, expectingStatus, version));
			this.containers.addAll(searcher.getContainersContaining(text, host, expectingStatus, version));
			this.structures.addAll(searcher.getStructuresContaining(text, host, expectingStatus));
			this.templates.addAll(searcher.getTemplatesContaining(text, host, expectingStatus, version));
			this.files.addAll(searcher.getFilesContaining(text, host, expectingStatus, version));
			this.htmlPages.addAll(searcher.getPagesContaining(text, host, expectingStatus));
			break;
		case CONTAINER:
			Logger.info(this, "Search for Type: 'Container'");
			this.containers.addAll(searcher.getContainersContaining(text, host, expectingStatus, version));
			break;
		case CONTENT: 
			Logger.info(this, "Search for Type: 'Content'");
			this.contentlets.addAll(searcher.getContentletsContaining(Structure.STRUCTURE_TYPE_CONTENT, text, host, languageId, expectingStatus, version));
			break;
		case FILE:
			Logger.info(this, "Search for Type: 'File'");
			this.files.addAll(searcher.getFilesContaining(text, host, expectingStatus, version));
			break;
		case HTMLPAGE: 
			Logger.info(this, "Search for Type: 'HTML Page'");
			this.htmlPages.addAll(searcher.getPagesContaining(text, host, expectingStatus));
			break;
		case STRUCTURE: 
			Logger.info(this, "Search for Type: 'Structure'");
			this.structures.addAll(searcher.getStructuresContaining(text, host, expectingStatus));
			break;
		case TEMPLATE: 
			Logger.info(this, "Search for Type: 'Template'");
			this.templates.addAll(searcher.getTemplatesContaining(text, host, expectingStatus, version));
			break;
		case WIDGET: 
			Logger.info(this, "Search for Type: 'Widget'");
			this.widgets.addAll(searcher.getContentletsContaining(Structure.STRUCTURE_TYPE_WIDGET, text, host, languageId, expectingStatus, version));
			Logger.info(this, "Widgets size: " + this.widgets.size());
			break;
		}

	}
	
}
