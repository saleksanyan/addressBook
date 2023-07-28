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
                String[] arr = searchData(data,insertableData);
                while(arr == null) {
                    System.out.println("There is no data. Please try again");
                    arr = searchData(data,insertableData);
                }
                System.out.println(Arrays.toString(arr));
            }else {
                while (!checkData(insertableData)) {
                    System.out.println("Entered data is invalid. Please try again.");
                    insertableData = readUser.nextLine().split(" ");
                }
                String[] changeableData;
                if (action.equalsIgnoreCase("INSERT")) {
                    data = insertData(data, insertableData);
                } else if (action.equalsIgnoreCase("REMOVE")) {
                    data = removeData(data, insertableData);
                } else {
                    System.out.println("Enter new data (enter the whole data\n" +
                            "include name, surname, phone number and email");
                    changeableData = readUser.nextLine().split(" ");
                    while (!checkChangeableData(data, changeableData)) {
                        System.out.println("Entered data is invalid. Please try again.");
                        changeableData = readUser.nextLine().split(" ");
                    }
                    changeData(data, changeableData, insertableData);
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
                if(!isInTheDataset(data,insertableData,i))
                    data[i] = insertableData;
                else
                    System.out.println("This information already exists in the dataset");

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
    private static String[][] insertData(String[][] data, String[] insertableData){
        String[][] resultedData = new String[data.length+1][4];
        if(isInTheDataset(data,insertableData, data.length)) {
            System.out.println("This information already exists in the dataset");
            return data;
        }
        for (int i = 0; i < data.length; i++) {
            resultedData[i] = data[i];
        }
        resultedData[data.length] = insertableData;
        System.out.println("Entered data is inserted!");
        return resultedData;
    }
    //helps you to remove from a data in a file
    private static String[][] removeData(String[][] data, String[] removableData) {
        String[][] resultedData = new String[data.length-1][4];
        for (int i = 0, j = 0; i < data.length; i++) {
            if(!compare(removableData, data[i])) {
                resultedData[j] = data[i];
                j++;
            }
        }
        System.out.println("Entered data is removed!");
        return resultedData;
    }
    //helps you to change a data in a file
    private static void  changeData(String[][] data, String[] insertableData, String[] changeableData) {
        if(isInTheDataset(data,insertableData, data.length)) {
            System.out.println("This information already exists in the dataset");
            return;
        }
        for (int i = 0; i < data.length; i++) {
            if(compare(changeableData, data[i])) {
                data[i] = insertableData;
            }
        }
        System.out.println("Entered data is changed!");

    }

    //helps you to search data in a file
    private static String[] searchData(String[][] data, String[] searchableData){
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < searchableData.length; j++) {
                if(Arrays.asList(data[i]).contains(searchableData[j]))
                    return data[i];
            }
        }
        System.out.println("Here is the data that you searched for: ");
        return null;
    }

    private static boolean checkData(String[] insertableData){
        if(insertableData==null)
            return false;
        if(insertableData.length != 4)
            return false;
        int dataLength = insertableData.length;
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
                (phoneNumber.charAt(0) == '+' && phoneNumber.length() == 10)))
            return false;
        else if(!(phoneNumber.chars().allMatch(Character::isDigit)
                || (phoneNumber.substring(1).chars().allMatch(Character::isDigit) && phoneNumber.charAt(0) == '+')))
            return false;
        return true;
    }

    //checks if the data is valid for inserting it in the dataset
    private static boolean checkChangeableData(String[][] data, String[] insertableData){
        if(insertableData.length != 4)
            return false;
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < 4; j++) {
                if(data[i][j].equals(insertableData[j]))
                    return true;
            }
        }
        return false;
    }


    //checking if i already have the data in the dataset or not
    private static boolean isInTheDataset(String[][] data, String[] insertableData, int i){
        for (int j = 0; j < i; j++) {
            if(compare(data[i],insertableData))
                return true;
        }
        return false;
    }


    //compares given names, phone numbers and emails of people
    private static boolean compare(String[] arr1, String[] arr2){
        for (int i = 0; i < 4; i++) {
            if(!Objects.equals(arr1[i], arr2[i]))
                return false;
        }
        return true;
    }
}
