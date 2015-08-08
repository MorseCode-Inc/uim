import com.nimsoft.nimbus.NimException;
import com.nimsoft.nimbus.NimProbe;

/**
 * 
 * 2015 MorseCode Incorporated
 * Company Confidential / Proprietary Information
 *
 * Created: Aug 7, 2015
 * Last Modified: Aug 7, 2015
 *
 */
public abstract class CustomProbeSkeleton extends NimProbe {

	
	protected CustomProbeSkeleton(String name, String version, String company, String[] args) throws NimException {
		super(name, version, company, args);
	}
	

}
