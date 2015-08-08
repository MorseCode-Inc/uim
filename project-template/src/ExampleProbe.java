import com.nimsoft.nimbus.NimConfig;
import com.nimsoft.nimbus.NimException;
import com.nimsoft.nimbus.NimLog;
import com.nimsoft.nimbus.PDS;


/**
 * 
 * &copy; MorseCode Incorporated 2015
 * =--------------------------------=<br/><pre>
 * Created: Aug 8, 2015
 * Project: project-template
 *
 * Description:
 * 
 * </pre></br>
 * =--------------------------------=
 */
public class ExampleProbe extends CustomProbeSkeleton {
	
	public static final String PROBE_NAME= "example_probe";
	public static final String PROBE_VERSION= "1.0";
	public static final String PROBE_MANUFACTURER= "MorseCode Incorporated";
	
	protected NimLog log;
	
	private int interval= 300;		// the number of seconds the probe will sleep between calls to execute()

	public ExampleProbe(String[] args) throws NimException {
		super(PROBE_NAME, PROBE_VERSION, PROBE_MANUFACTURER, args);
		// instantiate a NimLog 
		this.log= NimLog.getLogger(this.getClass());
		
		// try to get our configuration file
		PDS config= NimConfig.getInstance().getPDS(false);
		
		// represents the <setup> section in the configuration file
		PDS setup= (PDS)config.get("setup");
		
		// read the configuration file <setup> section
		int loglevel= setup.getInt("loglevel", 3);
		NimLog.setLogLevel(loglevel);
		
		interval= setup.getInt("interval", 300);
		
		if (interval <= 10) {
			// safety check to make sure the interval is reasonable
			interval= 10;		// limit the probe to 10 second cycle as the fastest possible execution cycle
		}
	}
	
	
	/**
	 * 
	 * @return true if the probe is ready to execute.
	 */
	public boolean startup() {
		
		try {
			registerCallbackOnTimer(this, "execute", interval, true);
		} catch (NimException nx) {
			// failure to register callbacks
			log.error("Callback Registration Failure: "+ nx.getMessage());
			log.error("Java Exception: "+ nx.toString() +" @"+ nx.getStackTrace()[0].getFileName() +":"+ nx.getStackTrace()[0].getLineNumber());
			return false;
		}
		
		
		return true;
	}

	
	public void logInfo(String message) { log.info(message); }
	public void logWarning(String message) { log.warn(message); }
	public void logFatal(String message) { log.fatal(message); }
	public void logError(String message) { log.error(message); }
	
	public int getInterval() { return interval; }
	
}
