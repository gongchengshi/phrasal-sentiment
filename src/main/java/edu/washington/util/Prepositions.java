package edu.washington.util;

import java.util.ArrayList;
import java.util.List;

public class Prepositions {

	
	private static List<String> preps = new ArrayList<String>();
	static{
		preps.add("abroad");
		preps.add("about");
		preps.add("above");
		preps.add("across");
		preps.add("after");
		preps.add("against");
		preps.add("along");
		preps.add("amid");
		preps.add("among");
		preps.add("anti");
		preps.add("around");
		preps.add("as");
		preps.add("at");
		preps.add("before");
		preps.add("behind");
		preps.add("below");
		preps.add("beneath");
		preps.add("beside");
		preps.add("besides");
		preps.add("between");
		preps.add("beyond");
		preps.add("but");
		preps.add("by");
		preps.add("concerning");
		preps.add("considering");
		preps.add("despite");
		preps.add("down");
		preps.add("during");
		preps.add("except");
		preps.add("excepting");
		preps.add("excluding");
		preps.add("following");
		preps.add("for");
		preps.add("from");
		preps.add("in");
		preps.add("inside");
		preps.add("into");
		preps.add("like");
		preps.add("minus");
		preps.add("near");
		preps.add("of");
		preps.add("off");
		preps.add("on");
		preps.add("onto");
		preps.add("opposite");
		preps.add("outside");
		preps.add("over");
		preps.add("past");
		preps.add("per");
		preps.add("plus");
		preps.add("regarding");
		preps.add("round");
		preps.add("save");
		preps.add("since");
		preps.add("than");
		preps.add("through");
		preps.add("to");
		preps.add("toward");
		preps.add("towards");
		preps.add("under");
		preps.add("underneath");
		preps.add("unlike");
		preps.add("until");
		preps.add("up");
		preps.add("upon");
		preps.add("versus");
		preps.add("via");
		preps.add("with");
		preps.add("within");
		preps.add("without");	
		}
	
	public static boolean isPrep(String prep){
		return preps.contains(prep.toLowerCase().trim());
	}
}
