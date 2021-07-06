package PCS.VacancyDispHandler;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.AppThread;
import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;

//======================================================================
// VacancyDispHandler
public class VacancyDispHandler extends AppThread {
    protected final MBox pcsCore;

    //------------------------------------------------------------
    // VacancyDispHandler
    /**
     * Constructor for VacanyDispHandler
     * @param id name of the handler
     * @param appKickstarter name of the appKickstarter
     */
    public VacancyDispHandler(String id, AppKickstarter appKickstarter) {
        super(id, appKickstarter);
        pcsCore = appKickstarter.getThread("PCSCore").getMBox();
    } // VacancyDispHandler

    //------------------------------------------------------------
    // run
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
            case DisplayUpdateRequest:  handleDisplayUpdateRequest(msg.getDetails());  break;
            case Poll:		   handlePollReq();	     break;
            case PollAck:	   handlePollAck();	     break;
            case Terminate:	   quit = true;		     break;
            default:
                log.warning(id + ": unknown message type: [" + msg + "]");
        }
        return quit;
    } // processMsg

    /**
     * For handling the display update request that sent from PCS
     * @param floorSpaces A String that store all the available spaces of each floor
     */
    protected final void handleDisplayUpdateRequest(String floorSpaces) {
        log.info(id + ": send signal to vacancy display now.");
        sendUpdateDisplaySignal(floorSpaces);
    }

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
    // sendUpdateDisplaySignal
    /**
     * For send a update signal to the display hardware
     * @param floorSpaces A String that store all the available spaces of each floor
     */
    protected void sendUpdateDisplaySignal(String floorSpaces) {
        // fixme: send gate open signal to hardware
        log.info(id + ": sending update display signal to hardware.");
    } // sendUpdateDisplaySignal

    //------------------------------------------------------------
    // sendPollReq
    protected void sendPollReq() {
        // fixme: send gate poll request to hardware
        log.info(id + ": poll request received");
    } // sendPollReq

}// VacancyDispHandler
