package edu.uw.advjava.wilsonma;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import edu.uw.pce.advjava.support.account.AccountException;
import edu.uw.pce.advjava.support.account.StateCode;
import edu.uw.pce.advjava.support.account.USAddress;

/**
 * Class to assist with reading and writing to an address file.
 * This can be a text file.
 * 
 * @author Michael Wilson
 */
public class AddressService {	
	/** Street property. */
	private static final String STREET_PROP = "street";
	
	/** City property. */
	private static final String CITY_PROP = "city";
	
	/** State property. */
	private static final String STATE_PROP = "state";
	
	/** Zip property. */
	private static final String ZIP_PROP = "zip";
	
	/** Provides detailed information of what's happening in the class. */
	private static final Logger logger = Logger.getLogger(BankService.class.getName()); 
	
	/** Default constructor. */
	public AddressService() {}
	
	/**
	 * Reads contents from an account file and returns the appropriate account object.
	 * 
	 * @param fis The file input stream to read from.
	 * 
	 * @return The address that comes from the file.
	 */
	public static USAddress readFile(FileInputStream fis) throws AccountException {
		logger.info("Reading address information.");
		try {
			Properties addressProps = new Properties();
			addressProps.load(fis);
			String streetAddress = addressProps.getProperty(STREET_PROP);
			String city = addressProps.getProperty(CITY_PROP);
			String state = addressProps.getProperty(STATE_PROP);
			String zip = addressProps.getProperty(ZIP_PROP);
			USAddress address = new USAddress(streetAddress, city, StateCode.valueOf(state), zip);
			logger.info("Returning address information.");
			return address;
		} catch (IOException e) {
			String exceptionMessage = "Failed to read address information: " + e.getMessage();
			logger.severe(exceptionMessage);
			throw new AccountException(exceptionMessage);
		}
	}
	
	/**
	 * Writes the address contents of an account to a file.
	 * 
	 * @param fos The file output strema to write content to.
	 * @param address The address information to process.
	 * @throws AccountException 
	 * 
	 * @throws IOException A failure in the write occurs.
	 */
	public static void writeFile(FileOutputStream fos, USAddress address) throws AccountException {
		logger.info("Writing address information.");
		try {
			Properties addressProps = new Properties();
			String streetAddress = address.streetAddress();
			if (streetAddress != null) {
				addressProps.setProperty(STREET_PROP, streetAddress);
			}
			
			String city = address.city();
			if (city != null) {
				addressProps.setProperty(CITY_PROP, address.city());
			}
			
			String state = address.state().name();
			if (state != null) {
				addressProps.setProperty(STATE_PROP, address.state().name());
			}
			
			String zip = address.zipCode();
			if (zip != null) {
				addressProps.setProperty(ZIP_PROP, address.zipCode());
			}
			addressProps.store(fos, "Address details");
		} catch (IOException e) {
			String exceptionMessage = "Failed to write to file: " + e.getMessage();
			logger.severe(exceptionMessage);
			throw new AccountException(exceptionMessage);
		}
		
		logger.info("Finished writing address information.");
	}
}
