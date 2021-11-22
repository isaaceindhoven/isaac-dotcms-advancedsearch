package nl.isaac.dotcms.searcher.servlet;
/**
* dotCMS Searcher plugin by ISAAC - The Full Service Internet Agency is licensed
* under a Creative Commons Attribution 3.0 Unported License
* - http://creativecommons.org/licenses/by/3.0/
* - http://www.geekyplugins.com/
*
* @copyright Copyright (c) 2012 ISAAC Software Solutions B.V. (http://www.isaac.nl)
*/

import com.dotmarketing.business.APILocator;
import com.dotmarketing.portlets.contentlet.model.Contentlet;
import com.dotmarketing.portlets.languagesmanager.model.Language;
import com.dotmarketing.util.Logger;
import com.liferay.portal.model.Portlet;
import nl.isaac.dotcms.searcher.dao.HostDAO;
import nl.isaac.dotcms.searcher.service.SearcherService;
import nl.isaac.dotcms.searcher.shared.*;
import nl.isaac.dotcms.searcher.shared.Status.StatusEnum;
import nl.isaac.dotcms.searcher.util.TextUtil;
import nl.isaac.dotcms.util.osgi.RequestHelper;
import org.apache.commons.lang3.StringUtils;
import org.h2.tools.Csv;
import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collection;

public class SearcherServletCSV extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final String PAGE_QUERY_PARAM_REGEX = "&page=[^&]+";

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		UserSearchValues userSearchValues = paramsToUserSearchValues(req);

		SearcherService searcherService = new SearcherService(userSearchValues);

		Collection<PortletHit> searchResults = searcherService.getPortletHits();

		resp.setHeader("Content-Type", "text/csv");
		//resp.addHeader("Content-Disposition", "attachment;filename=myfilename.csv")

		TextUtil textUtil = new TextUtil(userSearchValues.getSearchMode().toString(), userSearchValues.getSnippetSizeBefore(), userSearchValues.getSnippetSizeAfter(), userSearchValues.getExcludeText());
		CsvListWriter csv = new CsvListWriter(resp.getWriter(), CsvPreference.STANDARD_PREFERENCE);
		csv.writeHeader("identifier", "countryCode", "languageCode", "host", "title", "field", "snippet");
		for(PortletHit portletHit: searchResults) {
			Contentlet contentlet = (Contentlet)portletHit.getObject();
			Language language = APILocator.getLanguageAPI().getLanguage(contentlet.getLanguageId());
			for(String snippet: textUtil.getSnippetFromText(portletHit.getValue(), userSearchValues.getSearchString())) {
				csv.write(contentlet.getIdentifier(), language.getCountryCode(), language.getLanguageCode(), contentlet.getHost(), contentlet.getTitle(), portletHit.getName(), snippet);
			}
		}
	}

	private UserSearchValues paramsToUserSearchValues(HttpServletRequest req) {
		Logger.info(this.getClass().getName(), "Parsing posted parameters to SearchFilterRequest Object");

		RequestHelper requestHelper = new RequestHelper(req);

		String text = requestHelper.getParamAsString("text");

		String searchModeAsString = requestHelper.getParamAsString("searchMode");
		SearchMode searchMode = SearchMode.valueOf(searchModeAsString.toUpperCase());

		String languageId = requestHelper.getParamAsString("language_id");

		String statusAsString = requestHelper.getParamAsString("status");
		Status status = new Status();
		status.setStatus(StatusEnum.valueOf(statusAsString.toUpperCase()));

		String versionAsString = requestHelper.getParamAsString("version");
		Version version = Version.valueOf(versionAsString.toUpperCase());

//		String typeId = requestHelper.getParamAsString("type_id");
//		Type type = Type.valueOf(typeId.toUpperCase());
		Type type = Type.CONTENT;

		String hostString = requestHelper.getParamAsString("hostName", new HostDAO().getCurrentHost(req).getHostname());

		int snippetSizeBefore = requestHelper.getParamAsInteger("snippetSizeBefore", 20);
		int snippetSizeAfter = requestHelper.getParamAsInteger("snippetSizeAfter", 20);

		String excludeText = req.getParameter("excludeText");
		if (excludeText != null && excludeText.trim().isEmpty()) {
			excludeText = null;
		}

		int maxResults = requestHelper.getParamAsInteger("maxResults", 100);
		int resultsPerPage = requestHelper.getParamAsInteger("resultsPerPage", 10);

		boolean filterSystemHost = requestHelper.getParamAsBoolean("filterSystemHost", false);

		return new UserSearchValues(searchMode, type, text, hostString, languageId, status, version,
				Integer.valueOf(snippetSizeBefore), Integer.valueOf(snippetSizeAfter), excludeText, maxResults,
				resultsPerPage, filterSystemHost);
	}

}
