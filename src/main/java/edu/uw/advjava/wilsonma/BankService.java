package edu.uw.advjava.wilsonma;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

import edu.uw.pce.advjava.support.account.AccountException;
import edu.uw.pce.advjava.support.account.BankAccount;
import edu.uw.pce.advjava.support.account.BankAccountType;
import edu.uw.pce.advjava.support.account.BankingInstitution;

/**
 * Class to assist with reading and writing to a bank file.
 * 
 * @author Michael Wilson
 */
public class BankService {
	/** Default value to use if something is null. Specific to writeUTF. */
	private static final String DEFAULT_STRING = "NULL";
	
	/** Provides detailed information of what's happening in the class. */
	private static final Logger logger = Logger.getLogger(BankService.class.getName()); 
	
	/** Default constructor. */
	public BankService() {}
	
	/**
	 * Reads contents from an account file and returns the appropriate account object.
	 * 
	 * @param fis The file input stream to read from.
	 */
	public static BankAccount readFile(FileInputStream fis) throws AccountException {
		logger.info("Reading bank information.");
		try {
			DataInputStream dis = new DataInputStream(fis);
			BankAccountType bankType = BankAccountType.valueOf(dis.readUTF());
			String bankName = dis.readUTF();
			String bankRoute = dis.readUTF();
			BankingInstitution bankInstituion = new BankingInstitution(bankName, bankRoute);
			String holder = dis.readUTF();
			String accountNumber = dis.readUTF();
			return new BankAccount(bankType, bankInstituion, holder, accountNumber);
		} catch (IOException e) {
			String exceptionMessage = "Could not read bank information: " + e.getMessage();
			logger.severe(exceptionMessage);
			throw new AccountException(exceptionMessage);
		}
	}
	
	/**
	 * Writes the bank contents of an account to a file.
	 * 
	 * @param fos The file output strema to write content to.
	 * @param bankAccount The bank information to process.
	 * @throws AccountException 
	 * 
	 * @throws IOException A failure in the write occurs.
	 */
	public static void writeFile(FileOutputStream fos, BankAccount bankAccount) throws AccountException {
		logger.info("Writing bank information.");
		try {
			DataOutputStream dos = new DataOutputStream(fos);
			dos.writeUTF(bankAccount.type().name());
			dos.writeUTF(bankAccount.bank().name());
			dos.writeUTF(bankAccount.bank().routingNumber());
			dos.writeUTF(bankAccount.holder());
			dos.writeUTF(bankAccount.accountNumber());
		} catch (IOException e) {
			String exceptionMessage = "Failed to write to file: " + e.getMessage();
			logger.severe(exceptionMessage);
			throw new AccountException(exceptionMessage);
		}
	}
}
