package PCS;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.Msg;
import AppKickstarter.timer.Timer;

import PCS.PCSCore.PCSCore;
import PCS.GateHandler.GateHandler;

import PCS.SensorHandler.SensorHandler;
import PCS.VacancyDispHandler.VacancyDispHandler;
import javafx.application.Platform;

import java.util.ArrayList;


//======================================================================
// PCSStarter
public class PCSStarter extends AppKickstarter {
    protected Timer timer;
    protected PCSCore pcsCore;
    protected GateHandler gateHandler;
	protected VacancyDispHandler vacancyDispHandler;
	protected SensorHandler sensorHandler;
    public static int numberOfLevels;
    protected ArrayList<SensorHandler> sensorHandlerArrayList = new ArrayList<SensorHandler>();


    //------------------------------------------------------------
    // main
    public static void main(String [] args) {
        new PCSStarter().startApp();
    } // main


    //------------------------------------------------------------
    // PCSStart
    public PCSStarter() {
	super("PCSStarter", "etc/PCS.cfg");
	this.numberOfLevels=Integer.parseInt(super.getProperty("ParkingCfg.NumberOfLevels"));
    } // PCSStart


    //------------------------------------------------------------
    // startApp
    protected void startApp() {
	// start our application
	log.info("");
	log.info("");
	log.info("============================================================");
	log.info(id + ": Application Starting...");

	startHandlers();
    } // startApp


    //------------------------------------------------------------
    // startHandlers
    protected void startHandlers() {
	// create handlers
	try {
	    timer = new Timer("timer", this);
	    pcsCore = new PCSCore("PCSCore", this);
	    gateHandler = new GateHandler("GateHandler", this);
		vacancyDispHandler = new VacancyDispHandler("VacancyDispHandler", this);
	    for(int num=0; num<numberOfLevels;num++) {
	    	sensorHandlerArrayList.add(sensorHandler = new SensorHandler("SensorHandler(Up)Lv"+num, num, "up", this));
			sensorHandlerArrayList.add(sensorHandler = new SensorHandler("SensorHandler(Down)Lv"+num, num, "dn", this));
		}

	} catch (Exception e) {
	    System.out.println("AppKickstarter: startApp failed");
	    e.printStackTrace();
	    Platform.exit();
	}

	// start threads
	new Thread(timer).start();
	new Thread(pcsCore).start();
	new Thread(gateHandler).start();
		new Thread(vacancyDispHandler).start();
	for(int num=0; num<numberOfLevels*2; num++)
		new Thread(sensorHandlerArrayList.get(num)).start();

    } // startHandlers


    //------------------------------------------------------------
    // stopApp
    public void stopApp() {
	log.info("");
	log.info("");
	log.info("============================================================");
	log.info(id + ": Application Stopping...");
	pcsCore.getMBox().send(new Msg(id, null, Msg.Type.Terminate, "Terminate now!"));
	gateHandler.getMBox().send(new Msg(id, null, Msg.Type.Terminate, "Terminate now!"));
	timer.getMBox().send(new Msg(id, null, Msg.Type.Terminate, "Terminate now!"));
		vacancyDispHandler.getMBox().send(new Msg(id, null, Msg.Type.Terminate, "Terminate now!"));
		sensorHandler.getMBox().send(new Msg(id, null, Msg.Type.Terminate, "Terminate now!"));
    } // stopApp
} // PCS.PCSStarter
