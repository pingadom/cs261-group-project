package sim.model.stores;




/** runway store */

public class Runway {
    private int ID;
    private String occupied;
    private RunwayMode mode;
    private RunwayStatus status;
    private double timeRemaining;

    public enum RunwayMode { NONE, LANDING, TAKEOFF, MIXED }
    public enum RunwayStatus { NONE, AVAILABLE, INSPECTION, SNOW, FAILURE }

    public Runway(int _ID,RunwayMode _mode,RunwayStatus _status,double _timeRemaining){
        ID = _ID;
        occupied = "";
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

    public RunwayMode getMode(){
            return mode;
        }

    public RunwayStatus getStatus(){
            return status;
        }

    public double getTimeRemaining(){
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

    public int setMode(RunwayMode newMode){
        mode = newMode;
        return 1;
    }

    public int setStatus(RunwayStatus newStatus){
        status = newStatus;
        return 1;
    }

    public int setTimeRemaining(double newTimeRemaining){
        if (newTimeRemaining > 0){
        timeRemaining = newTimeRemaining;
        return 1;}
        return 0;
    }



}