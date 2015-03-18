
package security;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import user.User;
import dbms.DataBaseManager;
import exceptions.UserDoesNotExistException;
import exceptions.WrongPasswordException;


public class LoginHandler {
	private DataBaseManager dbm;
	public LoginHandler(DataBaseManager dbm) {
		this.dbm = dbm;
	}
	
	public User checkPW(String username, String entered_pw) throws UserDoesNotExistException, WrongPasswordException{
		User u = dbm.getUser(username);
		try {
			if(PasswordHash.validatePassword(entered_pw, u.getPassword())){
				return u;
			}else{
				throw new WrongPasswordException();
			}
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String createHash(String password){
		try {
			return PasswordHash.createHash(password);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			e.printStackTrace();
			return null;
		}
	}
}
