import java.io.*;
import java.net.*;
import java.util.*;

public class QuizServer {
    private static final int PORT = 12345;
    static final List<String> QUESTIONS = Arrays.asList(
            "What is a socket? ;  An endpoint for communication between two machines over a network.",
            "What does a socket consist of?;An IP address and a port number.",
            " Which protocol provides reliable, connection-oriented communication using sockets?;TCP (Transmission Control Protocol).");

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Quiz Server is running...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new QuizHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class QuizHandler implements Runnable {
    private Socket clientSocket;

    public QuizHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            int score = 0;
            for (String question : QuizServer.QUESTIONS) {
                String[] parts = question.split(";");
                out.println(parts[0]); // Send question to client
                String answer = in.readLine(); // Receive answer from client
                if (answer.equalsIgnoreCase(parts[1])) {
                    score++;
                }
            }
            out.println("Your score: " + score + "/" + QuizServer.QUESTIONS.size());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}