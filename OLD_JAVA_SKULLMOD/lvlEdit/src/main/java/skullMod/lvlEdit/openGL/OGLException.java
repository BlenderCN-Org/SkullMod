package skullMod.lvlEdit.openGL;

public class OGLException extends RuntimeException{
    private final String[] glErrors;

    public OGLException(String[] strings) {
        this.glErrors = strings;
    }

    public OGLException(String string){
        this.glErrors = new String[1];
        this.glErrors[0] = string;
    }

    public String getMessage(){
        //TODO ugly, beautify
        String message = "OGLException(";
        for(String error : glErrors){
            message += error + " ";
        }

        message += ")";
        return message;
    }
}
