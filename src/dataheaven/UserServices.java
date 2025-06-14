package dataheaven;

import java.io.File;
import java.security.MessageDigest;

public class UserServices {
    public static boolean checkUser(String user){
        StringBuilder sb = new StringBuilder();
        sb.append(System.getProperty("user.dir"));
        sb.append("\\users\\");
        sb.append(user);
        File dir = new File(sb.toString());
        return dir.exists();
    }
    public static boolean registerUser(String user){
        StringBuilder sb = new StringBuilder();
        sb.append(System.getProperty("user.dir"));
        sb.append("\\users\\");
        sb.append(user);
        File dir = new File(sb.toString());
        if(!dir.exists()){
            return dir.mkdirs();
        } else {
            return false;
        }
    }
    public static String generateUserSecret(String str1, String str2) {
        StringBuilder sb = new StringBuilder();
        for (char ch1 : str1.toCharArray()) {
            for (char ch2 : str2.toCharArray()) {
                sb.append(ch1);
                sb.append(ch2);
            }
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = messageDigest.digest(sb.toString().getBytes());
            StringBuilder hexStringBuilder = new StringBuilder();
            for (byte hashByte : hashBytes) {
                hexStringBuilder.append(String.format("%02X", hashByte));
            }
            return hexStringBuilder.toString();
        } catch(Exception e){
            return null;
        }
    }
}
