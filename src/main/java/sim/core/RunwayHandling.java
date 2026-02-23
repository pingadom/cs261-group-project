import stores;

public runwayHandling(List<Aircraft> arrivals, 
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
        moveToTakeOff(departures,takeOffQueue,clock);
    }
    while (holdingPattern.getSize() != 0 || takeOffQueue.getSize() != 0 ){
        if (holdingPattern.getSize() != 0){
            landPlane(holdingPattern,runways,postProcessing,clock);
        }
        if (takeOffQueue.getSize() != =) {
            takeOff(takeOffQueue,runways,postProcessing,clock);
        }
    }

    moveToHoldingPattern(List<Aircraft> arrivals,HoldingPattern<Aircraft> holdingPattern,Clock clock){

    }

    moveToTakeOff(List<Aircraft> departures,List<Aircraft> takeOffQueue,Clock clock){}

    landPlane(HoldingPattern<Aircraft> holdingPattern,List<Runway> runways,List<Aircraft> postProcessing,Clock clock){}

    takeOff(List<Aircraft> takeOffQueue,List<Aircraft> runways,List<Aircraft> postProcessing,Clock clock){}
    
                        }