package stores;

/** runway store */

public class Runway {
    private int ID;
    private String occupied;
    private String mode;
    private String status;
    private int timeRemaining;

    public Runway(int _ID,String _occupied,String _mode,String _status,int _timeRemaining){
        ID = _ID;
        occupied = _occupied;
        mode = _mode;
        status = _status;
        timeRemaining = _timeRemaining;
    }

    public int getID(){
            return ID;
        }

    public String getOccupied(){
            return occupied;
        }

    public String getMode(){
            return mode;
        }

    public String getStatus(){
            return status;
        }

    public int getTimeRemaining(){
        return timeRemaining;
    }
    

    public int setID(int newID){
        if (newID > 0 && newID < 11){
        ID = newID;
        return 1;}
        return 0;
    }

    public int setOccupied(String newOccupied){
        occupied = newOccupied;
        return 1;
    }

    public int setMode(String newMode){
        if (newMode == "landing" || newMode == "takeoff" || newMode == "mixed"){
        mode = newMode;
        return 1;}
        return 0;
    }

    public int setStatus(String newStatus){
        if (newStatus == "available"){
        status = newStatus;
        return 1;}
        return 0;
    }

    public int setTimeRemaining(int newTimeRemaining){
        if (newTimeRemaining > 0){
        timeRemaining = newTimeRemaining;
        return 1;}
        return 0;
    }



}