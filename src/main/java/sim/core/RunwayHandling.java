package sim.core;

import sim.model.stores.*;

import java.time.LocalTime;

public class RunwayHandling{

    public void runwayHandling(List<Aircraft> arrivals, 
                            List<Aircraft> departures, 
                            List<Aircraft> takeOffQueue, 
                            HoldingPattern<Aircraft> holdingPattern,
                            List<Runway> runways,
                            List<Aircraft> postProcessing,
                            Clock clock){
        boolean flag = true;
        while (flag)  {
            flag = moveToHoldingPattern(arrivals,holdingPattern,clock);
        }               
        flag = true;
        while (flag){
            flag = moveToTakeOff(departures,takeOffQueue,clock);
        }
        boolean takeOffFlag = true;
        boolean landingFlag = true;
        while (takeOffFlag || landingFlag){
            if (landingFlag){
                landingFlag = landPlane(holdingPattern,runways,postProcessing,clock);
            }
            if (takeOffFlag) {
                takeOffFlag = takeOff(takeOffQueue,runways,postProcessing,clock);
            }
        }
                            }

        public boolean moveToHoldingPattern(List<Aircraft> arrivals, HoldingPattern<Aircraft> holdingPattern, Clock clock){
            if (arrivals.getSize() == 0) return false;

            if (arrivals.get(0).getValue().getTime().compareTo(clock.simulationTime) <= 0){
                LinkedListElement<Aircraft> arrival = arrivals.pop(0);
                holdingPattern.add(arrival);
                return true;
            }
            return false;
        }

        public boolean moveToTakeOff(List<Aircraft> departures, List<Aircraft> takeOffQueue, Clock clock){
            if (departures.getSize() == 0) return false;

            if (departures.get(0).getValue().getTime().compareTo(clock.simulationTime) <= 0){
                LinkedListElement<Aircraft> departure = departures.pop(0);
                takeOffQueue.add(departure);
                return true;
            }
            return false;
        }

        public boolean landPlane(HoldingPattern<Aircraft> holdingPattern,List<Runway> runways,List<Aircraft> postProcessing,Clock clock){
            if (holdingPattern.getSize() == 0){
                return false;
            }
            LinkedListElement<Aircraft> arrival = new LinkedListElement<>();
            LinkedListElement<Runway> ptr = runways.getHead();
            while (ptr != null){
                if (ptr.getValue().getOccupied().compareTo("") == 0 &&
                ptr.getValue().getMode().compareTo("landing") == 0 &&
                ptr.getValue().getStatus().compareTo("available") == 0){
                    arrival = holdingPattern.pop();
                    postProcessing.add(arrival);
                    ptr.getValue().setOccupied(arrival.getValue().getCallsign());
                    return true;
                }
                ptr.getNext();
            }

            while (ptr != null){
                if (ptr.getValue().getOccupied().compareTo("") == 0 &&
                ptr.getValue().getMode().compareTo("mixed") == 0 &&
                ptr.getValue().getStatus().compareTo("available") == 0){
                    arrival = holdingPattern.pop();
                    postProcessing.add(arrival);
                    ptr.getValue().setOccupied(arrival.getValue().getCallsign());
                    return true;
                }
                ptr.getNext();
            }
            return false;

        }

        public boolean takeOff(List<Aircraft> takeOffQueue,List<Runway> runways,List<Aircraft> postProcessing,Clock clock){
            if (takeOffQueue.getSize() == 0){
                return false;
            }
            LinkedListElement<Aircraft> departure = new LinkedListElement<>();
            LinkedListElement<Runway> ptr = runways.getHead();
            while (ptr != null){
                if (ptr.getValue().getOccupied().compareTo("") == 0 &&
                ptr.getValue().getMode().compareTo("takeoff") == 0 &&
                ptr.getValue().getStatus().compareTo("available") == 0){
                    departure = takeOffQueue.pop(0);
                    postProcessing.add(departure);
                    ptr.getValue().setOccupied(departure.getValue().getCallsign());
                    return true;
                }
            }

            while (ptr != null){
                if (ptr.getValue().getOccupied().compareTo("") == 0 &&
                ptr.getValue().getMode().compareTo("mixed") == 0 &&
                ptr.getValue().getStatus().compareTo("available") == 0){
                    departure = takeOffQueue.pop(0);
                    postProcessing.add(departure);
                    ptr.getValue().setOccupied(departure.getValue().getCallsign());
                    return true;
                }
            }
            return false;

        }
}   
                        