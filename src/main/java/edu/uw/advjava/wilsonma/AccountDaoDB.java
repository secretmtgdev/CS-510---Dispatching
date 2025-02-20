package edu.uw.advjava.wilsonma;

import java.sql.Statement;
import java.sql.Types;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;


import edu.uw.pce.advjava.services.AccountDao;
import edu.uw.pce.advjava.support.account.Account;
import edu.uw.pce.advjava.support.account.AccountException;
import edu.uw.pce.advjava.support.account.BankAccount;
import edu.uw.pce.advjava.support.account.BankAccountType;
import edu.uw.pce.advjava.support.account.BankingInstitution;
import edu.uw.pce.advjava.support.account.StateCode;
import edu.uw.pce.advjava.support.account.USAddress;

/**
 * Account dao implementation with SQL.
 * 
 * @author Michael Wilson
 */
public class AccountDaoDB implements AccountDao {
	/** Marker for an invalid id */
	private static final Integer INVALID_ID = -1;
	
	/** DB URL */
	private static String DB_URL = "jdbc:derby://localhost/memory:accountDb";
	
	/** DB username */
	private static String DB_USERNAME = "student";
	
	/** DB password */
	private static String DB_PASSWORD = "student";
	
	
	/** Insert a bank record */
	private static String INSERT_BANK_RECORD = """
			INSERT INTO bank (routing_number, name) \
			VALUES (?, ?)
			""";
	
	/** Determine if a bank record exists with a specific route number */
	private static String GET_RECORD_BY_ROUTING_NUMBER = """
			SELECT * \
			FROM bank \
			WHERE routing_number = ?
			""";
	
	/** Insert a bank account */
	private static String INSERT_BANK_ACCOUNT = """
			INSERT INTO bank_account (account_number, routing_number, account_type, holder_name) \
			VALUES (?, ?, ?, ?)
			""";
	
	/** Deletes a bank account */
	private static String DELETE_BANK_BY_ID = """
			DELETE FROM bank_account \
			WHERE bank_account_id = ?
			""";
	
	/** Get a bank account id by routing and account numbers */
	private static String GET_BANK_ACCOUNT_ID = """
			SELECT bank_account_id \
			FROM bank_account \
			WHERE routing_number = ? \
			AND account_number = ?
			""";
	
	/** Get the account information and bank name for a bank account id */
	private static String GET_BANK_ACCOUNT_BY_ID = """
			SELECT a.account_number, a.routing_number, a.account_type, a.holder_name, b.name \
			FROM bank_account a, bank b \
			WHERE bank_account_id = ? \
			AND a.routing_number = b.routing_number
			""";
	
	/** Names for the bank columns */
	private static String COLUMN_ACCOUNT_NUMBER = "account_number";
	private static String COLUMN_ROUTING_NUMBER = "routing_number";
	private static String COLUMN_ACCOUNT_TYPE = "account_type";
	
	/** Insert a postal address */
	private static String INSERT_POSTAL_ADDRESS = """
			INSERT INTO postal_address (street_address, city, state_code, zip_code) \
			VALUES (?, ?, ?, ?)
			""";
	
	/** Get the postal address id for an address */
	private static String GET_POSTAL_ADDRESS_ID = """
			SELECT postal_address_id \
			FROM postal_address \
			WHERE street_address = ? \
			AND city = ? \
			AND state_code = ? \
			AND zip_code = ?
			""";
	
	/** Get the address value for an address by postal address id */
	private static String GET_POSTAL_ADDRESS_BY_ID = """
			SELECT street_address, city, state_code, zip_code \
			FROM postal_address \
			WHERE postal_address_id = ?
			""";
	
	/** Delete an address by a given id */
	private static String DELETE_ADDRESS_BY_ID = """
			DELETE FROM postal_address \
			WHERE postal_address_id = ?
			""";
	
	/** Names for the address columns */
	private static String COLUMN_STREET_ADDRESS = "street_address";
	private static String COLUMN_CITY = "city";
	private static String COLUMN_STATE_CODE = "state_code";
	private static String COLUMN_ZIP_CODE = "zip_code";
	
	/** Insert account */
	private static String INSERT_ACCOUNT = """
			INSERT INTO account (name, password_hash, balance, holder_name, email, phone, postal_address_id, bank_account_id) \
			VALUES (?, ?, ?, ?, ?, ?, ?, ?)
			""";
	
	/** Get all accounts */
	private static String GET_ALL_ACCOUNT_NAMES = """
			SELECT name \
			FROM account
			""";
	
	/** Get account information for an account by account name */
	private static String GET_ACCOUNT_BY_NAME = """
			SELECT name, password_hash, balance, holder_name, email, phone, postal_address_id, bank_account_id \
			FROM account \
			WHERE name = ?
			""";
	
	/** Update account */
	private static String UPDATE_ACCOUNT = """
			UPDATE account \
			SET password_hash = ?, balance = ?, holder_name = ?, email = ?, phone = ?, postal_address_id = ?, bank_account_id = ? \
			WHERE name = ?
			""";
	
	/** Delete an account by name */
	private static String DELETE_ACCOUNT = """
			DELETE FROM account \
			WHERE name = ?
			""";
	
	/** Names for the account columns */
	private static String COLUMN_NAME = "name";
	private static String COLUMN_PASSWORD = "password_hash";
	private static String COLUMN_BALANCE = "balance";
	private static String COLUMN_HOLDER_NAME = "holder_name";
	private static String COLUMN_EMAIL = "email";
	private static String COLUMN_PHONE = "phone";
	private static String COLUMN_POSTAL_ADDRESS_ID = "postal_address_id";
	private static String COLUMN_BANK_ACCOUNT_ID = "bank_account_id";
	
	/** Logger for class. */
	private final Logger logger = Logger.getLogger(AccountDaoImpl.class.getName());
	
	/** State of dao being opened or closed */
	private boolean isClosed;
	
	/**
	 * Simple constructor to ensure that the accounts directory is created.
	 */
	public AccountDaoDB() {
		isClosed = false;
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
		try {
			Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(GET_ALL_ACCOUNT_NAMES);
			Set<String> accounts = new HashSet<>();
			while (resultSet.next()) {
				accounts.add(resultSet.getString(COLUMN_NAME));
			}
			logger.info("Returning accounts:" + accounts.toString());
			connection.close();
			return accounts;
		} catch (SQLException e) {
			String exceptionMessage = "Could not connect get accounts: " + e.getMessage();
			logger.severe(exceptionMessage);
			throw new AccountException(exceptionMessage);
		}
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
		try {
			Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
			PreparedStatement statement = connection.prepareStatement(INSERT_ACCOUNT);
			statement.setString(1, account.getName());
			statement.setBytes(2, account.getPasswordHash());
			statement.setInt(3, account.getBalance());
			getOrDefaultString(statement, 4, account.getHolderName());
			getOrDefaultString(statement, 5, account.getEmail());
			getOrDefaultString(statement, 6, account.getPhone());
			int postalId = getPostalAddressId(account.getAddress());
			if (postalId == INVALID_ID && account.getAddress() != null) {
				postalId = addAddress(connection, account.getAddress());
			}
			
			int bankId = getBankAccountId(account.getBankAccount());
			if (bankId == INVALID_ID && account.getBankAccount() != null) {
				bankId = addBankAccount(connection, account.getBankAccount());
			}
			getOrDefaultInteger(statement, 7, postalId);
			getOrDefaultInteger(statement, 8, bankId);
			statement.executeUpdate();
			connection.close();
		} catch (SQLException e) {
			String exceptionMessage = "Could not create an account: " + e.getMessage();
			logger.severe(exceptionMessage);
			throw new AccountException(exceptionMessage);
		}
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
		
		// There is no such account
		if (accountName.isEmpty() || (accountName) == null) {
			logger.info("ATTEMPTED TO DELETE A NON-EXISTENT ACCOUNT");
			return;
		}
		
		try {
			Account account = retrieveAccount(accountName);
			if (account == null) {
				return;
			}
			
			Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
			PreparedStatement statement = connection.prepareStatement(DELETE_ACCOUNT);
			statement.setString(1, accountName);
			statement.executeUpdate();
			
			statement = connection.prepareStatement(DELETE_BANK_BY_ID);
			int bankId = getBankAccountId(account.getBankAccount());
			statement.setInt(1, bankId);
			statement.executeUpdate();
			
			statement = connection.prepareStatement(DELETE_ADDRESS_BY_ID);
			int addressId = getPostalAddressId(account.getAddress());
			statement.setInt(1,  addressId);
			statement.executeUpdate();
			connection.close();
		} catch (SQLException e) {
			String exceptionMessage = "Could not delete account: " + e.getMessage();
			logger.severe(exceptionMessage);
			throw new AccountException(exceptionMessage);
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
			Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
			PreparedStatement statement = connection.prepareStatement(GET_ACCOUNT_BY_NAME);
			statement.setString(1, accountName);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				/* name, password_hash, balance, holder_name, email, phone, postal_address_id, bank_account_id */
				String name = resultSet.getString(COLUMN_NAME);
				byte[] password = resultSet.getBytes(COLUMN_PASSWORD);
				int balance = resultSet.getInt(COLUMN_BALANCE);
				String holderName = resultSet.getString(COLUMN_HOLDER_NAME);
				String email = resultSet.getString(COLUMN_EMAIL);
				String phone = resultSet.getString(COLUMN_PHONE);
				int postalAddressId = resultSet.getInt(COLUMN_POSTAL_ADDRESS_ID);
				int bankAccountId = resultSet.getInt(COLUMN_BANK_ACCOUNT_ID);
				
				Account account = new Account(name, password, balance, holderName);
				account.setEmail(email);
				account.setPhone(phone);
				
				/* street_address, city, state_code, zip_code */
				// Ensure that a postal address id is found
				if (postalAddressId != INVALID_ID) {
					logger.info("FOUND A VALID POSTAL ADDRESS, GETTING ADDRESS DETAILS");
					USAddress usAddress = getUSAddressById(connection, postalAddressId);
					if (usAddress != null) {
						account.setAddress(usAddress);
					}
				}
				
				/* a.account_number, a.routing_number, a.account_type, a.holder_name, b.name */
				// Ensure that a bank account id record is found
				if (bankAccountId != INVALID_ID) {
					logger.info("FOUND A VALID BANK, GETTING BANK DETAILS");
					BankAccount bankAccount = getBankAccountById(connection, bankAccountId);
					if (bankAccount != null) {
						account.setBankAccount(bankAccount);
					}
				}
				
				connection.close();
				return account;
			}
		} catch (SQLException e) {
			String exceptionMessage = "Could not retrieve account: " + e.getMessage();
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
		try {
			Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
			PreparedStatement statement = connection.prepareStatement(UPDATE_ACCOUNT);
			statement.setBytes(1, account.getPasswordHash());
			statement.setInt(2, account.getBalance());
			getOrDefaultString(statement, 3, account.getHolderName());
			getOrDefaultString(statement, 4, account.getEmail());
			getOrDefaultString(statement, 5, account.getPhone());
			getOrDefaultInteger(statement, 6, getPostalAddressId(account.getAddress()));
			getOrDefaultInteger(statement, 7, getBankAccountId(account.getBankAccount()));
			getOrDefaultString(statement, 8, account.getName());
			statement.executeUpdate();
			connection.close();
		} catch (SQLException e) {
			String exceptionMessage = "Could not update account: " + e.getMessage();
			logger.severe(exceptionMessage);
			throw new AccountException(exceptionMessage);
		}
		logger.info("Updated the account.");
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
	 * Private helper method to handle the null case for strings.
	 * 
	 * @param statement The prepared statement to update.
	 * @param idx The index in the prepared statement.
	 * @param val The value to check for nullness.
	 * 
	 * @throws SQLException If the value can't be set.
	 */
	private void getOrDefaultString(PreparedStatement statement, int idx, String val) throws SQLException {
		if (val == null) {
			statement.setNull(idx, Types.VARCHAR);
		} else {
			statement.setString(idx, val);
		}
	}
	
	/**
	 * Private helper method to handle the null case for ints.
	 * 
	 * @param statement The prepared statement to update.
	 * @param idx The index in the prepared statement.
	 * @param val The value to check for nullness.
	 * 
	 * @throws SQLException If the value can't be set.
	 */
	private void getOrDefaultInteger(PreparedStatement statement, int idx, int val) throws SQLException {
		if (val == INVALID_ID) {
			statement.setNull(idx, Types.INTEGER);
		} else {
			statement.setInt(idx, val);
		}
	}
	
	/**
	 * Gets a postal address id.
	 * 
	 * @return The postal address id
	 * @throws AccountException if the db fails to find.
	 */
	private int getPostalAddressId(USAddress address) throws AccountException {				
		logger.info("Getting the postal address id");
		if (address == null) {
			logger.info("ADDRESS IS NULL, RETURNING: " + INVALID_ID);
			return INVALID_ID;
		}
		
		try {
			Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
			PreparedStatement statement = connection.prepareStatement(GET_POSTAL_ADDRESS_ID);
			statement.setString(1, address.streetAddress());
			statement.setString(2, address.city());
			statement.setString(3, address.state().name());
			statement.setString(4, address.zipCode());
			ResultSet resultSet = statement.executeQuery();
			logger.info("SEARCHING DB FOR ADDRESS INFORMATION");
			if (resultSet.next()) {
				int postalAddressId = resultSet.getInt(COLUMN_POSTAL_ADDRESS_ID);
				connection.close();
				return postalAddressId;
			}
		} catch (SQLException e) {
			String exceptionMessage = "Could not get the postal address id: " + e.getMessage();
			logger.severe(exceptionMessage);
			throw new AccountException(exceptionMessage);
		}
		
		logger.info("COULD NOT FIND ADDRESS IN DB");
		return INVALID_ID;
	}
	
	/**
	 * Private helper method to get us address information based off of a address id.
	 * 
	 * @param connection The connection that has already been established by the caller.
	 * @param addressId The id of the address to find.
	 * @return The address associated with the given id.
	 * @throws SQLException If the bank account can't be found
	 */
	private USAddress getUSAddressById(Connection connection, int addressId) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(GET_POSTAL_ADDRESS_BY_ID);
		statement.setInt(1, addressId);
		ResultSet postalResultSet = statement.executeQuery();
		if (postalResultSet.next()) {
			logger.info("FOUND US ADDRESS, POPULATING DETAILS");
			String streetAddress = postalResultSet.getString(COLUMN_STREET_ADDRESS);
			String city = postalResultSet.getString(COLUMN_CITY);
			String stateCode = postalResultSet.getString(COLUMN_STATE_CODE);
			String zipCode = postalResultSet.getString(COLUMN_ZIP_CODE);
			return new USAddress(streetAddress, city, StateCode.valueOf(stateCode), zipCode);
		} else {
			return null;
		}
	}
	
	/**
     * Adds an address to the address DB.
     * 
     * @param connection The connection to the database.
     * @param address The address to insert.
     * @throws SQLException If the insertion of the address fails.
     * @return The id of the recently added address, used as a foreign key.
     */
    private int addAddress(Connection connection, USAddress address) throws SQLException {
    	logger.info("ADDING NEW ADDRESS TO DB");
    	/* street_address, city, state_code, zip_code */
    	// Need to return an id to bind the accoun to, use generated keys.
    	PreparedStatement statement = connection.prepareStatement(INSERT_POSTAL_ADDRESS, Statement.RETURN_GENERATED_KEYS);
    	statement.setString(1,  address.streetAddress());
    	statement.setString(2, address.city());
    	statement.setString(3, address.state().name()); // getName() gives the full name, name() truncates
    	statement.setString(4, address.zipCode());
    	statement.executeUpdate();
    	ResultSet key = statement.getGeneratedKeys();
    	if (key.next()) {
    		return key.getInt(1);
    	} else {
    		return INVALID_ID;
    	}
    }
    
    /**
	 * Gets the bank account id.
	 * 
	 * @return The bank account id
	 * @throws AccountExpception if the db fails to find.
	 */
	private int getBankAccountId(BankAccount bank) throws AccountException {
		logger.info("Getting the bank id");
		if (bank == null) {
			logger.info("BANK IS NULL, RETURNING: " + INVALID_ID);
			return INVALID_ID;
		}
		
		try {
			Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
			PreparedStatement statement = connection.prepareStatement(GET_BANK_ACCOUNT_ID);
			statement.setString(1,  bank.bank().routingNumber());
			statement.setString(2, bank.accountNumber());
			ResultSet resultSet = statement.executeQuery();
			logger.info("SEARCHING DB FOR BANK INFORMATION");
			if (resultSet.next()) {
				logger.info("FOUND BANK INFORMATION");
				int bankAccountId = resultSet.getInt(COLUMN_BANK_ACCOUNT_ID);
				connection.close();
				return bankAccountId;
			}
		} catch (SQLException e) {
			String exceptionMessage = "Could not get the bank id: " + e.getMessage();
			logger.severe(exceptionMessage);
			throw new AccountException(exceptionMessage);
		}
		
		logger.info("COULD NOT FIND BANK IN DB");
		return INVALID_ID;
	}
	
	/**
	 * Private helper method to get bank information based off of a bank id.
	 * 
	 * @param connection The connection that has already been established by the caller.
	 * @param bankId The id of the bank to find.
	 * @return The bank associated with the given id.
	 * @throws SQLException If the bank account can't be found
	 */
	private BankAccount getBankAccountById(Connection connection, int bankId) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(GET_BANK_ACCOUNT_BY_ID);
		statement.setInt(1,  bankId);
		ResultSet bankAccountResultSet = statement.executeQuery();
		if (bankAccountResultSet.next()) {
			String bankAccountNumber = bankAccountResultSet.getString(COLUMN_ACCOUNT_NUMBER);
			String bankRoutingNumber = bankAccountResultSet.getString(COLUMN_ROUTING_NUMBER);
			String bankType = bankAccountResultSet.getString(COLUMN_ACCOUNT_TYPE);
			String bankHolderName = bankAccountResultSet.getString(COLUMN_HOLDER_NAME);
			String bankName = bankAccountResultSet.getString(COLUMN_NAME);
			BankingInstitution bankingInstitution = new BankingInstitution(bankName, bankRoutingNumber);
			BankAccount bankAccount = new BankAccount(BankAccountType.valueOf(bankType), bankingInstitution, bankHolderName, bankAccountNumber);
			return bankAccount;
		}
		
		return null;
	}
    
    /**
     * Adds a bank to the bank DB.
     * 
     * @param connection The connection to the database.
     * @param bank The bank to insert.
     * @return The id of the recently added bank, used as a foreign key.
     * @throws SQLException If the insertion of the account fails.
     */
    private int addBankAccount(Connection connection, BankAccount bank) throws SQLException {
    	logger.info("ADDING NEW BANK TO DB");
    	if (!bankInstitutionExists(connection, bank.bank())) {
    		addBankInstitution(connection, bank.bank()); 		
    	}
   
    	/* account_number, routing_number, account_type, holder_name */
    	// Need to return an id to bind the accoun to, use generated keys.
    	PreparedStatement statement = connection.prepareStatement(INSERT_BANK_ACCOUNT, Statement.RETURN_GENERATED_KEYS);
    	statement.setString(1, bank.accountNumber());
    	statement.setString(2,  bank.bank().routingNumber());
    	statement.setString(3, bank.type().name());
    	statement.setString(4, bank.holder());
    	statement.executeUpdate();
    	ResultSet key = statement.getGeneratedKeys();
    	logger.info("ADDED THE BANK");
    	if (key.next()) {
    		return key.getInt(1);
    	} else {
    		return INVALID_ID;
    	}
    }
    
    /**
     * Inserts a banking institution to ensure there's a relationship between with the bank.
     * The bank_account references the bank DB
     * 
     * @param connection The connection to the database.
     * @param bank The institution to add.
     * @throws SQLException If the update fails.
     */
    private void addBankInstitution(Connection connection, BankingInstitution bank) throws SQLException {
    	logger.info("Adding bank institution");
    	PreparedStatement statement = connection.prepareStatement(INSERT_BANK_RECORD);
    	statement.setString(1, bank.routingNumber());
    	statement.setString(2, bank.name());
    	statement.executeUpdate();
    }
    
    /**
     * Determines if a bank record already exists.
     * 
     * @param connection The connection to the database.
     * @param bank The bank to check on.
     * @return True if the bank institution exists, otherwise false.
     * @throws SQLException If the operation fails.
     */
    private boolean bankInstitutionExists(Connection connection, BankingInstitution bank) throws SQLException {
    	logger.info("Checking to see if the bank institution exists");
    	PreparedStatement statement = connection.prepareStatement(GET_RECORD_BY_ROUTING_NUMBER);
    	statement.setString(1, bank.routingNumber());
    	ResultSet resultSet = statement.executeQuery();
    	return resultSet.next();
    }
}
