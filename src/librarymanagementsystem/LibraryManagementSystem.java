package librarymanagementsystem;

import java.util.UUID;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays; //for appending

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONArray;

public class LibraryManagementSystem {
    private Scanner scanner = new Scanner(System.in);
    private AccountManager accountManager;
    private Book[] books;

    public LibraryManagementSystem(AccountManager accountManager, Book[] books){
        this.accountManager = accountManager;
        this.books = books;
    }

    public void Welcome(){
        while (true){
            System.out.println("Welcome to the Library. (1=Login, 2=Register, 3=exit)");
            String action = scanner.nextLine();
            switch(action){
                case "1":
                    Login();
                    break;
                case "2":
                    Register();
                    break;
                case "3":
                    System.out.println("Exiting Program");
                    return;
                default:
                    throw new IllegalArgumentException("Error: dunno what u want");
            }
        }
    }
 
    public void Login(){
        System.out.println("What user type are you? (user, librarian)");
        String userType = scanner.nextLine().trim().toLowerCase();
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        if(accountManager.AuthenticateUser(username, password)){
            User user = accountManager.getUser(username);
            userSession(user);
        }else{
            System.out.println("Wrong username or wrong password");
        }
    }

    public void Register(){
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        if(!accountManager.checkUserExist(username)){
            accountManager.addUser(username, password);
            System.out.println("User created, you need to Login");
        }else{
            System.out.println("Username already taken");
        }
    }

    public void userSession(User user){
        System.out.println("Welcome " + user.getName() + "!");
        while(true){
            System.out.println("What do you want to do? (1=Show books, 2=Search a book, 3=Print profile data, 4=log out)");
            String action = scanner.nextLine().trim();
            switch (action) {
            	case "1":
            		user.get_all_books(books);
            		break;
                case "2":
                    System.out.print("Title: ");
                    String search = scanner.nextLine().trim();
                    Book book = user.get_book_info(books, search);
                    if(book == null){
                        System.out.println("Book not found");
                        break;
                    }
                    System.out.println(book);
                    break;
                case "3":
                    System.out.println(user);
                    break;
                case "4":
                    System.out.println("Logging out");
                    return;
                default:
                    System.out.println("Error");
                    break;
            }
        }
    }
    
    public static String getJSONFromFile(String filename) {
        String jsonText = "";
        try {		
            BufferedReader bufferedReader = 
                          new BufferedReader(new FileReader(filename));
        
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                jsonText += line + "\n";
            }
        
            bufferedReader.close();
        
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        return jsonText;
    }


    public static void main(String[] args){

        
        String strBooks = getJSONFromFile("C:\\Users\\mocci\\Documents\\Projects\\javaworkspace\\librarymanagementsystem\\src\\java\\books.json");
        String strUsers = getJSONFromFile("C:\\Users\\mocci\\Documents\\Projects\\javaworkspace\\librarymanagementsystem\\src\\java\\users.json");
        
        try{
        	Book[] books = new Book[0];
        	User[] users = {new User("Yabujin", "1234"), new User("Bladee", "5436")};
        	
            JSONParser parser = new JSONParser(); //create the parser
            
            //BOOKS
            Object bookObject = parser.parse(strBooks); //get the object from it ig idk
            JSONArray jsonBookArray = (JSONArray) bookObject; //converts object into array
            for(Object book : jsonBookArray) {
            	JSONObject bookjson = (JSONObject) book;
            	String title = (String) bookjson.get("title");
            	String author = (String) bookjson.get("author");
            	long year = (long) bookjson.get("year");
            	long pages = (long) bookjson.get("pages");
            	//todo print only title and author when all are getting printed
            	
            	Book newBook = new Book(title, author, year, pages);
            	Book[] newBooks = Arrays.copyOf(books, books.length + 1);
                newBooks[newBooks.length -1] = newBook;
                books = newBooks;
                //System.out.println(newBook);
            }
            
            //USERS
            Object userObject = parser.parse(strUsers);
            JSONArray jsonUserArray = (JSONArray) userObject;
            for(Object user : jsonUserArray) {
            	JSONObject userjson = (JSONObject) user;
            	String username = (String) userjson.get("username");
            	String password = (String) userjson.get("password");

            	User newUser = new User(username, password);
            	User[] newUsers = Arrays.copyOf(users, users.length + 1);
            	newUsers[newUsers.length -1] = newUser;
            	users = newUsers;
            }
            //STARTUP
            AccountManager accountManager = new AccountManager(users);
            LibraryManagementSystem lms = new LibraryManagementSystem(accountManager, books);
            lms.Welcome();
          

        } catch(Exception ex) {
            ex.printStackTrace();
        }

    }
}

class AccountManager{
    private User[] users;

    public AccountManager(User[] users){
        this.users = users;
    }

    public boolean AuthenticateUser(String username, String password){
        for(User user : users){
            if(user.getName().equals(username) && user.checkPassword(password)){
                return true;
            }
        }
        return false;
    }

    public User getUser(String username){
        for(User user : users){
            if(user.getName().equals(username)){
                return user;
            }
        }
        return null;
    }

    public void addUser(String username, String password){
        User newUser = new User(username, password);
        //APPENDS
        User[] newUsers = Arrays.copyOf(users, users.length + 1);
        newUsers[newUsers.length -1] = newUser;
        users = newUsers; //REPLACES
    }

    public boolean checkUserExist(String username){
        for(User user : users){
            if(user.getName().equals(username)){
                return true;
            }
        }
        return false;
    }
}

class User{
    private String name;
    private String id = UUID.randomUUID().toString();
    private String password;

    public User(String name, String password){
        setName(name);
        setPassword(password);
    }

    public Book get_book_info(Book[] books, String search){
        for(Book book : books){
            if(book.getTitle().equals(search) || book.getAuthor().equals(search)){
                return book;
            }
        }
        return null;
    }
    
    public void get_all_books(Book[] books) {
    	for(Book book : books) {
    		System.out.println(book);
    	}
    }

    public boolean checkPassword(String password){
        return this.password.equals(password);
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        if(name == null || name.isEmpty()){
            throw new IllegalArgumentException("incorrect input");
        }
        this.name = name;
    }

    public String getId(){
        return id;
    }

    public void setPassword(String password){
        if(password == null || password.isEmpty()){
            throw new IllegalArgumentException("incorrect input");
        }
        this.password = password;
    }

    @Override
    public String toString(){
        return "------User------\n" + "Name: " + name + "\nId: " + id;
    }
}

class Book{
    private String title;
    private String author;
    private long publication;
    private long pages;

    public Book(String title, String author, long year, long pages){
        setTitle(title);
        setAuthor(author);
        setPublication(year);
        setPages(pages);
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        if(title == null || title.isEmpty()){
            throw new IllegalArgumentException("incorrect input");
        }
        this.title = title;
    }

    public String getAuthor(){
        return author;
    }

    public void setAuthor(String author){
        if(author == null || author.isEmpty()){
            throw new IllegalArgumentException("incorrect input");
        }
        this.author = author;
    }


    public long getPublication(){
        return publication;
    }

    public void setPublication(long publication){
        this.publication = publication;
    }
    
    public long getPages() {
    	return pages;
    }
    
    public void setPages(long pages){
        this.pages = pages;
    }

    @Override
    public String toString(){
        return "------Information------\n" + "Title: " + title + "\nAuthor: " + author + "\nPublication: " + publication + "\nPages: " + pages;
    }
}

class Librarian{
    private String name;
    private String id = UUID.randomUUID().toString();
    private String password;
    private String searchString;

    public Librarian(String name, String password, String searchString){
        setName(name);
        setPassword(password);
        setSearchString(searchString);
    }

    public boolean checkPassword(String password){
        return this.password.equals(password);
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        if(name == null || name.isEmpty()){
            throw new IllegalArgumentException("incorrect input");
        }
        this.name = name;
    }

    public String getId(){
        return id;
    }

    public void setPassword(String password){
        if(password == null || password.isEmpty()){
            throw new IllegalArgumentException("incorrect input");
        }
        this.password = password;
    }

    public String getSearchString(){
        return searchString;
    }

    public void setSearchString(String searchString){
        if(searchString == null || searchString.isEmpty()){
            throw new IllegalArgumentException("incorrect input");
        }
        this.searchString = searchString;
    }

    @Override
    public String toString(){
        return "Librarian------\n" + "Name: " + name + "\nId: " + id + "\nSearchString: " + searchString;
    }
}