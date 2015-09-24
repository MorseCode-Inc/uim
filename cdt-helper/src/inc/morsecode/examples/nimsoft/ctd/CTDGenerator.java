package inc.morsecode.examples.nimsoft.ctd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;

import inc.morsecode.examples.nimsoft.probe.pfprobe02.pfprobe02;

import com.nimsoft.ids.ctd.base.CtdEntity;
import com.nimsoft.ids.ctd.base.interfaces.Typeable;
import com.nimsoft.ids.ctd.extension.CtdComputerSystem;
import com.nimsoft.ids.ctd.extension.CtdMemory;
import com.nimsoft.ids.ctd.extension.CtdProcessor;
import com.nimsoft.ids.ctd.extension.CtdVirtualSystem;
import com.nimsoft.ids.ctd.graph.CtdGraphBase;
import com.nimsoft.ids.ctd.graph.CtdGraphScheme;
import com.nimsoft.ids.ctd.template.CtdChildRelationshipInfo;
import com.nimsoft.ids.ctd.template.CtdEntityInfo;
import com.nimsoft.ids.ctd.template.CtdTemplateDefinition;
import com.nimsoft.ids.ctd.template.types.CtdRelationshipCardinality;
import com.nimsoft.ids.ctd.ui.types.CtdTreeIconType;
import com.nimsoft.nimbus.NimException;
import com.nimsoft.pf.common.ctd.FilterBuilder;
import com.nimsoft.pf.common.ctd.template.TemplateDefinitionCreator;
import com.nimsoft.pf.common.log.Log;
import com.nimsoft.pf.common.pom.MvnPomVersion;
import com.nimsoft.probe.framework.devkit.template.AbstractTemplateDefinitionCreator;
import com.nimsoft.probe.framework.genprobe.cfg.GenMonitorType;

/**
 * This demonstrates how to generate a template definition. Creating a template definition
 * involved the following steps:
 * </p>
 * Understand the inventory model for the probe which you wish to create a template definition for.
 * </p>
 * Create a {@code GenMonitorType} Enum's to represent each type in your probes inventory model.
 * </p>
 * Define 2 {@code EnumMap's}. One will map your {@code GenMonitorType's} to the label you will see in the ui,
 * and the second will represent parent/child relationships in your inventory.
 * </p>
 * Implement the abstract methods {@code AbstractTemplateDefinitionCreator.getTypeToLabel()} and
 * {@code AbstractTemplateDefinitionCreator.getTypeToChildType()} so the framework can obtain access
 * to our mappings.
 * </p>
 * Implement the abstract method {@code AbstractTemplateDefinitionCreator.createEntityInfos()} to build 
 * a list of {@code CtdEntityInfo} objects that represent your probe inventory model. Here you also define
 * any filters you wish to support.
 * </p> 
 * Define a {@code main()} method that will be invoked at build time from a Maven plugin. In this main method you 
 * should to the following:
 * 1: Create an instance of this class.
 * 2: Invoke the method {@code createProbeGraph()} to generate your {@code CtdGraphBase}
 * 3: Pass the generated {@code CtdGraphBase} to the method {@code createTemplate()} to create your {@code CtdTemplateDefinition}
 * 4: And finally, pass the {@code CtdTemplateDefinition} to the {@code outputTemplate()} method to complete the generation.
 *
 */
public class CTDGenerator extends AbstractTemplateDefinitionCreator<GenMonitorType>{

    // private GenMonitorType myHostType;
    // private GenMonitorType myVMType;
    // private GenMonitorType myVMCpuType;
    // private GenMonitorType myVMMemoryType;
    private static String HELP_TEXT = "Select monitors to include in template.";
    
    /**
     * Parent/child structure of our inventory types
     */
    private HashMap<CTDElement, CTDElement> myTypeToChildMap;
    
    public CTDGenerator(String buildDirectory, String outputDirectory) throws IOException, NimException{
        super();
        // Initialize the graph helper in the parent class 
        setGraphHelper();
        
        // Initialize model information that will be used to generate the template
        // Because the template definition creator class is not used when the probe 
        // is actually running, but rather when the probe is built, we must manually
        // populate EnumMaps below 
        
        // Initialize Generic Monitor type Enums that will be used to model
        // parent child relationships of the probes inventory.
         
        // Initialize the 2 maps we must populate
        myTypeToChildMap = new HashMap<CTDElement, CTDElement>();
        
        
        String name= "Host";
        String label= "Host";
        CTDElement myHostType = define(name, label, null,  CtdComputerSystem.CTD_ELEMENT_TYPE, CtdTreeIconType.SERVER, CtdRelationshipCardinality.MANY);
        CTDElement myVMType = define("VM", "VM", myHostType, CtdVirtualSystem.CTD_ELEMENT_TYPE, CtdTreeIconType.VM_ACTIVE, CtdRelationshipCardinality.MANY);
        CTDElement myVMCpuType = define("VMCPU", "VM CPU", myVMType, CtdVirtualSystem.CTD_ELEMENT_TYPE, CtdTreeIconType.VM_ACTIVE, CtdRelationshipCardinality.SINGLE);
        CTDElement myVMMemoryType = define("VMMemory", "VM Memory", myVMType, CtdVirtualSystem.CTD_ELEMENT_TYPE, CtdTreeIconType.VM_ACTIVE, CtdRelationshipCardinality.SINGLE);
        CTDElement myVMVirtualDisk = define("VirtualDisk", "VM VirtualDisk", myVMType, CtdVirtualSystem.CTD_ELEMENT_TYPE, CtdTreeIconType.VM_ACTIVE, CtdRelationshipCardinality.SINGLE);
        
        
        
    }

	private CTDElement define(String name, String label, CTDElement parent, String elementType, CtdTreeIconType icon, CtdRelationshipCardinality cardinality) throws NimException {
		
		CTDElement element= new CTDElement(name, label, parent, elementType, icon, cardinality);
		
		if (parent != null) {
			myTypeToChildMap.put(parent, element);
		}
		
		return element;
	}

    public static void main(String[] args) throws Exception{
    	
        Log.always(String.format("Entering " + pfprobe02.PROBE_NAME + " template definition creation with args %s", Arrays.deepToString(args)));
        
        try{
            // determine directories or use defaults
            final boolean argsExist = args.length > 0;
            final String outputDirectory = !argsExist || args[0] == null ? "target" : args[0];
            final String buildDirectory = !(args.length > 1) || args[1] == null ? "." : args[1];
            
            CTDGenerator definitionCreator = new CTDGenerator(buildDirectory, outputDirectory);
            CtdGraphBase graph = definitionCreator.createProbeGraph(buildDirectory);
            final CtdTemplateDefinition template = definitionCreator.createTemplate(graph);
            TemplateDefinitionCreator.outputTemplate(template, outputDirectory);
            
        }catch (Exception e) {
            Log.always("Problem generating template definition", e);
            throw e;
        }
        Log.always(String.format("Completed " + pfprobe02.PROBE_NAME + " template definition creation with args %s", Arrays.deepToString(args)));
    }
    
    @Override
    public void addHelpContentToScheme(CtdGraphScheme scheme) {
    	
    	for (CTDElement element : myTypeToChildMap.keySet()) {
    		
    		GenMonitorType type= element.getType();
    		
    		addHelpContent(scheme, type.getName() +" Help", element.getLabel(), element.getHelpText(), type.getName());
    		
    	}
    }
    
    /**
     * We must override the TemplateDefinitionCreator.createEntityInfos() method.
     * Here is where we create a list of CtdEntityInfo objects, each representing a type name
     * of an item within our probes inventory. 
     */
    @Override
    public List<CtdEntityInfo> createEntityInfos() {
        // Construct CTD entity objects for each of our types
        CtdEntityInfo profile = getProfileEntity();
        CtdEntityInfo resource = getResourceEntity();
        
        // Add all the CTD entity object we created to the return data
        List<CtdEntityInfo> entityInfos = new ArrayList<>();
        entityInfos.add(profile);
        entityInfos.add(resource);
        
        CTDElement root= null;
        
        for (CTDElement element : myTypeToChildMap.keySet()) {
        	CtdEntityInfo info= newCtdEntityInfo(element, element.getType(), element.getElementType(), element.getIcon());
        	info.addFilterInfo(element.getFilters());
        	
        	if (!element.hasParent()) {
        		// this is a root node
        		resource.addRelationshipInfo(new CtdChildRelationshipInfo(element.getTypeName(), element.getRelationshipCardinality()));
        		root= element;
        	} else {
        		// this is a child
        		CTDElement parent= element.getParent();
        		info.addRelationshipInfo(new CtdChildRelationshipInfo(element.getTypeName(), element.getRelationshipCardinality()));
        	}
        	
        	entityInfos.add(info);
        }
        
        return entityInfos;
        
        /*
        CtdEntityInfo myHostEntityInfo = newCtdEntityInfo(myHostType, CtdComputerSystem.CTD_ELEMENT_TYPE, CtdTreeIconType.SERVER);
        CtdEntityInfo myVMEntityInfo = newCtdEntityInfo(myVMType, CtdVirtualSystem.CTD_ELEMENT_TYPE, CtdTreeIconType.VM_ACTIVE);
        CtdEntityInfo myVMCpuEntityInfo = newCtdEntityInfo(myVMCpuType, CtdProcessor.CTD_ELEMENT_TYPE, CtdTreeIconType.CPU);
        CtdEntityInfo myMemoryEntityInfo = newCtdEntityInfo(myVMMemoryType, CtdMemory.CTD_ELEMENT_TYPE, CtdTreeIconType.MEMORY);
        
        // create relationships that model our inventory structure:
        resource.addRelationshipInfo(new CtdChildRelationshipInfo(myHostType.getName(), CtdRelationshipCardinality.MANY));
        myHostEntityInfo.addRelationshipInfo(new CtdChildRelationshipInfo(myVMType.getName(), CtdRelationshipCardinality.MANY));
        myVMEntityInfo.addRelationshipInfo(new CtdChildRelationshipInfo(myVMCpuType.getName()));
        myVMEntityInfo.addRelationshipInfo(new CtdChildRelationshipInfo(myVMMemoryType.getName()));
        
        // Specify the filters that will be available on the MyHost object
        myHostEntityInfo.addFilterInfo(FilterBuilder.getLabelFilter(),
                FilterBuilder.getOperationalState(),
                FilterBuilder.getPrimaryDnsFilter(),
                FilterBuilder.getPrimaryIPv4Filter(),
                FilterBuilder.getPrimaryIPv6Filter());
        
        // Specify the filters that will be available on the MyVM object
        myVMEntityInfo.addFilterInfo(
                FilterBuilder.getComputerNameFilter(),
                FilterBuilder.getLabelFilter(),
                FilterBuilder.getOperationalState(),
                FilterBuilder.getPrimaryDnsFilter(),
                FilterBuilder.getPrimaryIPv4Filter(),
                FilterBuilder.getPrimaryIPv6Filter()
        );
        
        // Add all the CTD entity object we created to the return data
        List<CtdEntityInfo> entityInfos = new ArrayList<>();
        entityInfos.add(profile);
        entityInfos.add(resource);
        entityInfos.add(myHostEntityInfo);
        entityInfos.add(myVMEntityInfo);
        entityInfos.add(myVMCpuEntityInfo);
        entityInfos.add(myMemoryEntityInfo);
        
        return entityInfos;
        */
    }
    
    /**
     * Factory method for creating a new instance of CtdEntityInfo
     * based on the passed in GenMonitorType, Element Type, and Icon 
     * @param element 
     */
    private CtdEntityInfo newCtdEntityInfo(CTDElement element, GenMonitorType myType, String CTD_ELEMENT_TYPE, CtdTreeIconType icon){
        CtdEntityInfo entityInfo = new CtdEntityInfo(myType.getName(), CTD_ELEMENT_TYPE, element.getLabel());
        entityInfo.setTreeIconOverride(icon);
        entityInfo.setTableDisplayOverride(tableDisplay);
        return entityInfo;
    }
    
    /**
     * Abstract method from AbstractTemplateDefinitionCreator
     * we must implement
     */
    @Override
    protected String getProbeName() {
        return pfprobe02.PROBE_NAME;
        
    }

    /**
     * Abstract method from AbstractTemplateDefinitionCreator
     * we must implement
     */
    @Override
    protected String getProbeLabel() {
        return getProbeName() + '-' + MvnPomVersion.getVersionFromPom();
    }

    /**
     * Abstract method from AbstractTemplateDefinitionCreator
     * we must implement this as the framework needs access to our 
     * type to label mapping to complete the template generation.
     */
    @Override
    protected EnumMap<GenMonitorType, String> getTypeToLabel() {
    	EnumMap<GenMonitorType, String> map= new EnumMap<GenMonitorType, String>(GenMonitorType.class);
    	
    	for (CTDElement element : myTypeToChildMap.keySet()) {
    		map.put(element.getType(), element.getLabel());
    	}
    	
        return map;
    }

    /**
     * Abstract method from AbstractTemplateDefinitionCreator
     * we must implement this as the framework needs access to our 
     * parent/child mapping to complete the template generation.
     */
    @Override
    protected EnumMap<GenMonitorType, GenMonitorType> getTypeToChildType() {
    	EnumMap<GenMonitorType, GenMonitorType> map= new EnumMap<GenMonitorType, GenMonitorType>(GenMonitorType.class);
    	
    	for (CTDElement element : myTypeToChildMap.keySet()) {
    		if (element.hasParent()) {
    			map.put(element.getParent().getType(), element.getType());
    		}
    	}
    	
        return map;
    }

}
