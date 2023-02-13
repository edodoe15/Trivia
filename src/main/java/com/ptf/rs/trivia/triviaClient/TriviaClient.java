package com.ptf.rs.trivia.triviaClient;

import javax.swing.*;

import com.ptf.rs.trivia.triviaUser.TriviaUser;

import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class TriviaClient {

    String serverAdresa;
    Scanner in;
    PrintWriter out;
    JFrame frame = new JFrame("Trivia");
    JTextField txtChat = new JTextField(50);
    JTextArea txtArea = new JTextArea(16, 50);

    TriviaUser triviaUser;

    public TriviaClient(final String serverAdresa){
        this.serverAdresa = serverAdresa;
        txtChat.setEnabled(false);
        JScrollPane scrollPane = new JScrollPane(txtArea);

        frame.getContentPane().add(txtChat, BorderLayout.SOUTH);
        frame.getContentPane().add(scrollPane);
        frame.pack();

        txtChat.addActionListener(e -> {
            out.println("ANSWER" + txtChat.getText() + ";NAME" + triviaUser.getName());  //slanje odgovora na server
            txtArea.append(txtChat.getText() + "\n");
            txtChat.setText("");
        });
    }

    public static void main(String[] args) throws IOException {
        TriviaClient triviaClient = new TriviaClient("127.0.0.1");
        triviaClient.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        triviaClient.frame.setVisible(true);
        triviaClient.run();
    }

    private String getIme(){
        return JOptionPane.showInputDialog(frame, "Ime", "Unesite ime", JOptionPane.PLAIN_MESSAGE);
    }

    private void run() throws IOException{
        try{
            Socket socket = new Socket(serverAdresa, 8080);
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);

            while(in.hasNextLine()){
                String line = in.nextLine();
                if (line.startsWith("SEND.NAME")){  //unos imena
                    String ime = getIme();  //kreiranje TriviaUsera
                    triviaUser = new TriviaUser(ime);
                    out.println(ime); //slanje imena na server
                }
                else if (line.startsWith("NAME.ACCEPTED")){
                    this.frame.setTitle("Trivia - " + line.substring(13));
                }
                else if (line.startsWith("INFO")){
                    txtArea.append(line.substring(4) + "\n");
                }
                else if (line.startsWith("Q.START")){
                    txtArea.append(in.nextLine() + "\n");  //pitanje
                    txtArea.append(in.nextLine() + "\n");  //odgovorA
                    txtArea.append(in.nextLine() + "\n");  //odgovorB
                    txtArea.append(in.nextLine() + "\n");  //odgovorC
                }
                else if (line.startsWith("GAME.START")){
                    txtChat.setEnabled(true);
                    txtArea.append("GAME STARTED! " + "\n");
                }
                else if (line.startsWith("ERROR.CHOICE")){
                    txtArea.append("Unesite ispravan izbor (ili 0 za napuštanje)." + "\n");
                }
                else if (line.startsWith("GAME.WAIT")){
                    txtArea.append("Sačekajte dok se pridruže i ostali igrači. " + "\n");
                }
                else if (line.startsWith("THE.END")){
                    txtArea.append("Igra je gotova. " + "\n");
                }
                else if (line.startsWith("GAME.FINISHED")){
                    txtArea.append("Rezultat: " + "\n");
                    txtArea.append(line.substring(13) + "\n");
                }
            }
        }
        finally{
            frame.setVisible(false);
            frame.dispose();
        }
    }
}
