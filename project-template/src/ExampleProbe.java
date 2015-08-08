import com.nimsoft.nimbus.NimConfig;
import com.nimsoft.nimbus.NimException;
import com.nimsoft.nimbus.NimLog;
import com.nimsoft.nimbus.PDS;


public class ExampleProbe extends CustomProbeSkeleton {
	
	public static final String PROBE_NAME= "example_probe";
	public static final String PROBE_VERSION= "1.0";
	public static final String PROBE_MANUFACTURER= "MorseCode Incorporated";
	
	protected NimLog log;
	
	int interval= 300;		// the number of seconds the probe will sleep between calls to execute()

	public ExampleProbe(String[] args) throws NimException {
		super(PROBE_NAME, PROBE_VERSION, PROBE_MANUFACTURER, args);
		// instantiate a NimLog 
		this.log= NimLog.getLogger(this.getClass());
		
		// try to get our configuration file
		PDS config= NimConfig.getInstance().getPDS(false);
		
		// represents the <setup> section in the configuration file
		PDS setup= (PDS)config.get("setup");
	}
	
	
	/**
	 * 
	 * @return true if the probe is ready to execute.
	 */
	public boolean startup() {
		
		int interval= 
		registerCallbackOnTimer(this, "execute", interval, true);
	}

	
	public void logInfo(String message) { log.info(message); }
	public void logWarning(String message) { log.warn(message); }
	public void logFatal(String message) { log.fatal(message); }
	public void logError(String message) { log.error(message); }
	
}
