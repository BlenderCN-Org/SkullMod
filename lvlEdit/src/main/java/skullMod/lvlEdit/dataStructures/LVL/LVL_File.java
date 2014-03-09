package skullMod.lvlEdit.dataStructures.LVL;

import skullMod.lvlEdit.dataStructures.completeLevel.StageSettings;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

//TODO test must and nice to haves when reading/writing
public class LVL_File implements Serializable{
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
    public static final boolean shadowDirectionUp = true;
    public static final boolean shadowDirectionDown = false;

    //public Boolean shadowDirection; //Should not be used according to docs
    //Modernize files if they are saved no more shadowDirections

    //TODO May be null (use object instead!)
    public Integer shadowDistance = StageSettings.defaultShadowDistance; //Use this instead FIXME hardcoded start value
    //

    public List<LVL_Light> lights; //For Amb, Pt and Dir lights

    public String stageRelPath2D;
    public CameraTilt cameraTiltOptions; //Thank you Skullgirls team for documenting lvl files
    public CameraSetup cameraSetup;

    public String musicIntro;
    public String musicLoop;
    public boolean musicInterruptIntro;
    public String musicOutro; //Not used for now

    //TODO meh, so much switching, reduce it
    public LVL_File(String[] lvlContent){
        lights = new ArrayList<>();


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
                            shadowDistance = StageSettings.defaultShadowDistance;
                            break;
                        case "U":
                            shadowDistance = -1 * StageSettings.defaultShadowDistance;
                            break;
                        default:
                            throw new IllegalArgumentException("ERROR");
                    }
                    break;
                case shadowDistanceIdentifier:
                    if(fields.length != 2){ throw new IllegalArgumentException("ERROR"); }
                    shadowDistance = Integer.parseInt(fields[1]);
                    break;
                case lightIdentifier:
                    switch(fields.length){
                        case 5:
                            if(!fields[1].equals(LVL_Light.LightType.AMBIENT.abbrevation)){ throw new IllegalArgumentException("ERROR"); }
                            lights.add(new LVL_Light(Float.parseFloat(fields[2]),Float.parseFloat(fields[3]),Float.parseFloat(fields[4])));
                            break;
                        case 8:
                            if(!fields[1].equals(LVL_Light.LightType.DIRECTIONAL.abbrevation)){ throw new IllegalArgumentException("ERROR"); }
                            lights.add(new LVL_Light(Float.parseFloat(fields[2]),Float.parseFloat(fields[3]),Float.parseFloat(fields[4]),
                                    Float.parseFloat(fields[5]),Float.parseFloat(fields[6]),Float.parseFloat(fields[7])));

                            break;
                        case 9:
                            if(!fields[1].equals(LVL_Light.LightType.POINT.abbrevation)){ throw new IllegalArgumentException("ERROR"); }
                            lights.add(new LVL_Light(Float.parseFloat(fields[2]),Float.parseFloat(fields[3]),Float.parseFloat(fields[4]),
                                    Float.parseFloat(fields[5]),Float.parseFloat(fields[6]),Float.parseFloat(fields[7]),Integer.parseInt(fields[8]), false));

                            break;
                        case 10:
                            if(!fields[1].equals(LVL_Light.LightType.POINT.abbrevation)){ throw new IllegalArgumentException("ERROR"); }
                            if(!fields[9].equals(LVL_Light.neverCullString)){ throw new IllegalArgumentException("ERROR"); }
                            lights.add(new LVL_Light(Float.parseFloat(fields[2]),Float.parseFloat(fields[3]),Float.parseFloat(fields[4]),
                                    Float.parseFloat(fields[5]),Float.parseFloat(fields[6]),Float.parseFloat(fields[7]),Integer.parseInt(fields[8]), true));
                            break;
                        default:
                            throw new IllegalArgumentException("ERROR");
                    }
                    break;
                case stageRelPath2DIdentifier:
                    if(fields.length != 2){ throw new IllegalArgumentException("ERROR"); }
                    stageRelPath2D = fields[1];
                    break;
                case cameraTiltOptionsIdentifier:
                    if(fields.length != 4){ throw new IllegalArgumentException("ERROR"); }
                    cameraTiltOptions = new CameraTilt(Float.parseFloat(fields[1]), Float.parseFloat(fields[2]), Float.parseFloat(fields[3]));
                    break;
                case cameraSetupIdentifier:
                    if(fields.length != 2 && fields.length != 4){ throw new IllegalArgumentException("ERROR"); }
                    //rooftops night has only one, which z near and far to choose?
                    //TODO fix default values
                    if(fields.length == 2){
                        cameraSetup = new CameraSetup(Float.parseFloat(fields[1]),0,0);
                    }else{
                        cameraSetup = new CameraSetup(Float.parseFloat(fields[1]),Integer.parseInt(fields[2]),Integer.parseInt(fields[3]));
                    }

                    break;
                case musicIntroIdentifier:
                    if(fields.length != 2){ throw new IllegalArgumentException("ERROR"); }
                    musicIntro = fields[1];
                    break;
                case musicLoopIdentifier:
                    if(fields.length != 2){ throw new IllegalArgumentException("ERROR"); }
                    musicLoop = fields[1];
                    break;
                case musicInterruptIdentifier:
                    if(fields.length != 2){ throw new IllegalArgumentException("ERROR"); }
                    int interruptIntro = Integer.parseInt(fields[1]);
                    if(interruptIntro == 0){
                        musicInterruptIntro = false;
                    }else if(interruptIntro == 1){
                        musicInterruptIntro = true;
                    }else{
                        throw new IllegalArgumentException("ERROR");
                    }
                    break;
                case musicOutroIdentifier:
                    if(fields.length != 2){ throw new IllegalArgumentException("ERROR"); }
                    musicOutro = fields[1];
                    break;
                default:
                    throw new IllegalArgumentException("Unknown configuration: " + fields[0]);
            }


        }

        //TODO validate: all must have fields
        //TODO validate: lights array
        //TODO check paths
        //TODO add replace used in river king casino
    }


    public static String[] prepareLVL(String filePath){
        //String filePath = "/home/netbook/Working_files/Skullgirls_extracted/levels/rooftops_night_3d.lvl";
        //TODO verify



        String fileContent;

        try {
            fileContent = new String(Files.readAllBytes(Paths.get(filePath)), "US-ASCII");
        } catch (IOException e) {
            //TODO better handling
            throw new IllegalArgumentException("NOPE");
        }

        String[] lvlLines = fileContent.split("[\\r\\n]+"); //Split on line endings

        for(int i = 0;i < lvlLines.length;i++){
            String line = lvlLines[i];

            line = line.replaceFirst("#.*",""); //Remove comments
            line = line.replaceAll(":",""); //Remove ":", this makes wrong files parsable theoretically, meh... TODO keep behaviour?
            lvlLines[i] = line.trim().replaceAll("\\s+", " "); //Change aby whitespace to a single space
        }

        ArrayList<String> result = new ArrayList<>();
        for (String lvlLine : lvlLines) {
            if (!lvlLine.equals("") && !lvlLine.startsWith("#")) { //TODO is # still required?
                result.add(lvlLine);
            }
        }

        // http://stackoverflow.com/questions/174093/toarraynew-myclass0-or-toarraynew-myclassmylist-size
        return result.toArray(new String[result.size()]);
    }

    public String toString(){
        return "LVL file";
    }
}
