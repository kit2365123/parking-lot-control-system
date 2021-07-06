package PCS.SensorHandler.Emulator;

import PCS.PCSStarter;
import PCS.SensorHandler.SensorHandler;
import AppKickstarter.misc.*;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

//======================================================================
// SensorEmulator
public class SensorEmulator extends SensorHandler {
    private Stage myStage;
    private SensorEmulatorController sensorEmulatorController;
    private final PCSStarter pcsStarter;
    private final String id;
    private final String handlerID;
    private boolean autoPoll;

    //------------------------------------------------------------
    // SensorEmulator
    /**
     * Constructor for SensorEmulator
     * @param id name of the SensorEmulator
     * @param floor floor that the sensor located
     * @param dir direction that the sensor detect
     * @param pcsStarter the pcsStarter for the system
     */
    public SensorEmulator(String id, int floor, String dir, PCSStarter pcsStarter) {
        super(id, floor, dir, pcsStarter);
        handlerID=id;
        this.pcsStarter = pcsStarter;
        this.id = id + "Emulator";
        this.autoPoll = true;
    } // SensorEmulator

    //------------------------------------------------------------
    // start
    /**
     * Starter the GUI Emulator
     * @throws Exception
     */
    public void start() throws Exception {
        Parent root;
        myStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        String fxmlName = "SensorEmulator.fxml";
        loader.setLocation(SensorEmulator.class.getResource(fxmlName));
        root = loader.load();
        sensorEmulatorController = (SensorEmulatorController) loader.getController();
        sensorEmulatorController.initialize(id, pcsStarter, log, this, handlerID);
        myStage.initStyle(StageStyle.DECORATED);
        myStage.setScene(new Scene(root, 417, 347));
        myStage.setTitle(handlerID+" Emulator");
        myStage.setResizable(false);
        myStage.setOnCloseRequest((WindowEvent event) -> {
            pcsStarter.stopApp();
            Platform.exit();
        });
        myStage.show();
    } // start

    //------------------------------------------------------------
    // processMsg
    /**
     * For process the message that sent from PCS
     * @param msg message that sent from PCS
     * @return the value of quit
     */
    protected final boolean processMsg(Msg msg) {
        boolean quit = false;

        switch (msg.getType()) {

            case SensorEmulatorAutoPollToggle:
                handleSensorEmulatorAutoPollToggle();
                break;

            default:
                quit = super.processMsg(msg);
        }
        return quit;
    } // processMsg

    //------------------------------------------------------------
    // handlePayMachineEmulatorAutoPollToggle:
    /**
     * For handling the toggle of the autoPoll button
     * @return
     */
    public final boolean handleSensorEmulatorAutoPollToggle() {
        autoPoll = !autoPoll;
        logFine("Auto poll change: " + (autoPoll ? "off --> on" : "on --> off"));
        return autoPoll;
    } // handlePayMachineEmulatorAutoPollToggle

    //------------------------------------------------------------
    // sendPollReq
    /**
     * For sending the poll response when received poll request from PCS
     */
    @Override
    protected void sendPollReq() {
        logFine("Poll request received.  [autoPoll is " + (autoPoll ? "on]" : "off]"));
        if (autoPoll) {
            logFine("Send poll ack.");
            mbox.send(new Msg(id, mbox, Msg.Type.PollAck, ""));
        }
    } // sendPollReq

    //------------------------------------------------------------
    // logFine
    /**
     * For display logFine message in the text area
     * @param logMsg
     */
    private final void logFine(String logMsg) {
        sensorEmulatorController.appendTextArea("[FINE]: " + logMsg);
        log.fine(id + ": " + logMsg);
    } // logFine

    //------------------------------------------------------------
    // logInfo
    /**
     * For display looInfo message in the text area
     * @param logMsg
     */
    public final void logInfo(String logMsg) {
        sensorEmulatorController.appendTextArea("[INFO]: " + logMsg);
        log.info(id + ": " + logMsg);
    } // logFine
}// SensorEmulator
