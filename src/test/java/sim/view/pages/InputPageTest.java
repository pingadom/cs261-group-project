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
        startSimulationMethod = InputPage.class.getDeclaredMethod("startSimulation");
        startSimulationMethod.setAccessible(true);
    }
    
    @Test
    // Check exeception from invalid number caught 
    void handleInvalidNumber() throws Exception {
        inputPage.inboundRateField.setText("abc");
        inputPage.outboundRateField.setText("8");
        inputPage.durationField.setText("8");
        
        // Test invalid does not throw an exception
        assertDoesNotThrow(() -> {
            startSimulationMethod.invoke(inputPage);
        }, "Invalid numbers should be caught");
    }
    
    @Test
    // Check exeception from non positive values caught  
    void handleNonPositiveValues() throws Exception {
        inputPage.inboundRateField.setText("0");
        inputPage.outboundRateField.setText("-5");
        inputPage.durationField.setText("0");
        
        assertDoesNotThrow(() -> {
            startSimulationMethod.invoke(inputPage);
        }, "Non positive values should be caught");
    }
    
    @Test
    // Check exeception from empty fields caught  
    void handleEmptyFields() throws Exception {
        inputPage.inboundRateField.setText("");
        inputPage.outboundRateField.setText("");
        inputPage.durationField.setText("");
        
        assertDoesNotThrow(() -> {
            startSimulationMethod.invoke(inputPage);
        }, "Empty fields should be caught");
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