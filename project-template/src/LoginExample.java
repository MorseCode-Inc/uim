import com.nimsoft.nimbus.NimException;
import com.nimsoft.nimbus.NimUserLogin;


/**
 * 
 * &copy; MorseCode Incorporated 2015<br/>
 * =--------------------------------=<br/><pre>
 * Created: Aug 15, 2015
 * Project: project-template
 *
 * Description:
 * Example of logging in to the UIM message bus and handling the possible errors.
 * 
 * </pre></br>
 * =--------------------------------=
 */
public class LoginExample {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String user= "username";
		String password= "password";
		String message= "OK";
		
		try {
			NimUserLogin.login(user, password);
		} catch (NimException error) {
			switch (error.getCode()) {
			case NimException.E_LOGIN:
				message= "Login Failure (invalid credentials): "+ error.getMessage();
				break;
			case NimException.E_ACCESS:
				message= "Login Failure (access denied): "+ error.getMessage();
				break;
			default:
				message= "Login Failure: "+ error.getMessage();
			}
			
		}
		
		System.out.println(message);
	}

}
