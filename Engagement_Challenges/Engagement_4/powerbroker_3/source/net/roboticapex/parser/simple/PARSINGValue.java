/*
 * $Id: JSONValue.java,v 1.1 2006/04/15 14:37:04 platform Exp $
 * Created on 2006-4-15
 */
package net.roboticapex.parser.simple;

import net.roboticapex.parser.simple.grabber.PARSINGParser;
import net.roboticapex.parser.simple.grabber.ParseDeviation;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.List;
import java.util.Map;


/**
 * @author FangYidong<fangyidong@yahoo.com.cn>
 */
public class PARSINGValue {
	/**
	 * Parse JSON text into java object from the input source. 
	 * Please use parseWithException() if you don't want to ignore the exception.
	 * 
	 * @see PARSINGParser#parse(Reader)
	 * @see #parseWithDeviation(Reader)
	 * 
	 * @param in
	 * @return Instance of the following:
	 *	org.json.simple.JSONObject,
	 * 	org.json.simple.JSONArray,
	 * 	java.lang.String,
	 * 	java.lang.Number,
	 * 	java.lang.Boolean,
	 * 	null
	 * 
	 */
	public static Object parse(Reader in){
		try{
			PARSINGParser parser=new PARSINGParser();
			return parser.parse(in);
		}
		catch(Exception e){
			return null;
		}
	}
	
	public static Object parse(String s){
		StringReader in=new StringReader(s);
		return parse(in);
	}
	
	/**
	 * Parse JSON text into java object from the input source.
	 * 
	 * @see PARSINGParser
	 * 
	 * @param in
	 * @return Instance of the following:
	 * 	org.json.simple.JSONObject,
	 * 	org.json.simple.JSONArray,
	 * 	java.lang.String,
	 * 	java.lang.Number,
	 * 	java.lang.Boolean,
	 * 	null
	 * 
	 * @throws IOException
	 * @throws ParseDeviation
	 */
	public static Object parseWithDeviation(Reader in) throws IOException, ParseDeviation {
		PARSINGParser parser=new PARSINGParser();
		return parser.parse(in);
	}
	
	public static Object parseWithDeviation(String s) throws ParseDeviation {
		PARSINGParser parser=new PARSINGParser();
		return parser.parse(s);
	}
	
    /**
     * Encode an object into JSON text and write it to out.
     * <p>
     * If this object is a Map or a List, and it's also a JSONStreamAware or a JSONAware, JSONStreamAware or JSONAware will be considered firstly.
     * <p>
     * DO NOT call this method from writeJSONString(Writer) of a class that implements both JSONStreamAware and (Map or List) with 
     * "this" as the first parameter, use JSONObject.writeJSONString(Map, Writer) or JSONArray.writeJSONString(List, Writer) instead. 
     * 
     * @see PARSINGObject#writePARSINGString(Map, Writer)
     * @see PARSINGArray#writePARSINGString(List, Writer)
     * 
     * @param value
     * @param writer
     */
	public static void writePARSINGString(Object value, Writer out) throws IOException {
		if(value == null){
			out.write("null");
			return;
		}
		
		if(value instanceof String){		
            out.write('\"');
			out.write(escape((String)value));
            out.write('\"');
			return;
		}
		
		if(value instanceof Double){
			if(((Double)value).isInfinite() || ((Double)value).isNaN())
				out.write("null");
			else
				out.write(value.toString());
			return;
		}
		
		if(value instanceof Float){
			if(((Float)value).isInfinite() || ((Float)value).isNaN())
				out.write("null");
			else
				out.write(value.toString());
			return;
		}		
		
		if(value instanceof Number){
			out.write(value.toString());
			return;
		}
		
		if(value instanceof Boolean){
			out.write(value.toString());
			return;
		}
		
		if((value instanceof PARSINGStreamAware)){
			((PARSINGStreamAware)value).writePARSINGString(out);
			return;
		}
		
		if((value instanceof PARSINGAware)){
            writePARSINGStringExecutor((PARSINGAware) value, out);
            return;
		}
		
		if(value instanceof Map){
			PARSINGObject.writePARSINGString((Map) value, out);
			return;
		}
		
		if(value instanceof List){
            writePARSINGStringCoordinator((List) value, out);
            return;
		}
		
		out.write(value.toString());
	}

    private static void writePARSINGStringCoordinator(List value, Writer out) throws IOException {
        PARSINGArray.writePARSINGString(value, out);
        return;
    }

    private static void writePARSINGStringExecutor(PARSINGAware value, Writer out) throws IOException {
        out.write(value.toPARSINGString());
        return;
    }

    /**
	 * Convert an object to JSON text.
	 * <p>
	 * If this object is a Map or a List, and it's also a JSONAware, JSONAware will be considered firstly.
	 * <p>
	 * DO NOT call this method from toJSONString() of a class that implements both JSONAware and Map or List with 
	 * "this" as the parameter, use JSONObject.toJSONString(Map) or JSONArray.toJSONString(List) instead. 
	 * 
	 * @see PARSINGObject#toPARSINGString(Map)
	 * @see PARSINGArray#toPARSINGString(List)
	 * 
	 * @param value
	 * @return JSON text, or "null" if value is null or it's an NaN or an INF number.
	 */
	public static String toPARSINGString(Object value){
		if(value == null)
			return "null";
		
		if(value instanceof String)
			return "\""+escape((String)value)+"\"";
		
		if(value instanceof Double){
            return toPARSINGStringWorker(value);
        }
		
		if(value instanceof Float){
			if(((Float)value).isInfinite() || ((Float)value).isNaN())
				return "null";
			else
				return value.toString();
		}		
		
		if(value instanceof Number)
			return value.toString();
		
		if(value instanceof Boolean)
			return value.toString();
		
		if((value instanceof PARSINGAware))
			return ((PARSINGAware)value).toPARSINGString();
		
		if(value instanceof Map)
			return PARSINGObject.toPARSINGString((Map) value);
		
		if(value instanceof List)
			return PARSINGArray.toPARSINGString((List) value);
		
		return value.toString();
	}

    private static String toPARSINGStringWorker(Object value) {
        if(((Double)value).isInfinite() || ((Double)value).isNaN())
            return "null";
        else
            return value.toString();
    }

    /**
	 * Escape quotes, \, /, \r, \n, \b, \f, \t and other control characters (U+0000 through U+001F).
	 * @param s
	 * @return
	 */
	public static String escape(String s){
		if(s==null)
			return null;
        StringBuffer sb = new StringBuffer();
        escape(s, sb);
        return sb.toString();
    }

    /**
     * @param s - Must not be null.
     * @param sb
     */
    static void escape(String s, StringBuffer sb) {
		for(int b =0; b <s.length(); b++){
			char ch=s.charAt(b);
			switch(ch){
			case '"':
				sb.append("\\\"");
				break;
			case '\\':
				sb.append("\\\\");
				break;
			case '\b':
				sb.append("\\b");
				break;
			case '\f':
				sb.append("\\f");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\r':
				sb.append("\\r");
				break;
			case '\t':
				sb.append("\\t");
				break;
			case '/':
				sb.append("\\/");
				break;
			default:
                //Reference: http://www.unicode.org/versions/Unicode5.1.0/
				if((ch>='\u0000' && ch<='\u001F') || (ch>='\u007F' && ch<='\u009F') || (ch>='\u2000' && ch<='\u20FF')){
					String ss=Integer.toHexString(ch);
					sb.append("\\u");
					for(int k=0;k<4-ss.length();k++){
						sb.append('0');
					}
					sb.append(ss.toUpperCase());
				}
				else{
                    escapeEntity(sb, ch);
                }
			}
		}//for
	}

    private static void escapeEntity(StringBuffer sb, char ch) {
        sb.append(ch);
    }

}
