package inc.morsecode.examples.nimsoft.ctd;

/**
 * 
 */
import java.util.ArrayList;

import com.nimsoft.ids.ctd.base.interfaces.Typeable;
import com.nimsoft.ids.ctd.template.CtdChildRelationshipInfo;
import com.nimsoft.ids.ctd.template.CtdFilterInfo;
import com.nimsoft.ids.ctd.template.types.CtdRelationshipCardinality;
import com.nimsoft.ids.ctd.ui.types.CtdTreeIconType;
import com.nimsoft.nimbus.NimException;
import com.nimsoft.pf.common.ctd.FilterBuilder;
import com.nimsoft.probe.framework.genprobe.cfg.GenMonitorType;

/**
 * (c) MorseCode Incorporated 2015
 *
 *
 */
public class CTDElement {
	

	private GenMonitorType type;
	private String label;
	private String elementType;
	private CtdTreeIconType icon;
	private CTDElement parent;
	private ArrayList<CtdFilterInfo> filters= new ArrayList<CtdFilterInfo>();
	private CtdRelationshipCardinality cardinality;
	
	
	public CTDElement(String name, String label, CTDElement parent, String elementType, CtdTreeIconType icon, CtdRelationshipCardinality carinality) throws NimException {
		
		this.parent= parent;
		this.type= GenMonitorType.getEnumOrAddAtRuntime(name);
		
		this.label= label;
		this.elementType= elementType;
		this.icon= icon;
		this.cardinality= carinality;
		
        add(FilterBuilder.getLabelFilter());
	}
	
	

	private void add(CtdFilterInfo filter) {
		if (!filters.contains(filter)) {
			this.filters.add(filter);
		}
	}


	public CtdTreeIconType getIcon() {
		return icon;
	}
	
	public String getElementType() {
		return elementType;
	}
	
	public String getLabel() {
		return label;
	}
	
	public GenMonitorType getType() {
		return type;
	}
	
	public String getTypeName() {
		return type.getName();
	}
	
	public CTDElement getParent() {
		return parent;
	}

	public boolean hasParent() {
		return parent != null;
	}

	public String getHelpText() {
		return "No Help Defined.";
	}
	
	public CtdFilterInfo[] getFilters() {
		return filters.toArray(new CtdFilterInfo[] {});
	}


	public CtdRelationshipCardinality getRelationshipCardinality() {
		return cardinality;
	}
}
