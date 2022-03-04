//==================================================
//  Copyright 2020 Siemens Digital Industries Software
//==================================================


package com.teamcenter.cloning;

import java.util.Map;

import com.teamcenter.clientx.AppXSession;
import com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException;
import com.teamcenter.services.strong.cad._2007_01.StructureManagement.CloseBOMWindowsResponse;
import com.teamcenter.services.strong.cad._2007_01.StructureManagement.CreateBOMWindowsResponse;
import com.teamcenter.services.strong.cad._2007_01.StructureManagement.GetRevisionRulesResponse;
import com.teamcenter.services.strong.cad._2007_01.StructureManagement.RevisionRuleInfo;
import com.teamcenter.services.strong.cad._2019_06.StructureManagement.CreateWindowsInfo3;
import com.teamcenter.services.strong.core.DataManagementService;
import com.teamcenter.services.strong.core.SessionService;
import com.teamcenter.services.strong.core._2008_06.DataManagement.AttrInfo;
import com.teamcenter.services.strong.core._2008_06.DataManagement.DatasetFilter;
import com.teamcenter.services.strong.core._2008_06.DataManagement.DatasetInfo;
import com.teamcenter.services.strong.core._2008_06.DataManagement.GetItemAndRelatedObjectsInfo;
import com.teamcenter.services.strong.core._2008_06.DataManagement.GetItemAndRelatedObjectsItemOutput;
import com.teamcenter.services.strong.core._2008_06.DataManagement.GetItemAndRelatedObjectsResponse;
import com.teamcenter.services.strong.core._2008_06.DataManagement.ItemInfo;
import com.teamcenter.services.strong.core._2008_06.DataManagement.RevInfo;
import com.teamcenter.services.strong.core._2008_06.DataManagement.RevisionOutput;
import com.teamcenter.services.strong.manufacturing.StructureManagementService;
import com.teamcenter.services.strong.manufacturing._2018_11.StructureManagement.AdditionalInfo2;
import com.teamcenter.services.strong.manufacturing._2018_11.StructureManagement.CopyRecursivelyConfigurationInfo;
import com.teamcenter.services.strong.manufacturing._2018_11.StructureManagement.CopyRecursivelyInputInfo;
import com.teamcenter.services.strong.manufacturing._2018_11.StructureManagement.CopyRecursivelyNewObjectInfo;
import com.teamcenter.services.strong.manufacturing._2018_11.StructureManagement.CopyRecursivelyResponse;
import com.teamcenter.services.strong.manufacturing._2018_11.StructureManagement.CopyRecursivelyTemplateInfo;
//Include the Saved Query Service Interface
import com.teamcenter.services.strong.query.SavedQueryService;
// Input and output structures for the service operations
// Note: the different namespace from the service interface
import com.teamcenter.services.strong.query._2006_03.SavedQuery.GetSavedQueriesResponse;
import com.teamcenter.services.strong.query._2007_09.SavedQuery.QueryResults;
import com.teamcenter.services.strong.query._2007_09.SavedQuery.SavedQueriesResponse;
import com.teamcenter.services.strong.query._2008_06.SavedQuery.QueryInput;
import com.teamcenter.services.strong.structuremanagement.VariantManagementService;
import com.teamcenter.services.strong.structuremanagement._2013_05.VariantManagement.GetBOMVariantRuleInput;
import com.teamcenter.services.strong.structuremanagement._2019_06.VariantManagement.BOMVariantRuleContents2;
import com.teamcenter.services.strong.structuremanagement._2019_06.VariantManagement.BOMVariantRuleOutput2;
import com.teamcenter.services.strong.structuremanagement._2019_06.VariantManagement.BOMVariantRulesResponse2;
import com.teamcenter.soa.client.Connection;
import com.teamcenter.soa.client.model.ErrorStack;
import com.teamcenter.soa.client.model.ModelObject;
import com.teamcenter.soa.client.model.Property;
import com.teamcenter.soa.client.model.ServiceData;
import com.teamcenter.soa.client.model.strong.BOMWindow;
import com.teamcenter.soa.client.model.strong.ImanQuery;
import com.teamcenter.soa.client.model.strong.ItemRevision;
import com.teamcenter.soa.client.model.strong.PSBOMView;
import com.teamcenter.soa.client.model.strong.RevisionRule;
import com.teamcenter.soa.common.ObjectPropertyPolicy;
import com.teamcenter.soa.exceptions.NotLoadedException;

public class CloneTestUtils
{
    private static Connection m_connection = null;
    private static com.teamcenter.services.strong.cad.StructureManagementService m_smService;
    private static com.teamcenter.services.strong.core.DataManagementService m_dmService;
    private static com.teamcenter.services.internal.strong.core.DataManagementService m_IntDMService;
    private static com.teamcenter.services.strong.bom.StructureManagementService m_smBOMService;
    private static VariantManagementService variantService;
    ItemRevision itemRev = null;
    /**
     * Constructor
     * @param connection
     */
     static {
    	
        com.teamcenter.soa.client.model.StrongObjectFactoryCpd.init();
        com.teamcenter.soa.client.model.StrongObjectFactoryAppmodel.init();
        com.teamcenter.soa.client.model.StrongObjectFactory.init();
        com.teamcenter.soa.client.model.StrongObjectFactoryLibrarymgmt.init();
        m_connection = AppXSession.getConnection();
        m_smService = com.teamcenter.services.strong.cad.StructureManagementService.getService(m_connection);
        m_dmService = com.teamcenter.services.strong.core.DataManagementService.getService(m_connection);
        m_smBOMService = com.teamcenter.services.strong.bom.StructureManagementService.getService( m_connection );
        variantService = VariantManagementService.getService(m_connection);
        setObjectPolicy();
    }

    public static ItemRevision findItemRevision(String itemId, String revId) {
        ItemRevision output = null;

        try {

            AttrInfo[] attrInfo = new AttrInfo[1];
            attrInfo[0] = new AttrInfo();
            attrInfo[0].name = "item_id";
            attrInfo[0].value = itemId;

            ItemInfo itemInfo = new ItemInfo();
            itemInfo.ids = attrInfo;
            itemInfo.clientId = "itemInfo1";
            itemInfo.useIdFirst=true;


            RevInfo revInfo = new RevInfo();
            revInfo.id = revId;
            revInfo.nRevs = 1;
            revInfo.clientId="revInfo1";
            revInfo.useIdFirst=true;
            revInfo.processing="Ids";


    		DatasetInfo dsInfo = new DatasetInfo();
    		dsInfo.clientId="dsInfo1";

    		dsInfo.filter = new  DatasetFilter();
    		dsInfo.filter.processing = "None";
            GetItemAndRelatedObjectsInfo [] itemAndRelObjInfo = new 
                    GetItemAndRelatedObjectsInfo[1];
            itemAndRelObjInfo[0] = new GetItemAndRelatedObjectsInfo();
            itemAndRelObjInfo[0].itemInfo = itemInfo;
            itemAndRelObjInfo[0].revInfo = revInfo;
            itemAndRelObjInfo[0].datasetInfo = dsInfo;
            itemAndRelObjInfo[0].clientId = "itemAndRelObj1";

            GetItemAndRelatedObjectsResponse Resp = 
                    m_dmService.getItemAndRelatedObjects(itemAndRelObjInfo);

            if(!serviceDataError(Resp.serviceData))
            {
                for(GetItemAndRelatedObjectsItemOutput out :
                    Resp.output)
                {
                    try {
                        System.out.println("Item name: " +
                                out.item.get_object_name());

                        for(RevisionOutput revOut :
                            out.itemRevOutput)
                        {
                            System.out.println("ItemRevision name: " + 
                                    revOut.itemRevision.get_object_name());
                            output = revOut.itemRevision;

                        }
                    } catch (NotLoadedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

            }
        } catch (Exception e) {
            throw e;
        }
        return output;
    }

    public static ModelObject cloneStructure(ItemRevision itemRevision, CreateBOMWindowsResponse createEindowResp, String cloningRule) throws Exception {
        ModelObject clonedItemRevision = null;
        AdditionalInfo2 ai = new AdditionalInfo2();
        
        ModelObject[] mo = new ModelObject[1];
        CopyRecursivelyInputInfo[] inputInfo = new CopyRecursivelyInputInfo[1];
        CopyRecursivelyConfigurationInfo cI = new CopyRecursivelyConfigurationInfo();
        cI.additionalInfo = ai;
        cI.copyFutureEffectivities = false;
        cI.copySuppressedLines = false;
        cI.variantRules = getVariantRules(createEindowResp);
        cI.revisionRule = getRevisionRuleByName("Latest Working");
        
        //inputInfo[0].configurationInfo.
        CopyRecursivelyNewObjectInfo newObjectInfo = new CopyRecursivelyNewObjectInfo();
        newObjectInfo.additionalInfo = ai;
        newObjectInfo.newId="PUBU-000999";
        newObjectInfo.newDescription="newObjectInfoTesting";
        newObjectInfo.newName="Cloning Testing";
        newObjectInfo.newrevId = "A";
        newObjectInfo.pasteTarget = null;

        // get Bom Window
        BOMWindow bomWindow = createEindowResp.output[0].bomWindow;
        System.out.println("Topline Uid : " + bomWindow.get_top_line().getUid());
        ModelObject[] referenceWindow = new ModelObject[1];
        referenceWindow[0] = bomWindow;
        CopyRecursivelyTemplateInfo templateInfo = new CopyRecursivelyTemplateInfo();
        templateInfo.additionalInfo = ai;
        templateInfo.cloningRule = cloningRule;
        templateInfo.objectToClone = itemRevision.get_items_tag();
        templateInfo.referenceWindow = referenceWindow;

        inputInfo[0] = new CopyRecursivelyInputInfo();
        inputInfo[0].newObjectInfo = newObjectInfo;
        inputInfo[0].templateInfo = templateInfo;
        inputInfo[0].configurationInfo = cI;

        StructureManagementService m_smService1 = StructureManagementService.getService(m_connection);
        CopyRecursivelyResponse res = null;
        try {
        	res = m_smService1.copyRecursively(inputInfo);
        	System.out.println("error length :" + res.serviceData.sizeOfPartialErrors());
        } catch(Exception e) {
        	throw e;
        }

        if (res != null) {
        	if (!serviceDataError(res.serviceData)) {
        		System.out.println(res.serviceData.sizeOfCreatedObjects());
        		Map< ModelObject, ModelObject> output = res.dataMap;
        		for (Map.Entry<ModelObject, ModelObject> mm:output.entrySet()) {
        			System.out.println(mm.getKey().getUid());
        			System.out.println(mm.getValue().getUid());
        		}
        	}
        }
        return clonedItemRevision;

    }

    /**
     * Use this helper method to create multiple items at one go
     * @param nItems
     * @param itemName
     * @param itemType : If specified as "", then default will be taken, i.e., "Item".
     * @param itemsList : List of Items created
     * @param revsList : List of Revisions created
     * @return
     */
    public boolean checkErrorCode( final ServiceData sData, int errorCode )
    {
        int noPartErrors = sData.sizeOfPartialErrors();
        int[] errorCodes;
        boolean errorFound = false;
        if( noPartErrors > 0 )
        {
            String errorMessage = "";
            for( int i=0; i < noPartErrors; ++i )
            {
                ErrorStack errorStack = sData.getPartialError(i);
                String[] messages = errorStack.getMessages();
                errorCodes = errorStack.getCodes();
                for(int j=0;j < errorCodes.length;j++)
                {
                    errorMessage = errorMessage + messages[j] + "\n";
                    if (errorCodes[j] == errorCode)
                        errorFound = true;
                    System.out.println(errorMessage);
                }
            }
        }
        return errorFound;
    }

    /**
     * Retrieves the revision rule as per input value. It looks for revision
     * rule from all available revision rules in system.
     * @param revRuleName Revision Rule name. If null or empty string passed,
     *  first value from all revision rules is returned.
     * @return Revision Rule object
     * @throws Exception
     */
    public static RevisionRule getRevisionRuleByName( String revRuleName ) throws Exception
    {
        RevisionRule revRule = null;

        GetRevisionRulesResponse ruleResp = m_smService.getRevisionRules();

        RevisionRule retRevRule = null;

        // If an empty or null string passed, return first value
        if(revRuleName == null || revRuleName.length() == 0)
        {
            retRevRule = ruleResp.output.length > 0 ? ruleResp.output[0].revRule : null;
            return retRevRule;
        }

        for(RevisionRuleInfo revRuleInfo : ruleResp.output)
        {
            revRule = revRuleInfo.revRule;

            com.teamcenter.soa.client.model.Property[] objNameProp = getObjectProperties(revRule, new String[] {"object_name"});
            if(objNameProp[0].getDisplayableValue().equals(revRuleName))
            {
                retRevRule = revRule;
                break;
            }
        }

        return retRevRule;
    }


    /**
     * This function loads the property explicitly which are not part of property policy
     * @param object
     * @param properties
     * @return
     * @throws Exception
     */
    public static Property[] getObjectProperties (ModelObject object, String[] properties) throws Exception
    {
        Property[] retVal = new Property[properties.length];

        com.teamcenter.services.strong.core.DataManagementService m_dmService = com.teamcenter.services.strong.core.DataManagementService.getService(m_connection);
        ServiceData sd = m_dmService.getProperties(new ModelObject[]{object}, properties);
        ModelObject modelObject = sd.getPlainObject(0);

        int i = 0;
        for(String prop : properties)
        {
            retVal[i++] = modelObject.getPropertyObject(prop);
        }
        return retVal;
    }

    /**

    /**
     * Creates a BOMWindow
     * @param itemRev Item Revision
     * @return CreateBOMWindowsResponse for newly created bomwindow
     * @throws NotLoadedException 
     */
    public static CreateBOMWindowsResponse createBomWindow( ItemRevision itemRev ) throws NotLoadedException
    {
    	String[] uds = new String[] {itemRev.get_items_tag().getUid(), itemRev.getUid()};
    	m_dmService.loadObjects(uds);
    	//Now create BOMWindow
        CreateWindowsInfo3[] createBOMWindowsInfo = new CreateWindowsInfo3[1];
        createBOMWindowsInfo[0] = new CreateWindowsInfo3();
        createBOMWindowsInfo[0].itemRev = itemRev;
        createBOMWindowsInfo[0].bomView = (PSBOMView) itemRev.get_items_tag().get_bom_view_tags()[0];
        createBOMWindowsInfo[0].item = itemRev.get_items_tag();

        CreateBOMWindowsResponse createBOMWindowsResponse =
                m_smService.createOrReConfigureBOMWindows(createBOMWindowsInfo);
       
        return createBOMWindowsResponse;
    }

    /**
     * Process service data
     * @param serviceData: servic eData
     * @return
     */
    public static boolean serviceDataError(ServiceData serviceData)
    {
        boolean output = false;
        // Loop through one or more of the arrays contained within ServiceData.
        // Service documentation should make it clear which arrays may have data
        for (int i = 0; i < serviceData.sizeOfPartialErrors(); i++)
        {
            ErrorStack errorStack = serviceData.getPartialError(i);
            String[] messages = errorStack.getMessages();
            for (int j = 0; j < messages.length; j++)
            {
                output = true;
                System.out.println(messages[j]);
            }

    }
		return output;
    }
    
    public static CloseBOMWindowsResponse closeBOMWindows( BOMWindow[] bw )
    {
        CloseBOMWindowsResponse closeResp = m_smService.closeBOMWindows(  bw );
        return closeResp;
    }

    /**
     * Perform a simple query of the database
     * @throws Exception 
     *
     */
    public static ModelObject[] queryItems(String itemId, String rev) throws Exception
    {

    	ModelObject[] foundObjs = null;
        ImanQuery query = null;

        // Get the service stub.
        SavedQueryService queryService = SavedQueryService.getService(AppXSession.getConnection());
        DataManagementService dmService= DataManagementService.getService(AppXSession.getConnection());
        try
        {

            // *****************************
            // Execute the service operation
            // *****************************
            GetSavedQueriesResponse savedQueries = queryService.getSavedQueries();


            if (savedQueries.queries.length == 0)
            {
                throw new Exception("There are no saved queries in the system.");
            }

            // Find one called 'Item Name'
            for (int i = 0; i < savedQueries.queries.length; i++)
            {

                if (savedQueries.queries[i].name.equals("Item Revision..."))
                {
                    query = savedQueries.queries[i].query;
                    break;
                }
            }
        }
        catch (ServiceException e)
        {
            System.out.println("GetSavedQueries service request failed.");
            System.out.println(e.getMessage());
            throw e;
        }

        if (query == null)
        {
            System.out.println("There is not an 'Item Name' query.");
            throw new Exception("There is not an 'Item Revision...' query.");
        }

        try
        {
            //Search for all Items, returning a maximum of 25 objects
            QueryInput savedQueryInput[] = new QueryInput[1];
            savedQueryInput[0] = new QueryInput();
            savedQueryInput[0].query = query;
            savedQueryInput[0].maxNumToReturn = 25;
            savedQueryInput[0].limitList = new ModelObject[0];
            savedQueryInput[0].entries = new String[]{"Item ID", "Revision" };
            savedQueryInput[0].values = new String[2];
            savedQueryInput[0].values[0] = itemId;
            savedQueryInput[0].values[1] = rev;

            
            //*****************************
            //Execute the service operation
            //*****************************
            SavedQueriesResponse savedQueryResult = queryService.executeSavedQueries(savedQueryInput);
            QueryResults found = savedQueryResult.arrayOfResults[0]; 
            
            System.out.println("");
            System.out.println("Found Items:");
            
            // Page through the results 10 at a time
            for(int i=0; i< found.objectUIDS.length; i+=10)
            {
                int pageSize = (i+10<found.objectUIDS.length)? 10:found.objectUIDS.length-i;
            
                String[] uids = new String[pageSize];
                for(int j=0; j<pageSize; j++)
                {
                    uids[j]= found.objectUIDS[i+j];
                }
                ServiceData sd = dmService.loadObjects( uids );
                foundObjs = new ModelObject[ sd.sizeOfPlainObjects()];
                for( int k =0; k< sd.sizeOfPlainObjects(); k++)
                {
                    foundObjs[k] = sd.getPlainObject(k);
                }

                AppXSession.printObjects( foundObjs );
            }
        }
        catch (Exception e)
        {
            System.out.println("ExecuteSavedQuery service request failed.");
            System.out.println(e.getMessage());
            throw e;
        }
		return foundObjs;

    }
    
    public static ModelObject[] getVariantRules(CreateBOMWindowsResponse bomwindowResp) throws ServiceException {
    	ModelObject[] variantRules = new ModelObject[1];
    	GetBOMVariantRuleInput ruleInput = new GetBOMVariantRuleInput();
        
        ruleInput.clientId = "ID:" + ruleInput.hashCode();
        ruleInput.svrActionMode = 0; // to get variant rule from window. 
        ruleInput.window = bomwindowResp.output[0].bomWindow;
        
        BOMVariantRulesResponse2 resp = variantService.getBOMVariantRules2
        		(new GetBOMVariantRuleInput[]{ ruleInput });
        
        if(!serviceDataError(resp.serviceData))
        {
        	int i = 0;
            for(BOMVariantRuleOutput2 ruleOutput : resp.variantRuleData)
            {
                for(BOMVariantRuleContents2 ruleContent : ruleOutput.rules)
                {
                	variantRules[i] = ruleContent.variantRule;
                	i++;
                }
            }
        }
		return variantRules;
        
    }
    
    protected static void setObjectPolicy()
    {
        SessionService session = SessionService.getService(AppXSession.getConnection());
        ObjectPropertyPolicy policy = new ObjectPropertyPolicy();

        policy.addType("Item", new String[]{ "bom_view_tags", "revision_list", "object_string" });
        policy.addType("ItemRevision", new String[]{ "items_tag", "object_string" });
        policy.addType("BOMLine", new String[]{ "bl_line_name" });
        policy.addType("BOMWindow", new String[]{ "is_packed_by_default", "top_line" });

        session.setObjectPropertyPolicy(policy);
    }
}
