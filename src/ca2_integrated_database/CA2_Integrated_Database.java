/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package ca2_integrated_database;

import dbconnection.DBConnection;
import java.util.Scanner;

/**
 *
 * @author rafaribeiro
 */
public class CA2_Integrated_Database {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in); // open new scanner
        
        int choice; // declaring variables
        
        // menu of options in loop

        do {
            System.out.println("========== HOUSE RENTAL SYSTEM ==========");
            System.out.println("1. Search clients and their properties by name");
            System.out.println("2. Find properties above a given rent");
            System.out.println("3. Rentals within a date range");
            System.out.println("4. Total rent for a specific client");
            System.out.println("5. Highest-paying client in a city or area");
            System.out.println("6. Count of properties by owner");
            System.out.println("7. Properties rented longer than X months");
            System.out.println("8. Exit");
            System.out.print("Enter your choice: ");
            
            // validation loop for integer input

            while (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Enter a number between 1 and 8.");
                scanner.next();
            }

            choice = scanner.nextInt();
            scanner.nextLine();
            
            // switch case block calling queries method

            switch (choice) {
                case 1:
                    Queries.searchPropertiesByClientName(scanner);
                    break;
                case 2:
                    Queries.propertiesAboveRent(scanner);
                    break;
                case 3:
                    Queries.rentalsInDateRange(scanner);
                    break;
                case 4:
                    Queries.totalRentForClient(scanner);
                    break;
                case 5:
                    Queries.highestPayingClientByLocation(scanner);
                    break;
                case 6:
                    Queries.propertyCountByOwner(scanner);
                    break;
                case 7:
                    Queries.rentalsLongerThanXMonths(scanner);
                    break;
                case 8:
                    System.out.println("Exiting program...");
                    break;
                default:
                    System.out.println("Invalid choice. Please enter 1 to 8.");
                    break;
            }

        } while (choice != 8);

    }
}