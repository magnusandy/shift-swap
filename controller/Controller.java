/*
*  Connor Lavoy
*  cbl013
*  11118147
*  October 2, 2014
*/

package controller;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

/**
 * @author Connor Lavoy
 */
public class Controller {

	private Connection dbconnection;

	public Controller() {
            //System.out.print("connection");
            try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("fail");
		}
		try
		{
                    System.out.println("trying");
                    dbconnection = DriverManager.getConnection ("jdbc:postgresql://lovett.usask.ca:5432/", "cmpt370_group13", "1truegod");
                    System.out.println("got connection");
		}
		catch(SQLException sqle)
		{
                    System.out.println("Error connecting to Database...");
                    System.out.println(sqle.getMessage());
		}
	}

	/**
	* Called by other pieces of the system to make requests to the database/managers
	* @param request - a Request object which contains information to be parsed by the controller.
        * @return a container of any results sought from the request
        * @throws SQLException
	*/
	public RequestResults sendRequest (Request request) throws SQLException {
           RequestResults returnResults = null;
		switch (request.getMode()) {
			case TAKE:
			{
			}
			case GIVE:
			{
			}
			case TRADE:
			{
			}
			case LOGIN:
			{

                            boolean validated = false;
                            System.out.println("trying");
                            Statement loginRequest = dbconnection.createStatement();
                            ResultSet results = loginRequest.executeQuery(this.getloginQuery(request.getSender()));
                            System.out.println("got Passwords");
                            while (results.next() && !validated)
                            {
                                System.out.println("looping");
                                byte[] query = request.getPassword();
                                byte[] result = results.getBytes("emppassword");
                                validated = true;
                                for (int i = 0; validated && i < 32; i ++) {
                                    validated = query[i] != result[i];
                                    if (!validated)
                                        System.out.println("Character failed");
                                    else
                                        System.out.println("validated character");
                                }
                            }
                            if (validated){
                                    System.out.println("validated");
                                    request.setApproved(true);}
                            else {
                                     System.out.println("incorrect");
                                    request.setApproved(false);}
                            break;
			}
                        case CREATE:
                        {
                            
                        }
                        case REMOVE:
                        {
                            
                        }
                        case SCHEDULE:
                        {
                            Statement shiftPullRequest = dbconnection.createStatement();
                            ResultSet results = shiftPullRequest.executeQuery(this.getEmployeeShiftInfo(request.getSender()));
                            Date[] resultsList = new Date[results.getFetchSize()*2];
                            for (int i = 0; results.next(); i = i + 2)
                            {
                                resultsList[i] = results.getDate("shiftstarttime");
                                resultsList[i+1] = results.getDate("shiftendtime");
                            }
                            returnResults = new RequestResults();
                            returnResults.setShifts(resultsList);
                            return returnResults;
                        }
		}
                return returnResults;
	}

	/**
	* pulls the messages that are waiting for a user and returns them as a delimited string
	* @returns a ready-to-print string of the messages awaiting a user.
	*/
	private String getMessages() {
	    return null;
	}
        
        //Generate a query to select the login (username and password) information from the database so that it can be checked for authentication
        private String getloginQuery(String LoginID){
            return "SELECT empLogin, empPassword FROM login WHERE empLogin = '" + LoginID + "'" ;
        }
        
        /**
         * generate a query to get all important non shift information from
         * the database for a given loginID, First Name, Last Name, access level, login, password,
         * email and who their manager is.
         */
        private String getWorkerInfoQuery(String LoginID)
        {
            
            return "SELECT empfirstname, emplastname, empaccesslevel,"
                    + " emplogin, emppassword, empemail, empmanager "
                    + "FROM full_employee_info"
                    + " WHERE emplogin = '" + LoginID + "'";
        }
        
        /**
         * Generate a query to get ALL the shift info for a particular person denoted by LoginID
         * @param LoginID ID of the person you want to get the shifts of
         * @return String of the Query needed
         */
        private String getEmployeeShiftInfo(String LoginID)
        {
        return "SELECT shiftemployeelogin, shiftstarttime, shiftendtime"
                + " FROM employeeshifts"
                + " WHERE shiftemployeelogin = '" + LoginID + "'"
                + " ORDER BY shiftstarttime" ;
        }
        
        private String newEmployeeQuery(String firstName, String lastName, int accesslevel, String loginID, String password, String email, float wage)
        {
            return "INSERT INTO employees (empfirstname, emplastname, empaccesslevel,"
                    + " emplogin, emppassword, empemail, empwage)"
                    + " VALUES ('" + firstName + "', "
                    + "'" + lastName + "', "
                    + "'" + accesslevel + "', "
                    + "'" + loginID + "', "
                    + "'" + password + "', "
                    + "'" + email + "', "
                    + "'" + wage + "')";
        }
        /**
         *generate a query to update any or all employee info, it is assumed that the empLogin cannot be changed
         * @param firstName can be null
         * @param lastName can be null
         * @param accesslevel must be set to -1 if not specified
         * @param loginID CANNOT be null
         * @param password can be null
         * @param email can be null
         * @param wage set to -1 if not specified
         * @return a custom string for the update query given the parameters 
         */
        private String updateEmployeeQuery(String firstName, String lastName, int accesslevel, String loginID, String password, String email, float wage){
            boolean needComma = false;
            String ret = "UPDATE employees SET ";
            if(firstName != null){
                ret = ret+ "empfirstname = '"+ firstName+"'";
                needComma = true;
            }
            if(lastName != null){
                if(needComma == true){
                        ret = ret + ", ";
                }
                ret = ret+ "emplastname = '"+ lastName+"' ";
                needComma = true;
            }
            if(accesslevel != -1){
                if(needComma == true){
                        ret = ret + ", ";
                }
                ret = ret+ "empaccesslevel = '"+ accesslevel+"' ";
                needComma = true;
            }
            if(password != null){
                if(needComma == true){
                        ret = ret + ", ";
                }
                ret = ret+ "emppassword = '"+ password+"' ";
                needComma = true;
            }
            if(email != null){
                if(needComma == true) {
                        ret = ret + ", ";
                }
                ret = ret+ "empemail = '"+ email+"' ";
                needComma = true;
            }
            if(wage != -1){
                if(needComma == true){
                        ret = ret + ", ";
                }
                ret = ret+ "empwage = '"+ wage+"' ";
                needComma = true;
            }        
            ret = ret + " WHERE emplogin = '" + loginID +"'";
            return ret;
        }
        
        /**
         * Generate Query to insert into the bossmanager table, employee is the LoginID
         * of the employee and manager is the loginID of that persons manager
         * @param employee LoginID of the employee
         * @param manager LoginID of the employees manager
         * @return Query
         */
        private String newManagerQuery(String employee, String manager)
        {
             return "INSERT INTO bossmanager (employee, manager)"
                    + " VALUES ('" + employee + "', "
                    + "'" + manager + "')";
        }
        
        /**
         * Generate Query to update a employees manager
         * @param employee employee whos manager needs changing
         * @param newManager new managers LoginID
         * @return  the Query
         */
        private String updateManagerQuery(String employee, String newManager)
        {
            return "UPDATE bossmanager "
                    + "SET manager = '" + newManager +"' "
                    + "WHERE employee = '" + employee + "' ";
        }
        
        /**
         * Generate Query to insert a new message into the inbox table
         * @param sender LoginId of who sent it
         * @param reciever LoginID of who should recieve it
         * @param message  textual part of the message
         * @return the query
         */
        private String newInboxQuery(String sender, String reciever, String message )
        {
            return "INSERT INTO employeeinbox (mssgsender, mssgreciever, mssgtext)"
                    + "VALUES("
                    + "'" + sender + "', "
                    + "'" + reciever + "', "
                    + "'" + message + "' "
                    + ")";
        }
        
        private String getEmployeeInbox(String LoginID)
        {
            return "SELECT mssgreciever, mssgsender, mssgtext, mssgsendtime "
                    + "FROM employeeinbox "
                    + "WHERE mssgreciever = '" + LoginID + "' "
                    + "ORDER BY mssgsendtime";
        }
     
        
        public static void main (String[] args) {
            
            Controller c = new Controller();
            System.out.println(c.getloginQuery("testUsername"));
	    System.out.println(c.getWorkerInfoQuery("testUsername"));
            System.out.println(c.getEmployeeShiftInfo("testUsername"));
            System.out.println(c.newEmployeeQuery("Elmer", "Fudd", 1, "eFudd", "wabbit", "Fudd@mail.com", (float) 53.232));
            System.out.println(c.updateEmployeeQuery(null, "buster", 2, "eFudd", "jack", null, -1));
            System.out.println(c.newManagerQuery("testUsername", "testManager"));
            System.out.println(c.updateManagerQuery("magnusandy", "oneTrueGod"));
            System.out.println(c.newInboxQuery("magnusandy", "oneTrueGod", "Yoooo dawg lets do this"));
            System.out.println(c.getEmployeeInbox("oneTrueGod"));
        }  
}