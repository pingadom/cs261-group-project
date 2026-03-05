package sim.core;

import sim.model.stores.*;


public class RunwayHandling{

    public void runwayHandling(List<Aircraft> arrivals, 
                            List<Aircraft> departures, 
                            List<Aircraft> takeOffQueue, 
                            HoldingPattern<Aircraft> holdingPattern,
                            List<Runway> runways,
                            List<Aircraft> postProcessing,
                            SimClock clock){
        boolean flag = true;
        while (flag)  {
            flag = moveToHoldingPattern(arrivals,holdingPattern,clock);
        } 
        // Moves all aircraft ready to depart into the take off queue                       
        flag = true;
        while (flag){
            flag = moveToTakeOff(departures,takeOffQueue,clock);
        }
        // Assigns all aircraft in the take-off queue and holding pattern to a runway
        // Repeats this process until no aircraft from either are assigned a runway
        // This will happen either when both stores are empty, or there are no available runways
        boolean takeOffFlag = true;
        boolean landingFlag = true;
         // Loops as long as one of the stores is being emptied
        while (takeOffFlag || landingFlag){
            if (landingFlag){
                landingFlag = landPlane(holdingPattern,runways,postProcessing,clock);
            }
            if (takeOffFlag) {
                takeOffFlag = takeOff(takeOffQueue,runways,postProcessing,clock);
            }
        }
        freeRunways(runways, clock);
        adjustAltitude(holdingPattern);
                            }

        public void freeRunways(List<Runway> runways, SimClock clock){
            LinkedListElement<Runway> ptr = runways.getHead();
            while (ptr!= null){
                if (ptr.getValue().getOccupied().compareTo("") != 0 && ptr.getValue().getTimeRemaining() < clock.now()){
                    ptr.getValue().setOccupied("");
                }
                ptr = ptr.getNext();
            }
        }

        
                            

        public void adjustAltitude(HoldingPattern<Aircraft> holdingPattern){
            int i = 1;
            LinkedListElement<Aircraft> ptr = holdingPattern.getEmergency().getHead();
            while (ptr != null){
                ptr.getValue().setAltitude(i * 1000);
                i++;
            }
            ptr = holdingPattern.getNonEmergency().getHead();
            while (ptr != null){
                ptr.getValue().setAltitude(i * 1000);
                i++;
            }
        }


         // If the aircraft on top of the arrivals list has arrived at the airport, 
        // pop it from the list and add it to the holding pattern
        // Returns true on succesfully moving the aircraft
        // Returns false if there is no such aircraft to move
        
        public boolean moveToHoldingPattern(List<Aircraft> arrivals,HoldingPattern<Aircraft> holdingPattern,SimClock clock){
            if (arrivals.get(0).getValue().getTime() <= clock.now()){
                LinkedListElement<Aircraft> arrival = new LinkedListElement<>();
                arrival = arrivals.pop(0);
                holdingPattern.add(arrival);
                return true;
            }
            return false;
        }

         // If the aircraft on top of the departure list is ready to depart the airport, 
        // pop it from the list and add it to the take-off queue
        // Returns true on succesfully moving the aircraft
        // Returns false if there is no such aircraft to move
       
        public boolean moveToTakeOff(List<Aircraft> departures,List<Aircraft> takeOffQueue,SimClock clock){
            if (departures.get(0).getValue().getTime() <= clock.now()){
                LinkedListElement<Aircraft> departure = new LinkedListElement<>();
                departure = departures.pop(0);
                takeOffQueue.add(departure);
                return true;
            }
            return false;
        }

         // Searches through runways to find an eligible runway
        // Searches first for single-mode runways, then for dual mode runways
        // This saves the dual mode runways if they need to be used for taking off
        // Once a runway is found, land the plane and return true
        // Returning false indicates either there is no plane to land or there are no runways to land on
 
        public boolean landPlane(HoldingPattern<Aircraft> holdingPattern,List<Runway> runways,List<Aircraft> postProcessing,SimClock clock){
            if (holdingPattern.getSize() == 0){
                return false;
            }
            LinkedListElement<Aircraft> arrival = new LinkedListElement<>();
            LinkedListElement<Runway> ptr = runways.getHead();
            while (ptr != null){
                if (ptr.getValue().getOccupied().compareTo("") == 0 &&
                ptr.getValue().getMode() == Runway.RunwayMode.LANDING &&
                ptr.getValue().getStatus()== Runway.RunwayStatus.AVAILABLE){
                    arrival = holdingPattern.pop();
                    arrival.getValue().setRealTime(clock.now());
                    arrival.getValue().setStatus(Aircraft.AircraftStatus.ARRIVED);
                    postProcessing.add(arrival);
                    ptr.getValue().setOccupied(arrival.getValue().getCallsign());
                    ptr.getValue().setTimeRemaining(clock.now() + 45);
                    return true;
                }
                ptr = ptr.getNext();
            }

            while (ptr != null){
                if (ptr.getValue().getOccupied().compareTo("") == 0 &&
                ptr.getValue().getMode() == Runway.RunwayMode.MIXED &&
                ptr.getValue().getStatus() == Runway.RunwayStatus.AVAILABLE){
                    arrival = holdingPattern.pop();
                    arrival.getValue().setRealTime(clock.now());
                    arrival.getValue().setStatus(Aircraft.AircraftStatus.ARRIVED);
                    postProcessing.add(arrival);
                    ptr.getValue().setOccupied(arrival.getValue().getCallsign());
                    ptr.getValue().setTimeRemaining(clock.now() + 45);
                    return true;
                }
                ptr = ptr.getNext();
            }
            return false;

        }

        // Searches through runways to find an eligible runway
        // Searches first for single-mode runways, then for dual mode runways
        // This saves the dual mode runways if they need to be used for departing
        // Once a runway is found, depart the plane and return true
        // Returning false indicates either there is no plane to depart or there are no runways to take off on
      
        public boolean takeOff(List<Aircraft> takeOffQueue,List<Runway> runways,List<Aircraft> postProcessing,SimClock clock){
            if (takeOffQueue.getSize() == 0){
                return false;
            }
            LinkedListElement<Aircraft> departure = new LinkedListElement<>();
            LinkedListElement<Runway> ptr = runways.getHead();
            while (ptr != null){
                if (ptr.getValue().getOccupied().compareTo("") == 0 &&
                ptr.getValue().getMode() == Runway.RunwayMode.TAKEOFF &&
                ptr.getValue().getStatus() == Runway.RunwayStatus.AVAILABLE){
                    departure = takeOffQueue.pop(0);
                    departure.getValue().setRealTime(clock.now());
                    departure.getValue().setStatus(Aircraft.AircraftStatus.DEPARTED);
                    postProcessing.add(departure);
                    ptr.getValue().setOccupied(departure.getValue().getCallsign());
                    ptr.getValue().setTimeRemaining(clock.now() + 45);
                    
                    return true;
                }
                ptr = ptr.getNext();
            }

            while (ptr != null){
                if (ptr.getValue().getOccupied().compareTo("") == 0 &&
                ptr.getValue().getMode() == Runway.RunwayMode.MIXED &&
                ptr.getValue().getStatus() == Runway.RunwayStatus.AVAILABLE){
                    departure = takeOffQueue.pop(0);
                    departure.getValue().setRealTime(clock.now());
                    departure.getValue().setStatus(Aircraft.AircraftStatus.DEPARTED);
                    postProcessing.add(departure);
                    ptr.getValue().setOccupied(departure.getValue().getCallsign());
                    ptr.getValue().setTimeRemaining(clock.now() + 45);
                    return true;
                }
                ptr = ptr.getNext();
            }
            return false;

        }

        public void fuelConsumption(HoldingPattern<Aircraft> holdingPattern, double realDeltaSeconds, double speedMultiplier,List<Aircraft> postProcessing){
            LinkedListElement<Aircraft> ptr = holdingPattern.getEmergency().getHead();
            int i = 0;
            while (ptr != null){
                ptr.getValue().setFuel(ptr.getValue().getFuel() - realDeltaSeconds * speedMultiplier);
                if (ptr.getValue().getFuel() < 600){
                    holdingPattern.getEmergency().pop(i);
                    ptr.getValue().setStatus(Aircraft.AircraftStatus.DIVERTED);
                    postProcessing.add(ptr);
                }
                ptr = ptr.getNext();
                i++; 
            }
            ptr = holdingPattern.getNonEmergency().getHead();
            i = 0;
            while (ptr != null){
                ptr.getValue().setFuel(ptr.getValue().getFuel() - realDeltaSeconds * speedMultiplier);
                if (ptr.getValue().getFuel() < 1200){
                    holdingPattern.getNonEmergency().pop(i);
                    ptr.getValue().setEmergency(Aircraft.EmergencyStatus.FUEL);
                    ptr.setPriority(1);
                    holdingPattern.add(ptr);
                }
                ptr = ptr.getNext();
                i++; 
            }
        }
}   


                        