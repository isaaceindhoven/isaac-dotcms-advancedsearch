package nl.isaac.dotcms.util.osgi;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HttpObjectUtil {
    public static Boolean objectToBoolean(Object value, Boolean ifNullValue) {

    	if (value != null) {
    		if (value instanceof Boolean) {
    			return (Boolean)value;
    		} else {
    			String stringValue = value.toString();
    			if (!stringValue.equals("null") && stringValue.length() > 0) {
    				return (stringValue.equalsIgnoreCase("1") ||
    						stringValue.equalsIgnoreCase("true") ||
    						stringValue.equalsIgnoreCase("on"));
    			}
    		}
    	}
    	return ifNullValue;
    }

    public static Date objectToDate(Object value, Date ifNullValue) {
    	
    	if (value != null) {
    		if (value instanceof Date) {
    			return (Date)value;
    		} else {
    			String stringValue = value.toString();
    			if (!stringValue.equals("null") && stringValue.length() > 0) {
    				try {	
    					DateFormat df = new SimpleDateFormat("dd-MM-yyyy");    					
    					return df.parse(stringValue);
    				} catch (ParseException pa) {
    					System.out.println(stringValue + " is not a valid date");
    				}
    			}
    		}
    	}
    	return ifNullValue;
    }
    
    public static String objectToString(Object value, String ifNullValue) {

    	if (value != null) {
    		String stringValue = value.toString();
    		if (!stringValue.equals("null")) {
    			return stringValue;
    		}
    	}
    	return ifNullValue;
    }
    
    public static Integer objectToInteger(Object value, Integer ifNullValue) {

    	if (value != null) {
    		if (value instanceof Integer) {
    			return (Integer)value;
    		} else if (value instanceof Short) {
    			return Integer.valueOf(((Short)value).intValue());
    		} else {
    			String stringValue = value.toString();
    	    	if (!stringValue.equals("null") && stringValue.length() > 0){
    	    		return Integer.parseInt(stringValue);
    	    	}
    		}
    	}
    	return ifNullValue;
    }
    
    public static Long objectToLong(Object value, Long ifNullValue) {

    	if (value != null) {
    		if (value instanceof Long) {
    			return (Long)value;
    		} else if (value instanceof Integer) {
    			return Long.valueOf(((Integer)value).longValue());
    		} else {
    			String stringValue = value.toString();
    	    	if (!stringValue.equals("null") && stringValue.length() > 0){
    	    		return Long.parseLong(stringValue);
    	    	}
    		}
    	}
    	return ifNullValue;
    }
    
    public static Float objectToFloat(Object value, Float ifNullValue) {

    	if (value != null) {
    		if (value instanceof Float) {
    			return (Float)value;
    		} else if (value instanceof Integer) {
    			return new Float(((Integer)value).floatValue());
    		} else if (value instanceof Long) {
    			return new Float(((Long)value).floatValue());
    		} else {
    			String stringValue = value.toString();
    	    	if (!stringValue.equals("null") && stringValue.length() > 0){
    	    		return Float.parseFloat(stringValue);
    	    	}
    		}
    	}
    	return ifNullValue;
    }
    
    public static Double objectToDouble(Object value, Double ifNullValue) {

    	if (value != null) {
    		if (value instanceof Double) {
    			return (Double)value;
    		} else if (value instanceof Float) {
    			return new Double(((Float)value).doubleValue());
    		} else if (value instanceof Integer) {
    			return new Double(((Integer)value).doubleValue());
    		} else if (value instanceof Long) {
    			return new Double(((Long)value).doubleValue());
    		} else {
    			String stringValue = value.toString();
    	    	if (!stringValue.equals("null") && stringValue.length() > 0){
    	    		return Double.parseDouble(stringValue);
    	    	}
    		}
    	}
    	return ifNullValue;
    }
}