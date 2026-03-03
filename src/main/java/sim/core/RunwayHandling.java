package sim.core;

import sim.config.SimConfig;
import sim.core.metrics.Metrics;
import sim.model.stores.HoldingPattern;
import sim.model.stores.LinkedListElement;
import sim.model.stores.List;
import sim.model.stores.Aircraft;
import sim.model.stores.Runway;

public final class RunwayHandling {

  // Assign planes from holding pattern to runways (landing first, then mixed). //
  public void handleInbound(
      HoldingPattern<Aircraft> holdingPattern,
      List<Runway> runways,
      List<Aircraft> postProcessing,
      SimClock clock,
      Metrics metrics
      
  ) {
    // Keep trying to assign while we can (multiple runways may be free)
    if (holdingPattern.getSize() > 0) {
        System.out.printf("[t=%.0fs] DEBUG RUNWAYS (holding=%d)%n", clock.now(), holdingPattern.getSize());
        LinkedListElement<Runway> p = runways.getHead();
        while (p != null) {
            Runway r = p.getValue();
            if (r != null) {
            System.out.printf("  rw#%d mode=%s status=%s occ='%s' rem=%d avail=%s%n",
                r.getID(), r.getMode(), r.getStatus(),
                r.getOccupied(), r.getTimeRemaining(),
                r.isAvailableNow());
            } else {
            System.out.println("  rw=<null node>");
            }
            p = p.getNext();
        }
    }

    boolean assignedAny = false;
    boolean assigned = true;

    while (assigned) {
        assigned = landOne(holdingPattern, runways, postProcessing, clock, metrics);
        if (assigned) assignedAny = true;
    }

    if (!assignedAny && holdingPattern.getSize() > 0) {
    // Debug why nothing landed this tick
        System.out.printf("[t=%.0fs] NO LANDING POSSIBLE: holding=%d (no available LANDING/MIXED runway?)%n",
            clock.now(), holdingPattern.getSize());
        }
  }

  private boolean landOne(
      HoldingPattern<Aircraft> holdingPattern,
      List<Runway> runways,
      List<Aircraft> postProcessing,
      SimClock clock,
      Metrics metrics
  ) {
    if (holdingPattern.getSize() == 0) return false;

    // 1) Prefer dedicated LANDING runways
    Runway rw = findAvailableRunway(runways, SimConfig.RunwayMode.LANDING);
    if (rw == null) {
      // 2) Then allow MIXED
      rw = findAvailableRunway(runways, SimConfig.RunwayMode.MIXED);
    }
    if (rw == null) return false;

    LinkedListElement<Aircraft> arrival = holdingPattern.pop();
    postProcessing.add(arrival);

    rw.occupy(arrival.getValue().getCallsign());
    metrics.arrivalsProcessed++;
    System.out.printf("[t=%.0fs] LAND start: %s on runway #%d%n",
    clock.now(), arrival.getValue().getCallsign(), rw.getID());

    System.out.printf("[t=%.0fs] LAND start: %s on runway #%d (service=%ds)%n",
        clock.now(),
        arrival.getValue().getCallsign(),
        rw.getID(),
        rw.getServiceTimeSeconds()
    );

    return true;
  }

    private Runway findAvailableRunway(List<Runway> runways, SimConfig.RunwayMode mode) {
        LinkedListElement<Runway> ptr = runways.getHead();
        while (ptr != null) {
            Runway rw = ptr.getValue();

            // ✅ Skip sentinel / empty nodes
            if (rw != null && rw.getMode() == mode && rw.isAvailableNow()) return rw;

            ptr = ptr.getNext();
        }
        return null;
        }
}