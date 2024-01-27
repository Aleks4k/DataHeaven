package dataheaven;

import java.io.File;

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
}
