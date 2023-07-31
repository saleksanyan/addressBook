import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

/**
 * This class works with stored data(name, surname, phone number, email)
 * It can help you to remove, insert, search, change the dataset that was created in a file
 */
public class AddressBook {
    public static void main(String[] args) throws IOException {

        File file = new File("addressBook.txt");
        Scanner readFile = new Scanner(file);
        Scanner readUser = new Scanner(System.in);
        String[] insertableData = null;// in this var we keep not only the data that was
                                        // inserted but also the one that was searched for
        String[][] data = null;
        if(readFile.hasNext()) {
            int n = Integer.parseInt(readFile.nextLine());
            data = new String[n][4];
            for (int i = 0; i < n; i++) {
                String s = readFile.nextLine();
                System.out.println(s);
                data[i] = s.split(" ");
            }
            System.out.println("Hi there!\nWhat do you want to do with the data? \nINSERT   REMOVE  CHANGE  SEARCH");
            String action = readUser.nextLine();
            while (!(action.equalsIgnoreCase("INSERT") || action.equalsIgnoreCase("REMOVE")
                    || action.equalsIgnoreCase("CHANGE") || action.equalsIgnoreCase("SEARCH"))) {
                System.out.println("The input is invalid.\nChoose among INSERT   REMOVE  CHANGE SEARCH");
                action = readUser.nextLine();
            }
            System.out.println("Enter the data that you want to insert/search/remove/change: ");
                insertableData = readUser.nextLine().split(" ");
            if (action.equalsIgnoreCase("SEARCH")){
                String[][] searchResult = searchData(data,insertableData);
                while(searchResult[0][0] == null) {
                    System.out.println("There is no data. Please try again or if you want to stop enter 'stop'");
                    insertableData = readUser.nextLine().split(" ");
                    searchResult = searchData(data,insertableData);
                }
                if(insertableData[0].equalsIgnoreCase("stop"))
                    return;
                for (int i = 0; i < searchResult.length; i++) {
                    if(searchResult[i][0] != null)
                        System.out.println(Arrays.toString(searchResult[i]));
                }
            }else {
                while (!checkData(insertableData)) {
                    System.out.println("Entered data is invalid. Please try again.");
                    insertableData = readUser.nextLine().split(" ");
                }
                String[] changeableData;
                if (action.equalsIgnoreCase("INSERT")) {
                    data = insertData(data, insertableData, readUser, null, true);
                } else if (action.equalsIgnoreCase("REMOVE")) {
                    data = removeData(data, insertableData, true);
                } else {
                    System.out.println("Enter new data (enter the whole data\n" +
                            "include name, surname, phone number and email)");
                    changeableData = readUser.nextLine().split(" ");
                    while (!checkChangeableData(data, changeableData)) {
                        System.out.println("Entered data is invalid. Please try again.");
                        changeableData = readUser.nextLine().split(" ");
                    }
                    data = changeData(data, changeableData, insertableData, readUser);
                }
            }
        }
        else{
            System.out.println("There is no data to read...\nSo enter the data\n" +
                    "(the data should include NAME SURNAME PHONE NUMBER EMAIL \n" +
                    "e.g. Petros Petrosyan 091000000 PPetrosyan@example.com)\n" +
                    "type stop if you want to stop entering data");
            int index = 0;
            System.out.print("Enter the number of data you want to enter: ");
            int length = readUser.nextInt();
            readUser.nextLine();
            System.out.println("Now enter the data:");
            data = new String[length][4];
            for (int i = 0; i < length; i++) {
                insertableData = readUser.nextLine().split(" ");
                while (!checkData(insertableData)) {
                    System.out.println("Entered data is invalid. Please try again.");
                    insertableData = readUser.nextLine().split(" ");
                }
                data = insertData(data,insertableData,readUser,null,true);
            }
        }
        FileWriter fileWriter = new FileWriter(file);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.println(data.length);

        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < 4; j++) {
                printWriter.print(data[i][j]+" ");
            }
            printWriter.print("\n");
        }
        printWriter.close();

    }

    //helps you to insert data in a file
    private static String[][] insertData(String[][] data, String[] insertableData,
                                         Scanner readUser, String[] changeableData, boolean printInsert){
        String[][] resultedData = new String[data.length+1][4];
        while(!(insertableData[0].equalsIgnoreCase("stop"))
                && isInTheDataset(data,insertableData, changeableData,data.length)) {
            System.out.println("This information already exists in the dataset. If there is nothing to insert enter 'stop'");
            insertableData = readUser.nextLine().split(" ");
        }
        for (int i = 0; i < data.length; i++) {
            resultedData[i] = data[i];
        }
        resultedData[data.length] = insertableData;
        if(printInsert)
            System.out.println("Entered data is inserted!");
        return resultedData;
    }
    //helps you to remove from a data in a file
    private static String[][] removeData(String[][] data, String[] removableData, boolean printRemove) {
        String[][] resultedData = new String[data.length-1][4];
        for (int i = 0, j = 0; i < data.length; i++) {
            if(!compare(removableData, data[i])) {
                resultedData[j] = data[i];
                j++;
            }
        }
        if(printRemove)
            System.out.println("Entered data is removed!");
        return resultedData;
    }
    //helps you to change a data in a file
    private static String[][] changeData(String[][] data, String[] insertableData, String[] changeableData, Scanner readUser) {
        data = insertData(data, insertableData, readUser, changeableData, false);
        data = removeData(data, changeableData, false);
        System.out.println("Entered data is changed!");
        return data;

    }

    //helps you to search data in a file
    private static String[][] searchData(String[][] data, String[] searchableData){
        String[][] result = new String[data.length][4];
        for (int i = 0, k=0; i < data.length; i++) {
            for (int j = 0; j < searchableData.length; j++) {
                if(Arrays.asList(data[i]).contains(searchableData[j]))
                    result[k++] = data[i];
            }
        }
        if(result[0][0]!= null)
            System.out.println("Here is the data that you searched for: ");
        return result;
    }

    //returns false if the data is invalid and ture if it is valid

    private static boolean checkData(String[] insertableData){
        if(insertableData==null)
            return false;
        if(insertableData.length != 4)
            return false;
        String phoneNumber = insertableData[2];
        String email = insertableData[3];
        int count = 0;
        for (int i = 0; i < email.length(); i++) {
            if(email.charAt(i) == '@') {
                for (int j = i; j < email.length(); j++) {
                    if (email.charAt(j) == '.')// since there are email addresses that have more than 1 dot after '@'
                        count++;
                }
                if (count == 0)
                    return false;
                break;
            }
        }
        if(!(phoneNumber.length() == 9 ||
                (phoneNumber.charAt(0) == '+' && phoneNumber.length() == 12)))
            return false;
        else if(!(phoneNumber.chars().allMatch(Character::isDigit)
                || (phoneNumber.substring(1).chars().allMatch(Character::isDigit) && phoneNumber.charAt(0) == '+')))
            return false;
        return true;
    }

    //checks if the data is valid for inserting it in the dataset
    // returns true if there is no data with the same name and surname
    private static boolean checkChangeableData(String[][] data, String[] insertableData){
        if(insertableData.length != 4)
            return false;
        if(insertableData != null && Objects.equals(insertableData[0], insertableData[0])
                && Objects.equals(insertableData[1], insertableData[1]))
            return true;
        return false;
    }


    //checking if i already have the data in the dataset or not
    private static boolean isInTheDataset(String[][] data, String[] insertableData, String[] changeableData, int i){
        // if name and surname are the program considers that the user wants to enter new phone number or email
        if(changeableData != null && Objects.equals(changeableData[0], insertableData[0])
                && Objects.equals(changeableData[1], insertableData[1]))
            return false;
        for (int j = 0; j < i; j++) {
            if(compare(data[j],insertableData))
                return true;
        }
        return false;
    }


    //compares given names and surnames
    private static boolean compare(String[] arr1, String[] arr2){
        for (int i = 0; i < 4; i++) {
            if(!Objects.equals(arr1[i], arr2[i]))
                return false;
        }
        return true;
    }
}
