package PCS.SensorHandler;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.*;

//------------------------------------------------------------
// SensorHandler
public class SensorHandler extends AppThread {
    protected final MBox pcsCore;
    protected final int floor;
    protected final String dir;

    //------------------------------------------------------------
    // SensorHandler
    /**
     * Constructor for SensorHandler
     * @param id name of the SensorHandler
     * @param floor floor that the sensor located
     * @param dir direction that the sensor detect
     * @param appKickstarter the appKickstarter for the system
     */
    public SensorHandler(String id, int floor, String dir, AppKickstarter appKickstarter) {
        super(id, appKickstarter);
        this.floor = floor;
        this.dir = dir;
        pcsCore = appKickstarter.getThread("PCSCore").getMBox();
    } // SensorHandler

    public void run() {
        Thread.currentThread().setName(id);
        log.info(id + ": starting...");

        for (boolean quit = false; !quit;) {
            Msg msg = mbox.receive();

            log.fine(id + ": message received: [" + msg + "].");

            quit = processMsg(msg);
        }
        // declaring our departure
        appKickstarter.unregThread(this);
        log.info(id + ": terminating...");
    } // run

    //------------------------------------------------------------
    // processMsg
    /**
     * For process the message that sent from PCS
     * @param msg message that sent from PCS
     * @return
     */
    protected boolean processMsg(Msg msg) {
        boolean quit = false;

        switch (msg.getType()) {
            case SensorDetectedReply: handleSensorDetectedReply(); break;
            case Poll:		   handlePollReq();	     break;
            case PollAck:	   handlePollAck();	     break;
            case Terminate:	   quit = true;		     break;
            default:
                log.warning(id + ": unknown message type: [" + msg + "]");
        }
        return quit;
    } // processMsg

    //------------------------------------------------------------
    // handleSensorDetectedReply
    /** For handling the reply that sent from the sensor when it detected a car passing
     *
     */
    protected final void handleSensorDetectedReply() {
        log.info(id + ": sensor detected reply received");

        if(dir.equals("up")) {
            pcsCore.send(new Msg(id, mbox, Msg.Type.CarDrivingUpReply, Integer.toString(floor)));
        } else {
            pcsCore.send(new Msg(id, mbox, Msg.Type.CarDrivingDownReply, Integer.toString(floor)));
        }
    } // handleSensorDetectedReply

    //------------------------------------------------------------
    // handlePollReq
    protected final void handlePollReq() {
        log.info(id + ": poll request received.  Send poll request to hardware.");
        sendPollReq();
    } // handlePollReq

    //------------------------------------------------------------
    // handlePollAck
    protected final void handlePollAck() {
        log.info(id + ": poll ack received.  Send poll ack to PCS Core.");
        pcsCore.send(new Msg(id, mbox, Msg.Type.PollAck, id + " is up!"));
    } // handlePollAck

    //------------------------------------------------------------
    // sendPollReq
    protected void sendPollReq() {
        // fixme: send gate poll request to hardware
        log.info(id + ": poll request received");
    } // sendPollReq

} // SensorHandler
