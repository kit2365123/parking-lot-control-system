package PCS.PCSCore;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.*;
import AppKickstarter.timer.Timer;

import java.util.ArrayList;
import java.util.Arrays;


//======================================================================
// PCSCore
public class PCSCore extends AppThread {
    private MBox gateMBox;
    private final int pollTime;
    private final int PollTimerID=1;
    private final int openCloseGateTime;		// for demo only!!!
    private final int OpenCloseGateTimerID=2;		// for demo only!!!
    private boolean gateIsClosed = true;		// for demo only!!!

//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	private MBox sensorMBox;
	private ArrayList<MBox> sensorMBoxArrayList = new ArrayList<MBox>();
	private final int numOfLevels;

	private final int [] maxSpacesArray = new int[Integer.parseInt(appKickstarter.getProperty("ParkingCfg.NumberOfLevels"))];
	private int[] spacesArray = new int[Integer.parseInt(appKickstarter.getProperty("ParkingCfg.NumberOfLevels"))];
	private MBox displayMBox;


    //------------------------------------------------------------
    // PCSCore
    public PCSCore(String id, AppKickstarter appKickstarter) throws Exception {
	super(id, appKickstarter);
	this.pollTime = Integer.parseInt(appKickstarter.getProperty("PCSCore.PollTime"));
	this.openCloseGateTime = Integer.parseInt(appKickstarter.getProperty("PCSCore.OpenCloseGateTime"));		// for demo only!!!
//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
		this.numOfLevels = Integer.parseInt(appKickstarter.getProperty("ParkingCfg.NumberOfLevels"));

		for(int i=0; i<spacesArray.length; i++) {
			this.maxSpacesArray[i] = Integer.parseInt(appKickstarter.getProperty("ParkingCfg.AvailableSpaces.Level"+i));
			this.spacesArray[i] = Integer.parseInt(appKickstarter.getProperty("ParkingCfg.AvailableSpaces.Level"+i));
		}
    } // PCSCore


    //------------------------------------------------------------
    // run
    public void run() {
        Thread.currentThread().setName(id);
	Timer.setTimer(id, mbox, pollTime, PollTimerID);
	Timer.setTimer(id, mbox, openCloseGateTime, OpenCloseGateTimerID);	// for demo only!!!
	log.info(id + ": starting...");

	gateMBox = appKickstarter.getThread("GateHandler").getMBox();

//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
		displayMBox = appKickstarter.getThread("VacancyDispHandler").getMBox();
	// add the Sensor MBox
	for(int num=0; num<numOfLevels; num++) {
		sensorMBoxArrayList.add(sensorMBox = appKickstarter.getThread("SensorHandler(Up)Lv"+num).getMBox());
		sensorMBoxArrayList.add(sensorMBox = appKickstarter.getThread("SensorHandler(Down)Lv"+num).getMBox());
	}

	// get the sensor MBox
		for (int num=0;num<numOfLevels*2;num++)
			sensorMBoxArrayList.get(num);

	for (boolean quit = false; !quit;) {
	    Msg msg = mbox.receive();
	    log.fine(id + ": message received: [" + msg + "].");

	    //@@@@
	    int floor;
	    String floorSpaces = "";
	    switch (msg.getType()) {
		case TimesUp:
		    handleTimesUp(msg);
		    break;

		case GateOpenReply:
		    log.info(id + ": Gate is opened.");
		    gateIsClosed = false;
		    break;

		case GateCloseReply:
		    log.info(id + ": Gate is closed.");
		    gateIsClosed = true;
		    break;

		    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
		    case CarDrivingUpReply:
				log.info(id + ":Sensor Detected from level " + msg.getDetails() + ", dir: Up");
				floor = Integer.parseInt(msg.getDetails());
				if(spacesArray[floor] > 0) {
					spacesArray[floor]--;
				}
				if(floor > 0 && spacesArray[floor-1] < maxSpacesArray[floor-1]) {
					spacesArray[floor-1]++;
				}
				for(int num=0; num<spacesArray.length; num++)
					floorSpaces += spacesArray[num] + " ";
				log.info(floorSpaces);
				displayMBox.send(new Msg(id, mbox, Msg.Type.DisplayUpdateRequest, floorSpaces));
		    	break;

			case CarDrivingDownReply:
				log.info(id + ":Sensor Detected from level " + msg.getDetails() + ", dir: Down");
				floor = Integer.parseInt(msg.getDetails());
				if(spacesArray[floor] < maxSpacesArray[floor]) {
					spacesArray[floor]++;
				}
				if(floor > 0 && spacesArray[floor] > 0) {
					spacesArray[floor-1]--;
				}
				for(int num=0; num<spacesArray.length; num++)
					floorSpaces += spacesArray[num] + " ";
				log.info(floorSpaces);
				displayMBox.send(new Msg(id, mbox, Msg.Type.DisplayUpdateRequest, floorSpaces));
				break;

		case PollAck:
		    log.info("PollAck: " + msg.getDetails());
		    break;

		case Terminate:
		    quit = true;
		    break;

		default:
		    log.warning(id + ": unknown message type: [" + msg + "]");
	    }
	}

	// declaring our departure
	appKickstarter.unregThread(this);
	log.info(id + ": terminating...");
    } // run


    //------------------------------------------------------------
    // run
    private void handleTimesUp(Msg msg) {
	log.info("------------------------------------------------------------");
        switch (Timer.getTimesUpMsgTimerId(msg)) {
	    case PollTimerID:
		log.info("Poll: " + msg.getDetails());
		gateMBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
		//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
		displayMBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
		for(int num=0; num<sensorMBoxArrayList.size();num++)
			sensorMBoxArrayList.get(num).send(new Msg(id, mbox, Msg.Type.Poll, ""));

		Timer.setTimer(id, mbox, pollTime, PollTimerID);
	        break;

	    case OpenCloseGateTimerID:					// for demo only!!!
	        if (gateIsClosed) {
		    log.info(id + ": Open the gate now (for demo only!!!)");
		    gateMBox.send(new Msg(id, mbox, Msg.Type.GateOpenRequest, ""));
		} else {
		    log.info(id + ": Close the gate now (for demo only!!!)");
		    gateMBox.send(new Msg(id, mbox, Msg.Type.GateCloseRequest, ""));
		}
		Timer.setTimer(id, mbox, openCloseGateTime, OpenCloseGateTimerID);
		break;

	    default:
	        log.severe(id + ": why am I receiving a timeout with timer id " + Timer.getTimesUpMsgTimerId(msg));
	        break;
	}
    } // handleTimesUp
} // PCSCore
