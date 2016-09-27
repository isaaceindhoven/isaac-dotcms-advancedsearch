package nl.isaac.dotcms.searcher.viewtool;
/**
* dotCMS Searcher plugin by ISAAC - The Full Service Internet Agency is licensed 
* under a Creative Commons Attribution 3.0 Unported License
* - http://creativecommons.org/licenses/by/3.0/
* - http://www.geekyplugins.com/
* 
* @copyright Copyright (c) 2012 ISAAC Software Solutions B.V. (http://www.isaac.nl)
*/

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nl.isaac.dotcms.searcher.SearchResult;
import nl.isaac.dotcms.searcher.shared.Status;
import nl.isaac.dotcms.searcher.shared.Status.StatusEnum;
import nl.isaac.dotcms.searcher.shared.Type;
import nl.isaac.dotcms.searcher.shared.Version;
import nl.isaac.dotcms.searcher.status.StatusFactory;
import nl.isaac.dotcms.searcher.util.ContentletQuery;

import org.apache.velocity.tools.view.tools.ViewTool;

import com.dotcms.repackage.org.apache.commons.io.IOUtils;
import com.dotmarketing.beans.Host;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.cache.FieldsCache;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.portlets.containers.model.Container;
import com.dotmarketing.portlets.contentlet.model.Contentlet;
import com.dotmarketing.portlets.fileassets.business.FileAsset;
import com.dotmarketing.portlets.folders.model.Folder;
import com.dotmarketing.portlets.htmlpages.model.HTMLPage;
import com.dotmarketing.portlets.structure.factories.StructureFactory;
import com.dotmarketing.portlets.structure.model.Field;
import com.dotmarketing.portlets.structure.model.Field.FieldType;
import com.dotmarketing.portlets.structure.model.Structure;
import com.dotmarketing.portlets.templates.model.Template;
import com.dotmarketing.util.Logger;
import com.liferay.portal.model.User;

public class SearcherViewtool implements ViewTool {
	private List<String> errors = new ArrayList<String>();

	public void init(Object arg0) {}
	
	
	public Collection<SearchResult> getContainersContaining(String text, Host host, Status expectingStatus, Version version) {
		Collection<SearchResult> results = new ArrayList<SearchResult>();
		StatusFactory statusFactory = new StatusFactory();
		try {
			Logger.info(this.getClass(), "Searching containers for '" + text + "'");

			for(Container container: APILocator.getContainerAPI().findContainersUnder(host)) {
				Status status = statusFactory.getStatusForObject(Type.CONTAINER, container, expectingStatus);				
				String hostName = host.getHostname();
				
				if(status != null) {
					
					if(status.getStatus().equals(StatusEnum.SAVED) && version.equals(Version.LIVE)) {
						if(status.getObject() instanceof Container) {
							container = (Container) status.getObject();							
						}
					}
					
					if(checkValueForText(container.getCode(), text)) {
						results.add(new SearchResult(container, container.getTitle(), "Code", container.getCode(), getSnippetFromText(container.getCode(), text), hostName, status.getStatus()));
					}
					if(checkValueForText(container.getPreLoop(), text)) {
						results.add(new SearchResult(container, container.getTitle(), "Preloop", container.getPreLoop(), getSnippetFromText(container.getPreLoop(), text), hostName, status.getStatus()));
					}
					if(checkValueForText(container.getPostLoop(), text)) {
						results.add(new SearchResult(container, container.getTitle(), "Postloop", container.getPostLoop(), getSnippetFromText(container.getPostLoop(), text), hostName, status.getStatus()));
					}
				}
			}
			Logger.info(this.getClass(), "Found " + results.size());
		} catch (Throwable t) {
			Logger.error(this.getClass(), "Error while getting containers", t);
			errors.add("Error while getting containers: " + t.getMessage());
		}
		return results;
	}
	
	public Collection<SearchResult> getTemplatesContaining(String text, Host host, Status expectingstatus, Version version) {
		StatusFactory statusFactory = new StatusFactory();
		Collection<SearchResult> results = new ArrayList<SearchResult>();
		try {
			Logger.info(this.getClass(), "Searching templates for '" + text + "'");

			for(Template template: APILocator.getTemplateAPI().findTemplatesAssignedTo(host)) {
				Status status = statusFactory.getStatusForObject(Type.TEMPLATE, template, expectingstatus);

				if(status != null) {

					if(status.getStatus().equals(StatusEnum.SAVED) && version.equals(Version.LIVE)) {
						if(status.getObject() instanceof Template) {
							template = (Template) status.getObject();							
						}
					}
					
					String hostName = host.getHostname();
					if(checkValueForText(template.getTitle(), text)) {
						results.add(new SearchResult(template, template.getTitle(), "Title", template.getTitle(), getSnippetFromText(template.getTitle(), text), hostName, status.getStatus()));
					}
					if(checkValueForText(template.getBody(), text)) {
						results.add(new SearchResult(template, template.getTitle(), "Body", template.getBody(), getSnippetFromText(template.getBody(), text), hostName, status.getStatus()));
					}
					if(checkValueForText(template.getHeader(), text)) {
						results.add(new SearchResult(template, template.getTitle(), "Header", template.getHeader(), getSnippetFromText(template.getHeader(), text), hostName, status.getStatus()));
					}
					if(checkValueForText(template.getFooter(), text)) {
						results.add(new SearchResult(template, template.getTitle(), "Footer", template.getFooter(), getSnippetFromText(template.getFooter(), text), hostName, status.getStatus()));
					}
				}
			}
			Logger.info(this.getClass(), "Found " + results.size());
		} catch (Throwable t) {
			Logger.error(this.getClass(), "Error while getting templates", t);
			errors.add("Error while getting templates: " + t.getMessage());
		}
		
		return results;
	}
	
	public Collection<SearchResult> getStructuresContaining(String text, Host host, Status status) {
		Collection<SearchResult> results = new ArrayList<SearchResult>();
		try {
			List<Structure> allStructures = StructureFactory.getStructures();
			Logger.info(this.getClass(), "Searching " + allStructures.size() + " structures for '" + text + "'");
			for(Structure structure: allStructures) {
				if(structure.getHost().equals(host.getIdentifier())) {
					List<Field> fields = FieldsCache.getFieldsByStructureInode(structure.getInode());
					
					for(Field f : fields) {

						if(f.getFieldType().equalsIgnoreCase(FieldType.CUSTOM_FIELD.toString())
								|| f.getFieldType().equalsIgnoreCase(FieldType.TEXT_AREA.toString())
								|| f.getVelocityVarName().equalsIgnoreCase("widgetCode")) {
							
							List<String> snippets = getSnippetFromText(f.getValues(), text);
							if(snippets != null) {
								results.add(new SearchResult(structure, structure.getName(), f.getFieldName(), f.getValues() + " " + f.getDefaultValue(), snippets, host.getHostname(), status.getStatus()));
							}
						}
					}
				}
			}
			Logger.info(this.getClass(), "Found " + results.size());
		} catch (Throwable t) {
			Logger.error(this.getClass(), "Error while getting structures", t);
			errors.add("Error while getting structures: " + t.getMessage());
		}
		return results;
	}
	
	public Collection<SearchResult> getFilesContaining(String text, Host host, Status expectingStatus, Version version) {
		StatusFactory statusFactory = new StatusFactory();
		Collection<SearchResult> searchResults = new ArrayList<SearchResult>();
		List<Structure> structuresPerType = getStructuresPerType(Structure.STRUCTURE_TYPE_FILEASSET);
		
		if(structuresPerType.size() != 0) { 
			ContentletQuery cq = new ContentletQuery(structuresPerType);
			cq.addHost(host.getIdentifier());
	
			if(!(expectingStatus.getStatus().getLive() == false && expectingStatus.getStatus().getWorking() == false && expectingStatus.getStatus().getArchived() == false)) {
				cq.addDeleted(expectingStatus.getStatus().getArchived());
				cq.addWorking(expectingStatus.getStatus().getWorking());
				cq.addLive(expectingStatus.getStatus().getLive());
			}
		
			List<Contentlet> files = cq.executeSafe();
			Logger.info(this.getClass(), "Searching " + files.size() + " files for '" + text + "'");
		
			try {
				for(Contentlet fileContentlet: files) {
					
					Status status = statusFactory.getStatusForObject(Type.CONTENT, fileContentlet, expectingStatus);
					if(status != null) {
						if(status.getStatus().equals(StatusEnum.SAVED) && version.equals(Version.LIVE)) {
							if(status.getObject() instanceof Contentlet) {
								fileContentlet = (Contentlet) status.getObject();							
							}
						}
						FileAsset file = APILocator.getFileAssetAPI().fromContentlet(fileContentlet);
						
						if("vtl".equalsIgnoreCase(file.getExtension()) || "js".equalsIgnoreCase(file.getExtension())) {
							String fileText = new String(IOUtils.toByteArray(file.getFileInputStream()));
							List<String> snippets = new ArrayList<String>();
							snippets = getSnippetFromText(fileText, text);
							
							if(snippets != null) {
								searchResults.add(new SearchResult(file, file.getFileName(), "Text of file", fileText, snippets, host.getHostname(), status.getStatus()));
							}
						}
					}
				}
				Logger.info(this.getClass(), "Found " + searchResults.size());
			} catch (Throwable t) {
				Logger.error(this.getClass(), "Error while getting structures", t);
				errors.add("Error while getting structures: " + t.getMessage());
			}
		}
		return searchResults;
	}
	

	public Collection<SearchResult> getContentletsContaining(int structureType, String text, Host host, String languageId, Status expectingStatus, Version version) {
		StatusFactory statusFactory = new StatusFactory();
		Collection<SearchResult> searchResults = new ArrayList<SearchResult>();
		List<Structure> structuresPerType = getStructuresPerType(structureType);
		Logger.info(this, "structuresPerType: " + structuresPerType.size());
		if(structuresPerType.size() != 0) { 
			ContentletQuery cq = new ContentletQuery(structuresPerType);
		
			if(!languageId.equalsIgnoreCase("0")) {
				cq.addLanguage(languageId);
			}
			cq.addHostAndIncludeSystemHost(host.getIdentifier());
	
			if(!(expectingStatus.getStatus().getLive() == false && expectingStatus.getStatus().getWorking() == false && expectingStatus.getStatus().getArchived() == false)) {
				cq.addDeleted(expectingStatus.getStatus().getArchived());
				cq.addWorking(expectingStatus.getStatus().getWorking());
				cq.addLive(expectingStatus.getStatus().getLive());
			}
		
			List<Contentlet> contentlets = cq.executeSafe();
			Logger.info(this.getClass(), "Searching " + contentlets.size() + " contentlets for '" + text + "'");
			for(Contentlet contentlet: contentlets) {
				if(structureType == contentlet.getStructure().getStructureType()) {

					Status status = statusFactory.getStatusForObject(Type.CONTENT, contentlet, expectingStatus);
					if(status != null) {
						if(status.getStatus().equals(StatusEnum.SAVED) && version.equals(Version.LIVE)) {
							if(status.getObject() instanceof Contentlet) {
								contentlet = (Contentlet) status.getObject();
							}
						}

						Map<String, Object> row = contentlet.getMap();
						row.put("structureName", contentlet.getStructure().getName());

						// Extract the title in case it hasn't been loaded yet, if it isn't, it will cause a ConcurrentModificationException in the loop below
						final String title = contentlet.getTitle();

						for(Entry<String, Object> entry : row.entrySet()) {

							if(entry.getValue() instanceof String) {
								List<String> snippets = getSnippetFromText((String) entry.getValue(), text);

								if(snippets != null) {
									searchResults.add(new SearchResult(row, title, entry.getKey(), (String) entry.getValue(), snippets, host.getHostname(), status.getStatus()));
								}
							}
						}
					}
				}
			}
		}
		Logger.info(this.getClass(), "Found " + searchResults.size());
		return searchResults;
	}
	
	private List<Structure> getStructuresPerType(int structureType) {
		List<Structure> structuresPerType = new ArrayList<Structure>();
		for(Structure s : StructureFactory.getStructures()) {
			if(s.getStructureType() == structureType) {
				structuresPerType.add(s);
			}
		}
		return structuresPerType;
	}
	
	public Collection<SearchResult> getPagesContaining(String text, Host host, Status expectingstatus) {
		StatusFactory statusFactory = new StatusFactory();	
		Collection<SearchResult> searchResults = new ArrayList<SearchResult>();
		User systemUser;
		try {
			systemUser = APILocator.getUserAPI().getSystemUser();
		} catch (DotDataException e1) {
			throw new RuntimeException(e1.toString(), e1);
		}
		
		try {
			List<Folder> folders = APILocator.getFolderAPI().findFoldersByHost(host, systemUser, false);
			Logger.info(this.getClass(), "Searching " + folders.size() + " folders for Pages");
			//Get HTML pages per folder
			for(Folder f : folders) {
				List<HTMLPage> htmlPages = APILocator.getHTMLPageAPI().findWorkingHTMLPages(f);
				Logger.info(this.getClass(), "Searching " + htmlPages.size() + " HTML Pages for '" + text + "'");
				for(HTMLPage htmlPage : htmlPages) {
					Status status = statusFactory.getStatusForObject(Type.HTMLPAGE, htmlPage, expectingstatus);
					
					if(status != null) {
						Map<String, Object> row = htmlPage.getMap();
						row.put("URI", htmlPage.getURI());
						String hostName = host.getHostname();
						
						if(checkValueForText(htmlPage.getTitle(), text)) {
							searchResults.add(new SearchResult(row, htmlPage.getTitle(), "Title", htmlPage.getTitle(), getSnippetFromText(htmlPage.getTitle(), text), hostName, status.getStatus()));
						}
						if(checkValueForText(htmlPage.getFriendlyName(), text)) {
							searchResults.add(new SearchResult(row, htmlPage.getTitle(), "FriendlyName", htmlPage.getFriendlyName(), getSnippetFromText(htmlPage.getFriendlyName(), text), hostName, status.getStatus()));
						}
						if(checkValueForText(htmlPage.getMetadata(), text)) {
							searchResults.add(new SearchResult(row, htmlPage.getTitle(), "Meta Data", htmlPage.getMetadata(), getSnippetFromText(htmlPage.getMetadata(), text), hostName, status.getStatus()));
						}
						if(checkValueForText(htmlPage.getSeoDescription(), text)) {
							searchResults.add(new SearchResult(row, htmlPage.getTitle(), "SEO Description", htmlPage.getSeoDescription(), getSnippetFromText(htmlPage.getSeoDescription(), text), hostName, status.getStatus()));
						}
						if(checkValueForText(htmlPage.getSeoKeywords(), text)) {
							searchResults.add(new SearchResult(row, htmlPage.getTitle(), "SEO Keywords", htmlPage.getSeoKeywords(), getSnippetFromText(htmlPage.getSeoKeywords(), text), hostName, status.getStatus()));
						}
					}
				}
			}
			
		} catch (DotDataException e) {
			Logger.warn(this.getClass(), "Error while getting HTML Pages", e);
			errors.add("Error while getting HTML Pages: " + e.getMessage());
		} catch (DotSecurityException e) {
			Logger.warn(this.getClass(), "Error while getting HTML Pages", e);
			errors.add("Error while getting HTML Pages: " + e.getMessage());
		}
		Logger.info(this.getClass(), "Found " + searchResults.size());
		return searchResults;
	}
	
	private List<String> getSnippetFromText(String text, String wordToSearchFor) {
		 int snippetSize = 200;
		if(text != null && wordToSearchFor != null) {
			String lowerCaseText = text.toLowerCase();
			String lowerCaseWordToSearchFor = wordToSearchFor.toLowerCase();
			
			List<String> snippets = new ArrayList<String>();
			int textIndex = 0;

			while((textIndex = lowerCaseText.indexOf(lowerCaseWordToSearchFor)) > -1) {
				String snippet = text.substring(Math.max(0, textIndex - snippetSize), Math.min(textIndex + snippetSize, text.length()));
				snippets.add(snippet);
				text = text.substring(Math.max(0, textIndex + wordToSearchFor.length()));
				lowerCaseText = lowerCaseText.substring(Math.max(0, textIndex + wordToSearchFor.length()));
			}
			
			if(snippets.size() > 0) {
				return snippets;
			}
		}	
		return null;
	}

	public static Collection<Map<String, String>> extract(ResultSet resultSet)  
    throws SQLException {  
        Collection<Map<String, String>> table;  
        int columnCount = resultSet.getMetaData().getColumnCount();  
        
        ArrayList<String> columnNames = new ArrayList<String>();
        columnNames.add("");
        for(int c = 1; c <= columnCount; ++ c) {
        	columnNames.add(resultSet.getMetaData().getColumnName(c));
        }
          
        if(resultSet.getType() == ResultSet.TYPE_FORWARD_ONLY)   
            table = new ArrayList<Map<String, String>>();  
        else {    
            resultSet.last();  
            table = new ArrayList<Map<String, String>>(resultSet.getRow());  
            resultSet.beforeFirst();  
        }  
    
        for(Map<String, String> row; resultSet.next(); table.add(row)) {  
            row = new HashMap<String, String>();  
      
            for(int c = 1; c <= columnCount; ++ c) {
                row.put(columnNames.get(c), resultSet.getString(c) != null ? resultSet.getString(c).intern() : null);
            }
        }  
          
        return table;  
    }  	

	private boolean checkValueForText(String value, String text) {
		return value != null && value.toLowerCase().contains(text.toLowerCase());
	}
	
	public List<String> getErrors() {
		return errors;
	}
	
	public List<String> getAllHosts() throws DotDataException, DotSecurityException {
		List<Host> hosts = APILocator.getHostAPI().findAll(APILocator.getUserAPI().getSystemUser(), false);
		List<String> hostNames = new ArrayList<String>(hosts.size());
		for (Host host : hosts) {
			hostNames.add(host.getHostname());
		}
		return hostNames;
	}
}
