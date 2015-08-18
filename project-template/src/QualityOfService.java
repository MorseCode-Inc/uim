import inc.morsecode.probes.http_gateway.Probe;

import java.io.IOException;

import com.nimsoft.nimbus.NimException;
import com.nimsoft.nimbus.NimQoS;
import com.nimsoft.nimbus.ci.ConfigurationItem;


public class QualityOfService {

	public QualityOfService() {
		// TODO Auto-generated constructor stub
	}
	
	public NimQoS getQoS(String ciname, String source, String hostname, String ip, boolean async) throws NimException, IOException {
		ConfigurationItem ci= null;
		
		try {
			if (hostname != null && ip != null) {
				ci= new ConfigurationItem(getCiPath(), ciname, hostname, ip);
			} else if (hostname != null) {
				ci= new ConfigurationItem(getCiPath(), ciname, hostname);
			} else {
				ci= new ConfigurationItem(getCiPath(), ciname);
			}
		} catch (Exception x) {
			System.err.println("Error creating new ConfigurationItem: "+ getCiPath() +" ciname="+ ciname +", hostname="+ hostname +", ip="+ ip);
			System.err.println("CAUSE: "+ x.getMessage());
			x.printStackTrace();
			ci= new ConfigurationItem(getCiPath(), ciname);
		}
		
		/*
		 * it is not clear if the ciPath +":"+ metricId should be concatenated or not.
		 */
		NimQoS qos= new NimQoS(ci, getCiPath() +":"+ getMetricId(), getObjectName(), async);
		// NimQoS qos= new NimQoS(ci, getMetricId(), getObjectName(), async);
		qos.setDefinition(Probe.QOS_GROUP, getDescription(), getUnit(), getUnitShort());
		qos.setSampleRate(getSampleRate());
		qos.setSource(source);
		return qos;
	}



}
