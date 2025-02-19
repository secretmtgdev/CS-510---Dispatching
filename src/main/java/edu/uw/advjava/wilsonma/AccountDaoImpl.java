package edu.uw.advjava.wilsonma;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.uw.pce.advjava.services.AccountDao;
import edu.uw.pce.advjava.support.account.Account;
import edu.uw.pce.advjava.support.account.AccountException;
import edu.uw.pce.advjava.support.account.BankAccount;
import edu.uw.pce.advjava.support.account.USAddress;

/**
 * Account dao implementation.
 * 
 * @author Michael Wilson
 */
public class AccountDaoImpl implements AccountDao {
	/** Name of the accounts file. */
	private final String ACCOUNT_FILE = "account.dat";
	
	/** Name of the address file. */
	private final String ADDRESS_FILE = "address.properties";
	
	/** Name of the bank account information file. */
	private final String BANK_FILE = "bank.dat";
	
	/** Directory under /target in Maven. */
	private final File ACCOUNTS_DIR = new File("target", "accounts");
	
	/** Logger for class. */
	private final Logger logger = Logger.getLogger(AccountDaoImpl.class.getName());
	
	private boolean isClosed = false;
	
	/**
	 * Simple constructor to ensure that the accounts directory is created.
	 */
	public AccountDaoImpl() {
		ACCOUNTS_DIR.mkdir();
	}

	/**
	 * Get all the account names.
	 * 
	 * @return All the account names.
	 * 
	 * @throws AccountException If getting the account names fail.
	 */
	@Override
	public Set<String> accounts() throws AccountException {
		logger.info("Getting all account names.");
		handleClose();
		String[] accounts = ACCOUNTS_DIR.list();
		if (accounts == null) {
			return Set.of();
		}
		return Stream.of(accounts).collect(Collectors.toSet());
	}

	/**
	 * Close the DAO and release the resources used.
	 * All operations should throw an AccountException if the DAO is already closed.
	 * 
	 * @throws AccountException If resources are unable to be released.
	 */
	@Override
	public void close() throws AccountException {
		logger.info("Closing the account dao.");
		isClosed = true;
	}

	/**
	 * Adds an account.
	 * 
	 * @param account The account to add.
	 * 
	 * @throws AccountException If the creation fails.
	 */
	@Override
	public void createAccount(Account account) throws AccountException {
		logger.info("Creating an account for " + account.getHolderName());
		handleClose();
		createOrUpdate(account);
		logger.info("Created the account.");
	}

	/**
	 * Removes the account.
	 * 
	 * @param accountName The name of the account to remove.
	 * 
	 * @throws AccountException If the removal process fails.
	 */
	@Override
	public void deleteAccount(String accountName) throws AccountException {
		logger.info("Deleting account " + accountName);
		handleClose();
		File directory = new File("target/accounts/" + accountName);
		if (directory.exists()) {
			deleteDirectory(directory);	
			logger.info("Deleted account.");
		} else {
			logger.info("No such directory.");
		}
	}

	/**
	 * Lookup an account based on the account name.
	 * 
	 * @param accountName The name of the account to get.
	 * 
	 * @return The account associated with the given name.
	 * 
	 * @throws AccountException If the retrieval fails.
	 */
	@Override
	public Account retrieveAccount(String accountName) throws AccountException {
		logger.info("Getting account with the name " + accountName);
		handleClose();
		try {
			File accountDirectory = new File(ACCOUNTS_DIR, accountName);
			if (!accountDirectory.exists() && accountDirectory.isDirectory()) {
				String accountPath = "target/accounts/" + accountName + "/" + ACCOUNT_FILE;
				String addressPath = "target/accounts/" + accountName + "/" + ADDRESS_FILE;
				String bankPath = "target/accounts/" + accountName + "/" + BANK_FILE;
				
				FileInputStream accountFile = new FileInputStream(accountPath);
				FileInputStream addressFile = new FileInputStream(addressPath);
				FileInputStream bankFile = new FileInputStream(bankPath);
				
				Account account = AccountService.readFile(accountFile);
				USAddress address = AddressService.readFile(addressFile);
				BankAccount bank = BankService.readFile(bankFile);
				
				account.setAddress(address);
				account.setBankAccount(bank);
				logger.info("Returning the account information.");
				return account;
			} 
		} catch (IOException e) {
			String exceptionMessage = "Could not get the account information: " + e.getMessage();
			logger.severe(exceptionMessage);
			throw new AccountException(exceptionMessage);
		}
		
		return null;
	}

	/**
	 * Updates a given account.
	 * 
	 * @param account The account to update.
	 * 
	 * @throws AccountException If updating the account fails.
	 */
	@Override
	public void updateAccount(Account account) throws AccountException {
		logger.info("Updating the account for " + account.getName());
		handleClose();
		createOrUpdate(account);
	}
	
	/**
	 * Private helper method to create or update to an account.
	 */
	private void createOrUpdate(Account account) throws AccountException {
		try {
			File accountDirectory = new File(ACCOUNTS_DIR, account.getName());
			accountDirectory.mkdir(); // don't care if directory was already made
			File newAccountFile = new File(accountDirectory, ACCOUNT_FILE);
			FileOutputStream fos = new FileOutputStream(newAccountFile);
			AccountService.writeFile(fos, account);
			
			USAddress address = account.getAddress();
			if (address != null) {
				File newAddressFile = new File(accountDirectory, ADDRESS_FILE);
				FileOutputStream fosA = new FileOutputStream(newAddressFile);
				AddressService.writeFile(fosA, address);
			}
			
			BankAccount bankAccount = account.getBankAccount();
			if (bankAccount != null) {
				File newBankFile = new File(accountDirectory, BANK_FILE);
				FileOutputStream fosB = new FileOutputStream(newBankFile);
				BankService.writeFile(fosB, bankAccount);
			}
		} catch (IOException e) {
			String exceptionMessage = "Failed to create the account: " + e.getMessage();
			logger.severe(exceptionMessage);
			throw new AccountException(exceptionMessage);
		}
	}

	/**
	 * Private helper method to throw an exception if the DAO is already closed.
	 * 
	 * @throws AccountException If the account DAO is closed.
	 */
	private void handleClose() throws AccountException {
		if (isClosed) {
			throw new AccountException("DAO is already closed");
		}
	}
	
	/**
	 * Performs a DFS approach to delete all of an account.
	 * 
	 * @param directory The directory to run through and delete.
	 */
	private void deleteDirectory(File directory) {
		if (directory.exists()) {
			for (File file : directory.listFiles()) {
				if (file.isDirectory()) {
					deleteDirectory(file);
				} else {
					file.delete();
				}
			}
		}
		
		directory.delete();
	}
}
