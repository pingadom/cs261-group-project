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
        flag = true;
        while (flag){
            flag = moveToTakeOff(departures,takeOffQueue,clock);
        }
        boolean takeOffFlag = true;
        boolean landingFlag = true;
        while (takeOffFlag || landingFlag){
            if (landingFlag){
                landingFlag = landPlane(holdingPattern,runways,postProcessing);
            }
            if (takeOffFlag) {
                takeOffFlag = takeOff(takeOffQueue,runways,postProcessing);
            }
        }
        freeRunways(runways, clock);
                            }

        public void freeRunways(List<Runway> runways, SimClock clock){
            ptr = runways.getHead();
            while (ptr!= null){
                if (ptr.getValue().getOccupied().compareTo("") != 0 && ptr.getValue().getTimeRemaining() < clock.now()){
                    ptr.getValue.setOccupied("");
                }
                ptr = ptr.next()
            }
        }

        public boolean moveToHoldingPattern(List<Aircraft> arrivals,HoldingPattern<Aircraft> holdingPattern,SimClock clock){
            if (arrivals.get(0).getValue().getTime() <= clock.now()){
                LinkedListElement<Aircraft> arrival = new LinkedListElement<>();
                arrival = arrivals.pop(0);
                holdingPattern.add(arrival);
                return true;
            }
            return false;
        }

        public boolean moveToTakeOff(List<Aircraft> departures,List<Aircraft> takeOffQueue,SimClock clock){
            if (departures.get(0).getValue().getTime() <= clock.now()){
                LinkedListElement<Aircraft> departure = new LinkedListElement<>();
                departure = departures.pop(0);
                takeOffQueue.add(departure);
                return true;
            }
            return false;
        }

        public boolean landPlane(HoldingPattern<Aircraft> holdingPattern,List<Runway> runways,List<Aircraft> postProcessing){
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
                    arrival.getValue().setStatus("arrived");
                    postProcessing.add(arrival);
                    ptr.getValue().setOccupied(arrival.getValue().getCallsign());
                    ptr.getValue().setTimeRemaining(clock.now + 45);
                    return true;
                }
                ptr = ptr.getNext();
            }

            while (ptr != null){
                if (ptr.getValue().getOccupied().compareTo("") == 0 &&
                ptr.getValue().getMode().compareTo("mixed") == 0 &&
                ptr.getValue().getStatus().compareTo("available") == 0){
                    arrival = holdingPattern.pop();
                    arrival.getValue().setStatus("arrived");
                    postProcessing.add(arrival);
                    ptr.getValue().setOccupied(arrival.getValue().getCallsign());
                    ptr.getValue().setTimeRemaining(clock.now + 45);
                    return true;
                }
                ptr = ptr.getNext();
            }
            return false;

        }

        public boolean takeOff(List<Aircraft> takeOffQueue,List<Runway> runways,List<Aircraft> postProcessing){
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
                    departure.getValue().setStatus("departed");
                    postProcessing.add(departure);
                    ptr.getValue().setOccupied(departure.getValue().getCallsign());
                    ptr.getValue().setTimeRemaining(clock.now + 45);
                    
                    return true;
                }
                ptr = ptr.getNext();
            }

            while (ptr != null){
                if (ptr.getValue().getOccupied().compareTo("") == 0 &&
                ptr.getValue().getMode().compareTo("mixed") == 0 &&
                ptr.getValue().getStatus().compareTo("available") == 0){
                    departure = takeOffQueue.pop(0);
                    departure.getValue().setStatus("departed");
                    postProcessing.add(departure);
                    ptr.getValue().setOccupied(departure.getValue().getCallsign());
                    ptr.getValue().setTimeRemaining(clock.now + 45);
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
                    ptr.getValue().setStatus("diverted");
                    postProcessing.add(ptr);
                }
                ptr = ptr.getNext()
                i++; 
            }
            ptr = holdingPattern.getNonEmergency().getHead();
            i = 0;
            while (ptr != null){
                ptr.getValue().setFuel(ptr.getValue().getFuel() - realDeltaSeconds * speedMultiplier);
                if (ptr.getValue().getFuel() < 1200){
                    holdingPattern.getNonEmergency().pop(i);
                    ptr.setPriority(1);
                    holdingPattern.add(ptr);
                }
                ptr = ptr.GetNext()
                i++; 
            }
        }
}   
                        