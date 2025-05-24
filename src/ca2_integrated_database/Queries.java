/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ca2_integrated_database;

import java.sql.*;
import dbconnection.DBConnection;
import java.util.Scanner;

/**
 *
 * @author rafaribeiro
 */
public class Queries {
    public static void searchPropertiesByClientName(Scanner scanner) {
        
        //prompt user asking for name 
        
        System.out.println("Enter client name (partial or full): ");
        String name = scanner.nextLine().trim();
        
        //loop for empty typing 
        if (name.isEmpty()) {
            System.out.println("Client name cannot be empty.");
            return;
        }
        
        /*SQL query for join property_rentals on client.register 
        (Uses a JOIN between 3 tables to connect clients, rentals and properties)
        LIKE ? for to set name chosen by the user*/
        
        String sql = "SELECT client_register.Client_No, client_register.Client_Name, " +
                     "properties_register.PropertyNo, properties_register.PropertyAddress " +
                     "FROM client_register " +
                     "JOIN property_rentals ON client_register.Client_No = property_rentals.Client_No " +
                     "JOIN properties_register ON property_rentals.PropertyNo = properties_register.PropertyNo " +
                     "WHERE client_register.Client_Name LIKE ?";

        try (Connection conn = DBConnection.getConnection(); // connects to database and throws query in string sql
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name + "%");
            ResultSet rs = stmt.executeQuery();

            boolean found = false;
            while (rs.next()) {
                if (!found) {
                    System.out.println("Client ID | Client Name | Property ID | Property Address");
                    System.out.println("----------------------------------------------------------");

                    found = true;
                }
                System.out.println(
                    rs.getString("Client_No") + " | " +
                    rs.getString("Client_Name") + " | " +
                    rs.getString("PropertyNo") + " | " +
                    rs.getString("PropertyAddress")
                );

            }

                if (!found) System.out.println("No results found.");
                    } catch (SQLException e) {
                    System.out.println("Error: " + e.getMessage());
        }
    }

    public static void propertiesAboveRent(Scanner scanner) {
        
        //prompts request of minimum rent to the user
        System.out.print("Enter minimum rent amount: ");
        
        // validates if scanner read a double
        while (!scanner.hasNextDouble()) {
            System.out.println("Please enter a valid number.");
            scanner.next(); // discard invalid input
        }
        double amount = scanner.nextDouble(); 
        scanner.nextLine(); // clear line after valid input

        /*SQL query for listing all properties where the monthly rent is greater than the amount entered 
        by the user, it uses a simple filter with WHERE and a user-supplied number */
            
        String sql = "SELECT PropertyNo, PropertyAddress, Monthly_rent " +
                     "FROM properties_register WHERE Monthly_rent > ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) { // connects to database and throws query in string sql

            stmt.setDouble(1, amount);
            ResultSet rs = stmt.executeQuery();

            boolean found = false;

            while (rs.next()) {
                if (!found) {
                    System.out.println("Property ID | Address | Rent");
                    System.out.println("------------------------------------------");
                    found = true;
                            }               

                String propertyId = rs.getString("PropertyNo");
                String address = rs.getString("PropertyAddress");
                double rent = rs.getDouble("Monthly_rent");

                System.out.println(propertyId + " | " + address + " | " + rent);
                                }

            if (!found) {
                System.out.println("No properties above this rent.");
                }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void rentalsInDateRange(Scanner scanner) {
        
        // Prompt user to enter start and end dates in YYYY-MM-DD format
        System.out.print("Enter start date (YYYY-MM-DD): ");
        String start = scanner.nextLine().trim();
        System.out.print("Enter end date (YYYY-MM-DD): ");
        String end = scanner.nextLine().trim();
        
        // Validate that neither date is empty
        if (start.isEmpty() || end.isEmpty()) {
            System.out.println("Dates cannot be empty.");
            return;
        }
        
        // SQL query to select rentals that were active within the specified date range
        String sql = "SELECT client_register.Client_Name, properties_register.PropertyAddress, " +
                     "property_rentals.Rent_start, property_rentals.Rent_finish " +
                     "FROM property_rentals " +
                     "JOIN client_register ON property_rentals.Client_No = client_register.Client_No " +
                     "JOIN properties_register ON property_rentals.PropertyNo = properties_register.PropertyNo " +
                     "WHERE property_rentals.Rent_start <= ? AND property_rentals.Rent_finish >= ?";
        
        // Execute the query using a statement
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Set the ? values for the placeholders in the SQL query
            stmt.setString(1, end); // End date comparison
            stmt.setString(2, start); // Start date comparison
            
            ResultSet rs = stmt.executeQuery(); // Execute the query and get results
               
            boolean found = false;
            
            // run through results
            while (rs.next()) {
                // Print header when results are found
                if (!found) {
                    System.out.println("Client Name | Property | Start | End");
                    System.out.println("---------------------------------------------------------------");
                    found = true;
                }
                
                // Print each rental record found
                System.out.println(
                    rs.getString("Client_Name") + " | " +
                    rs.getString("PropertyAddress") + " | " +
                    rs.getString("Rent_start") + " | " +
                    rs.getString("Rent_finish")
                );
            }   
                // If no rentals were found for the given range
                if (!found) System.out.println("No rentals in the given period.");
             
            
        }catch (SQLException e) {
            // Handle and display any SQL errors that occur
            System.out.println("Error: " + e.getMessage());
            }
    
    }

        public static void totalRentForClient(Scanner scanner) {
            
         // Prompt user to enter part of name or name
        System.out.print("Enter client name (or part of it): ");
        String name = scanner.nextLine().trim();
        
        // validates if there was nothing typed
        if (name.isEmpty()) {
            System.out.println("Client name cannot be empty.");
            return;
        }
        
        // SQL query to select rentals that has something similar with the starting name typed by the user
        String sql = "SELECT client_register.Client_No, client_register.Client_Name, " +
                     "SUM(properties_register.Monthly_rent) AS Total_Rent " +
                     "FROM client_register " +
                     "JOIN property_rentals ON client_register.Client_No = property_rentals.Client_No " +
                     "JOIN properties_register ON property_rentals.PropertyNo = properties_register.PropertyNo " +
                     "WHERE client_register.Client_Name LIKE ? " +
                     "GROUP BY client_register.Client_No, client_register.Client_Name";
        
        // Execute the query using a statement
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Set the ? values for the placeholders in the SQL query
            stmt.setString(1, name + "%"); // name typed by the user
            ResultSet rs = stmt.executeQuery(); // Execute the query and get results
            
            // Declares boolean value
            boolean found = false;
            
            // Runs through the result
            while (rs.next()) {
                if (!found) {
                    // Prints the header
                    System.out.println("Client ID | Client Name | Total Rent");
                    System.out.println("-------------------------------------------");
                    found = true;
                    }
                    
                    // Print each row with client info and total rent
                    System.out.println(
                        rs.getString("Client_No") + " | " +
                        rs.getString("Client_Name") + " | " +
                        rs.getDouble("Total_Rent")
                    );
            }
            
            // If no results were found, inform the user
            if (!found) System.out.println("No client found.");

        } catch (SQLException e) {
            // Handle and display any SQL errors that occur
            System.out.println("Error: " + e.getMessage());
        }
    }
    
     
    public static void highestPayingClientByLocation(Scanner scanner) {
        System.out.print("Enter keyword in property address (e.g., city): ");
        String keyword = scanner.nextLine().trim();
        
        // Validate input
        if (keyword.isEmpty()) {
            System.out.println("Input cannot be empty.");
            return;
        }
        
        // SQL query to find the client paying the highest rent in a location
        String sql = "SELECT client_register.Client_Name, properties_register.PropertyAddress, " +
                     "properties_register.Monthly_rent " +
                     "FROM client_register " +
                     "JOIN property_rentals ON client_register.Client_No = property_rentals.Client_No " +
                     "JOIN properties_register ON property_rentals.PropertyNo = properties_register.PropertyNo " +
                     "WHERE properties_register.PropertyAddress LIKE ? " +
                     "ORDER BY properties_register.Monthly_rent DESC LIMIT 1";
        
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + keyword + "%"); // search for address containing the keyword
            ResultSet rs = stmt.executeQuery();
            
            // Display result if found
            if (rs.next()) {
                System.out.println("Client: " + rs.getString("Client_Name"));
                System.out.println("Address: " + rs.getString("PropertyAddress"));
                System.out.println("Rent: " + rs.getDouble("Monthly_rent"));
            } else {
                System.out.println("No results found for that location.");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void propertyCountByOwner(Scanner scanner) {
        System.out.print("Enter owner name or ID: ");
        String input = scanner.nextLine().trim();

        // Validate input
        if (input.isEmpty()) {
            System.out.println("Input cannot be empty.");
            return;
        }
        
        // SQL query to count properties owned by a specific owner
        String sql = "SELECT owners_register.Owner_No, owners_register.Owner_Name, " +
                     "COUNT(properties_register.PropertyNo) AS Property_Count " +
                     "FROM owners_register " +
                     "LEFT JOIN properties_register ON owners_register.Owner_No = properties_register.Owner_No " +
                     "WHERE owners_register.Owner_No = ? OR owners_register.Owner_Name LIKE ? " +
                     "GROUP BY owners_register.Owner_No, owners_register.Owner_Name";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, input);
            stmt.setString(2, input + "%");
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                System.out.println("Owner: " + rs.getString("Owner_Name") +
                                " (" + rs.getString("Owner_No") + ")");
                System.out.println("Property Count: " + rs.getInt("Property_Count"));
            } else {
                System.out.println("No owner found.");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void rentalsLongerThanXMonths(Scanner scanner) {
        
        System.out.print("Enter minimum rental duration (in months): ");
        
        // Validate that input is an integer
        while (!scanner.hasNextInt()) {
            System.out.println("Please enter a valid number.");
            scanner.next();
        }
        int months = scanner.nextInt();
        scanner.nextLine(); // consume a new line

        // SQL query to find rentals longer than X months using TIMESTAMPDIFF
        String sql = "SELECT client_register.Client_Name, properties_register.PropertyAddress, " +
                     "property_rentals.Rent_start, property_rentals.Rent_finish " +
                     "FROM property_rentals " +
                     "JOIN client_register ON property_rentals.Client_No = client_register.Client_No " +
                     "JOIN properties_register ON property_rentals.PropertyNo = properties_register.PropertyNo " +
                     "WHERE TIMESTAMPDIFF(MONTH, Rent_start, Rent_finish) >= ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, months); // set the number of months
            ResultSet rs = stmt.executeQuery();

            boolean found = false;
            
            // Display results if any
            while (rs.next()) {
                if (!found) {
                    System.out.println("Client Name | Property | Start | End");
                    System.out.println("--------------------------------------------------------------");
                    found = true;
                }
                System.out.println(
                    rs.getString("Client_Name") + " | " +
                    rs.getString("PropertyAddress") + " | " +
                    rs.getString("Rent_start") + " | " +
                    rs.getString("Rent_finish")
                );
            }

            if (!found) System.out.println("No rentals found.");

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
    


