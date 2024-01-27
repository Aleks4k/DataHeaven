package dataheaven;

public class NodeInfo {
    private String visibleMessage;
    private String hiddenMessage;
    public NodeInfo(String visibleMessage, String hiddenMessage) {
        this.visibleMessage = visibleMessage;
        this.hiddenMessage = hiddenMessage;
    }
    public String getVisibleMessage() {
        return visibleMessage;
    }
    public String getHiddenMessage() {
        return hiddenMessage;
    }
    @Override
    public String toString() {
        return visibleMessage;
    }
}