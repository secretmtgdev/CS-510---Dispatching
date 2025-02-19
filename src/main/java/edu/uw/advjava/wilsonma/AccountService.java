package edu.uw.advjava.wilsonma;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

import edu.uw.pce.advjava.support.account.Account;
import edu.uw.pce.advjava.support.account.AccountException;

/**
 * Class to assist with reading and writing to an account file.
 * Because the file will contain the actual content of the account it will be a .dat file.
 * Why .dat vs .bin? The file contains text and not just binary data.
 * 
 * @author Michael Wilson
 */
public class AccountService {
	/** Default value to use if something is null. Specific to writeUTF. */
	private static final String DEFAULT_STRING = "NULL";
	
	/** Provides detailed information of what's happening in the class. */
	private static final Logger logger = Logger.getLogger(AccountService.class.getName()); 
	
	/** Default constructor. */
	public AccountService() {}
	
	/**
	 * Reads contents from an account file and returns the appropriate account object.
	 * 
	 * @param fis The file input stream to read from.
	 */
	public static Account readFile(FileInputStream fis) {
		logger.info("Reading account information");
		try {
			DataInputStream dis = new DataInputStream(fis);
			String accountName = dis.readUTF();
			int passwordLength = dis.readInt();
			byte[] passwordHash = null;
			if (passwordLength >= 0) {
				passwordHash = new byte[passwordLength];
				dis.readFully(passwordHash);
			}
			
			int balance = dis.readInt();
			String holderName = dis.readUTF();
			String phone = dis.readUTF();
			String email = dis.readUTF();
			Account account = new Account(accountName, passwordHash, balance, holderName);
			account.setPhone(phone);
			account.setEmail(email);
			return account;
		} catch (IOException e) {
			logger.severe("Could not read from the file: " + e.getMessage());
		} catch (AccountException e) {
			logger.severe("Could not create an account from the file: " + e.getMessage());
		}
		
		return null;
	}
	
	/**
	 * Writes the contents of an account to a file.
	 * The contents stored are account name, password, full name, phone, and email.
	 * 
	 * @param fos The file output stream to write content to.
	 * @param account The account information to process.
	 * @throws IOException A Failure in the write occurs.
	 */
	public static void writeFile(FileOutputStream fos, Account account) throws IOException {
		logger.info("Writing account information.");
		try {
			DataOutputStream dos = new DataOutputStream(fos);
			dos.writeUTF(getOrDefault(account.getName()));
			dos.writeInt(account.getPasswordHash().length);
			dos.write(account.getPasswordHash());
			dos.writeInt(account.getBalance());
			dos.writeUTF(getOrDefault(account.getHolderName()));
			dos.writeUTF(getOrDefault(account.getPhone()));
			dos.writeUTF(getOrDefault(account.getEmail()));
		} catch (FileNotFoundException e) {
			logger.severe("Could not find the associated file: " + e.getMessage());
		} catch (IOException e) {
			logger.severe("Could not write to file: " + e.getMessage());
		}
		
		logger.info("Finished writing account information.");
	}
	
	/**
	 * Private helper method to handle the null case for writeUTF.
	 * 
	 * @param val The value to check for nullness.
	 * @return The value if it is non-null otherwise the null string.
	 */
	private static String getOrDefault(String val) {
		return val == null ? DEFAULT_STRING : val;
	}
}
