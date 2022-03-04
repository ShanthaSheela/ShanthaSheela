//==================================================
//  Copyright 2020 Siemens Digital Industries Software
//==================================================


package com.teamcenter.cloning;


import java.util.HashMap;
import java.util.logging.Logger;

import com.teamcenter.clientx.AppXSession;
import com.teamcenter.services.strong.cad._2007_01.StructureManagement.CreateBOMWindowsResponse;
import com.teamcenter.soa.client.model.ModelObject;
import com.teamcenter.soa.client.model.strong.BOMWindow;
import com.teamcenter.soa.client.model.strong.ItemRevision;


/**
 * This Class contains the main function for the sample client. It executes the MDO related test cases.
 *
 */
public class CloneMain
{

	static Logger log = Logger.getLogger(CloneMain.class.getName());
    /**
     * @param args
     */
    public static void main( String[] args )
    {
        AppXSession session = null;
        try
        {
            if ( args.length > 0 )
            {
                if (args[0].equals( "-help" ) || args[0].equals( "-h" ) )
                {
                    System.out.println( "usage: java -jar -itemId=<Item Id> -rev=< Revision Id>" );
                    System.exit(0);
                }
            }

            // Get optional host information
            String serverHost = "http://172.16.6.4:7001/tc";
           
            HashMap < String, String > argMap = new HashMap<>();
	        for (int x = 0; x < args.length; x++) {
	        	int sepIndex = args[x].indexOf("=");
	        	if (sepIndex != -1) {
	        		String key = args[x].substring(1, sepIndex);
	        		String value = args[x].substring(sepIndex + 1);
	        		log.info("Key : " + key);
	        		log.info("Value : " + value);
	        		argMap.put(key, value);
	        	}
	        }
	        
	        // Get Item Id
	        @SuppressWarnings("null")
			String itemId = argMap.get("itemId");
	        if (itemId == null || itemId.isEmpty()) {
	        	System.out.println( "usage: java -jar -itemId=<Item Id> -rev=< Revision Id>" );
                System.exit(0);
	        }
	        
	        // Get Item Id
	        @SuppressWarnings("null")
			String revId = argMap.get("rev");
	        if (revId == null || revId.isEmpty()) {
	        	System.out.println( "usage: java -jar -itemId=<Item Id> -rev=< Revision Id>" );
                System.exit(0);
	        }
	        
	        String userName = "infodba";
			String password = "infodba";
			String group = "dba";
	        if (serverHost != null && userName != null && password != null) {
	        	String[] credentials = new String[6];
	        	credentials[0] = userName;
	        	credentials[1] = password;
	        	credentials[2] = group;
	        	credentials[3] = "";
	        	credentials[4] = "";
	        	credentials[5] = "";
	        			
		        // Establish a session with the Teamcenter Server
		        session = new AppXSession(serverHost);
		        session.login();
		        log.info("logging into Teamcenter");
	        }
            
	        if (session == null) {
	        	throw new Exception("Unable to log into Teamcenter");
	        }
	        
            ModelObject[] itemRev = CloneTestUtils.queryItems(itemId, revId);
			CreateBOMWindowsResponse cbwr = CloneTestUtils.createBomWindow((ItemRevision) itemRev[0]);
            ModelObject output = CloneTestUtils.cloneStructure((ItemRevision) itemRev[0], cbwr, "Product.Template.Cloning.TPS");
            BOMWindow[] bws = new BOMWindow[1];
            bws[0] = cbwr.output[0].bomWindow;
            CloneTestUtils.closeBOMWindows(bws);
            if( session != null)
            {
                session.logout();
            }
        }
        catch( Exception ex )
        {
            if( session != null)
            {
                session.logout();
            }
            ex.printStackTrace();
        }
    }

    }
