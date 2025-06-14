package dataheaven;

public class NodeInfo {
    private String visibleMessage;
    private String ivFile;
    private String ivFileName;
    public NodeInfo(String visibleMessage, String ivFile, String ivFileName) {
        this.visibleMessage = visibleMessage;
        this.ivFile = ivFile;
        this.ivFileName = ivFileName;
    }
    public String getVisibleMessage() {
        return visibleMessage;
    }
    public String getIvFile() {
        return ivFile;
    }
    public String getIvFileName() {
        return ivFileName;
    }
    @Override
    public String toString() {
        return visibleMessage;
    }
}