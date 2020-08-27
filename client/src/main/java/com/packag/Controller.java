package com.packag;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    TextArea textArea;

    @FXML
    TextField msgField, loginField;

    @FXML
    PasswordField passField;

    @FXML
    HBox loginBox;

    private Network network;
    private boolean authenticated;
    private String nickname;

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
        loginBox.setVisible(!authenticated);
        loginBox.setManaged(!authenticated);
        msgField.setVisible(authenticated);
        msgField.setManaged(authenticated);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setAuthenticated(false);
        try {
            network = new Network(8181);
            Thread t = new Thread(() -> {
                try {

                    while (true) {
                        String msg = network.readMsg();
                        if (msg.startsWith("/authok ")) {
                            nickname = msg.split("\\s")[1];
                            setAuthenticated(true);
                            break;
                        }
                        textArea.appendText(msg + "\n");
                    }

                    while (true) {
                        String msg = network.readMsg();
                        if ("/end_confirm".equals(msg)) {
                            break;
                        }
                        textArea.appendText(msg + "\n");
                    }

                } catch (IOException e) {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.WARNING,
                                "Соединение с сервером разорвано", ButtonType.OK);
                        alert.showAndWait();
                    });
                } finally {
                    network.close();
                    Platform.exit();
                }
            }
            );
            t.setDaemon(true);
            t.start();

        } catch (IOException e) {
            throw new RuntimeException("Impossible connected to Server");
        }
    }

    public void sendMsg(ActionEvent actionEvent) {
        try {
            network.sendMsg(msgField.getText());
            msgField.clear();
            msgField.requestFocus();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING,
                    "Не удалось отправить сообщение", ButtonType.OK);
            alert.showAndWait();
        }
    }

    public void tryToAuth(ActionEvent actionEvent){
        try {
            network.sendMsg("/auth " + loginField.getText() + " " + passField.getText());
            loginField.clear();
            passField.clear();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING,
                    "Не удалось отправить сообщение", ButtonType.OK);
            alert.showAndWait();
        }
    }
}
