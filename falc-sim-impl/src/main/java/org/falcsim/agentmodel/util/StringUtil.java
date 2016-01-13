package org.falcsim.agentmodel.util;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.falcsim.agentmodel.domain.Location;


/** Conveniency methods to customize strings
* 
* @author  regioConcept AG
*
*/

public class StringUtil {

	public enum OPT {
		TO_LOWER_CASE, TO_UPPER_CASE, PLAIN
	}
	
	public static final Integer WITH_PAR = 1;
	public static final Integer WITHOUT_PAR = 2;
	private static final String comma = " , ";
	private static final String apos = " ' ";


	static public String fix (Object o){
		if (o == null)
			return " NULL! ";
		else
			return o.toString();
	}

	static private String package1(String str){
		int last = str.lastIndexOf(",");
		if (last<0){
			return str;
		}
		return "("+str.substring(0, last)+")";
	}

	static public String package2(String str){
		if (str.length()>0){
			int last = str.lastIndexOf(",");
			if (last<0){
				return str;
			}
			return str.substring(0, last);			
		}
		return str;
	}

	static public String firstToUpperCase(String s){

		return s.substring(0,1).toUpperCase().concat(s.substring(1));
	}

	static public List<String> sepStringsToList(String s, String separator, String option){

		List<String> outList = new ArrayList<String>();

		switch (OPT.valueOf(option)) {
		case TO_LOWER_CASE:
			s=s.toLowerCase();
			break;
		case TO_UPPER_CASE:
			s=s.toUpperCase();
			break;
		default:
			break;
		}

		int l = s.indexOf(separator);

		while (l>-1){
			String sout = s.substring(0, l);
			outList.add(sout.trim());
			s=s.substring(l+1);
			l = s.indexOf(separator);
		}


		String ls = s.trim();
		if(ls.length()>0)
			outList.add(ls);

		return outList;

	}


	static public String repStr(Integer k, String str){
		String m="";
		for (int i =0;  i<k  ;i++){
			m=m.concat(str);			
		}
		return m;
	}

	static public String packageLocsString(List<Location> locs){

		StringBuilder builder = new StringBuilder();
		for (Location l:locs){
			builder.append(l.getId()).append(comma);
		}

		return package1( builder.toString());
	}

	static public String packageIds(List<Integer> ids){

		StringBuilder builder = new StringBuilder();
		for (Integer id:ids){
			builder.append(id).append(comma);
		}

		return package1( builder.toString());
	}

	static public String packageObjectsToString1(@SuppressWarnings("rawtypes") List objs, Integer opt){

		StringBuilder builder = new StringBuilder();
		String objStr;
		for (Object obj:objs){
			objStr =obj.toString();
			if (obj instanceof String){
				builder.append(apos).append(objStr).append(apos);
			} else {
				builder.append(objStr);
			}
				
			builder.append(comma) ; 
		}

		if ((new Integer(1)).equals(opt))
			return  StringUtil.package1(builder.toString());
		else 
			return  StringUtil.package2(builder.toString());
	}



	static public String packageStringListToString1(List<String> strs, Integer opt){

		StringBuilder builder = new StringBuilder();
		for (String str:strs){
			builder.append(str).append(comma) ; 
		}
		if (WITHOUT_PAR.equals(opt))
			return  StringUtil.package2(builder.toString());
		else
			return  StringUtil.package1(builder.toString());
	}
	
	
	
	public static String readIt(Reader input) throws IOException {
	    StringBuilder sb = new StringBuilder();
	    char[] pillow = new char[4096];
	    int readChars;
	    while ((readChars = input.read(pillow)) >= 0) {
	        sb.append(pillow, 0, readChars);
	    }
	    input.close();
	    return sb.toString();
	}
	
	public static String intArrayToString(int[] a, String separator) {
		int len = a.length;
		if (len == 0){
			return "[]";
		}
		StringBuilder builder = new StringBuilder();
		builder.append("[ ").append(a[0]);
	    if (len > 1) {
	        for (int i=1; i<a.length; i++) {
	        	builder.append(separator).append(a[i]);
	        }
	    }
	    builder.append(" ]");
	    return builder.toString();
	}
	
	public static List<Integer> stringToIntegerList(String input){
		List<Integer> ret = new ArrayList<Integer>();
		String prepared = input.replaceAll(" ", "");
		for(String s : prepared.split(",")){
			ret.add(Integer.valueOf(s));
		}
		return ret;
	}
	
	public static String base642String(String input){
		byte[] dec = input.getBytes(StandardCharsets.UTF_8);
		return new String(Base64.decodeBase64(dec), StandardCharsets.UTF_8); 
	}
}
