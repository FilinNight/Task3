package by.gotovchits;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Game {

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        if (checkCorrectArgs(args)) {
            start(args);
        }
    }


    public static void test(){
        System.out.println("bla blaa bla");
    }

    public static void start(String[] args) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
        int indexYourMove = -1;
        final int indexComputerMove = randomValue(args.length);
        final byte[] key = secretRandomKey();
        final byte[] hmac = getHMAC(key, args[indexComputerMove]);

        System.out.println("\n HMAC: " + getValueBytes(hmac) +"\n");

        showMenu(args);

        indexYourMove = getIndexAnswerPlayer(args.length);

        System.out.println("\n Your move:     " + args[indexYourMove]);
        System.out.println(" Computer move: " + args[indexComputerMove]);

        startBattle(indexYourMove, indexComputerMove, args);

        System.out.println(" HMAC key: " + getValueBytes(key) + "\n\n");
    }

    private static void showMenu(String[] args) {
        System.out.println(" Available moves:\n");
        for (int i = 0; i < args.length; i++)
            System.out.println(" " + (i + 1) + " - " + args[i]);
        System.out.println("\n 0 - exit");
    }

    private static void startBattle(int indexYou, int indexComputer, String[] args) {
        int[] winIndices = new int[args.length / 2];
        boolean isWin = false;

        if (indexYou == indexComputer) {
            System.out.println("\n Result:\n ------------------\n    Nobody won    \n ------------------\n");
            return;
        }

        for (int i = 0, iRight = indexYou + 1, iStart = 0; i < args.length / 2; i++) {
            if (iRight <= args.length - 1) {
                winIndices[i] = iRight;
                iRight++;
            } else {
                winIndices[i] = iStart;
                iStart++;
            }
        }

        for (int index : winIndices) {
            if (index == indexComputer) {
                isWin = true;
            }
        }

        if (isWin) {
            System.out.println("\n Result:\n ------------------\n     YOU WIN!     \n ------------------\n");
        } else {
            System.out.println("\n Result:\n ------------------\n    You lose...   \n ------------------\n");
        }
    }

    private static int getIndexAnswerPlayer(int maxLength) {
        System.out.print("\n Enter your move: ");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int answer = 0;
        try {
            answer = Integer.parseInt(br.readLine());
            if(answer < 0 || answer > maxLength){
                System.out.println( "\n ------------------------\n Error: incorrect input!\n (Try again)\n ------------------------");
                answer = getIndexAnswerPlayer(maxLength) + 1;
            }
            if(answer == 0){
                System.out.println("\n Bye!\n");
                System.exit(0);
            }
        } catch (Exception e){
            System.out.println( "\n ------------------------\n Error: incorrect input!\n (Try again)\n ------------------------");
            answer = getIndexAnswerPlayer(maxLength) + 1;
        }
        return answer - 1;
    }

    private static boolean checkCorrectArgs(String[] args) {
        HashSet<String> tmp = new HashSet<String>(Arrays.asList(args));
        if(tmp.size() != args.length){
            System.out.println("\n Error: You entered the wrong number of arguments!");
            System.out
                .println(" (enter an odd number of parameters so that there are more than 2 of them, and that there are no duplicate elements)\n");
            return false;
        }


        if (args.length % 2 != 0 && args.length > 2) {
            return true;
        } else {
            System.out.println("\n Error: You entered the wrong number of arguments!");
            System.out
                .println(" (enter an odd number of parameters so that there are more than 2 of them, and that there are no duplicate elements)\n");
            return false;
        }
    }

    private static int randomValue(int range) {
        Random random = new Random();
        return random.nextInt(range);
    }

    private static byte[] secretRandomKey() throws NoSuchAlgorithmException {
        SecureRandom secureRandom = new SecureRandom();
        byte[] bytes = new byte[16];
        secureRandom.nextBytes(bytes);
        return bytes;
    }

    public static String getValueBytes(byte[] bytes) {
        StringBuilder value = new StringBuilder(bytes.length * 2);
        for (byte b : bytes)
            value.append(String.format("%02x", b));
        return value.toString();
    }

    private static byte[] getHMAC(byte[] key, String computerMove) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        final String HMAC_ALGO = "HmacSHA512";

        Mac signer = Mac.getInstance(HMAC_ALGO);
        SecretKeySpec keySpec = new SecretKeySpec(key, HMAC_ALGO);
        signer.init(keySpec);

        byte[] digest = signer.doFinal(computerMove.getBytes("utf-8"));

        return digest;
    }
}
