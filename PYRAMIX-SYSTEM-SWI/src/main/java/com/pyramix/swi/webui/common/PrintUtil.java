package com.pyramix.swi.webui.common;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.Clients;

public class PrintUtil {

	public static void print(Component comp) {
		
		print(comp, "/system/common/template.zul", 
			"/css/style_print.css");
	}
	
	public static void print(Component comp, String uri, String cssuri) {
		
        String script = "zkprint('" + comp.getUuid() + "', '" + uri + "'";
        if (cssuri != null) {
            script += ", '" + cssuri + "');";
        } else {
            script += ");";
        }
                
        Clients.evalJavaScript(script);
        // Clients.print();
	}
	
}
