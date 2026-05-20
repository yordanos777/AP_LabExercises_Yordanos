import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

class Card {

    private String rank;

    private String suit;

    Card(String rank, String suit) {

        this.rank = rank;

        this.suit = suit;
    }

    public String getRank() {

        return rank;
    }

    public String getSuit() {

        return suit;
    }

    @Override
    public String toString() {

        return rank + suit;
    }
}

class Player {

    private String name;

    private ArrayList<Card> cards;

    Player(String name) {

        this.name = name;

        cards = new ArrayList<>();
    }

    public void addCard(Card c) {

        cards.add(c);
    }

    public ArrayList<Card> getCards() {

        return cards;
    }

    public String getName() {

        return name;
    }
}

public class Listexe {


    static int getValue(String rank) {

        switch (rank) {

            case "A":
                return 14;

            case "K":
                return 13;

            case "Q":
                return 12;

            case "J":
                return 11;

            default:
                return Integer.parseInt(rank);
        }
    }

    static int getHighest(ArrayList<Card> player) {

        int max = 0;

        for (Card c : player) {

            int value = getValue(c.getRank());

            if (value > max) {

                max = value;
            }
        }

        return max;
    }

    static int countPairs(ArrayList<Card> player) {

        int count = 0;

        for (int i = 0; i < player.size(); i++) {

            for (int j = i + 1; j < player.size(); j++) {

                if (player.get(i).getRank()
                        .equals(player.get(j).getRank())) {

                    count++;
                }
            }
        }

        return count;
    }

    static synchronized void saveResult(String result) {

        try {

            FileWriter writer =
                    new FileWriter("game_result.txt", true);

            writer.write(
                    "\n===== GAME =====\n"
                            + result
                            + "\nSaved at: "
                            + LocalDateTime.now()
                            + "\n"
            );

            writer.close();

        } catch (IOException e) {

            System.out.println("File error");
        }
    }

    public static void main(String[] args) {

        String[] ranks = {
                "2","3","4","5","6",
                "7","8","9","10",
                "J","Q","K","A"
        };

        String[] suits = {"@","%","&","*"};

        ArrayList<Card> deck = new ArrayList<>();

        for (String rank : ranks) {

            for (String suit : suits) {

                deck.add(new Card(rank, suit));
            }
        }

        Collections.shuffle(deck);
        Player player1 = new Player("Player 1");

        Player player2 = new Player("Player 2");

        for (int i = 0; i < 5; i++) {

            player1.addCard(deck.remove(0));

            player2.addCard(deck.remove(0));
        }
        System.out.println(
                player1.getName() + ": "
                        + player1.getCards()
        );

        System.out.println(
                player2.getName() + ": "
                        + player2.getCards()
        );

        int p1 = countPairs(player1.getCards());

        int p2 = countPairs(player2.getCards());

        String result;

        
        if (p1 > p2) {

            result = "Player 1 wins!";

        } else if (p2 > p1) {

            result = "Player 2 wins!";

        } else {

            int h1 = getHighest(player1.getCards());

            int h2 = getHighest(player2.getCards());

            if (h1 > h2) {

                result =
                        "Tie on pairs -> Player 1 wins by highest card!";

            } else if (h2 > h1) {

                result =
                        "Tie on pairs -> Player 2 wins by highest card!";

            } else {

                result = "Complete Tie!";
            }
        }
        System.out.println(result);
        String finalResult = result;

        new Thread(() ->
                saveResult(finalResult)
        ).start();
    }
}