package mRUBiS.Observations;

import java.util.LinkedList;
import java.util.List;
import java.io.IOException;
import java.util.HashMap;

import org.eclipse.emf.ecore.resource.Resource;

import de.mdelab.morisia.comparch.Architecture;
import de.mdelab.morisia.comparch.Component;
import de.mdelab.morisia.comparch.Issue;
import de.mdelab.morisia.comparch.Tenant;
import de.mdelab.morisia.selfhealing.ArchitectureUtilCal;
import de.mdelab.morisia.selfhealing.EnvSetUp;
import de.mdelab.sdm.interpreter.core.SDMException;

public class Observations {
	
	static Architecture mRubis;
	
	public static void main(String[] args) throws SDMException, IOException {
		Observations.initializeMRubisInstance();
		Observations.executionLoop();
	}
	
	private static void initializeMRubisInstance() {
		
		if (mRubis == null) {
			Resource architectureResource = EnvSetUp
					
					.loadFreshInstance("model/enriched/mRUBiS-1shop_enriched.comparch");
																								
																								

			Architecture architecture = (Architecture) architectureResource.getContents().get(0);
			
			mRubis = architecture;
		}
		
	}
	
	private static void executionLoop() {
		// inject failures
	}
	
	// TODO:
	
	// one static method per python method
	
	public static String getComponentsUtility(){
		
		
		ArchitectureUtilCal.computeOverallUtility(mRubis);
		
		for (Tenant shop : mRubis.getTenants())
		{ArchitectureUtilCal.computeShopUtility(shop);
		
			shop.getName();
			shop.getCriticality();
			shop.getPerformance();
			shop.getRequest();
		
			HashMap<String, HashMap<String, HashMap<String, Double>>> shopMap = new HashMap<String, HashMap<String, HashMap<String, Double>>>();
			HashMap<String, HashMap<String, Double>> componentMap = new HashMap<String, HashMap<String, Double>>();
			shopMap.put(shop.getName(), componentMap);
			
		for ( Component component : shop.getComponents())
		{    
			
			HashMap<String, Double> parameterMap = new HashMap<String, Double>();
			
			parameterMap.put("adt", component.getADT());
			parameterMap.put("connectivity", new Double(component.getProvidedInterfaces().size() + component.getRequiredInterfaces().size()));
			parameterMap.put("importance", component.getTenant().getImportance());
			parameterMap.put("reliability", component.getType().getReliability());
			parameterMap.put("criticality", component.getType().getCriticality());
			parameterMap.put("request", component.getRequest());
			parameterMap.put("sat_point", component.getType().getSatPoint());
			parameterMap.put("replica", component.getInUseReplica());
			parameterMap.put("perf_max", component.getType().getPerformanceMax());
			parameterMap.put("component_utility", ArchitectureUtilCal.computeComponentUtility(component));
			
			componentMap.put(component.getUid() , parameterMap);
			
		}
		
		//Gson gson = new Gson(); 
		//String json = gson.toJson(myObject);
		
		// https://stackoverflow.com/questions/12155800/how-to-convert-hashmap-to-json-object-in-java
		String json = new ObjectMapper().writeValueAsString(shopMap);
		
		return json;
		
		
		}
		
		
		List<Issue> allIssues = new LinkedList<>();
		allIssues.addAll(mRubis.getAnnotations().getIssues());
		for (Issue issue : allIssues)
		{issue.getAffectedComponent();
		issue.getUtilityDrop();
		issue.getHandledBy();
		issue.getHandledBy().get(0).getCosts();}

}
}




