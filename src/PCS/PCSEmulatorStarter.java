package PCS;

import AppKickstarter.misc.MBox;
import AppKickstarter.timer.Timer;

import PCS.PCSCore.PCSCore;
import PCS.GateHandler.GateHandler;
import PCS.GateHandler.Emulator.GateEmulator;

import PCS.SensorHandler.Emulator.SensorEmulator;
import PCS.SensorHandler.SensorHandler;
import PCS.VacancyDispHandler.Emulator.VacancyDispEmulator;
import PCS.VacancyDispHandler.VacancyDispHandler;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

//======================================================================
// PCSEmulatorStarter
public class PCSEmulatorStarter extends PCSStarter {
    //------------------------------------------------------------
    // main
    public static void main(String [] args) {
	new PCSEmulatorStarter().startApp();
    } // main


    //------------------------------------------------------------
    // startHandlers
    @Override
    protected void startHandlers() {
        Emulators.pcsEmulatorStarter = this;
        new Emulators().start();
    } // startHandlers


    //------------------------------------------------------------
    // Emulators
    public static class Emulators extends Application {
        private static PCSEmulatorStarter pcsEmulatorStarter;

	//----------------------------------------
	// start
        public void start() {
            launch();
	} // start

	//----------------------------------------
	// start
        public void start(Stage primaryStage) {
	    Timer timer = null;
	    PCSCore pcsCore = null;
	    GateEmulator gateEmulator = null;
	    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
            VacancyDispEmulator vacancyDispEmulator = null;
            SensorEmulator[] sensorEmulator = new SensorEmulator[numberOfLevels*2];
            MBox[] sensorMBox = new MBox[numberOfLevels*2];

	    // create emulators
	    try {
	        timer = new Timer("timer", pcsEmulatorStarter);
	        pcsCore = new PCSCore("PCSCore", pcsEmulatorStarter);
	        gateEmulator = new GateEmulator("GateHandler", pcsEmulatorStarter);

	        //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
            vacancyDispEmulator = new VacancyDispEmulator("VacancyDispHandler", pcsEmulatorStarter);
            int floor = 0;
            for (int num=0;num<numberOfLevels*2;num=num+2) {
                sensorEmulator[num] = new SensorEmulator("SensorHandler(Up)Lv"+floor, floor, "up",  pcsEmulatorStarter);
                sensorEmulator[num+1] = new SensorEmulator("SensorHandler(Down)Lv"+floor, floor, "dn",  pcsEmulatorStarter);
                sensorMBox[num] = sensorEmulator[num].getMBox();
                sensorMBox[num+1] = sensorEmulator[num+1].getMBox();
                floor++;
            }

		// start emulator GUIs
            //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
            for (int num=0;num<numberOfLevels*2;num++)
                sensorEmulator[num].start();
            vacancyDispEmulator.start();
		gateEmulator.start();
	    } catch (Exception e) {
		System.out.println("Emulators: start failed");
		e.printStackTrace();
		Platform.exit();
	    }
	    pcsEmulatorStarter.setTimer(timer);
	    pcsEmulatorStarter.setPCSCore(pcsCore);
	    pcsEmulatorStarter.setGateHandler(gateEmulator);
	    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
            pcsEmulatorStarter.setVacancyDispHandler(vacancyDispEmulator);
            for (int num=0;num<numberOfLevels*2;num++)
                pcsEmulatorStarter.setSensorHandler(sensorEmulator[num]);


            // start threads
	    new Thread(timer).start();
	    new Thread(pcsCore).start();
	    new Thread(gateEmulator).start();

	    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
            new Thread(vacancyDispEmulator).start();
            for (int num=0;num<numberOfLevels*2;num++)
                new Thread(sensorEmulator[num]).start();
	} // start
    } // Emulators


    //------------------------------------------------------------
    //  setters
    private void setTimer(Timer timer) {
        this.timer = timer;
    }
    private void setPCSCore(PCSCore pcsCore) {
        this.pcsCore = pcsCore;
    }
    private void setGateHandler(GateHandler gateHandler) {
	this.gateHandler = gateHandler;
    }
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    private void setSensorHandler(SensorHandler sensorHandler) {
        this.sensorHandler = sensorHandler;
    }
    private void setVacancyDispHandler(VacancyDispHandler vacancyDispHandler) {this.vacancyDispHandler = vacancyDispHandler;}
} // PCSEmulatorStarter
