package src.org.truebd.gp;

import javax.ejb.Local;

@Local
public interface GPChargingConnectorNGW {
	public int doChargeNGW(String phone, String amount, String productID, String description, String GPServiceID);
}