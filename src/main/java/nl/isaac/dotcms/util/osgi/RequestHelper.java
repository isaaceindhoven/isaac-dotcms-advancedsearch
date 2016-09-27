package nl.isaac.dotcms.util.osgi;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Request wrapper with utility methods
 * 
 * @author Jan-Willem
 *
 */
public class RequestHelper extends HttpServletRequestWrapper {

	public RequestHelper(HttpServletRequest servletRequest) {
		super(servletRequest);
	}

    /**
     * @return	The page part of the referer url.
     * 			ie: in http://www.domain.com:8080/test/index.jsp?q=v
     * 			only index.jsp is returned. 
     */
	public String getRefererFileWithoutPathAndQueryString() {
		String referer					= getHeader("referer");
		String singlePointOfEntryFile	= referer.split("\\?")[0];
		String[] parts					= singlePointOfEntryFile.split("/");
		return parts[parts.length - 1];
	}
	
    public Boolean getParamAsBoolean(String param, Boolean ifNullValue){
    	String value = super.getParameter(param);
    	return HttpObjectUtil.objectToBoolean(value, ifNullValue);
    }

    public Boolean getParamAsBoolean(String param)
    {
    	Boolean value = getParamAsBoolean(param, null);
    	if (value == null) {
    		throw new IllegalArgumentException(param);
    	}
    	return value;
    }

    public Boolean getAttributeAsBoolean(String param, Boolean ifNullValue){
    	Object value = super.getAttribute(param);
    	return  HttpObjectUtil.objectToBoolean(value, ifNullValue);
    }

    public Boolean getAttributeAsBoolean(String param) {
    	Boolean value = getAttributeAsBoolean(param, null);
    	if (value == null) {
    		throw new IllegalArgumentException(param);
    	}
    	return value;
    }

    public String getParamAsString(String param, String ifNullValue) {
    	String value = super.getParameter(param);
    	return  HttpObjectUtil.objectToString(value, ifNullValue);
    }

    public String getParamAsString(String param) {
    	String value = getParamAsString(param, null);
    	if (value == null) {
    		throw new IllegalArgumentException(param);
    	}
    	return value;
    }

    public String getAttributeAsString(String param, String ifNullValue) {
    	Object value = super.getAttribute(param);
    	return  HttpObjectUtil.objectToString(value, ifNullValue);
    }

    public String getAttributeAsString(String param) {
    	String value = getAttributeAsString(param, null);
    	if (value == null) {
    		throw new IllegalArgumentException(param);
    	}
    	return value;
    }

    public Integer getParamAsInteger(String param, Integer ifNullValue) {
    	String value = super.getParameter(param);
    	return  HttpObjectUtil.objectToInteger(value, ifNullValue);
    }

    public Integer getParamAsInteger(String param) {
    	Integer value = getParamAsInteger(param, null);
    	if (value == null) {
    		throw new IllegalArgumentException(param);
    	}
    	return value;
    }

    public Integer getAttributeAsInteger(String param, Integer ifNullValue) {
    	Object value = super.getAttribute(param);
    	return  HttpObjectUtil.objectToInteger(value, ifNullValue);
    }

    public Integer getAttributeAsInteger(String param) {
    	Integer value = getAttributeAsInteger(param, null);
    	if (value == null) {
    		throw new IllegalArgumentException(param);
    	}
    	return value;
    }

    public Long getParamAsLong(String param, Long ifNullValue) {
    	String value = super.getParameter(param);
    	return  HttpObjectUtil.objectToLong(value, ifNullValue);
    }

    public Long getParamAsLong(String param) {
    	Long value = getParamAsLong(param, null);
    	if (value == null) {
    		throw new IllegalArgumentException(param);
    	}
    	return value;
    }

    public Long getAttributeAsLong(String param, Long ifNullValue) {
    	Object value = super.getAttribute(param);
    	return  HttpObjectUtil.objectToLong(value, ifNullValue);
    }

    public Long getAttributeAsLong(String param) {
    	Long value = getAttributeAsLong(param, null);
    	if (value == null) {
    		throw new IllegalArgumentException(param);
    	}
    	return value;
    }

    public Float getParamAsFloat(String param, Float ifNullValue) {
    	String value = super.getParameter(param);
    	return  HttpObjectUtil.objectToFloat(value, ifNullValue);
    }

    public Float getParamAsFloat(String param) {
    	Float value = getParamAsFloat(param, null);
    	if (value == null) {
    		throw new IllegalArgumentException(param);
    	}
    	return value;
    }

    public Float getAttributeAsFloat(String param, Float ifNullValue) {
    	Object value = super.getAttribute(param);
    	return  HttpObjectUtil.objectToFloat(value, ifNullValue);
    }

    public Float getAttributeAsFloat(String param) {
    	Float value = getAttributeAsFloat(param, null);
    	if (value == null) {
    		throw new IllegalArgumentException(param);
    	}
    	return value;
    }

    public Double getParamAsDouble(String param, Double ifNullValue) {
    	String value = super.getParameter(param);
    	return  HttpObjectUtil.objectToDouble(value, ifNullValue);
    }

    public Double getParamAsDouble(String param) {
    	Double value = getParamAsDouble(param, null);
    	if (value == null) {
    		throw new IllegalArgumentException(param);
    	}
    	return value;
    }

    public Date getParamAsDate(String param, Date ifNullValue) {
    	Object value = super.getParameter(param);
    	return  HttpObjectUtil.objectToDate(value, ifNullValue);
    }

    public Date getParamAsDate(String param) {
    	Date value = getParamAsDate(param, null);
    	if (value == null) {
    		throw new IllegalArgumentException(param);
    	}
    	return value;
    }

    public Double getAttributeAsDouble(String param, Double ifNullValue) {
    	Object value = super.getAttribute(param);
    	return  HttpObjectUtil.objectToDouble(value, ifNullValue);
    }

    public Double getAttributeAsDouble(String param) {
    	Double value = getAttributeAsDouble(param, null);
    	if (value == null) {
    		throw new IllegalArgumentException(param);
    	}
    	return value;
    }
    
    /**
     * Returns the root url for this web application
     * eg, http://localhost:8080/LoyaltySuite
     * @return
     */
    public String getRootUrl() {
    	
    	StringBuffer result = new StringBuffer();
    	result.append(getScheme());
    	result.append("://");
    	result.append(getServerName());
    	result.append(":");
    	result.append(getServerPort());
    	result.append(getContextPath());

    	return result.toString();
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public Map<String, String[]> getParameterMap() {
    	return super.getParameterMap();
    }
	
    public Collection<Integer> getParamAsIntegerCollection(String param) {
    	
    	Collection<Integer> collection = new HashSet<Integer>();
    	
    	String[] values = getParameterValues(param);
    	if (values != null) {
    		for (String element : values) {
    			collection.add(Integer.parseInt(element));
    		}
    	}
    	
    	return collection;
    }
    
    /**
     * format the given string as an Enum of type e
     * @param param
     * @param e
     * @param if_null_value
     * @return Enum variabele
     */
    public <E extends Enum<E>> E getParamAsEnum(Class<E> e, String param, E if_null_value) {
    	
    	String value = super.getParameter(param);
    	
    	E result = if_null_value;
    	if (value != null && value.length() > 0) {
    		try {
    			result = Enum.valueOf(e, value);
    		} catch (IllegalArgumentException ex){
    			result = if_null_value;
    		}
    	}
    	
    	return result;
    }
    
    public <E extends Enum<E>> E getParamAsEnum(Class<E> e, String param)
    {
    	E value = getParamAsEnum(e, param, null);
    	if (value == null) {
    		throw new IllegalArgumentException(param);
    	}
    	return value;
    }
    
    /**
     * prints the parameters of the request to System.out
     */
    public void printParameters() {
		for(Entry<String, String[]> entry: getParameterMap().entrySet()) {
			System.out.println(entry.getKey() + " " + Arrays.toString(entry.getValue()));
		}
    }
    
    /**
     * @return all the cookies as a Map with the cookie's name as the key.
     */
    public Map<String, String> getCookieMap() {
    	Map<String, String> cookieMap = new HashMap<String, String>();
    	for(Cookie cookie: getCookies()) {
    		cookieMap.put(cookie.getName(), cookie.getValue());
    	}
    	return cookieMap;
    }
    
    /**
     * @param name the name of the cookie, ignoring uppercase/lowercase
     * @return the value of the cookie with the given name
     */
    public String getCookieValue(String name) {
    	if(getCookies() != null) {
	    	for(Cookie cookie: getCookies()) {
	    		if(cookie.getName().equalsIgnoreCase(name)) {
	    			return cookie.getValue();
	    		}
	    	}
    	}
    	return null;
    }
    
	public Cookie getCookieByName(String name) {
		Cookie[] cookies = getCookies();
		if(cookies != null) {
			for(Cookie cookie : cookies) {
				if (cookie.getName().equalsIgnoreCase(name)) {
					return cookie;
				}
			}	
		}
		return null;
	}
    
}