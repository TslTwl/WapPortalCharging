package src.org.truebd.gp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Calendar;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import sun.misc.BASE64Encoder;
import org.apache.log4j.Logger;

/**
 * Session Bean implementation class GpCharge
 */
@Stateless
@LocalBean
public class GPChargingConnectorImplNGW implements GPChargingConnectorNGW {

    Logger logger = Logger.getLogger(this.getClass());
    public static final String description = "binodon24";

    public int doChargeNGW(String phone, String amount, String productID, String purchaseCategoryCode, String GPServiceID) {
        String response = "";
        int HTTPResponseCode = 0;
        // String res = "";
        // disable SSL certificate authentication
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }

        public void checkClientTrusted(X509Certificate[] certs, String authType) {
        }

        public void checkServerTrusted(X509Certificate[] certs, String authType) {
        }
    }};
        HostnameVerifier hv = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(hv);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // end of disable SSL certificate authentication

        try {
            // URL url = new URL("https://192.168.206.79:8181/ngp/1/payment/tel%3A%2B" + phone + "/transactions/amount");
            URL url = new URL("https://192.168.206.125:8181/ngp/1/payment/tel%3A%2B" + phone + "/transactions/amount");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");

            BASE64Encoder enc = new sun.misc.BASE64Encoder();
            String userPassword = "Truebd" + ":" + "Truebd@123";
            String encodedAuthorization = enc.encode(userPassword.getBytes());
            conn.setRequestProperty("Authorization", "Basic " + encodedAuthorization.trim());

            String clientCorrelator = Long.toString(Calendar.getInstance().getTimeInMillis());
            String referenceCode = clientCorrelator.substring(3);

            String input = "{\"endUserId\":\"tel:+" + phone + "\",\"transactionOperationStatus\":\"Charged\"," + "\"amount\":\"" + amount
                    + "\",\"referenceCode\":\"" + referenceCode + "\",\"description\":\"" + description + "\",\"currency\":\"BDT\","
                    + "\"clientCorrelator\":\"" + clientCorrelator + "\",\"onBehalfOf\":\"Truebd\",\"purchaseCategoryCode\":\""
                    + purchaseCategoryCode + "\"," + "\"channel\":\"Wap\",\"taxAmount\":\"0\",\"productID\":\"" + productID + "\",\"serviceID\":\""
                    + GPServiceID + "\"}";
            // System.out.println(input);
            logger.debug("Charge input: " + input);

            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();

            HTTPResponseCode = conn.getResponseCode();
            logger.debug(HTTPResponseCode);
            /*
             * if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) { throw
             * new RuntimeException("Failed : HTTP error code : " +
             * conn.getResponseCode());
             * 
             * }
             */

            BufferedReader br;

            if (conn.getResponseCode() >= 400) {
                br = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
            } else {
                br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            }

            // System.out.println("Output from Server .... \n");
            logger.debug("Charge response: ");
            while ((response = br.readLine()) != null) {
                //System.out.println(response);
                logger.debug("NGW response: " + response);
            }
            conn.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
            logger.error(e);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e);
        }

        return HTTPResponseCode;
    }
}
