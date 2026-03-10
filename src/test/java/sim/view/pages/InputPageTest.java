package sim.view.pages;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sim.view.App;
import sim.view.controllers.PageDataController;

class InputPageTest {
    
    private InputPage inputPage;
    private Method startSimulationMethod;
    private Method addNewRunwayMethod;
    private Method deleteRunwayMethod;
    
    @BeforeEach
    void setUp() throws Exception {
        // Construct inputPage
        App app = new App();
        PageDataController dataController = new PageDataController();
        inputPage = new InputPage(app, dataController);
        // Access private methods
        startSimulationMethod = InputPage.class.getDeclaredMethod("startSimulation");
        startSimulationMethod.setAccessible(true);
        addNewRunwayMethod = InputPage.class.getDeclaredMethod("addNewRunway");
        addNewRunwayMethod.setAccessible(true);
        deleteRunwayMethod = InputPage.class.getDeclaredMethod("deleteRunway");
        deleteRunwayMethod.setAccessible(true);
    }
    
    @Test
    // Check exeception from textual inbound rate caught 
    void handleTextInbound() throws Exception {
        inputPage.inboundRateField.setText("abc");
        inputPage.outboundRateField.setText("8");
        inputPage.durationField.setText("8");
        
        // Test invalid does not throw an exception
        assertDoesNotThrow(() -> {
            startSimulationMethod.invoke(inputPage);
        }, "Textual inbound should be caught");
    }

    @Test
    // Check exeception from textual outbound rate caught 
    void handleTextOutbound() throws Exception {
        inputPage.inboundRateField.setText("8");
        inputPage.outboundRateField.setText("abc");
        inputPage.durationField.setText("8");
        
        // Test invalid does not throw an exception
        assertDoesNotThrow(() -> {
            startSimulationMethod.invoke(inputPage);
        }, "Textual outbound should be caught");
    }

    @Test
    // Check exeception from textual duration caught 
    void handleTextDuration() throws Exception {
        inputPage.inboundRateField.setText("8");
        inputPage.outboundRateField.setText("8");
        inputPage.durationField.setText("abc");
        
        // Test invalid does not throw an exception
        assertDoesNotThrow(() -> {
            startSimulationMethod.invoke(inputPage);
        }, "Textual duration should be caught");
    }
    
    @Test
    // Check exeception from negative inbound rate caught  
    void handleNegativeInbound() throws Exception {
        inputPage.inboundRateField.setText("-5");
        inputPage.outboundRateField.setText("8");
        inputPage.durationField.setText("8");
        
        assertDoesNotThrow(() -> {
            startSimulationMethod.invoke(inputPage);
        }, "Negative inbound should be caught");
    }

    @Test
    // Check exeception from negative outbound rate caught  
    void handleNegativeOutbound() throws Exception {
        inputPage.inboundRateField.setText("8");
        inputPage.outboundRateField.setText("-5");
        inputPage.durationField.setText("8");
        
        assertDoesNotThrow(() -> {
            startSimulationMethod.invoke(inputPage);
        }, "Negative outbound should be caught");
    }
    
    @Test
    // Check exeception from negative duration caught  
    void handleNegativeDuration() throws Exception {
        inputPage.inboundRateField.setText("8");
        inputPage.outboundRateField.setText("8");
        inputPage.durationField.setText("-5");
        
        assertDoesNotThrow(() -> {
            startSimulationMethod.invoke(inputPage);
        }, "Negative duration should be caught");
    }

    @Test
    // Check exeception from zero inbound rate caught  
    void handleZeroInbound() throws Exception {
        inputPage.inboundRateField.setText("0");
        inputPage.outboundRateField.setText("8");
        inputPage.durationField.setText("8");
        
        assertDoesNotThrow(() -> {
            startSimulationMethod.invoke(inputPage);
        }, "Zero inbound should be caught");
    }

    @Test
    // Check exeception from zero outbound rate caught  
    void handleZeroOutbound() throws Exception {
        inputPage.inboundRateField.setText("8");
        inputPage.outboundRateField.setText("0");
        inputPage.durationField.setText("8");
        
        assertDoesNotThrow(() -> {
            startSimulationMethod.invoke(inputPage);
        }, "Zero outbound should be caught");
    }
    
    @Test
    // Check exeception from zero duration caught  
    void handleZeroDuration() throws Exception {
        inputPage.inboundRateField.setText("8");
        inputPage.outboundRateField.setText("8");
        inputPage.durationField.setText("0");
        
        assertDoesNotThrow(() -> {
            startSimulationMethod.invoke(inputPage);
        }, "Zero duration should be caught");
    }


    @Test
    // Check exeception from empty inbound caught  
    void handleEmptyInbound() throws Exception {
        inputPage.inboundRateField.setText("");
        inputPage.outboundRateField.setText("8");
        inputPage.durationField.setText("8");
        
        assertDoesNotThrow(() -> {
            startSimulationMethod.invoke(inputPage);
        }, "Empty inbound should be caught");
    }
    
    @Test
    // Check exeception from empty outbound caught  
    void handleEmptyOutbound() throws Exception {
        inputPage.inboundRateField.setText("8");
        inputPage.outboundRateField.setText("");
        inputPage.durationField.setText("8");
        
        assertDoesNotThrow(() -> {
            startSimulationMethod.invoke(inputPage);
        }, "Empty outbound should be caught");
    }

    @Test
    // Check exeception from empty duration caught  
    void handleEmptyDuration() throws Exception {
        inputPage.inboundRateField.setText("8");
        inputPage.outboundRateField.setText("8");
        inputPage.durationField.setText("");
        
        assertDoesNotThrow(() -> {
            startSimulationMethod.invoke(inputPage);
        }, "Empty duration should be caught");
    }    


    @Test
    // Check exeception from adding runways over max limit caught  
    void addOverMaxRunways() throws Exception {
        // Add until max 10
        for (int i = 1; i < 10; i++) {
            addNewRunwayMethod.invoke(inputPage);
        }
        
        // Check button disabled rather than exception thrown 
        assertDoesNotThrow(() -> {
            addNewRunwayMethod.invoke(inputPage);
        });
        
        assertFalse(inputPage.addRunwayButton.isEnabled());
    }
    
    @Test
    // Check exeception from deleting runways below 1 caught  
    void deleteBelowOneRunways() throws Exception {
        // Delete until one remains
        while (inputPage.numRunways > 1) {
            deleteRunwayMethod.invoke(inputPage.numRunways);
        }
        
        // Check button disabled rather than exception thrown 
        assertDoesNotThrow(() -> {
            deleteRunwayMethod.invoke(1);
        });
        
        assertFalse(inputPage.removeRunwayButton.isEnabled());
    }
}