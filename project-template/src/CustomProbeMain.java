import com.nimsoft.nimbus.NimException;


/**
 * 
 * &copy; MorseCode Incorporated 2015
 * =--------------------------------=<br/><pre>
 * Created: Aug 7, 2015
 * Project: pf-template
 *
 * Description:
 * Provides an example main() method to bootstrap your custom probe.
 * 
 * </pre></br>
 * =--------------------------------=
 */
public class CustomProbeMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			
			ExampleProbe probe= new ExampleProbe(args);
			
			// probe startup() may read the configuration and prepare to execute
			// however, the code execution must reach the probe.doForever() for the probe
			// to register properly with the local robot.
			if (!probe.startup()) {
				// probe failed to startup correctly.
				// make sure an error message gets somewhere so that an administrator can 
				// understand something about what went wrong.
			}
			
			do {
				// WARNING: careful not to create a tight loop that can spin the CPU
				
				if (probe.isStoppingOrRestarting()) {
					// perfect opportunity to refresh the configuration file?
					// probes are usually required to restart before accepting configuration changes.
					
				}
				
			} while (probe.doForever());	// probe.doForever() will block until the probe is stopped or restarted.
			
		} catch (NimException error) {
			// STDERR output is hard to see sometimes.
			// Consider using logging facility to capture this error
			error.printStackTrace(System.err);
		} finally {
			// do any last minute things before the JVM terminates
			System.out.flush();
			System.err.flush();
		}
	}

}
