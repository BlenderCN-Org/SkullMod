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
    public Boolean shadowDirection; //Should not be used according to docs
    public boolean shadowDirectionUp = true;
    public boolean shadowDirectionDown = false;

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
                    if(fields.length != 3){ throw new IllegalArgumentException("ERROR"); }
                    this.stageSize = new LVL_StageSize(Integer.parseInt(fields[1]), Integer.parseInt(fields[2]));
                    break;
                case bottomClearanceIdentifier:
                    if(fields.length != 2){ throw new IllegalArgumentException("ERROR"); }
                    this.bottomClearance = Integer.parseInt(fields[1]);
                    break;
                case start1Identifier:
                    if(fields.length != 2){ throw new IllegalArgumentException("ERROR"); }
                    start1 = Integer.parseInt(fields[1]);
                    break;
                case start2Identifier:
                    if(fields.length != 2){ throw new IllegalArgumentException("ERROR"); }
                    start2 = Integer.parseInt(fields[1]);
                    break;
                case shadowDirectionIdentifier:
                    if(fields.length != 2){ throw new IllegalArgumentException("ERROR"); }

                    //Valid values U and D
                    switch(fields[1]){
                        case "D":
                            shadowDirection = shadowDirectionDown;
                            break;
                        case "U":
                            shadowDirection = shadowDirectionUp;
                            break;
                        default:
                            throw new IllegalArgumentException("ERROR");
                    }
                    break;
                case shadowDistanceIdentifier:
                    if(fields.length != 2){ throw new IllegalArgumentException("ERROR"); }
                    break;
                case lightIdentifier:
                    if(fields.length != 5 && fields.length != 8 && fields.length != 9 && fields.length != 10){ throw new IllegalArgumentException("ERROR"); }

                    break;
                case stageRelPath2DIdentifier:
                    if(fields.length != 2){ throw new IllegalArgumentException("ERROR"); }

                    break;
                case cameraTiltOptionsIdentifier:
                    if(fields.length != 4){ throw new IllegalArgumentException("ERROR"); }
                    break;
                case cameraSetupIdentifier:
                    if(fields.length != 2 && fields.length != 4){ throw new IllegalArgumentException("ERROR"); }
                    //rooftops night has only one, which z near and far to choose?

                    break;
                case musicIntroIdentifier:
                    if(fields.length != 2){ throw new IllegalArgumentException("ERROR"); }
                    break;
                case musicLoopIdentifier:
                    if(fields.length != 2){ throw new IllegalArgumentException("ERROR"); }
                    break;
                case musicInterruptIdentifier:
                    if(fields.length != 2){ throw new IllegalArgumentException("ERROR"); }
                    break;
                case musicOutroIdentifier:
                    if(fields.length != 2){ throw new IllegalArgumentException("ERROR"); }
                    break;
                default:
                    throw new IllegalArgumentException("Unknown configuration: " + fields[0]);
            }
        }
    }
}
