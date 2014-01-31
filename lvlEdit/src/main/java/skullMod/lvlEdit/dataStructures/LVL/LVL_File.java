package skullMod.lvlEdit.dataStructures.LVL;

import java.io.Serializable;
import java.util.List;

//TODO test must and nice to haves when reading/writing
public class LVL_File implements Serializable{
    public final static String colon = ":";


    public final static String stageSizeIdentifier = "StageSize";
    public final static String bottomClearanceIdentifier = "BottomClearance";
    public final static String start1Identifier = "Start1";
    public final static String start2Identifier = "Start2";
    public final static String shadowDirectionIdentifier = "ShadowDir";
    public final static String shadowDistanceIdentifier = "ShadowDist";
    public final static String lightIdentifier = "Light";

    public final static String stageRelPath2DIdentifier = "2D";
    public final static String cameraTiltOptionsIdentifier = "3D";
    public final static String cameraSetupIdentifier= "CAMERA";

    public final static String musicIntroIdentifier = "Music_Intro";
    public final static String musicLoopIdentifier = "Music_Loop";
    public final static String musicInterruptIdentifier = "Music_InterruptIntro";
    public final static String musicOutroIdentifier = "Music_Outro";


    public LVL_StageSize stageSize;
    public int bottomClearance;
    public int start1, start2;

    //
    public boolean shadowDirection; //Sholud not be used according to docs
    public int shadowDistance; //Use this instead
    //

    public LVL_Light[] lights; //For Amb, Pt and Dir lights

    public String stageRelPath2D;
    public CameraTilt cameraTiltOptions; //Thank you Skullgirls team for documenting lvl files
    public CameraSetup cameraSetup;

    public String musicIntro;
    public String musicLoop;
    public boolean musicInterruptIntro;
    public String musicOutro; //Not used for now

    public LVL_File(String[] lvlContent){
        for(String line : lvlContent){
            String[] fields = line.split(" ");

            switch(fields[0]){
                case stageSizeIdentifier:
                    break;
                case bottomClearanceIdentifier:
                    break;
                case start1Identifier:
                    break;
                case start2Identifier:
                    break;
                case shadowDirectionIdentifier:
                    break;
                case shadowDistanceIdentifier:
                    break;
                case lightIdentifier:
                    break;
                case stageRelPath2DIdentifier:
                    break;
                case cameraTiltOptionsIdentifier:
                    break;
                case cameraSetupIdentifier:
                    break;
                case musicIntroIdentifier:
                    break;
                case musicLoopIdentifier:
                    break;
                case musicInterruptIdentifier:
                    break;
                case musicOutroIdentifier:
                    break;
                default:
                    throw new IllegalArgumentException("Unknown configuration: " + fields[0]);
            }
        }
    }
}
