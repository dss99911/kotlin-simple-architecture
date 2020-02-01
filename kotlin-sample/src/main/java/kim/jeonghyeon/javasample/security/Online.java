package kim.jeonghyeon.javasample.security;

import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.util.encoders.Hex;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;

import javax.crypto.Cipher;

public class Online {

    static final String DIGEST_ALGO = "SHA-1";
    static final String ENCRYPTION_ALGO = "RSA/ECB/PKCS1Padding";
    public static String vendor = "trueCredit";
    public static String returnURL = "https://www.google.com";
    static String email = "test@perfios.com";
    static String server = "demo.perfios.com";
    static String privateKey = "-----BEGIN RSA PRIVATE KEY-----\n" +
            "MIIJJwIBAAKCAgEA0JJA203QjHfYVDLx7ouQS+kSP+CLPDgmJEqdETkf6WdELjUr\n" +
            "vl2NM8nH+/a3ZZJ+IhOjiYFHN60JbQHCK09vUch3ooRcWk6NIi0JmwBQJHtTJxVb\n" +
            "dHWgIu3FqoztGpqJYWZU8rAtkZCsfwmbpYF3/DCX6scdXNnPnKwbY9VcC6TCvDIJ\n" +
            "uuEaNw9alWzKRO71XGA2W+E8yDFbjRYROJoKlUAFvxkVmaDfvxYXcDoG6uZfoyNe\n" +
            "2mBnsmc1sTm821GKqVk8sTd5qR5ks0YkSHMFKZ0Y42gKLm+EJ8RWXkJmlzbr+fOd\n" +
            "zrW6ZZCSo7nZjcIdRqKIl//I2H1CYfOPa3zxaADor/z3+MFLY6RPEUCzGBMFqMCn\n" +
            "OArgcAY5x74/8iX35hhsVIWLnc8UbBV9Dwld21rf8pywAAoLR79AO0yifWZ/Jbi7\n" +
            "xG4lE4UquwEIcJdKa1fYRBQA1DC2bPtfOUe+9WiFrqDKdJrRxT3yNfXIyszfDTJE\n" +
            "7J6YSoEeg/NAieExkJBLkKuQJf8zRnCI7V+5/AwAjTMv/BZTPA4vMn7PCbIOCo9O\n" +
            "yA9JBPrkoilECz0xyouzzvOWlRBpWOGYRS2K6FhsX6VSTMvKgrKmJkP0wAerDuIj\n" +
            "BLTDbDJa1JX9VpMAB2HseQuciBcJge0Gwk2yme+tgWuAAHDGWH8D+PIwzt8CAwEA\n" +
            "AQKCAgAIHllF8A55GUUjaeQ+69HPEqByOhRRYMIjUYAxpm6WkD14Kvur6kpPxL/z\n" +
            "D9RtsLkWLHuGI7EY+rROOO5M70efAFWfztUlcr9NoEEqdmWoNeCzucIYk2eLVIQI\n" +
            "MnocOf20+928oJz1eJMuUrprQ9He0Clpqxx6e8P4Dj72sNxnW5904eG9E3wIvqFB\n" +
            "Bb4N1ttYICqdgvPxuvIcWMqM0Hou+xH+MUTE+R0cfAinhqq0RAle+UZ865m9uQS/\n" +
            "BEdUpiTmmKGjKEylB5jV3ZoV89w0lXfdijQzQIX9YkgWXTFPG4Ut8oym+i3qPClp\n" +
            "FBfiy7aGBFeaywsPWjjWRTvE7FUdLELPbrfOHniwdFpPUYX9+cT8Sgx5/B3yPqvb\n" +
            "GZMDkls3L+GUhXI1U1qh4x5bvtJZoeu3mPdRR4aaZ/01SblqDNmklkCtvZd//Guj\n" +
            "Ur2ybvhpLVJRFlRRloBevgpIh10XiasdnK+TBQ7FGmb6rwEnvfNjn/Y5a9i2mSck\n" +
            "CCRmMzUG4Yri+tSY7Zpk8xScXkI94W48NpQ7yV2B3h3SlHWpQp1dazveE4madQgS\n" +
            "Uxjikh06HTMQoNMmE2ncYy3AQYJruLLmrORqHjCHCEYiL4KVfm8WXBVPcT2Ad++N\n" +
            "gtulIQqwSCuH34sD9AVniSPTkdCpSW33gub2igw9l566XvgBcQKCAQEA8MG7A61z\n" +
            "tkyu5p1D4HT17CdLu1NvdYuZtpC1OxWCDT41QnpODNeJXFuH49DTtYHabhltGY2g\n" +
            "wNrW0APFW4MxI97fy28+9dnC3lGU9qQYnqcF6u7WaTqOk0qVl8bmqqTLF6vqLo5G\n" +
            "NCJzpGR0p/kNRmdj3t8S798JY+bZfQ5907p87ZSb+w8cL6wYj/7vS4JaQmon+9GN\n" +
            "W4ZHAaNMz1yO0apK/bIPq7XFaAVIpqq4YTMl5hb92cl9ABglvYKhT6iMmhPgZ4P/\n" +
            "fwo1cnzYdOOBzgdhVx38kNxytuJtQF4KYTBh6FBt5pd98V4q7eJruboFTyVj0OOl\n" +
            "o0efKoQhkNZXNwKCAQEA3cbZhkNPhvMsQchQdBTWnC9XmSujmAw+6su3jLAX2WyQ\n" +
            "to7vygQvL5cmDOtEV1y17yYbEdgv1bHnMOfV4s07X9wKII1zR2XzbpaKQUhtlJom\n" +
            "ITI+Vgk3LqLPa4Uy1ldj8T/MSy3cP8Tg4udC3G3SSaP1qLZ+2yqbvwMprj/chl4U\n" +
            "e0yhOW1+ZobSm9dregOin4ctcFMOVLZavoDJ2/rJhoLatE42l5cP8VoLnpOJWdt/\n" +
            "LCrvBFkYnAO/VHDRMzdvMzG5Km3C8/l7CAwnz5eddUp8JwBDMVrpSBZqGmX/WxMN\n" +
            "DaYTEKjD/zLCnt4FIKR5hc/5Y8VBHH6o4t4sgohJmQKCAQBmRtastEXg3ui6PXr1\n" +
            "YpqKbRMmDEbgV6LARO9In1V1TMURbaW0nOfUnWm4JSGbDQP/G7wWS+1LoGv7SG7R\n" +
            "9oSsqWTHJoZHqO+4xE2VDZcL5waDjFz3rRGejuPH645YVdOVRcohZv157lzULHzG\n" +
            "Uu4Jx6hMqNWBo214zDv+fWaEyMeDoks1N3EQ63ovyexcVo8DgddkidwUUpHdp0Ld\n" +
            "IIPnnAFpjvhLSNgI3Z3Ed+gGGtVxzrieiZMZtChsPo9KqN66IdGGCDRFAtA7x7+o\n" +
            "wHGHVelLOgjufUgaW0xwOMMOEKC3MVvOxvKQ1CFGl5aN+vGAcvDZb6wRFj8Or70W\n" +
            "q4e3AoIBAEPUKXtS4uPXQ9XquhCGvwyNvjDuk95RAH03STASO3kYJzpuhRA4L2ZS\n" +
            "yCRzYDeH92zpLqZKhHbLLnZasaPoORycY8yI9pUDrWxJRoeo6zrj2n2UrFQMBYCR\n" +
            "8vZjiqTbnYmPhaAIqrAmOtGrAVwBiqtTJjMvfaTyZygIg5rzlTLTW1hKm5TA1EPv\n" +
            "QELBo/FxYy9/XHpaLKvkCeTx5ssM7M3i/5jdwvyiCxI6BAu/++Yrp72OdAPUbB5r\n" +
            "Vv0wdAtCDRSPRJ6ha1rLFsHjVdm7+UCCQ256OhLCR07w7Aqe33avYPKntHUH6MoI\n" +
            "cFW+ebr+EtzGOvQ0xDE+29BnOx3BKSECggEAQEbc06wSmKxeMZTsRceNnHEbbrY+\n" +
            "y9QZRJSpDFGl4A84fwqLtTGvxsiZfuX3Jshw8SvOo3czQCWSlrxXMjJjsyH0txlL\n" +
            "p26DLGg09JmXUr6I7YYR92CadsB8UAerPlOAJDXRJeR7PtocbSCyqaPREAr7IXMU\n" +
            "hwWMBLQEMUDX/n74fPgYZRj9MyBkMQrrS4oxiLOCITxgMSbEGoKHPgOJfdkAlU2K\n" +
            "8sHu2OmlhRhTgb62lNk0Zs/aJG2035Hb3t+PaAbDs6I5Q8kWF5kIK5kOzEWlBMGc\n" +
            "s9T3TyPvjgTAe6JEmNla1mx6amvmZXwBBVAz8k/CyqxbQWvlRgNZxnlv8Q==\n" +
            "-----END RSA PRIVATE KEY-----";
    static String applicationId = "test";
    public static String payloadNetbanking = "<payload>\n" +
            "<apiVersion>2.1</apiVersion>" +
            "<vendorId>" + vendor + "</vendorId>\n" +
            "<txnId>" + applicationId + "</txnId>\n" +
            "<transactionCompleteCallbackUrl>https://www.example.com</transactionCompleteCallbackUrl>" +
            "<emailId>#email#</emailId>\n" +
            "<destination>netbankingFetch</destination>\n" +
            "<employmentType>Salaried</employmentType>" + //SelfEmployed
            "<employerName>abcd</employerName>" +
            "<productType>pl</productType>" +//
            "<returnUrl>" + returnURL + "</returnUrl>\n" +
            "</payload>";
    public static String payloadStatement = "<payload>\n" +
            "<apiVersion>2.1</apiVersion>" +
            "<vendorId>" + vendor + "</vendorId>\n" +
            "<txnId>" + applicationId + "</txnId>\n" +
            "<transactionCompleteCallbackUrl>https://www.example.com</transactionCompleteCallbackUrl>" +
            "<emailId>#email#</emailId>\n" +
            "<employmentType>Salaried</employmentType>" + //SelfEmployed
            "<employerName>abcd</employerName>" +
            "<productType>pl</productType>" +//
            "<destination>statement</destination>\n" +
            "<returnUrl>" + returnURL + "</returnUrl>\n" +
            "</payload>";
    static String perfiosTransactionId = "98ER1577961442446";
    static String format = "xlsx";//json,xlsx

    public static String getTxnStatusPayload() {
        return "<payload>\n" +
                "<apiVersion>2.1</apiVersion>" +
                "<vendorId>" + vendor + "</vendorId>\n" +
                "<txnId>" + applicationId + "</txnId>\n" +
                "</payload>";
    }


    public static String getRetrievePayload() {
        return "<payload>\n" +
                "<apiVersion>2.1</apiVersion>" +
                "<vendorId>" + vendor + "</vendorId>\n" +
                "<txnId>" + applicationId + "</txnId>\n" +
                "<perfiosTransactionId>" + perfiosTransactionId + "</perfiosTransactionId>\n" +
                "<reportType>" + format + "</reportType>\n" +
                "</payload>";
    }

    public static String getDeleteTransactionPayload() {
        return "<payload>\n" +
                "<apiVersion>2.1</apiVersion>" +
                "<vendorId>" + vendor + "</vendorId>\n" +
                "<perfiosTransactionId>" + perfiosTransactionId + "</perfiosTransactionId>\n" +
                "</payload>";
    }


    public static void main(String[] args) throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        String signature = getSignature(ENCRYPTION_ALGO, DIGEST_ALGO, buildPrivateKey(privateKey), "<payload><apiVersion>2.1</apiVersion><vendorId>trueCredit</vendorId><txnId>test</txnId><transactionCompleteCallbackUrl>https://dev1.truebalance.cc/api/v2/referral-loan/rupeelend/webhooks/qa</transactionCompleteCallbackUrl><emailId>154a9207aa42c0b240aaaffa5a83f53fb064a53a29807ddf17e4f849827e9fea55f08decdf6e8ccedc7aafe31a6e0ea44f2e53c7938e58f31ecfee2694518f2af061db85100bf591e6f63d7ba9f65a0dae9e68e82ac1727727993b491073462b328b95734597060c89f180e38fc89d372bb4356f52647b4f799b7a628a0305a2ec92f1c68aefb28945f2d76bd53b3833cea3a249dbb78cffcfeb85e0a1aa03e443b3e5abda026b860b87b668aef62751e4234da0757db60b01140931d9efb4c49377969383724c3d63c4cf2d373934bd76eff8355fd71743ef88ceb4e34c3f5c6f816618a6e905f3c40bd3b0104b785ab3ad20b3131d0ab3f465cc3568d37d39b4a1ee11972f05231c04e55a92eeaa8ab14971b27152142a6997136556b5722f968df189e01de91ff081d070561a609cd90f25606b6da8e927850179307a9f2331aa22425d73287973d01995123250be40994b3dd8fc8a39371ff724978027275d331be2374f48f091877f25184d1f84024bc1442771f2a53631dc2ec3258883d39b8789f0fcfa3b06bba23f9c9039412838bf42664eddd88e034fece8cc051afe5cc0d648e8a7ff8316ba6ae502e29552b9aa59bd86dee0c75b705befc3dc09a4083d3d4ff0e8466261c8d0e51f8e5466577ed78a51407acb7231a46656a7ca264a943a30cbfc99fbac447b859bb3faf3497ddc4c649c5781aede53aae36279</emailId><destination>netbankingFetch</destination><employmentType>Salaried</employmentType><employerName>abcd</employerName><productType>pl</productType><returnUrl>https://www.google.com</returnUrl></payload>");
        System.out.print(signature);
//        if(args.length > 1 && args[0] != null ){
//            if("encrypt".equals(args[0])){
//                if(args[1] != null) {
//                    String encrypt = encrypt(args[1], ENCRYPTION_ALGO,buildPublicKey(privateKey));
//                    System.out.print(encrypt);
//                } else {
//                    throw new Exception("Wrong number of arguments provided.");
//                }
//            } else if ("signature".equals(args[0])){
//                if(args[1] != null) {
//                    String signature = getSignature(ENCRYPTION_ALGO, DIGEST_ALGO, buildPrivateKey(privateKey), args[1]);
//                    System.out.print(signature);
//                } else {
//                    throw new Exception("Wrong number of arguments provided.");
//                }
//            }
//        } else {
//
//            String argPerfiosTxnId = System.getProperty("perfiosTransactionId") ;
//            if(argPerfiosTxnId != null) perfiosTransactionId = argPerfiosTxnId;
//
//            String argTxnId = System.getProperty("applicationId") ;
//            if(argTxnId != null) applicationId = argTxnId;
//
//            System.out.println("This program helps you try out Perfios APIs to initiate and track Perfios online transactions. " +
//                    "\nIt generates HTML files that can be opened in a browser to initiate or track the transaction.");
//
//            /** Create a folder for customer */
//            File folder = new File(vendor);
//            String message ="\n\nnetbanking and statement APIs are the APIs to start the transaction. \n" +
//                    "Only integration supported to start the transaction is through autopost form as in the netbanking and statement htmls.\n" +
//                    "All other APIs are xml over HTTP and do not need browser to be present.\n" +
//                    "You can directly invoke those APIs using other mechanisms.\n\n" +
//                    "Trying this program:\n" +
//                    "\t(1)First run the program and it will generate the netbanking and statement upload files.\n" +
//                    "\t(2)Depending upon whether you have requested these features to be available, you should be able to start the transactions.\n" +
//                    "\t(3)netbanking and statment HTML start the perfios transaction using browser to browser integration.\n" +
//                    "\t\t(3.1)To start netbanking transaction, open netbanking_* file in your browser..\n" +
//                    "\t\t(3.2)To start statement upload transaction, open statement_* file in your browser..\n" +
//                    "\t(4)You can then check the status of all transactions using txnstatus API.\n" +
//                    "\t\t(4.1)To check the status of transaction, open txnstatus_* file in your browser. \n" +
//                    "\t\t\tThis API could also be accessed without a browser. Without autoform load request\n" +
//                    "\t(5)To retrieve a report, you will need to re-run the program. Change the value of perfiosTransactionId variable in your program.\n" +
//                    "\t\t Compile and run the java program. open retrieve_* file in your browser. This API could also be accessed without a browser.\n" +
//                    "\t\t Without autoform load request\n" +
//                    "\t(5)To delete the transaction related artifacts, you will need to re-run the program. \n" +
//                    "\t\tChange the value of perfiosTransactionId variable in your program. Compile and run the java program. open delete_* \n" +
//                    "\t\tfile in your browser. This API could also be accessed without a browser. Without autoform load request\n" +
//                    "You can pass applicationId and perfiosTransactionId through command line by providing system properties too. " +
//                    "For e.g. java -DperfiosTransactionId=HDJDJ com.perfios.sample.OnlineSamplePNBHousing\n" +
//                    "\t(6)For more details please refer the API guide.\n";
//
//            System.out.println(message);
//
//            if(!folder.exists()) folder.mkdir();
//
//            System.out.println("Your files will be created in the following location: " + folder.getAbsolutePath());
//
//            /** Create files for the customer */
//
//            String  myHTML = genericCreateHTML(Online.payloadNetbanking);
//            createFile("netbanking", myHTML);
//            myHTML = genericCreateHTML(Online.payloadStatement);
//            createFile("statement", myHTML);
//
//            myHTML = genericCreateHTML(getTxnStatusPayload(), "txnstatus");
//            createFile("txnstatus", myHTML);
//
//            myHTML = genericCreateHTML(getRetrievePayload(), "retrieve");
//
//            createFile("retrieve", myHTML);
//
//            myHTML = genericCreateHTML(getDeleteTransactionPayload(), "delete");
//            createFile("delete", myHTML);
//
//
//        }

    }

    private static void createFile(String classification,
                                   String myHTML) {
        String filename = vendor + "/" + classification + "_" + server + ".html";

        try {
            PrintWriter out = new PrintWriter(filename);
            out.print(myHTML);
            out.close();
            System.out.println("Successfully created file " + filename);
        } catch (Exception e) {
            System.out.println("Error while creating file " + filename);
            e.printStackTrace();
        }
    }

    private static String genericCreateHTML(String payload) {
        return genericCreateHTML(payload, null);
    }

    private static String genericCreateHTML(String payload, String operation) {

        String emailEncrypted = encrypt(email, ENCRYPTION_ALGO, buildPublicKey(privateKey));
        payload = payload.replaceAll("\n", "");
        payload = payload.replaceAll("#email#", emailEncrypted);

        String signature = getSignature(ENCRYPTION_ALGO, DIGEST_ALGO, buildPrivateKey(privateKey), payload);
        if (operation == null) operation = "start";
        String myHTML =
                "<html>\n" +
                        "	<body onload='document.autoform.submit();'>\n" +
                        "		<form name='autoform' method='post' action='https://" + server + "/KuberaVault/insights/" + operation + "'>\n" +
                        "			<input type='hidden' name='payload' value='" + payload + "'>\n" +
                        "			<input type='hidden' name='signature' value='" + signature + "'>\n" +
                        "		</form>\n" +
                        "	</body>\n" +
                        "</html>\n";
        return myHTML;
    }


    /**
     * make hash of payload
     * base16(encrypt(base16(sha1(condense(payload)))));
     * base 64 has = +, and html query string may occur error with that string. that's the reason to use base 16
     *
     * @param encryptAlgo
     * @param digestAlgo
     * @param k
     * @param xml
     * @return
     */
    public static String getSignature(String encryptAlgo, String digestAlgo, Key k, String xml) {
        String dig = makeDigest(xml, digestAlgo);
        return encrypt(dig, encryptAlgo, k);
    }

    private static PrivateKey buildPrivateKey(String privateKeySerialized) {
        StringReader reader = new StringReader(privateKeySerialized);
        PrivateKey pKey = null;
        try {
            PEMReader pemReader = new PEMReader(reader);
            KeyPair keyPair = (KeyPair) pemReader.readObject();
            pKey = keyPair.getPrivate();
            pemReader.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
        return pKey;
    }

    private static PublicKey buildPublicKey(String privateKeySerialized) {
        StringReader reader = new StringReader(privateKeySerialized);
        PublicKey pKey = null;
        try {
            PEMReader pemReader = new PEMReader(reader);
            KeyPair keyPair = (KeyPair) pemReader.readObject();
            pKey = keyPair.getPublic();
            pemReader.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
        return pKey;
    }

    public static String makeDigest(String payload, String digestAlgo) {
        String strDigest = "";
        try {
            MessageDigest md = MessageDigest.getInstance(digestAlgo);
            md.update(payload.getBytes(StandardCharsets.UTF_8));
            byte[] digest = md.digest();
            byte[] encoded = Hex.encode(digest);
            strDigest = new String(encoded);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strDigest;
    }

    public static String encrypt(String raw, String encryptAlgo, Key k) {
        String strEncrypted = "";
        try {
            Cipher cipher = Cipher.getInstance(encryptAlgo);
            cipher.init(Cipher.ENCRYPT_MODE, k);
            byte[] encrypted = cipher.doFinal(raw.getBytes(StandardCharsets.UTF_8));
            byte[] encoded = Hex.encode(encrypted);
            strEncrypted = new String(encoded);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strEncrypted;
    }

}