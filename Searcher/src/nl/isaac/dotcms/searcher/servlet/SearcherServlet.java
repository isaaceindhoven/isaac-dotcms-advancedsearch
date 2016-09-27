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
import java.util.Calendar;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.isaac.dotcms.searcher.SearchResult;
import nl.isaac.dotcms.searcher.viewtool.SearcherViewtool;

public class SearcherServlet extends HttpServlet {
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
		
		SearcherViewtool searcher = new SearcherViewtool();
		Collection<SearchResult> contentlets = searcher.getContentletsContaining(text);
		Collection<SearchResult> containers = searcher.getContainersContaining(text);
		Collection<SearchResult> structures = searcher.getStructuresContaining(text);
		Collection<SearchResult> templates = searcher.getTemplatesContaining(text);
		Collection<SearchResult> files = searcher.getFilesContaining(text);
		
		req.getSession().setAttribute("searcher_contentlets", contentlets);
		req.getSession().setAttribute("searcher_containers", containers);
		req.getSession().setAttribute("searcher_structures", structures);
		req.getSession().setAttribute("searcher_templates", templates);
		req.getSession().setAttribute("searcher_files", files);
		req.getSession().setAttribute("searcher_time", Calendar.getInstance().getTime());
		req.getSession().setAttribute("searcher_text", text);
		req.getSession().setAttribute("searcher_errors", searcher.getErrors());
		
		resp.sendRedirect(redirectPage);
	}
}
