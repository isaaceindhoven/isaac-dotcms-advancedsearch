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

import org.apache.velocity.tools.view.tools.ViewTool;

import com.dotmarketing.db.DbConnectionFactory;
import com.dotmarketing.portlets.containers.factories.ContainerFactory;
import com.dotmarketing.portlets.containers.model.Container;
import com.dotmarketing.portlets.files.business.FileFactory;
import com.dotmarketing.portlets.files.model.File;
import com.dotmarketing.portlets.structure.factories.StructureFactory;
import com.dotmarketing.portlets.structure.model.Field;
import com.dotmarketing.portlets.structure.model.Structure;
import com.dotmarketing.portlets.templates.factories.TemplateFactory;
import com.dotmarketing.portlets.templates.model.Template;
import com.dotmarketing.util.Logger;

public class SearcherViewtool implements ViewTool {
	private List<String> errors = new ArrayList<String>();

	public void init(Object arg0) {}
	
	public Collection<SearchResult> getContainersContaining(String text) {
		return getContainersContaining(text, false);
	}
	
	public Collection<SearchResult> getContainersContaining(String text, boolean live) {
		Collection<SearchResult> results = new ArrayList<SearchResult>();
		try {
			List<Container> allContainers = ContainerFactory.getContainerByCondition(live ? "live=1" : "working=1");
			Logger.info(this.getClass(), "Searching " + allContainers.size() + " containers for '" + text + "'");
			for(Container container: allContainers) {
				if(checkValueForText(container.getCode(), text)) {
					results.add(new SearchResult(container, "code", container.getCode(), getSnippetFromText(container.getCode(), text)));
				}
				if(checkValueForText(container.getPreLoop(), text)) {
					results.add(new SearchResult(container, "preloop", container.getPreLoop(), getSnippetFromText(container.getPreLoop(), text)));
				}
				if(checkValueForText(container.getPostLoop(), text)) {
					results.add(new SearchResult(container, "postloop", container.getPostLoop(), getSnippetFromText(container.getPostLoop(), text)));
				}
			}
			Logger.info(this.getClass(), "Found " + results.size());
		} catch (Throwable t) {
			Logger.error(this.getClass(), "Error while getting containers", t);
			errors.add("Error while getting containers: " + t.getMessage());
		}
		return results;
	}
	
	public Collection<SearchResult> getTemplatesContaining(String text) {
		return getTemplatesContaining(text, false);
	}

	public Collection<SearchResult> getTemplatesContaining(String text, boolean live) {
		Collection<SearchResult> results = new ArrayList<SearchResult>();
		try {
			List<Template> allTemplates = TemplateFactory.getTemplateByCondition(live ? "live=1" : "working=1");
			Logger.info(this.getClass(), "Searching " + allTemplates.size() + " templates for '" + text + "'");
			for(Template template: allTemplates) {
				if(checkValueForText(template.getTitle(), text)) {
					results.add(new SearchResult(template, "title", template.getTitle(), getSnippetFromText(template.getTitle(), text)));
				}
				if(checkValueForText(template.getBody(), text)) {
					results.add(new SearchResult(template, "body", template.getBody(), getSnippetFromText(template.getBody(), text)));
				}
				if(checkValueForText(template.getHeader(), text)) {
					results.add(new SearchResult(template, "header", template.getHeader(), getSnippetFromText(template.getHeader(), text)));
				}
				if(checkValueForText(template.getFooter(), text)) {
					results.add(new SearchResult(template, "footer", template.getFooter(), getSnippetFromText(template.getFooter(), text)));
				}
			}
			Logger.info(this.getClass(), "Found " + results.size());
		} catch (Throwable t) {
			Logger.error(this.getClass(), "Error while getting templates", t);
			errors.add("Error while getting templates: " + t.getMessage());
		}
		
		return results;
	}
	
	public Collection<SearchResult> getStructuresContaining(String text) {
		Collection<SearchResult> results = new ArrayList<SearchResult>();
		try {
			List<Structure> allStructures = StructureFactory.getStructures();
			Logger.info(this.getClass(), "Searching " + allStructures.size() + " structures for '" + text + "'");
			for(Structure structure: allStructures) {
				Field field = structure.getFieldVar("widgetCode");
				if(field != null) {
					String snippet = getSnippetFromText(field.getValues(), text);
					if(snippet != null) {
						results.add(new SearchResult(structure, "widget code", structure.getFieldVar("widgetCode").getValues(), snippet));
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
	
	public Collection<SearchResult> getFilesContaining(String text) {
		Collection<SearchResult> results = new ArrayList<SearchResult>();
		try {
			List<File> allFiles = FileFactory.getWorkingFiles();
			Logger.info(this.getClass(), "Searching " + allFiles.size() + " files for '" + text + "'");
			for(File file: allFiles) {
				if("vtl".equalsIgnoreCase(file.getExtension())) {
					String fileText = new String(FileFactory.getFileData(file));
					String snippet = getSnippetFromText(fileText, text);
					if(snippet != null) {
						results.add(new SearchResult(file, "Text of file", fileText, snippet));
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
	
	public Collection<SearchResult> getContentletsContaining(String text) {
		return getContentletsContaining(text, false);
	}
	

	public Collection<SearchResult> getContentletsContaining(String text, boolean live) {
		Collection<SearchResult> searchResults = new ArrayList<SearchResult>();
		ResultSet referenceResult = null;

		Connection conn = DbConnectionFactory.getConnection();
		try {
			Statement statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			try {
				statement.execute("SELECT * FROM Contentlet where " + (live ? "live" : "working") + "=1");
				referenceResult = statement.getResultSet();
				Collection<Map<String, String>> resultAsArray = extract(referenceResult);
				Logger.info(this.getClass(), "Searching " + resultAsArray.size() + " contentlets for '" + text + "'");
				Map<String, String> structureNameMap = getStructureNameMap();
				Iterator<Map<String, String>> rowIterator = resultAsArray.iterator();
				while(rowIterator.hasNext()) {
					Map<String, String> row = rowIterator.next();
					row.put("structureName", structureNameMap.get(row.get("structure_inode")));
					Iterator<Entry<String, String>> entryIterator = row.entrySet().iterator();
					while(entryIterator.hasNext()) {
						Entry<String, String> entry = entryIterator.next();
						String snippet = getSnippetFromText(entry.getValue(), text);
						if(snippet != null) {
							searchResults.add(new SearchResult(row, entry.getKey(), entry.getValue(), snippet));
						}
					}
				}
				Logger.info(this.getClass(), "Found " + searchResults.size());
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
}
