package vo;
public class User{
	private String userName;
	private String password;
	private String name;
	private String role;
	
	
	public User() {
		super();
	}
	public User(String userName, String password) {
		super();
		this.userName = userName;
		this.password = password;
	}
	public User(String userName,String password,String name,String role){
		this.userName=userName;
		this.password=password;
		this.name=name;
		this.role=role;
	}
	public void setUserName(String userName){
		this.userName=userName;
	}
	public String getUserName(){
		return userName;
	}
	public void setPassword(String password){
		this.password=password;
	}
	public String getPassword(){
		return password;
	}
	public void setName(String name){
		this.name=name;
	}
	public String getName(){
		return name;
	}
	public void setRole(String role){
		this.role=role;
	}
	public String getRole(){
		return role;
	}
	public String toString(){
		return userName+"\t"+password+"\t"+name+"\t"+role;
	}
}