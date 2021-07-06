package PCS.VacancyDispHandler.Emulator;

import AppKickstarter.misc.Msg;
import PCS.PCSStarter;
import PCS.VacancyDispHandler.VacancyDispHandler;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class VacancyDispEmulator extends VacancyDispHandler {
    private Stage myStage;
    private VacancyDispEmulatorController vacancyDispEmulatorController;
    private final PCSStarter pcsStarter;
    private final String id;
    private boolean autoPoll;

    //------------------------------------------------------------
    // VacancyDispEmulator
    /**
     * Constructor for VacancyDispEmulator
     * @param id name of the VacancyDispEmulator
     * @param pcsStarter the pcsStarter for the system
     */
    public VacancyDispEmulator(String id, PCSStarter pcsStarter) {
        super(id, pcsStarter);
        this.pcsStarter = pcsStarter;
        this.id = id + "Emulator";
        this.autoPoll = true;
    } // VacancyDispEmulator

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
        String fxmlName = "VacancyDispEmulator.fxml";
        loader.setLocation(VacancyDispEmulator.class.getResource(fxmlName));
        root = loader.load();
        vacancyDispEmulatorController = (VacancyDispEmulatorController) loader.getController();
        vacancyDispEmulatorController.initialize(id, pcsStarter, log, this);
        myStage.initStyle(StageStyle.DECORATED);
        myStage.setScene(new Scene(root, 600, 374));
        myStage.setTitle("Vacancy Display Emulator");
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

            case VacancyDispEmulatorAutoPollToggle:
                handleVacancyDispEmulatorAutoPollToggle();
                break;

            default:
                quit = super.processMsg(msg);
        }
        return quit;
    } // processMsg

    //------------------------------------------------------------
    // handleVacancyDispEmulatorAutoPollToggle:
    /**
     * For handling the toggle of the autoPoll button
     * @return
     */
    public final boolean handleVacancyDispEmulatorAutoPollToggle() {
        autoPoll = !autoPoll;
        logFine("Auto poll change: " + (autoPoll ? "off --> on" : "on --> off"));
        return autoPoll;
    } // handleVacancyDispEmulatorAutoPollToggle

    //------------------------------------------------------------
    // sendUpdateDisplaySignal
    /**
     * Override the sendUpdateDisplaySignal method in VacancyDispHandler to simulate the display received the update signal
     * @param floorSpaces A String that store all the available spaces of each floor
     */
    protected void sendUpdateDisplaySignal(String floorSpaces) {
        vacancyDispEmulatorController.appendTextArea("[INFO]: Vancancy display update: Vancancy Disp-MsgDetails: " + floorSpaces);
        String[] spaces = floorSpaces.split(" ");
        for(int num=0; num<spaces.length; num++)
            vacancyDispEmulatorController.appendTextArea("[INFO]:     >>> Level " + num + " " + spaces[num]);
    }

    //------------------------------------------------------------
    // sendPollReq
    @Override
    /**
     * For sending the poll response when received poll request from PCS
     */
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
        vacancyDispEmulatorController.appendTextArea("[FINE]: " + logMsg);
        log.fine(id + ": " + logMsg);
    } // logFine

    //------------------------------------------------------------
    // logInfo
    /**
     * For display looInfo message in the text area
     * @param logMsg
     */
    public final void logInfo(String logMsg) {
        vacancyDispEmulatorController.appendTextArea("[INFO]: " + logMsg);
        log.info(id + ": " + logMsg);
    } // logInfo
}
