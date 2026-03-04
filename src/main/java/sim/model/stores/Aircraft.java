package sim.model.stores;

import java.time.LocalTime; 

/** aircaft store */

public class Aircraft {
    private String callsign;
    private String operator;
    private String origin;
    private LocalTime time;
    private int altitude;
    private int groundSpeed;
    private int fuel;
    private String emergency;

    public Aircraft(String _callsign,String _operator,String _origin,LocalTime _time,int _altitude,int _groundSpeed,int _fuel,String _emergency){
        callsign = _callsign;
        operator = _operator;
        origin = _origin;
        time = _time;
        altitude = _altitude;
        groundSpeed = _groundSpeed;
        fuel = _fuel;
        emergency = _emergency;
    }
    
    public String getCallsign(){
        return callsign;
    }

    public String getOperator(){
        return operator;
    }

    public String getOrigin(){
        return origin;
    }

    public LocalTime getTime(){
        return time;
    }

    public int getAltitude(){
        return altitude;
    }

    public int getGroundspeed(){
        return groundSpeed;
    }

    public int getFuel(){
        return fuel;
    }

    public String getEmergency(){
        return emergency;
    }


    public int setCallsign(String newCallsign){
        if (newCallsign.length() > 0 && newCallsign.length() < 10){
        callsign = newCallsign;
        return 1;}
        else {
            return 0;
        }
    }

    public int setOperator(String newOperator){
        operator = newOperator;
        return 1;
    }

    public int setOrigin(String newOrigin){
        origin = newOrigin;
        return 1;
    }

    public int setTime(LocalTime newTime){
        time = newTime;
        return 1;
    }

    public int setAltitude(int newAltitude){
        if (newAltitude > 0 && newAltitude < 40000){
        altitude = newAltitude;
        return 1;}
        return 0;
    }

    public int setGroundspeed(int newGroundspeed){
        if (newGroundspeed > 0){
        groundSpeed = newGroundspeed;
        return 1;}
        return 0;
    }

    public int setFuel(int newFuel){
        if (newFuel > 0){
        fuel = newFuel;
        return 1;}
        return 0;
    }

    public int setEmergency(String newEmergency){
        emergency = newEmergency;
        return 1;
    }


}
