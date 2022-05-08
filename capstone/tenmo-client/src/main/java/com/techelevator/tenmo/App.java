package com.techelevator.tenmo;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;
import com.techelevator.tenmo.services.TransferService;
import com.techelevator.tenmo.services.UserService;

import javax.sound.midi.Soundbank;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private final UserService userService = new UserService(API_BASE_URL);
    private final TransferService transferService=new TransferService(API_BASE_URL);

    private AuthenticatedUser currentUser;

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }
    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if(currentUser.getToken() != null){
            userService.setAuthToken(currentUser.getToken());
            transferService.setAuthToken(currentUser.getToken());
        }
        if (currentUser == null) {
            consoleService.printErrorMessage();
        }
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

	private void viewCurrentBalance() {
		// TODO Auto-generated method stub
        System.out.println("Your current account balance is: $"+userService.getBalance(currentUser.getUser().getId()));
	}

	private void viewTransferHistory() {
		// TODO Auto-generated method stub
        Map<Long,String>transferType=new HashMap<>();
        transferType.put(1L,"Request");
        transferType.put(2L,"Send");
        Map<Long,String>transferStatus=new HashMap<>();
        transferStatus.put(1L,"Pending");
        transferStatus.put(2L,"Approved");
        transferStatus.put(3L,"Rejected");

        Transfer [] transfers = transferService.listOfTransfers(currentUser.getUser().getUsername());
        listOfTransfersPrinted(transfers, Math.toIntExact(userService.getAccountIdByUserId(Math.toIntExact(currentUser.getUser().getId()))));
       int transferId;
        boolean inputIsValid=false;
        while(inputIsValid==false){
            transferId=consoleService.promptForInt("Please enter transfer ID to view details (0 to cancel): ");
            if(transferId==0){
                inputIsValid=true;
            }
            for(Transfer transfer:transfers){
                if(transfer.getTransfer_id()==transferId){
                    inputIsValid=true;
                    System.out.println("\nTransfer Details\n");
                    System.out.println("Id: "+transfer.getTransfer_id());
                    System.out.println("From: "+transfer.getUsername_from());
                    System.out.println("To: "+transfer.getUsername_to());
                    System.out.println("Type: "+transferType.get(transfer.getTransfer_type_id()));
                    System.out.println("Status: "+transferStatus.get(transfer.getTransfer_status_id()));
                    System.out.println("Amount: $"+transfer.getAmount());
                }
            }
            if(inputIsValid==false){
                System.out.println("Invalid transfer id. Please input a valid id from the list above.");
            }
        }
	}

	private void viewPendingRequests() {
        // TODO Auto-generated method stub
        Map<Long, String> transferType = new HashMap<>();
        transferType.put(1L, "Request");
        transferType.put(2L, "Send");
        Map<Long, String> transferStatus = new HashMap<>();
        transferStatus.put(1L, "Pending");

        transferStatus.put(2L, "Approved");
        transferStatus.put(3L, "Rejected");

        Transfer[] transfers = transferService.listOfPendingTransfers(currentUser.getUser().getUsername());
        listOfPendingTransfersPrinted(transfers, Math.toIntExact(userService.getAccountIdByUserId(Math.toIntExact(currentUser.getUser().getId()))));
        int transferId=0;
        Transfer transferToReview = new Transfer();
        boolean inputIsValid = false;
        while (inputIsValid == false) {
            transferId = consoleService.promptForInt("\nPlease enter transfer ID to approve/reject (0 to cancel): ");
            if (transferId == 0) {
                inputIsValid = true;
            }
            for (Transfer transfer : transfers) {
                if (transfer.getTransfer_id() == transferId) {
                    inputIsValid = true;
                    transferToReview = transfer;
                    System.out.println("1: Approve\n" +
                            "2: Reject\n" +
                            "0: Don't approve or reject");
                }
            }
            if (inputIsValid == false) {
                System.out.println("Invalid transfer id. Please input a valid id from the list above.");
            }
        }
        if (transferId != 0) {
            int choice;
            boolean choiceValid = false;
            while (choiceValid == false) {
                choice = consoleService.promptForInt("Your input: ");
                if (choice == 0) {
                    choiceValid = true;
                } else if (choice == 1) {
                    if (transferToReview.getAmount().compareTo(userService.getBalance(currentUser.getUser().getId())) > 0) {
                        System.out.println("Transfer amount rejected due to insufficient funds.\n");
                    }
                    else {
                        transferToReview.setTransfer_status_id(2L);
                        transferService.updateTransfer(transferToReview);
                        transferService.transferMoney(transferToReview);
                        choiceValid = true;
                    }
                } else if (choice == 2) {
                        transferToReview.setTransfer_status_id(3L);
                        transferService.updateTransfer(transferToReview);
                        choiceValid = true;

                    }
                    else {
                    System.out.println("Your input was not accepted. Please choose an option from the list above.");
                }
            }
        }
    }

	private void sendBucks() {
		// TODO Auto-generated method stub
        User[] users=userService.displayAllUsers();
        for(User user:users) {
            if(!currentUser.getUser().getUsername().equals(user.getUsername())){
                System.out.println(user);
            }
        }

        int account_to = 0;
        boolean validation = false;
        while (validation == false) {
            account_to = consoleService.promptForInt("Enter userId you would like to transfer money to (0 to cancel): ");
            if (account_to == currentUser.getUser().getId()) {
                System.out.println("\nYou cannot send money to yourself, please enter another valid userID.\n");
            }
            boolean found = false;
            for(User user:users) {
                if(account_to == user.getId()){
                    found = true;
                }
            }
            if(account_to==0){
                found=true;
            }
            if (found == false){
                System.out.println("\nUserID not found. Please enter a valid userID to send money to.\n");
            }
            else if (found == true && account_to != currentUser.getUser().getId()){
                validation = true;
            }
        }
if(account_to!=0) {
    BigDecimal amount = new BigDecimal("0.00");
    boolean validationMoney = false;
    while (validationMoney == false) {
        amount = consoleService.promptForBigDecimal("Enter amount of $$ you wish to transfer (0 to cancel): ");
        if (amount==BigDecimal.valueOf(0)){
            validationMoney=true;
        }
        if (amount.compareTo(new BigDecimal("0.00")) <0) {
            System.out.println("\nTransfer amount should not be less than or equal to 0.\nPlease enter valid amount.\n");
        } else if (amount.compareTo(userService.getBalance(currentUser.getUser().getId())) > 0) {
            System.out.println("Transfer amount rejected due to insufficient funds.\n");
            viewCurrentBalance();
        } else {
            validationMoney = true;
        }
    }
if(amount!=BigDecimal.valueOf(0)) {
    Transfer transfer = new Transfer();
    transfer.setTransfer_type_id(2);
    transfer.setTransfer_status_id(2);
    transfer.setAccount_to(userService.getAccountIdByUserId(account_to));
    transfer.setAccount_from(userService.getAccountIdByUserId(Math.toIntExact(currentUser.getUser().getId())));
    transfer.setAmount(amount);
    transferService.transferMoney(transfer);
    transferService.logTransfer(transfer);
}
}
	}

	private void requestBucks() {
		// TODO Auto-generated method stub
        User[] users=userService.displayAllUsers();
        for(User user:users) {
            if(!currentUser.getUser().getUsername().equals(user.getUsername())){
                System.out.println(user);
            }
        }

        int account_from = 0;
        boolean validation = false;
        while (validation == false) {
            account_from = consoleService.promptForInt("Enter userId you would like to request money from (0 to cancel): ");

            if (account_from == currentUser.getUser().getId()) {
                System.out.println("\nYou cannot request money from yourself, please enter another valid userID.\n");
            }
            boolean found = false;
            if(account_from==0){
                found=true;
                validation=true;
            }
            for(User user:users) {
                if(account_from == user.getId()){
                    found = true;
                }
            }
            if (found == false){
                System.out.println("\nUserID not found. Please enter a valid userID to send money to.\n");
            }
            else if (found == true && account_from != currentUser.getUser().getId()){
                validation = true;
            }
        }
if(account_from!=0) {
    BigDecimal amount = new BigDecimal("0.00");
    boolean validationMoney = false;
    while (validationMoney == false) {
        amount = consoleService.promptForBigDecimal("Enter amount of $$ you wish to request (0 to cancel): ");
        if (amount.compareTo(new BigDecimal("0.00")) < 0) {
            System.out.println("\nTransfer amount should not be less than or equal to 0.\nPlease enter valid amount.\n");
        } else {
            validationMoney = true;
        }
    }
if(amount!=BigDecimal.valueOf(0)) {
    Transfer transfer = new Transfer();
    transfer.setTransfer_type_id(1);
    transfer.setTransfer_status_id(1);
    transfer.setAccount_from(userService.getAccountIdByUserId(account_from));
    transfer.setAccount_to(userService.getAccountIdByUserId(Math.toIntExact(currentUser.getUser().getId())));
    transfer.setAmount(amount);

    transferService.logTransfer(transfer);
}
}
	}

    private void listOfTransfersPrinted(Transfer[] transfers, int accountId){
        System.out.println("\nTransfers");
        System.out.println("ID      From/to        Amount");
        for(Transfer transfer: transfers){
            System.out.print(transfer.getTransfer_id() +"     ");
            if (transfer.getAccount_from() == accountId){
                System.out.print("To:   " + transfer.getUsername_to());
            }
            else{
                System.out.print("From: " + transfer.getUsername_from());
            }
            System.out.print("    $ " + transfer.getAmount());
            System.out.println();

        }
    }
    private void listOfPendingTransfersPrinted(Transfer[] transfers, int accountId){
        System.out.println("\nTransfers");
        System.out.println("ID       To          Amount");
        for(Transfer transfer: transfers){
            System.out.print(transfer.getTransfer_id() +"     ");
                System.out.print(transfer.getUsername_to());
            System.out.print("        $ " + transfer.getAmount());
            System.out.println();

        }
    }

}
