package com.p3ps0n.robotarm;

import com.fazecast.jSerialComm.SerialPort;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.io.IOException;

public class Controller {
    @FXML AnchorPane mainPane;
    @FXML AnchorPane connectionPane;
    @FXML AnchorPane controlPane;
    @FXML Button refreshBtn;
    @FXML Button connectBtn;
    @FXML Button disconnectBtn;
    @FXML ChoiceBox chooseDevice;
    @FXML Label logLabel;
    @FXML Label connectionLabel;
    @FXML Label baseValue;
    @FXML Label armValue;
    @FXML Label forearmValue;
    @FXML Label grapplerValue;
    @FXML Slider sliderBase;
    @FXML Slider sliderArm;
    @FXML Slider sliderForearm;
    @FXML Slider sliderGrappler;

    private SerialPort[] ports;
    private SerialPort port;

    @FXML
    private void initialize() {
        refreshDevices(); // Refresh device list when app starts

        /* KeyEvents */
        mainPane.setOnKeyPressed(event -> {
            switch(event.getCode()) {
                case Q:
                    if(sliderBase.getValue() > 0)
                        sliderBase.setValue(sliderBase.getValue()-1);
                    break;

                case W:
                    if(sliderBase.getValue() < 180)
                        sliderBase.setValue(sliderBase.getValue()+1);
                    break;

                case E:
                    if(sliderArm.getValue() > 0)
                        sliderArm.setValue(sliderArm.getValue()-1);
                    break;

                case R:
                    if(sliderArm.getValue() < 180)
                        sliderArm.setValue(sliderArm.getValue()+1);
                    break;

                case A:
                    if(sliderForearm.getValue() > 0)
                        sliderForearm.setValue(sliderForearm.getValue()-1);
                    break;

                case S:
                    if(sliderForearm.getValue() < 180)
                        sliderForearm.setValue(sliderForearm.getValue()+1);
                    break;

                case D:
                    if(sliderGrappler.getValue() > 0)
                        sliderGrappler.setValue(sliderGrappler.getValue()-1);
                    break;

                case F:
                    if(sliderGrappler.getValue() < 180)
                        sliderGrappler.setValue(sliderGrappler.getValue()+1);
                    break;

                case T:
                    sliderBase.setValue(90);
                    sliderArm.setValue(90);
                    sliderForearm.setValue(90);
                    sliderGrappler.setValue(90);
                    break;
            }
        });

        sliderBase.valueProperty().addListener(event -> baseValue.textProperty().setValue(String.valueOf((int)sliderBase.getValue())));
        sliderArm.valueProperty().addListener(event -> armValue.textProperty().setValue(String.valueOf((int)sliderArm.getValue())));
        sliderForearm.valueProperty().addListener(event -> forearmValue.textProperty().setValue(String.valueOf((int)sliderForearm.getValue())));
        sliderGrappler.valueProperty().addListener(event -> grapplerValue.textProperty().setValue(String.valueOf((int)sliderGrappler.getValue())));



    }

    @FXML private void refresh() {
        refreshDevices();
    }

    @FXML private void connect() {
        int deviceIndex = chooseDevice.getSelectionModel().getSelectedIndex();
        port = ports[deviceIndex];


        /* Try to open serial port*/
        if(port.openPort()) {
            // Successfully connected with selected port
            port.setComPortParameters(115200, 8, 1, 0);
            port.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0);

            logLabel.setText("");

            /*  Change appearance of connection pane when connected  */
            connectionLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
            connectionLabel.setText("Connected with " + port.getSystemPortName());
            chooseDevice.setVisible(false);
            refreshBtn.setVisible(false);
            connectBtn.setVisible(false);
            disconnectBtn.setVisible(true);
            connectionPane.setMinHeight(100);
            connectionPane.setPrefHeight(100);
            controlPane.setVisible(true);

            sliderBase.setValue(90);
            sliderArm.setValue(90);
            sliderForearm.setValue(90);
            sliderGrappler.setValue(90);


            Runnable connection = () -> {
                while(port.isOpen()) {
                    try {
                        String data = (int)sliderBase.getValue() + ":" +
                                (int)sliderArm.getValue() + ":" +
                                (int)sliderForearm.getValue() + ":" +
                                (int)sliderGrappler.getValue() + "\n";

                        for(int i = 0; i < data.toCharArray().length; i++) {
                            port.getOutputStream().write(data.charAt(i));
                            port.getOutputStream().flush();
                        }

                        Thread.sleep(25);
                    } catch(IOException ex) {
                        ex.printStackTrace();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }

                }

                /*  Change appearance of connection pane when not connected  */
                Platform.runLater(() -> {
                    connectionLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
                    connectionLabel.setText("Connect with device");
                    chooseDevice.setVisible(true);
                    refreshBtn.setVisible(true);
                    connectBtn.setVisible(true);
                    disconnectBtn.setVisible(false);
                    connectionPane.setMinHeight(145);
                    connectionPane.setPrefHeight(145);
                    controlPane.setVisible(false);
                });
            };
            Thread connectionThread = new Thread(connection);
            connectionThread.setDaemon(true);
            connectionThread.start();

        } else {
            // Something went wrong, e.g. port is busy
            logLabel.setText("Unable to open the port.");
        }

    }

    @FXML private void disconnect() {
        if(!port.closePort()) {
            logLabel.setText("An error occurred while closing port.");
        }
    }

    private void refreshDevices() {
        ports = SerialPort.getCommPorts();
        chooseDevice.getItems().clear();
        for(SerialPort port : ports)
            chooseDevice.getItems().add(port.getDescriptivePortName());

        if(!chooseDevice.getItems().isEmpty())
            chooseDevice.setValue(chooseDevice.getItems().get(0));
    }
}
