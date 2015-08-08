import com.nimsoft.nimbus.NimException;
import com.nimsoft.nimbus.NimProbe;
import com.nimsoft.nimbus.NimProbeBase;


public abstract class CustomProbeSkeleton extends NimProbe {

	
	protected CustomProbeSkeleton(String name, String version, String company, String[] args) throws NimException {
		super(name, version, company, args);
	}
	

}
