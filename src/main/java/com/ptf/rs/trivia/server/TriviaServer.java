package com.ptf.rs.trivia.server;


import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Handler;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptf.rs.trivia.answers.Answer;
import com.ptf.rs.trivia.questions.Question;
import com.ptf.rs.trivia.triviaUser.TriviaUser;
import com.fasterxml.jackson.annotation.JsonProperty;



public class TriviaServer {

    private static final int USER_NUMBER = 3;
    private static final Set<TriviaUser> triviaUsers = new HashSet<>();
    private static final List<Scanner> clientsIn = new ArrayList<>();
    private static final List<PrintWriter> clients = new ArrayList<>();
    private static final Question[] questions;

    static {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            //ucitavanje pitanja iz .json-a
            questions = objectMapper.readValue(new File("C:\\Users\\user\\OneDrive\\Radna površina\\pitanja.json"), Question[].class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        System.out.println("Trivia server je pokrenut.");
        ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(500);

        try (ServerSocket listener = new ServerSocket(8080)){
            while(true){
                pool.execute(new Handler(listener.accept()));
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static class Handler implements Runnable {

        private String username;
        private final Socket clientSocket;
        private Scanner in;
        private PrintWriter out;

        public Handler(Socket clientSocket){
            this.clientSocket = clientSocket;
        }

        public void run(){
            try{
                System.out.println("Novi thread.");
                in = new Scanner(clientSocket.getInputStream());
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                while(true){
                    out.println("SEND.NAME");
                    username = in.nextLine();
                    if (username == null)
                        return;

                        synchronized (triviaUsers) {
                            if (!username.isEmpty() && !triviaUsers.contains(new TriviaUser(username))) {
                                triviaUsers.add(new TriviaUser(username));
                                break;
                            }
                        }
                    }

                    out.println("NAME.ACCEPTED" + username);

                    sendJoinedInfo();

                    clients.add(out);
                    clientsIn.add(in);

                    if (triviaUsers.size() != USER_NUMBER){
                        out.println("GAME.WAIT");
                    }

                    while(true){
                        if (triviaUsers.size() == USER_NUMBER){
                            System.out.println("Svi su se pridružili.");
                            break;
                        }
                    }

                    System.out.println("Slanje informacija klijentima");
                    for(PrintWriter client : clients){
                        client.println("GAME.START");
                    }

                    int questionCounter = 0;
                    while(true) {

                        if (questionCounter == questions.length){
                            //pronalazi pobjednika kada se izlistaju sva pitanja (svih 12)
                            Set<TriviaUser> listResult = triviaUsers.stream()
                                    .sorted(Comparator.comparing(TriviaUser::getResult).reversed())
                                    .collect(Collectors.toCollection(LinkedHashSet::new));

                            for (PrintWriter client : clients){
                                client.println("GAME.FINISHED" + listResult);
                            }
                            questionCounter++;
                        } else if (questionCounter < questions.length){

                            Question currentQuestion = questions[questionCounter];
                            questionCounter++;

                            for(PrintWriter client : clients){
                                client.println("Q.START");
                                client.println(currentQuestion);
                            }

                            boolean moveToNextQuestion = false;
                            int counter = 0;
                            while(!moveToNextQuestion){

                                for(Scanner client : clientsIn){

                                    boolean inputValid = false;
                                    String answer = null;
                                    String[] inputs = null;

                                    while(!inputValid) {

                                        //čita se odgovor klijenta
                                        String userInput = client.nextLine();
                                        System.out.println("Input " + userInput);
                                        if (userInput.toLowerCase().startsWith("/kraj")){
                                            return;
                                        }
                                        else if (userInput.startsWith("ANSWER")) {
                                            try{
                                                inputs = userInput.split(";");
                                                answer = inputs[0].substring(6);
                                                System.out.println("Odgovor " + answer);

                                                if (answer.equals("0")){
                                                    for(PrintWriter c : clients){
                                                        c.println("GAME.FINSIHED" + c);
                                                    }
                                                    return;
                                                }
                                                else if (!answer.equalsIgnoreCase("A") && !answer.equalsIgnoreCase("B") && !answer.equalsIgnoreCase("C")) {
                                                    System.out.println("Pogresan odabir.");
                                                    int clientIndex = clients.indexOf(client);
                                                    clients.get(clientIndex).println("ERROR.CHOICE");
                                                }
                                                else {
                                                    inputValid = true;
                                                    break;
                                                }
                                            } catch (Exception e) {
                                                System.out.println("Pogrešan odabir.");
                                                int clientIndex = clientsIn.indexOf(client);
                                                clients.get(clientIndex).println("ERROR.CHOICE");
                                            }
                                        }
                                    }

                                    String clientName = inputs[1].substring(4);

                                    String finalAnswer = answer;
                                    Optional<Answer> correctAnswer = currentQuestion.getAnswers()
                                            .stream()
                                            .filter(a-> a.getChoice().equalsIgnoreCase(finalAnswer)).findFirst();
                                    if (correctAnswer.isPresent()) {
                                        if (correctAnswer.get().isCorrect()){
                                            TriviaUser user = triviaUsers
                                                    .stream()
                                                    .filter(c -> c.getName().equals(clientName)).findFirst().get();
                                                    user.povecajRezultat();
                                        }
                                    }
                                    counter++;
                                }

                                if (counter == USER_NUMBER){
                                    moveToNextQuestion = true;
                                    counter = 0;
                                }
                            }
                        }
                    }

        } catch(IOException e){
                System.out.println("GRESKA " + e);
            } finally {
                if (out != null)
                    clients.remove(out);
                if (username != null) {
                    triviaUsers.remove(username);
                    for(PrintWriter client : clients){
                        client.println(username + " je napustio/la igru.");
                    }
                }

                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void sendJoinedInfo() {
            for (PrintWriter client : clients) {
                client.println(username + " se pridruzio/la igri.");
            }
        }
    }
}
