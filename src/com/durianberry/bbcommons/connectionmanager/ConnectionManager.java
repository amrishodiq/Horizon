package com.durianberry.bbcommons.connectionmanager;

import java.io.IOException;

import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.system.GlobalEventListener;


public class ConnectionManager implements GlobalEventListener {
	protected static final int MAX_CONFIG_NUMBER = 5;
	protected static final int CONFIG_NONE = -1;

	protected static final int WIFI_CONFIG = 0;
	protected static final int TCP_CONFIG = 1;
	protected static final int SERVICE_BOOK_CONFIG = 2;
	protected static final int BES_CONFIG = 3;
	protected static final int BIS_CONFIG = 4;

	private AbstractConfiguration[] connections = null;
	private boolean _bisSupport = true; // Boolean representing whether BIS-B is
									// supported by device.

	protected int currConfigID = CONFIG_NONE;

	// singleton
	private static ConnectionManager instance = null;

	// singleton
	public static ConnectionManager getInstance() {
		if (instance == null) {
			instance = new ConnectionManager();
		}

		return instance;
	}

	public ConnectionManager() {
		connections = initConnectionType();
	}

	private AbstractConfiguration[] initConnectionType() {
		connections = new AbstractConfiguration[MAX_CONFIG_NUMBER];
		connections[WIFI_CONFIG] = new WiFiConfig();
		connections[TCP_CONFIG] = new TcpConfig();
		connections[SERVICE_BOOK_CONFIG] = new ServiceBookConfig();
		connections[BES_CONFIG] = new BESConfig();
		connections[BIS_CONFIG] = new BISConfig();
		return connections;
	}

	private void reloadServiceBook() {
		_bisSupport = ConnectionUtils.isBISAvailable();

		connections[SERVICE_BOOK_CONFIG]
				.setUrlParameters(AbstractConfiguration.BASE_CONFIG_PARAMETERS
						+ ConnectionUtils.getServiceBookOptionsNew());
	}

	private boolean isAvailable(int configuration) {
		switch (configuration) {
		case WIFI_CONFIG:
			return (ConnectionUtils.isWifiActive() && ConnectionUtils
					.isWifiAvailable());
		case TCP_CONFIG:
		case BES_CONFIG:
			return !ConnectionUtils.isDataBearerOffline();
		case BIS_CONFIG:
			return (!ConnectionUtils.isDataBearerOffline() && _bisSupport);
		case SERVICE_BOOK_CONFIG:
			return (!ConnectionUtils.isDataBearerOffline() && !connections[SERVICE_BOOK_CONFIG]
					.getUrlParameters().trim()
					.equals(AbstractConfiguration.BASE_CONFIG_PARAMETERS));
		default:
			break;
		}
		return false;
	}

	private void closeConnection(Connection conn) {
		// Close the connection in case it got opened
		if (conn != null) {
			try {
				conn.close();
			} catch (Exception e) {

			}
		}
	}

	public synchronized Connection open(String url) throws IOException {
		return open(url, Connector.READ_WRITE, true);
	}

	public synchronized Connection open(String url, int accessMode,
			boolean enableTimeoutException) throws IOException {

		System.out.println("Opening url: "+url);
		
//		Connection ret = null;
		
//		try {
//			String fullUrl = url
//					+ ";deviceside=false;ConnectionType=mds-public";
//			
//			if (url.startsWith("https"))
//				fullUrl += ";EndToEndRequired";
//			ret = Connector.open(fullUrl, accessMode,
//					enableTimeoutException);
//			setCommonRequestProperty(ret);
//			return ret;
//		} catch (Exception ioe) {
//			currConfigID = CONFIG_NONE;
//			closeConnection(ret);
//			ret = setupConnection(url, accessMode, enableTimeoutException);
//		}
//		return ret;
		
		System.out.println("Trying TCP");
		if (isConnectionAllowed(TCP_CONFIG)) {
			// user should set the APN on their devices, no APN injection
			Connection ret = Connector.open(url
					+ AbstractConfiguration.BASE_CONFIG_PARAMETERS);
			setCommonRequestProperty(ret);
			return ret;
		}

		System.out.println("Trying WiFi");
		if (!ConnectionUtils.isWifiActive()
				|| !ConnectionUtils.isWifiAvailable()) {
			if (currConfigID == WIFI_CONFIG) {
				currConfigID = TCP_CONFIG;
			}
		}

		if (currConfigID > 0 && ConnectionUtils.isDataBearerOffline()) {
			if (ConnectionUtils.isWifiActive()
					&& ConnectionUtils.isWifiAvailable()) {
				currConfigID = WIFI_CONFIG;
			} else {
				throw new IOException();
			}
		}

		if (currConfigID < 0) {
			return setupConnection(url, accessMode, enableTimeoutException);
		} else {
			// check if the current config is already allowed
			if (!isConnectionAllowed(currConfigID)) {
				currConfigID = CONFIG_NONE;
				return setupConnection(url, accessMode, enableTimeoutException);
			}

			Connection ret = null;
			try {
				String fullUrl = url
						+ connections[currConfigID].getUrlParameters();
				
				System.out.println("Full url: "+fullUrl);
				
				if (currConfigID == BIS_CONFIG) {
					if (url.startsWith("https"))
						fullUrl += ";EndToEndRequired";
				}
				ret = Connector.open(fullUrl, accessMode,
						enableTimeoutException);
				setCommonRequestProperty(ret);
				return ret;
			} catch (Exception ioe) {
				currConfigID = CONFIG_NONE;
				closeConnection(ret);
				ret = setupConnection(url, accessMode, enableTimeoutException);
			}
			return ret;
		}
	}

	private Connection setupConnection(String url, int accessMode,
			boolean enableTimeoutException) throws IOException {

		Connection ret = null;
		String requestUrl = null;
		String detailedErrorMsr = "";

		for (int i = 0; i < MAX_CONFIG_NUMBER; i++) {
			try {

				if (isConnectionAllowed(i)) {
					currConfigID = i % connections.length;
					String options = connections[i].getUrlParameters();
					requestUrl = url + options;
				} else {
					continue;
				}

				if (currConfigID == BIS_CONFIG) {
					if (requestUrl.startsWith("https"))
						requestUrl += ";EndToEndRequired";
				}
				ret = Connector.open(requestUrl, accessMode,
						enableTimeoutException);
				setCommonRequestProperty(ret);
				return ret;
			} catch (Exception ioe) {
				// print out detailed error message
				switch (i) {
				case WIFI_CONFIG:
					detailedErrorMsr += "\nWiFi conn: ";
					break;
				case TCP_CONFIG:
					detailedErrorMsr += "\nTCP conn: ";
					break;
				case SERVICE_BOOK_CONFIG:
					detailedErrorMsr += "\nWAP conn: ";
					break;
				case BES_CONFIG:
					detailedErrorMsr += "\nBES conn: ";
					break;
				case BIS_CONFIG:
					detailedErrorMsr += "\nBIS conn: ";
					break;
				default:
					break;
				}

				detailedErrorMsr += ioe.getMessage();
				closeConnection(ret);
				ret = null;
			}
		}

		if (ret != null) {
			return ret;
		} else {
			currConfigID = CONFIG_NONE;
			if (detailedErrorMsr != null)
				throw new IOException(detailedErrorMsr);
			else
				throw new IOException("No route to blog");
		}
	}

	// For now, all available connection is allowed
	private boolean isConnectionAllowed(int configNumber) {
		if (!isAvailable(configNumber)) {
			return false;
		}

		switch (configNumber) {
		case WIFI_CONFIG:
			return (ConnectionUtils.isWifiActive()
					&& ConnectionUtils.isWifiAvailable() && true);
		case TCP_CONFIG:
			return false;
		case SERVICE_BOOK_CONFIG:
			return true;
		case BES_CONFIG:
			return true;
		case BIS_CONFIG:
			return true;
		default:
			return false;
		}
	}

	/**
	 * Setting the common http connection properties
	 * 
	 * @param conn
	 * @throws IOException
	 */
	private void setCommonRequestProperty(Connection conn) throws IOException {
		try {
			if (conn instanceof HttpConnection) {
				HttpConnection connCasted = (HttpConnection) conn;
				connCasted.setRequestProperty("User-Agent",
						LibraryConfig.customUserAgent);
			}
		} catch (IOException e) {
			throw e;
		}
	}

	/**
	 * Invoked when the specified global event occured. The eventOccurred method
	 * provides two object parameters and two integer parameters for supplying
	 * details about the event itself. The developer determines how the
	 * parameters will be used.
	 * 
	 * For example, if the event corresponded to sending or receiving a mail
	 * message, the object0 parameter might specify the mail message itself,
	 * while the data0 parameter might specify the identification details of the
	 * message, such as an address value.
	 * 
	 * @param guid
	 *            - The GUID of the event.
	 * @param data0
	 *            - Integer value specifying information associated with the
	 *            event.
	 * @param data1
	 *            - Integer value specifying information associated with the
	 *            event.
	 * @param object0
	 *            - Object specifying information associated with the event.
	 * @param object1
	 *            - Object specifying information associated with the event.
	 */
	public void eventOccurred(long guid, int data0, int data1, Object object0,
			Object object1) {
		if (guid == ServiceBook.GUID_SB_ADDED
				|| guid == ServiceBook.GUID_SB_CHANGED
				|| guid == ServiceBook.GUID_SB_OTA_SWITCH
				|| guid == ServiceBook.GUID_SB_OTA_UPDATE
				||
				// This item was added to the JDE in v4.1. If compiling in that
				// version uncomment this line
				// and otherwise leave it out.
				guid == ServiceBook.GUID_SB_POLICY_CHANGED
				|| guid == ServiceBook.GUID_SB_REMOVED) {

			reloadServiceBook();

		}
	}
}
