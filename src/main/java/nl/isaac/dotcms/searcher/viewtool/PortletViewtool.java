package nl.isaac.dotcms.searcher.viewtool;

import java.util.List;

import org.apache.velocity.tools.view.tools.ViewTool;

import com.dotmarketing.business.APILocator;
import com.dotmarketing.portlets.languagesmanager.model.Language;

public class PortletViewtool implements ViewTool {

	@Override
	public void init(Object initData) {	}
	
	public String replaceHTMLEncodedTextWithMatchHighlight(String encodedResult, String searchParam) {
		String matcher = "<span class=\"match\">" + searchParam +  "</span>";
		String result = encodedResult.replace(searchParam, matcher);
		return result;
	}

	
	public String getLanguagesSelector() {
		List<Language> languages = APILocator.getLanguageAPI().getLanguages();
		String result = "{	id : '0', value : '', lang : 'All',	imageurl : '/html/images/languages/all.gif', label : '<span style=\"background-image:url(/html/images/languages/all.gif);\"></span>All' }";
		for(Language language : languages) {
			result += ", {	id : '"+language.getId()+"', value : '"+language.getId()+"', "
					+"lang : '"+ language.getLanguage() + "-" + language.getCountry() + "', "
					+"imageurl : '/html/images/languages/"+language.getLanguageCode()+"_"+language.getCountryCode()+".gif', "
					+"label : '<span style=\"background-image:url(/html/images/languages/"+language.getLanguageCode()+"_"+language.getCountryCode()+".gif);\">"
					+"</span>"+ language.getLanguage() + "-" + language.getCountry() + "' }";
		}
		return result;
		
	}
}

