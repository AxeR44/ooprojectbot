package Helpers;

public class OS {

    public enum OSName{
        WINDOWS, LINUX, UNSUPPORTED
    };

    protected static OSName osName = null;

    private static void detectOS(){
        String osProp = System.getProperty("os.name").toLowerCase();
        if(osProp.contains("win")){
            osName = OSName.WINDOWS;
        }else if(osProp.contains("nix") || osProp.contains("nux") || osProp.contains("aix")){
            osName = OSName.LINUX;
        }else{
            osName = OSName.UNSUPPORTED;
        }
    }

    public static OSName getOS(){
        if(osName == null)
            detectOS();
        return osName;
    }

    public static boolean isOSSupported(){
        return getOS()==OSName.LINUX || getOS() == OSName.WINDOWS;
    }
}