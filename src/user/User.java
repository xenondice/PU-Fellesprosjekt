package user;

/**
 * Represents a user in the system.
 * The class is immutable. That means no instance of this class can change any of its attributes.
 *
 */
public class User {
	private final String username;
	private final String name; 
	private final String password;
	private final String salt;
	private final String email;
	
	public User(String username, String name, String password, String salt, String email){
		this.username = username;
		this.name = name;
		this.password = password;
		this.salt = salt;
		this.email = email;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getEmail() {
		return email;
	}
	public String getName() {
		return name;
	}
	public String getPassword() {
		return password;
	}
	public String getSalt() {
		return salt;
	}
	
	
	/**
	 * returns identical (but new) instance of this user (deep-copy).
	 */
	public User clone(){
		return new User(username, name, password, salt, email);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("User [username=");
		builder.append(username);
		builder.append(", name=");
		builder.append(name);
		builder.append(", password=");
		builder.append(password);
		builder.append(", salt=");
		builder.append(salt);
		builder.append(", email=");
		builder.append(email);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((salt == null) ? 0 : salt.hashCode());
		result = prime * result
				+ ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof User)) {
			return false;
		}
		User other = (User) obj;
		if (email == null) {
			if (other.email != null) {
				return false;
			}
		} else if (!email.equals(other.email)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (password == null) {
			if (other.password != null) {
				return false;
			}
		} else if (!password.equals(other.password)) {
			return false;
		}
		if (salt == null) {
			if (other.salt != null) {
				return false;
			}
		} else if (!salt.equals(other.salt)) {
			return false;
		}
		if (username == null) {
			if (other.username != null) {
				return false;
			}
		} else if (!username.equals(other.username)) {
			return false;
		}
		return true;
	}
}
