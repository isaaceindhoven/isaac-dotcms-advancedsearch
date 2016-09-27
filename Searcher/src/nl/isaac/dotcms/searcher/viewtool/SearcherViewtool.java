package nl.isaac.dotcms.searcher.viewtool;
/**
* dotCMS Searcher plugin by ISAAC - The Full Service Internet Agency is licensed 
* under a Creative Commons Attribution 3.0 Unported License
* - http://creativecommons.org/licenses/by/3.0/
* - http://www.geekyplugins.com/
* 
* @copyright Copyright (c) 2012 ISAAC Software Solutions B.V. (http://www.isaac.nl)
*/

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nl.isaac.dotcms.searcher.SearchResult;

import org.apache.axiom.attachments.utils.IOUtils;
import org.apache.velocity.tools.view.tools.ViewTool;

import com.dotmarketing.beans.Host;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.db.DbConnectionFactory;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.portlets.containers.model.Container;
import com.dotmarketing.portlets.contentlet.model.Contentlet;
import com.dotmarketing.portlets.fileassets.business.FileAsset;
import com.dotmarketing.portlets.structure.factories.StructureFactory;
import com.dotmarketing.portlets.structure.model.Field;
import com.dotmarketing.portlets.structure.model.Structure;
import com.dotmarketing.portlets.templates.model.Template;
import com.dotmarketing.util.Logger;

public class SearcherViewtool implements ViewTool {
	private List<String> errors = new ArrayList<String>();

	public void init(Object arg0) {}
	
	public Collection<SearchResult> getContainersContaining(String text, Host host) {
		return getContainersContaining(text, false, host);
	}
	
	public Collection<SearchResult> getContainersContaining(String text, boolean live, Host host) {
		Collection<SearchResult> results = new ArrayList<SearchResult>();
		try {
			Logger.info(this.getClass(), "Searching containers for '" + text + "'");

			for(Container container: APILocator.getContainerAPI().findContainersUnder(host)) {
				String hostName = host.getHostname();
				if(checkValueForText(container.getCode(), text)) {
					results.add(new SearchResult(container, container.getTitle(), "code", container.getCode(), getSnippetFromText(container.getCode(), text), hostName));
				}
				if(checkValueForText(container.getPreLoop(), text)) {
					results.add(new SearchResult(container, container.getTitle(), "preloop", container.getPreLoop(), getSnippetFromText(container.getPreLoop(), text), hostName));
				}
				if(checkValueForText(container.getPostLoop(), text)) {
					results.add(new SearchResult(container, container.getTitle(), "postloop", container.getPostLoop(), getSnippetFromText(container.getPostLoop(), text), hostName));
				}
			}
			Logger.info(this.getClass(), "Found " + results.size());
		} catch (Throwable t) {
			Logger.error(this.getClass(), "Error while getting containers", t);
			errors.add("Error while getting containers: " + t.getMessage());
		}
		return results;
	}
	
	public Collection<SearchResult> getTemplatesContaining(String text, Host host) {
		return getTemplatesContaining(text, false, host);
	}

	public Collection<SearchResult> getTemplatesContaining(String text, boolean live, Host host) {
		Collection<SearchResult> results = new ArrayList<SearchResult>();
		try {
			Logger.info(this.getClass(), "Searching templates for '" + text + "'");

			for(Template template: APILocator.getTemplateAPI().findTemplatesAssignedTo(host)) {
				String hostName = host.getHostname();
				if(checkValueForText(template.getTitle(), text)) {
					results.add(new SearchResult(template, template.getTitle(), "title", template.getTitle(), getSnippetFromText(template.getTitle(), text), hostName));
				}
				if(checkValueForText(template.getBody(), text)) {
					results.add(new SearchResult(template, template.getTitle(), "body", template.getBody(), getSnippetFromText(template.getBody(), text), hostName));
				}
				if(checkValueForText(template.getHeader(), text)) {
					results.add(new SearchResult(template, template.getTitle(), "header", template.getHeader(), getSnippetFromText(template.getHeader(), text), hostName));
				}
				if(checkValueForText(template.getFooter(), text)) {
					results.add(new SearchResult(template, template.getTitle(), "footer", template.getFooter(), getSnippetFromText(template.getFooter(), text), hostName));
				}
			}
			Logger.info(this.getClass(), "Found " + results.size());
		} catch (Throwable t) {
			Logger.error(this.getClass(), "Error while getting templates", t);
			errors.add("Error while getting templates: " + t.getMessage());
		}
		
		return results;
	}
	
	public Collection<SearchResult> getStructuresContaining(String text, Host host) {
		Collection<SearchResult> results = new ArrayList<SearchResult>();
		try {
			List<Structure> allStructures = StructureFactory.getStructures();
			Logger.info(this.getClass(), "Searching " + allStructures.size() + " structures for '" + text + "'");
			for(Structure structure: allStructures) {
				if(structure.getHost().equals(host.getIdentifier())) {
					Field field = structure.getFieldVar("widgetCode");
					if(field != null) {
						String snippet = getSnippetFromText(field.getValues(), text);
						if(snippet != null) {
							results.add(new SearchResult(structure, structure.getName(), "widget code", structure.getFieldVar("widgetCode").getValues(), snippet, host.getHostname()));
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
	
	public Collection<SearchResult> getFilesContaining(String text, Host host) {
		Collection<SearchResult> results = new ArrayList<SearchResult>();
		try {
			List<Contentlet> files = APILocator.getContentletAPI().search("+structureName:FileAsset +deleted:false +working:true +conhost:" + host.getIdentifier(), 0, 0, "", APILocator.getUserAPI().getSystemUser(), false);
			Logger.info(this.getClass(), "Searching " + files.size() + " files for '" + text + "'");
			for(Contentlet fileContentlet: files) {
				FileAsset file = APILocator.getFileAssetAPI().fromContentlet(fileContentlet);
				if("vtl".equalsIgnoreCase(file.getExtension()) || "js".equalsIgnoreCase(file.getExtension())) {
					String fileText = new String(IOUtils.getStreamAsByteArray(file.getFileInputStream()));
					String snippet = getSnippetFromText(fileText, text);
					if(snippet != null) {
						results.add(new SearchResult(file, file.getFileName(), "Text of file", fileText, snippet, host.getHostname()));
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
	
	public Collection<SearchResult> getContentletsContaining(String text, Host host) {
		return getContentletsContaining(text, false, host);
	}
	

	public Collection<SearchResult> getContentletsContaining(String text, boolean live, Host host) {
		Collection<SearchResult> searchResults = new ArrayList<SearchResult>();
		ResultSet referenceResult = null;

		Connection conn = DbConnectionFactory.getConnection();
		try {
			Statement statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			try {
				List<Contentlet> contentlets = APILocator.getContentletAPI().findContentletsByHost(host, APILocator.getUserAPI().getSystemUser(), false);
//				String liveOrWorking = live? "live" : "working";
//				statement.execute("SELECT * FROM Contentlet where inode in" +
//						"(select " + liveOrWorking + "_inode from contentlet_version_info)");
//				referenceResult = statement.getResultSet();
//				Collection<Map<String, String>> resultAsArray = extract(referenceResult);
//				Logger.info(this.getClass(), "Searching " + resultAsArray.size() + " contentlets for '" + text + "'");
//				Iterator<Map<String, String>> rowIterator = resultAsArray.iterator();
				
				for(Contentlet contentlet: contentlets) {
					Map<String, Object> row = contentlet.getMap();
					row.put("structureName", contentlet.getStructure().getName());
					Iterator<Entry<String, Object>> entryIterator = row.entrySet().iterator();
					while(entryIterator.hasNext()) {
						Entry<String, Object> entry = entryIterator.next();
						if(entry.getValue() instanceof String) {
							String snippet = getSnippetFromText(entry.getValue().toString(), text);
							if(snippet != null) {
								searchResults.add(new SearchResult(row, contentlet.getTitle(), entry.getKey(), entry.getValue().toString(), snippet, host.getHostname()));
							}
						}	
					}
				}
				Logger.info(this.getClass(), "Found " + searchResults.size());
			} catch (DotDataException e) {
				Logger.warn(this.getClass(), "Can't find host for contentlet", e);
			} catch (DotSecurityException e) {
				Logger.warn(this.getClass(), "Can't find host for contentlet", e);
			} finally {
				statement.close();
			}
		} catch (SQLException e) {
			Logger.error(this.getClass(), "Failed to select from contentlet table" , e);
			errors.add("Error while getting contentlets: " + e.getMessage());
		}
		
		return searchResults;
	}
	
	private String getSnippetFromText(String text, String wordToSearchFor) {
		if(text != null && wordToSearchFor != null) {
			int textIndex = text.indexOf(wordToSearchFor);
			if(textIndex > -1) {
				return text.substring(Math.max(0, textIndex - 20), Math.min(textIndex + 20, text.length()));
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
		return value != null && value.contains(text);
	}
	
	private Map<String, String> getStructureNameMap() {
		Map<String, String> structureNameMap = new HashMap<String, String>();
		List<Structure> allStructures = StructureFactory.getStructures();
		for(Structure structure: allStructures) {
			structureNameMap.put(structure.getInode(), structure.getName());
		}
		return structureNameMap;
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
