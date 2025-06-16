package Menu;

import Analytics.AnalyticsService;
import Dump.SqlDump;
import ERD.Erd_functionclass;
import QueryParser.QueryParser;

import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;


public class Menu {

    private void showMenu() {
        System.out.println("1. Write Queries");
        System.out.println("2. Export");
        System.out.println("3. Data Model");
        System.out.println("4. Analytics");
        System.out.println("5 Erd");
        System.out.println("6. Exit");
        System.out.println();
    }

    private void menu() throws IOException, InterruptedException {
        Scanner menu = new Scanner(System.in);
        showMenu();
        String choice;
        do {
            choice = menu.nextLine();
            if (Objects.equals(choice, "1")) {
                Scanner queryMenu = new Scanner(System.in);
                String query;
                do {
                    System.out.println("Type Queries (or) Type EXIT for exiting");
                    query = queryMenu.nextLine();
                    if (query.equals("EXIT")) {
                        showMenu();
                        break;
                    }
                    QueryParser queryParser = new QueryParser();
                    queryParser.getToSpecificQuery(query);
                } while (true);
                queryMenu.close();
            } else if (Objects.equals(choice, "2")) {
                Scanner queryMenu = new Scanner(System.in);
                String query;
                do {
                    SqlDump dump = new SqlDump();
                    SqlDump.createDump();
                    showMenu();
                    break;
                } while (true);
            } else if (Objects.equals(choice, "4")) {
                Scanner queryMenu = new Scanner(System.in);
                String query;
                do {
                    System.out.println("Type Analytics (or) Type EXIT for exiting");
                    query = queryMenu.nextLine();
                    if (query.equals("EXIT")) {
                        showMenu();
                        break;
                    }
                    AnalyticsService analyticsService = new AnalyticsService();
                    analyticsService.queryParser(query);
                } while (true);
                queryMenu.close();

            } else if (Objects.equals(choice, "5")) {
                System.out.println("Enter THE DATABASE NAME for which you " +
                        "want a ERD");
                Scanner input = new Scanner(System.in);
                String databasename = input.nextLine();
                Erd_functionclass erd_object = new Erd_functionclass();
                erd_object.make_erd(databasename);
                showMenu();
                continue;

            }
        } while (!Objects.equals(choice, "6"));
        menu.close();
    }

    public void run() throws IOException, InterruptedException {
        menu();
    }
}
